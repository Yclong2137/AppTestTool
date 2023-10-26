package ltd.qisi.test

import ltd.qisi.test.bean.ScanResult

class AppTestConfig {


    //注入代码类的扫描结果
    ScanResult injectScanResult
    //扫描结果
    List<ScanResult> scanResults = new ArrayList<>()

    AppTestExtension extension


}