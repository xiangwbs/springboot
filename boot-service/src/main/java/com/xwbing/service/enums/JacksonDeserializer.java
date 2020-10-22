package com.xwbing.service.enums;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import lombok.extern.slf4j.Slf4j;

/**
 * jackson枚举反序列化
 *
 * @author daofeng
 * @version $
 * @since 2019年11月29日 18:59
 */
// @JsonComponent
@Slf4j
public final class JacksonDeserializer<T extends Enum<T> & BaseEnum> extends JsonDeserializer<T>
        implements ContextualDeserializer {

    private T[] enums;

    @Override
    public T deserialize(JsonParser p, DeserializationContext context) throws IOException {
        try {
            int code = p.getIntValue();
            return findByCode(code);
        } catch (Exception ex) {
            TreeNode node = p.getCodec().readTree(p).get("code");
            if (node instanceof IntNode) {
                IntNode intNode = (IntNode)node;
                int code = intNode.asInt();
                return findByCode(code);
            } else {
                log.error("{} not IntNode", node.getClass().getCanonicalName(), ex);
                return null;
            }
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        JacksonDeserializer<T> d = new JacksonDeserializer<>();
        JavaType javaType = property.getType();
        if (javaType.isCollectionLikeType()) {
            d.enums = (T[])javaType.getBindings().getBoundType(0).getRawClass().getEnumConstants();
        } else {
            d.enums = (T[])javaType.getRawClass().getEnumConstants();
        }
        return d;
    }

    private T findByCode(int code) {
        return Arrays.stream(enums).filter(e -> e.getCode() == code).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown type " + code));
    }
}
