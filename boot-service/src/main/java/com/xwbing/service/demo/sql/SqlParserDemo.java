package com.xwbing.service.demo.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
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
    public static void main(String[] args) throws Exception {
//        String s = "SELECT * from sys_user_info union all select * from sys_user_info union all select * from sys_user_info";
        String s = "SELECT a.name,b.aid from tablea a right join tableb b on(a.id=b.aid) where a.name=1";
        Statement statement = CCJSqlParserUtil.parse(s);
        PlainSelect selectt = (PlainSelect) statement;
        FromItem fromItem = selectt.getFromItem();
        List<Join> joins = selectt.getJoins();
        joins.forEach(join -> {
            FromItem fromItem1 = join.getRightItem();
        });
        Set<String> tables = new TablesNamesFinder().getTables(statement);
        if (statement instanceof PlainSelect) {
            PlainSelect statement1 = (PlainSelect) statement;
        } else if (statement instanceof SetOperationList) {
            SetOperationList statement1 = (SetOperationList) statement;
            statement1.getSelects().forEach(select -> {
                PlainSelect select1 = (PlainSelect) select;
                Limit limit = new Limit();
                limit.setRowCount(new LongValue(1000));
                select1.setLimit(limit);
                System.out.println("");
            });
            System.out.println("");
        }
        System.out.println("");
//        base("select distinct r.name,count(a.id) from ROLE r left join AUTHORITY a on(r.id=a.roleId) where r.id in(1000,1001) group by r.id having count(a.id)>10 order by r.creationDate desc limit 10");
//        formatDate("select region from region_data where date is not null and date!='2023' and (date in('2023','2024') and date between '2023' and '2024')");
        System.out.println("");
    }

    private static void base(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql.toLowerCase());
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
        Distinct distinct = select.getDistinct();
        List<SelectItem<?>> selectList = select.getSelectItems();
        Expression where = select.getWhere();
        GroupByElement groupBy = select.getGroupBy();
        Expression having = select.getHaving();
        List<OrderByElement> orderByList = select.getOrderByElements();
        Limit limit = select.getLimit();
        System.out.println("");
    }

    private static Map<String, String> formatDate(String sql) {
        Map<String, String> sqlMap = new HashMap<>();
        PlainSelect select = SqlUtil.getSelect(sql);
        Expression where = select.getWhere();
        if (where == null) {
            return sqlMap;
        }
        List<Expression> expressions = SqlUtil.listWhereExpression(where, new ArrayList<>());
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

    public static String reSql(String sql) {
        PlainSelect select = SqlUtil.getSelect(sql);
        Table table = (Table) select.getFromItem();
        String tableName = table.getName();
        List<String> selectItemList = select.getSelectItems().stream().map(selectItem -> {
            String item = selectItem.toString();
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column) {
                Column column = (Column) expression;
                item = column.getColumnName();
            }
            return item;
        }).collect(Collectors.toList());
        String whereStr = Optional.ofNullable(select.getWhere()).map(Object::toString).orElse(null);
        String orderByStr = "";
        if (CollectionUtils.isNotEmpty(select.getOrderByElements())) {
            orderByStr = select.getOrderByElements().stream().map(OrderByElement::toString).collect(Collectors.joining(","));
        }
        StringBuilder sqlBuilder = new StringBuilder()
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
        return sqlBuilder.toString().toLowerCase();
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