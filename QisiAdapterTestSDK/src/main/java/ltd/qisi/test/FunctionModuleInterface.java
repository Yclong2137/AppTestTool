package ltd.qisi.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能模块接口
 */
public interface FunctionModuleInterface {

    List<String> sortedMethodNames = new ArrayList<>();

    Map<Class<?>, Object> sInterfaceCache = new HashMap<>();


    default void putInterface(Class<?> clazz, Object instance) {
        sInterfaceCache.put(clazz, instance);
    }


    default Object getInterface(Class<?> clazz) {
        return sInterfaceCache.get(clazz);
    }

}
