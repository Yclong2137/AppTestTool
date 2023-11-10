package com.ycl.test;

import com.ycl.test.beans.User;

import java.util.List;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.annotaitons.Mock;
import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.annotaitons.MockMethod;

@Mock(moduleName = "车辆健康")
public class CarHealthManager1Test1 implements FunctionModuleInterface {


    private ICarHealthStateChangeLisener mICarHealthStateChangeLisener;

    @MockMethod(desc = "testA(CAN)")
    @Deprecated
    public void testA() {

    }


    @MockMethod(desc = "testArray(can)")
    public void testArray(@MockBody(rawType = List.class, type = {User.class}) List<User> a) {

    }

    @MockMethod(desc = "testBody")
    @Deprecated
    public void testBody(int a, @MockBody User user) {
        if (mICarHealthStateChangeLisener != null)
            mICarHealthStateChangeLisener.onTestAbs(a, user,a);
    }


    @MockMethod(desc = "b")
    public void b(boolean b) {

    }

    public void registerListener(ICarHealthStateChangeLisener lisener) {
        this.mICarHealthStateChangeLisener = lisener;
    }

    public void unregisterListener(ICarHealthStateChangeLisener lisener) {
        this.mICarHealthStateChangeLisener = null;
    }

}
