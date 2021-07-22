package com.xwbing.service.enums.base;

/**
 * @author daofeng
 * @version $
 * @since 2019年11月29日 19:21
 */
public class BaseEnumUtil {
    public static <E extends Enum<?> & BaseEnum> E ofCode(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<?> & BaseEnum> E ofName(Class<E> enumClass, String name) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}