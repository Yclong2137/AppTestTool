package ltd.qisi.test;


import ltd.qisi.test.items.BooleanTypeItem;
import ltd.qisi.test.items.ByteTypeItem;
import ltd.qisi.test.items.DoubleTypeItem;
import ltd.qisi.test.items.FloatTypeItem;
import ltd.qisi.test.items.IntTypeItem;
import ltd.qisi.test.items.LongTypeItem;
import ltd.qisi.test.items.ParameterTypeItem;
import ltd.qisi.test.items.ShortTypeItem;
import ltd.qisi.test.items.StringTypeItem;
import ltd.qisi.test.bean.ParameterInfo;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数类型注册表
 *
 * @author Yclong
 */
final class ParameterTypeItemRegistry {

    private static final String TAG = "ParameterTypeItemRegistry";

    //注册表
    private static final Map<Class<?>, Class<? extends ParameterTypeItem<?>>> registry = new HashMap<>();

    //注册参数类型
    static {
        //基本数据类型
        registerParameterType(int.class, IntTypeItem.class);
        registerParameterType(double.class, DoubleTypeItem.class);
        registerParameterType(float.class, FloatTypeItem.class);
        registerParameterType(short.class, ShortTypeItem.class);
        registerParameterType(long.class, LongTypeItem.class);
        registerParameterType(boolean.class, BooleanTypeItem.class);
        registerParameterType(byte.class, ByteTypeItem.class);
        //引用数据类型
        registerParameterType(Integer.class, IntTypeItem.class);
        registerParameterType(Double.class, DoubleTypeItem.class);
        registerParameterType(Float.class, FloatTypeItem.class);
        registerParameterType(Short.class, ShortTypeItem.class);
        registerParameterType(Long.class, LongTypeItem.class);
        registerParameterType(String.class, StringTypeItem.class);
        registerParameterType(Boolean.class, BooleanTypeItem.class);
        registerParameterType(Byte.class, ByteTypeItem.class);
    }

    /**
     * 注册参数类型
     *
     * @param type  参数类型
     * @param typeItemClazz 参数映射类型
     */
    public static void registerParameterType(Class<?> type, Class<? extends ParameterTypeItem<?>> typeItemClazz) {
        registry.put(type, typeItemClazz);
    }

    /**
     * 反注册
     *
     * @param type 参数类型
     */
    public static void unregisterParameterType(Class<?> type) {
        registry.remove(type);
    }

    /**
     * 获取参数映射类型
     *
     * @param type 参数类型
     * @return 参数映射类型
     */
    public static ParameterTypeItem<?> getTypeItem(Class<?> type, ParameterInfo parameterInfo) {
        Class<? extends ParameterTypeItem<?>> typeItemClazz = registry.get(type);
        if (typeItemClazz != null) {
            ParameterTypeItem<?> typeItem = null;
            try {
                Constructor<? extends ParameterTypeItem<?>> constructor = typeItemClazz.getConstructor(ParameterInfo.class);
                constructor.setAccessible(true);
                typeItem = constructor.newInstance(parameterInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return typeItem;
        }
        return null;
    }


}
