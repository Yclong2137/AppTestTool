package ltd.qisi.test.items;

import android.content.Context;
import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import ltd.qisi.test.ParameterTypeItem;
import ltd.qisi.test.bean.ParameterInfo;

/**
 * 接口参数类型
 *
 * @author Yclong
 */
public class InterfaceTypeItem extends ParameterTypeItem<Object> {

    /**
     * cache
     */
    private static final Map<Class<?>, Object> CACHES = new HashMap<>();

    private final InvocationHandler handler;

    private final Class<?> parameterType;

    public InterfaceTypeItem(ParameterInfo parameterInfo, InvocationHandler handler) {
        super(parameterInfo);
        this.parameterType = parameterInfo.parameterType;
        this.handler = handler;
    }


    @Override
    public Object getValue() throws Exception {
        Object value = null;
        if (parameterType != null) {
            value = CACHES.get(parameterType);
            if (value == null) {
                value = Proxy.newProxyInstance(parameterType.getClassLoader(), new Class[]{parameterType}, handler);
                CACHES.put(parameterType, value);
            }
        }
        return value;
    }


    @Override
    public View getView(Context context) {
        return null;
    }
}
