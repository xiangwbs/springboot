package com.xwbing.service.demo.sql;

import cn.hutool.core.collection.ListUtil;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.SelectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年06月18日 1:44 PM
 */
@Slf4j
public class SqlUtil {
    public static PlainSelect getSelect(String sql) {
        try {
            return (PlainSelect) CCJSqlParserUtil.parse(sql.toLowerCase());
        } catch (Exception e) {
            log.error("getSelect error", e);
            return null;
        }
    }

    public static Statement getStatement(String sql) {
        try {
            return CCJSqlParserUtil.parse(sql.toLowerCase());
        } catch (Exception e) {
            log.error("getSelect error", e);
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

    public static List<String> listField(String sql) {
        PlainSelect select = getSelect(sql);
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
        // 获取order by字段
        List<OrderByElement> orderByList = select.getOrderByElements();
        if (CollectionUtils.isNotEmpty(orderByList)) {
            orderByList.forEach(orderByElement -> {
                Column column = (Column) orderByElement.getExpression();
                fieldList.add(column.getColumnName());
            });
        }
        // 获取group by字段
        GroupByElement groupBy = select.getGroupBy();
        if (groupBy != null) {
            groupBy.getGroupByExpressionList().forEach(expression -> {
                Column column = (Column) expression;
                fieldList.add(column.getColumnName());
            });
        }
        return ListUtil.toList(fieldList);
    }

    public static String formatSql(String sql) {
        return SqlFormatter.format(sql, FormatConfig.builder().uppercase(true).build());
    }

    public static void main(String[] args) {
        String sql = "select `纳税人名称` as `纳税人名称`, `数量` as `数量` from `重点税源企业基本信息表` where `数量` < 10 and regdate between '2023-01-01' and '2024-12-31' limit 1000";
        String s = formatSql(sql);
        System.out.println("");
    }
}