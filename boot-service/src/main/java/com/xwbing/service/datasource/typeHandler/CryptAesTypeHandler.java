package com.xwbing.service.datasource.typeHandler;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author daofeng
 * @version $
 * @since 2024年12月24日 4:08 PM
 */
public class CryptAesTypeHandler extends BaseTypeHandler<String> {
    private final AES aes;

    public CryptAesTypeHandler() {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), "xwbing__aes__key".getBytes()).getEncoded();
        this.aes = SecureUtil.aes(key);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 将原始数据加密后存储
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库获取数据后解密
        return decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        // 从数据库获取数据后解密
        return decrypt(resultSet.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从数据库获取数据后解密
        return decrypt(cs.getString(columnIndex));
    }

    private String encrypt(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        return aes.encryptBase64(data);
    }

    private String decrypt(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        return aes.decryptStr(data);
    }
}