package com.xwbing.service.demo.sql;

import net.sf.jsqlparser.JSQLParserException;
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
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        String sql = "select region from region_data where date is not null and date!='2023' and (date in('2023','2024') and date between '2023' and '2024')";
        Map<String, String> dateSqlMap = formatDate(sql);
        System.out.println(dateSqlMap);
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
                        sqlMap.put(in.toString(), column + " in (" + String.join(",", valueList) + ")");
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