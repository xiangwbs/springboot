package com.xwbing.service.enums.base;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

/**
 * fastJson枚举反序列化
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年10月21日 4:30 PM
 */
public class FastJsonDeserializer implements ObjectDeserializer {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        final JSONLexer lexer = parser.lexer;
        final int token = lexer.token();
        Class enumType = (Class)type;
        Object[] enumConstants = enumType.getEnumConstants();
        if (BaseEnum.class.isAssignableFrom(enumType)) {
            JSONObject jsonObject = JSONObject.parseObject(parser.getInput()).getJSONObject(String.valueOf(fieldName));
            for (Object enumConstant : enumConstants) {
                BaseEnum baseEnum = (BaseEnum)enumConstant;
                if (jsonObject.getInteger("code") == baseEnum.getCode()) {
                    return (T)enumConstant;
                }
            }
        } else {
            //没实现BaseEnum的 默认的按名字或者按ordinal
            if (token == JSONToken.LITERAL_INT) {
                int intValue = lexer.intValue();
                lexer.nextToken(JSONToken.COMMA);
                if (intValue < 0 || intValue > enumConstants.length) {
                    throw new JSONException(
                            String.format("parse enum %s error, value : %s", enumType.getName(), intValue));
                }
                return (T)enumConstants[intValue];
            } else if (token == JSONToken.LITERAL_STRING) {
                return (T)Enum.valueOf(enumType, lexer.stringVal());
            }
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
