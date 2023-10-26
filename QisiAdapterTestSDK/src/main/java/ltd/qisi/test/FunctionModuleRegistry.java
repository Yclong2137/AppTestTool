package ltd.qisi.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ltd.qisi.test.annotaitons.Mock;
import ltd.qisi.test.bean.ModuleInfo;

/**
 * 功能模块注册中心
 */
public final class FunctionModuleRegistry {


    /**
     * 存储自动扫描的功能接口
     */
    private static final Set<Class<?>> FUNCTION_MODULE_INTERFACES = new LinkedHashSet<>();


    /**
     * 扫描插件自动调用
     *
     * @param moduleClass 模块类
     */
    static void register(Class<?> moduleClass) {
        FUNCTION_MODULE_INTERFACES.add(moduleClass);
    }


    /**
     * 返回自动扫描功能信息
     *
     * @return 功能信息
     */
    public static List<ModuleInfo> getModuleInfoList() {
        List<ModuleInfo> list = new ArrayList<>();
        for (Class<?> clazz : FUNCTION_MODULE_INTERFACES) {
            String name = clazz.getSimpleName();
            if (clazz.isAnnotationPresent(Mock.class)) {
                Mock mock = clazz.getAnnotation(Mock.class);
                if (mock != null) {
                    name = mock.moduleName();
                }
            }
            list.add(new ModuleInfo(clazz, name));
        }
        return list;
    }

}
