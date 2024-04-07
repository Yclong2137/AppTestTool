package ltd.qisi.test.items;

import android.content.Context;
import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.bean.ParameterInfo;

/**
 * 接口参数类型
 *
 * @author Yclong
 */
public class InterfaceTypeItem extends ParameterTypeItem<Object> {


    private final InvokeCallback handler;

    private final Class<?> parameterType;

    private FunctionModuleInterface moduleInterface;

    public void setModuleInterface(FunctionModuleInterface moduleInterface) {
        this.moduleInterface = moduleInterface;
    }

    public InterfaceTypeItem(ParameterInfo parameterInfo, InvokeCallback handler) {
        super(parameterInfo);
        this.parameterType = parameterInfo.parameterType;
        this.handler = handler;
    }


    @Override
    public Object getValue() throws Exception {
        Object value;
        if (moduleInterface != null) {
            value = moduleInterface.getInterface(parameterType);
            if (value == null) {
                value = newProxyInstance(parameterType);
                moduleInterface.putInterface(parameterType, value);
            }
            return value;
        }
        return newProxyInstance(parameterType);
    }


    private Object newProxyInstance(Class<?> parameterType) {
        return Proxy.newProxyInstance(parameterType.getClassLoader(), new Class[]{parameterType}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    // FIXME: 2024/3/27 兼容动态代理与容器
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if ("toString".equals(method.getName())) {
                        return parameterType.getSimpleName() + proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    return method.invoke(this, args);
                }
                if (handler != null) handler.invoke(proxy, method, args);
                return null;
            }

        });

    }


    @Override
    protected View getView(Context context) {
        return null;
    }


    public interface InvokeCallback {

        void invoke(Object proxy, Method method, Object[] args);

    }

}
