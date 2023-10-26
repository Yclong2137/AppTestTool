package ltd.qisi.test.bean;

import java.lang.reflect.Method;

/**
 * @author Yclong
 */
public class MethodInvokeInfo {


    public static MethodInvokeInfo create(Method method, Object[] args) {
        return new MethodInvokeInfo(method, args);
    }

    public Method method;

    public Object[] args;

    private MethodInvokeInfo(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }
}
