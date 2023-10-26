package ltd.qisi.test.bean

class ScanResult {

    //扫描的文件
    String filePath
    //扫描的类名
    String className
    //是否实现功能接口
    boolean hasInterface = false

    List<String> sortedMethodNames = new ArrayList<>()


}