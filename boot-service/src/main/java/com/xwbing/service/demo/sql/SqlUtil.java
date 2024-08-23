package com.xwbing.service.demo.sql;

import cn.hutool.core.collection.ListUtil;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年06月18日 1:44 PM
 */
@Slf4j
public class SqlUtil {
    public static Statement getStatement(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        try {
            return CCJSqlParserUtil.parse(sql.toLowerCase());
        } catch (Exception e) {
            log.error("getStatement sql:{} error", sql, e);
            return null;
        }
    }

    public static boolean isSelect(String sql) {
        return getStatement(sql) instanceof PlainSelect;
    }

    public static PlainSelect getSelect(String sql) {
        Statement statement = getStatement(sql);
        if (statement instanceof PlainSelect) {
            return (PlainSelect) statement;
        } else {
            return null;
        }
    }

    public static List<Expression> listWhereExpression(Expression expression, List<Expression> expressions) {
        if (expression instanceof AndExpression || expression instanceof OrExpression) {
            BinaryExpression expr = (BinaryExpression) expression;
            listWhereExpression(expr.getLeftExpression(), expressions);
            listWhereExpression(expr.getRightExpression(), expressions);
        } else if (expression instanceof Parenthesis) {
            Parenthesis expr = (Parenthesis) expression;
            listWhereExpression(expr.getExpression(), expressions);
        } else {
            expressions.add(expression);
        }
        return expressions;
    }

    public static String addColumn(String sql, String field) {
        PlainSelect select = getSelect(sql);
        if (select == null) {
            return sql;
        }
        List<String> selectColumList = select.getSelectItems().stream()
                .filter(selectItem -> selectItem.getExpression() instanceof Column)
                .map(selectItem -> {
                    Expression expression = selectItem.getExpression();
                    Column column = (Column) expression;
                    return column.getColumnName();
                }).collect(Collectors.toList());
        if (!selectColumList.contains(field.toLowerCase())) {
            SelectUtils.addExpression(select, new Column(field.toLowerCase()));
        }
        return select.toString().toLowerCase();
    }

    public static List<String> listField(String sql) {
        PlainSelect select = getSelect(sql);
        if (select == null) {
            return Collections.emptyList();
        }
        // 获取select字段
        Set<String> fieldList = select.getSelectItems().stream().filter(selectItem -> {
            Expression expression = selectItem.getExpression();
            return expression instanceof Column;
        }).map(selectItem -> {
            Column column = (Column) selectItem.getExpression();
            return column.getColumnName();
        }).collect(Collectors.toSet());
        // 获取where字段
        Expression where = select.getWhere();
        if (where != null) {
            List<Expression> expressions = SqlUtil.listWhereExpression(where, new ArrayList<>());
            expressions.forEach(expression -> expression.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(Column column) {
                    fieldList.add(column.getColumnName());
                }
            }));
        }
        // 获取group by字段
        GroupByElement groupBy = select.getGroupBy();
        if (groupBy != null) {
            groupBy.getGroupByExpressionList().forEach(expression -> {
                Column column = (Column) expression;
                fieldList.add(column.getColumnName());
            });
        }
        // 获取order by字段
        List<OrderByElement> orderByList = select.getOrderByElements();
        if (CollectionUtils.isNotEmpty(orderByList)) {
            orderByList.forEach(orderByElement -> {
                Column column = (Column) orderByElement.getExpression();
                fieldList.add(column.getColumnName());
            });
        }
        return ListUtil.toList(fieldList);
    }

    public static String formatSql(String sql) {
        FormatConfig formatConfig = FormatConfig.builder()
                // 关键词大写
                .uppercase(true)
                // 多个查询语句之间的换行数
                .linesBetweenQueries(2)
                // ()查询是否跳过换行
                .skipWhitespaceNearBlockParentheses(false)
                .build();
        return SqlFormatter.of(Dialect.MySql).format(sql, formatConfig);
    }

    public static DealSqlVO dealSql(String tableName, String sql, List<SqlFieldVO> fieldList, boolean addLimit) {
        PlainSelect select = SqlUtil.getSelect(sql);
        if (select == null) {
            return null;
        }
        DealSqlVO vo = new DealSqlVO();
        vo.setOriginalSql(sql);
        List<String> selectFieldList = new ArrayList<>();
        Map<String, Byte> functionDataTypeMap = new HashMap<>();
        Map<String, String> fieldNameMap = fieldList.stream().collect(Collectors.toMap(SqlFieldVO::getCode, SqlFieldVO::getName));
        Map<String, String> columnAliasMap = new HashMap<>();
        List<SelectItem<?>> selectItems = select.getSelectItems();
        if (selectItems.size() == 1 && selectItems.get(0).getExpression() instanceof AllColumns) {
            // select * 替换成字段
            selectItems.remove(0);
            for (SqlFieldVO sqlField : fieldList) {
                SelectUtils.addExpression(select, new Column(sqlField.getCode()));
            }
        }
        selectItems.forEach(selectItem -> {
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column) {
                Column column = (Column) expression;
                // 设置别名
                Alias alias = selectItem.getAlias();
                if (alias == null) {
                    String columnName = fieldNameMap.get(column.getColumnName());
                    if (StringUtils.isNotEmpty(columnName)) {
                        alias = new Alias("`" + columnName + "`", true);
                        selectItem.setAlias(alias);
                    }
                }
                if (alias != null) {
                    columnAliasMap.put(alias.getName(), column.getColumnName());
                }
                // 汇总查询字段
                selectFieldList.add(column.getColumnName());
            } else {
                // 映射涉及计算列的字段类型
                String expressionStr = expression.toString();
                String key = Optional.ofNullable(selectItem.getAlias()).map(Alias::getName).orElse(expressionStr);
                fieldList.stream().filter(chatBiFieldVO -> expressionStr.contains(chatBiFieldVO.getCode())).findFirst().ifPresent(matchField -> functionDataTypeMap.put(key, matchField.getDataType()));
                // 汇总查询列
                selectFieldList.add(key);
            }
        });
        String aliasSql = select.toString().toLowerCase();
        if (addLimit) {
            Limit limit = select.getLimit();
            if (limit == null) {
                aliasSql = aliasSql + " limit 1000";
            }
        }
        vo.setAliasSql(aliasSql);
        vo.setSelectFieldList(selectFieldList);
        vo.setFunctionDataTypeMap(functionDataTypeMap);
        // 获取去除别名的sql 便于动态sql查询到的数据(map<key,value>)key能匹配到对应的字段
        select = SqlUtil.getSelect(aliasSql);
        select.getSelectItems().forEach(selectItem -> {
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column) {
                selectItem.setAlias(null);
            }
        });
        GroupByElement groupBy = select.getGroupBy();
        if (groupBy != null) {
            groupBy.getGroupByExpressionList().forEach(expression -> {
                if (expression instanceof Column) {
                    Column column = (Column) expression;
                    String columnName = columnAliasMap.get(column.getColumnName());
                    if (StringUtils.isNotEmpty(columnName)) {
                        column.setColumnName(columnName);
                    }
                }
            });
        }
        Expression having = select.getHaving();
        if (having != null) {
            having.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(Column column) {
                    String columnName = columnAliasMap.get(column.getColumnName());
                    if (StringUtils.isNotEmpty(columnName)) {
                        column.setColumnName(columnName);
                    }
                }
            });
        }
        List<OrderByElement> orderByElements = select.getOrderByElements();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderByElements)) {
            orderByElements.forEach(orderByElement -> {
                Expression expression = orderByElement.getExpression();
                if (expression instanceof Column) {
                    Column column = (Column) expression;
                    String columnName = columnAliasMap.get(column.getColumnName());
                    if (StringUtils.isNotEmpty(columnName)) {
                        column.setColumnName(columnName);
                    }
                }
            });
        }
        vo.setNoAliasSql(select.toString().toLowerCase());
        // 加工展示的sql
        select = SqlUtil.getSelect(aliasSql);
        Table fromTable = (Table) select.getFromItem();
        fromTable.setName("`" + tableName + "`");
        String displaySql = select.toString().toLowerCase();
        for (SqlFieldVO field : fieldList) {
            displaySql = displaySql.replaceAll("\\b" + field.getCode() + "\\b", "`" + field.getName() + "`");
        }
        vo.setDisplaySql(displaySql);
        return vo;
    }

    public static void main(String[] args) {
//        String formatSql = formatSql("select a as `a`,b,c from table1 where a=1 and b=1 and (c between 1 and 2) group by a order by b limit 10;select * from table2");
//        List<String> fieldList = SqlUtil.listField("select region from region_data where date is not null and date!='2023' and (code in('2023','2024') and age between '10' and '20') group by code order by id desc");
//        String addColumnSql = SqlUtil.addColumn("select name,age from user where id=1", "sex");
        SqlFieldVO sqlField = new SqlFieldVO();
        sqlField.setCode("a");
        sqlField.setName("我是a");
        SqlFieldVO sqlField1 = new SqlFieldVO();
        sqlField1.setCode("b");
        sqlField1.setName("我是b");
        SqlUtil.dealSql("表", "select a as `第一`,b from table1 where a=1 and b=1 group by `第一` having `第一`>0 order by `第一` limit 10", ListUtil.toList(sqlField, sqlField1), false);
        System.out.println("");
    }
}