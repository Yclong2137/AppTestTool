package ltd.qisi.test.bean;

import java.io.Serializable;

/**
 * 功能模块信息
 */
public class ModuleInfo implements Serializable {

    private final Class<?> moduleClass;
    private final String moduleName;

    public ModuleInfo(Class<?> moduleClass, String moduleName) {
        this.moduleClass = moduleClass;
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return "ModuleInfo{" + "moduleClass=" + moduleClass + ", moduleName='" + moduleName + '\'' + '}';
    }

    public Class<?> getModuleClass() {
        return moduleClass;
    }

    public String getModuleName() {
        return moduleName;
    }
}
