package com.xwbing.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author daofeng
 * @version $
 * @since 2019年11月29日 18:59
 */
public class Jackson {
    private static volatile Base base;

    /**
     * 获取默认实例
     */
    public static Base build() {
        if (base == null) {
            synchronized (Jackson.class) {
                if (base == null) {
                    base = new Base();
                }
            }
        }
        return base;
    }

    public static class Base {
        protected ObjectMapper mapper;

        private Base() {
            this.mapper = new ObjectMapper();
            //输入时忽略JSON字符串中存在而Java对象实际没有的属性
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
            //默认采用东八区方式来解释日期字符串
            //mapper.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            //对于值为空的属性不生成
            mapper.setSerializationInclusion(Include.NON_NULL);
        }

        public TypeFactory getTypeFactory() {
            return this.mapper.getTypeFactory();
        }

        public ObjectMapper getObjectMapper() {
            return this.mapper;
        }

        public <T> T readValue(String content, Class<T> valueType) {
            try {
                return this.mapper.readValue(content, valueType);
            } catch (IOException e) {
                throw new RuntimeException("Jackson 出异常了：", e);
            }
        }

        public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
            try {
                return this.mapper.readValue(content, valueTypeRef);
            } catch (IOException e) {
                throw new RuntimeException("Jackson 出异常了：", e);
            }
        }

        public <T> T readValue(String content, JavaType valueType) {
            try {
                return this.mapper.readValue(content, valueType);
            } catch (IOException e) {
                throw new RuntimeException("Jackson 出异常了：", e);
            }
        }

        public void writeValue(Writer w, Object value) {
            try {
                this.mapper.writeValue(w, value);
            } catch (IOException e) {
                throw new RuntimeException("Jackson 出异常了：", e);
            }
        }

        public String writeValueAsString(Object value) {
            try {
                return this.mapper.writeValueAsString(value);
            } catch (IOException e) {
                throw new RuntimeException("Jackson 出异常了：", e);
            }
        }

        public Map<String, Object> convertToMap(Object value) {
            return this.mapper.convertValue(value, Map.class);
        }
    }
}
