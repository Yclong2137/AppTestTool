package com.ycl.test;

import com.ycl.test.beans.User;

import java.util.List;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.annotaitons.Mock;
import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.annotaitons.MockMethod;

@Mock(moduleName = "车辆健康")
public class CarHealthManagerTest implements FunctionModuleInterface {

    @MockMethod(desc = "testA")
    public void testA(int a, String b) {

    }


    @MockMethod(desc = "testArray")
    public void testArray(@MockBody(rawType = List.class, type = {User.class}) List<User> a) {

    }

    @MockMethod(desc = "testBody")
    public void testBody(int a,@MockBody User user) {

    }


    @MockMethod(desc = "b")
    public void b(boolean b) {

    }
}
