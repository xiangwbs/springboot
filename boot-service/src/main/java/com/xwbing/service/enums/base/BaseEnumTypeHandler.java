package com.xwbing.service.enums.base;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * mybatis枚举类型转换器
 *
 * @author daofeng
 */
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<BaseEnum> {

    private final Class<E> type;

    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    /**
     * 用于定义在Mybatis设置参数时该如何把Java类型的参数转换为对应的数据库类型
     *
     * @param ps 当前的PreparedStatement对象
     * @param i 当前参数的位置
     * @param parameter 当前参数的Java对象
     * @param jdbcType 当前参数的数据库类型
     *
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BaseEnum parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    /**
     * 用于在Mybatis获取数据结果集时如何把数据库类型转换为对应的Java类型
     *
     * @param rs 当前的结果集
     * @param columnName 当前的字段名称
     *
     * @return 转换后的Java对象
     *
     * @throws SQLException
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : codeOf(code);
    }

    /**
     * 用于在Mybatis通过字段位置获取字段数据时把数据库类型转换为对应的Java类型
     *
     * @param rs 当前的结果集
     * @param columnIndex 当前字段的位置
     *
     * @return 转换后的Java对象
     *
     * @throws SQLException
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : codeOf(code);
    }

    /**
     * 用于Mybatis在调用存储过程后把数据库类型的数据转换为对应的Java类型
     *
     * @param cs 当前的CallableStatement执行后的CallableStatement
     * @param columnIndex 当前输出参数的位置
     *
     * @return
     *
     * @throws SQLException
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : codeOf(code);
    }

    private E codeOf(int code) {
        try {
            return Arrays.stream(type.getEnumConstants()).filter(e -> e.getCode() == code).findFirst().orElse(null);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", ex);
        }
    }
}
