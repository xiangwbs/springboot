package com.xwbing.service.demo.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * sql解析
 *
 * @author daofeng
 * @version $
 * @since 2024年03月05日 1:45 PM
 */
public class SqlParserDemo {
    public static void main(String[] args) throws JSQLParserException {
//        base("select role.name,count(authority.id) from ROLE role left join AUTHORITY authority on(role.id=authority.roleId) where role.id in(1000,1001) group by role.id  order by role.creationDate desc limit 10");
//        String addColumnSql = addColumn("select name,age from user where id=1", "sex");
        System.out.println("");
    }

    private static void base(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        PlainSelect select = (PlainSelect) statement;
        // 获取所有表
        Set<String> tables = new TablesNamesFinder().getTables(statement);
        // 获取from中的表
        Table fromTable = (Table) select.getFromItem();
        String tableName = fromTable.getName();
        Alias tableAlias = fromTable.getAlias();
        // 获取关联的表
        List<Table> joinTableList = select.getJoins()
                .stream()
                .map(join -> (Table) join.getRightItem())
                .collect(Collectors.toList());

        List<SelectItem<?>> selectList = select.getSelectItems();
        List<OrderByElement> orderByList = select.getOrderByElements();
        GroupByElement groupBy = select.getGroupBy();
        Limit limit = select.getLimit();
        System.out.println("");
    }

    private static String addColumn(String sql, String field) throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql.toLowerCase());
        List<SelectItem<?>> selectItems = select.getSelectItems();
        List<String> selectColumList = selectItems.stream()
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

    private static Map<String, String> formatDate(String sql) throws JSQLParserException {
        Map<String, String> sqlMap = new HashMap<>();
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
        Expression where = select.getWhere();
        if (where == null) {
            return sqlMap;
        }
        List<Expression> expressions = listExpression(where, new ArrayList<>());
        expressions.stream()
                .filter(expression -> expression.toString().contains("date"))
                .forEach(expression -> {
                    if (expression instanceof ComparisonOperator) {
                        ComparisonOperator comparison = (ComparisonOperator) expression;
                        String column = comparison.getLeftExpression().toString();
                        String operator = comparison.getStringExpression();
                        String value = comparison.getRightExpression().toString();
                        sqlMap.put(comparison.toString(), column + operator + value);
                        System.out.println("");
                    } else if (expression instanceof InExpression) {
                        InExpression in = (InExpression) expression;
                        String column = in.getLeftExpression().toString();
                        List<String> valueList = (List<String>) in.getRightExpression(ExpressionList.class).stream().map(o -> o.toString()).collect(Collectors.toList());
                        String operator = in.isNot() ? " not in" : " in ";
                        sqlMap.put(in.toString(), column + operator + "(" + String.join(",", valueList) + ")");
                        System.out.println("");
                    } else if (expression instanceof Between) {
                        Between between = (Between) expression;
                        String column = between.getLeftExpression().toString();
                        String start = between.getBetweenExpressionStart().toString();
                        String end = between.getBetweenExpressionEnd().toString();
                        sqlMap.put(between.toString(), column + " between " + start + " and " + end);
                        System.out.println("");
                    }
                });
        return sqlMap;
    }

    private static List<Expression> listExpression(Expression expression, List<Expression> expressions) {
        if (expression instanceof AndExpression || expression instanceof OrExpression) {
            BinaryExpression expr = (BinaryExpression) expression;
            listExpression(expr.getLeftExpression(), expressions);
            listExpression(expr.getRightExpression(), expressions);
        } else if (expression instanceof Parenthesis) {
            Parenthesis expr = (Parenthesis) expression;
            listExpression(expr.getExpression(), expressions);
        } else {
            expressions.add(expression);
        }
        return expressions;
    }

    public static void reSql(PlainSelect select, List<SqlFieldVO> fieldList) {
        Map<String, Byte> functionDataTypeMap = new HashMap<>();
        Table table = (Table) select.getFromItem();
        String tableName = table.getName();
        List<String> selectItemList = select.getSelectItems().stream().map(selectItem -> {
            String item = selectItem.toString();
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column) {
                Column column = (Column) expression;
                item = column.getColumnName();
            } else {
                // 映射涉及计算列的字段类型
                String expressionStr = expression.toString();
                SqlFieldVO matchField = fieldList.stream().filter(chatBiFieldVO -> expressionStr.contains(chatBiFieldVO.getCode())).findFirst().orElse(null);
                if (matchField != null) {
                    String key = Optional.ofNullable(selectItem.getAlias()).map(Alias::getName).orElse(expressionStr);
                    functionDataTypeMap.put(key, matchField.getDataType());
                }
            }
            return item;
        }).collect(Collectors.toList());
        String whereStr = Optional.ofNullable(select.getWhere()).map(Object::toString).orElse(null);
        String orderByStr = "";
        if (CollectionUtils.isNotEmpty(select.getOrderByElements())) {
            orderByStr = select.getOrderByElements().stream().map(OrderByElement::toString).collect(Collectors.joining(","));
        }
        StringBuilder sql = new StringBuilder()
                .append("select ")
                .append(select.getDistinct() != null ? "distinct " : "")
                .append(String.join(",", selectItemList))
                .append(" from ")
                .append(tableName)
                .append(StringUtils.isNotEmpty(whereStr) ? " where " + whereStr : "")
                .append(select.getGroupBy() != null ? " " + select.getGroupBy().toString() : "")
                .append(select.getHaving() != null ? " having " + select.getHaving().toString() : "")
                .append(StringUtils.isNotEmpty(orderByStr) ? " order by " + orderByStr : "")
                .append(select.getLimit() != null ? " " + select.getLimit().toString() : " limit 100");
        String sqlSr = sql.toString().toLowerCase();
    }


    public static void join() throws JSQLParserException {
        List<String> sqlList = new ArrayList<>();
        sqlList.add("select table1.税收收入 from table1 where table1.region='杭州'");
        sqlList.add("select table2.非税收入 from table2 where table2.region='杭州'");
        List<String> selectList = new ArrayList<>();
        List<String> tableList = new ArrayList<>();
        List<String> whereList = new ArrayList<>();
        for (String sql : sqlList) {
            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
            List<SelectItem<?>> selectItems = select.getSelectItems();
            selectList.addAll(selectItems.stream().map(selectItem -> selectItem.getExpression().toString()).collect(Collectors.toList()));
            Table table = (Table) select.getFromItem();
            tableList.add(table.getName());
            Expression where = select.getWhere();
            whereList.add(where.toString());
        }
        String sql = "select " +
                String.join(",", selectList) +
                " from " +
                String.join(",", tableList) +
                " where " +
                String.join(" and ", whereList);
        System.out.println("sql语句为:" + sql);
    }
}