package ltd.qisi.test

class AppTestExtension {

    //注入的类
    String injectClassName
    //扫描接口
    String scanInterface
    //匹配正则
    String[] includePatterns
    //日志开关
    boolean logEnabled = false
}