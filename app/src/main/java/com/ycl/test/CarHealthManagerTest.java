package com.ycl.test;

import com.ycl.test.beans.User;

import java.util.List;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.annotaitons.Mock;
import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.annotaitons.MockMethod;

@Mock(moduleName = "车辆健康")
public class CarHealthManagerTest implements FunctionModuleInterface {


    private ICarHealthStateChangeLisener mICarHealthStateChangeLisener;


    @MockMethod(desc = "testBody")
    public void testBody(@MockBody User user) {
    }

    @MockMethod(desc = "testBody")
    public void testList(@MockBody(rawType = List.class, type = {User.class}) List<User> users) {
    }

    public void testIntArray(@MockBody int[] a, Integer[] b, User[] c) {

    }


    public void testInt(int a, Integer b, Double c, float d, short f) {

    }


}
