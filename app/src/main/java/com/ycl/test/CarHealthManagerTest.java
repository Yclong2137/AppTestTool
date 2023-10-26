package com.ycl.test;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.annotaitons.Mock;
import ltd.qisi.test.annotaitons.MockMethod;

@Mock(moduleName = "车辆健康")
public class CarHealthManagerTest implements FunctionModuleInterface {

    @MockMethod(desc = "testA")
    public void testA() {

    }


    @MockMethod(desc = "a")
    public void a() {

    }

    @MockMethod(desc = "c")
    public void c() {

    }


    @MockMethod(desc = "b")
    public void b() {

    }
}
