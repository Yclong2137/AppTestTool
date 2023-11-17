package ltd.qisi.test;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.content.res.Resources;
import android.util.TypedValue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yclong
 */
public final class Utils {

    private Utils() {

    }

    public static int dp2px(int value) {
        return (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());

    }


    /**
     * 异常信息
     *
     * @param t
     * @return
     */
    public static String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, false);
        Throwable tCause = t.getCause();
        if (tCause != null) {
            tCause.printStackTrace(pw);
        } else {
            t.printStackTrace(pw);
        }
        pw.flush();
        return sw.toString();
    }


    public static Class<?> getActualTypeArgument(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            if (types.length > 0) {
                return (Class<?>) types[0];
            }
        }
        return null;
    }

    /**
     * 查找目标注解
     *
     * @param bridgeMethod   方法
     * @param annotationType 注解
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T findMethodAnnotation(Method bridgeMethod, Class<T> annotationType) {
        boolean has = bridgeMethod.isAnnotationPresent(annotationType);
        MockClient.printLog("findMethodAnnotation() called with: bridgeMethod = [" + bridgeMethod + "], annotationType = [" + annotationType + "],has " + has);
        if (!has) {
            Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
            while (superclass != null && Object.class != superclass) {
                Method method = searchForMatch(superclass, bridgeMethod);
                if (method != null && method.isAnnotationPresent(annotationType)) {
                    return method.getAnnotation(annotationType);
                }
                superclass = superclass.getSuperclass();
            }
        } else {
            return bridgeMethod.getAnnotation(annotationType);
        }
        return null;
    }

    private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
        try {
            return type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }


}
