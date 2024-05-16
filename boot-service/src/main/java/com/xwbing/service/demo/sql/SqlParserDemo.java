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
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
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
        String sql = "\nSELECT\n  STATISTICS_DATE,\n  TOTAL_FISCAL_REVENUE \nFROM\n  bot_chat_bi_economic \nWHERE\n  STATISTICS_DATE = 20211231 \n  AND CITY = '杭州市' \n  AND DISTRICT IS NULL;";
//        String sql = "SELECT \n" +
//                "    t2.REGION, \n" +
//                "    ROUND((t2.TOTAL_EXPORT - t1.TOTAL_EXPORT) / t1.TOTAL_EXPORT * 100,2) AS 增长率\n" +
//                "FROM \n" +
//                "    bot_bi_national_economic_data t1\n" +
//                "JOIN \n" +
//                "    bot_bi_national_economic_data t2 ON t1.REGION = t2.REGION\n" +
//                "WHERE \n" +
//                "    t1.DATE = 20171231 AND  t2.DATE = 20221231 \n" +
//                "\tand t2.region='宁波市'\n" +
//                "\torder by 增长率 desc";
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
        Set<String> tables = TablesNamesFinder.findTables(sql);
        Table table = (Table) select.getFromItem();
        String tableName = table.getName();
        List<SelectItem<?>> selectItems = select.getSelectItems();
        selectItems.forEach(selectItem -> {
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column) {
                selectItem.setAlias(null);
            }
        });
        String reSql = select.toString().toLowerCase();
        Limit limit = select.getLimit();
        if (limit == null) {
            reSql = reSql + " limit 100";
        }
        System.out.println("");


//        // find in Statements
//        String sqlStr = "SELECT a.CURRENTYEARTAXREVENU/b.CURRENTYEARTAXREVENU*100  from\n" +
//                "(SELECT CURRENTYEARTAXREVENU  FROM DWS_LEVY_DOMAIN_QYSRTJ_HZ\n" +
//                "WHERE YEARMONTH =202312 AND TYPECODE =1 AND RECEIVINGTRENAME LIKE '%余杭区%') a,\n" +
//                "(SELECT sum(CURRENTYEARTAXREVENU) CURRENTYEARTAXREVENU FROM DWS_LEVY_DOMAIN_QYSRTJ_HZ\n" +
//                "WHERE YEARMONTH =202312 AND TYPECODE =1 ) b";
//        Set<String> tableNames = TablesNamesFinder.findTables(sqlStr);
//        // find in Expressions
//        String exprStr = "A.id=B.id and A.age = (select age from C)";
//        tableNames = TablesNamesFinder.findTablesInExpression(exprStr);
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