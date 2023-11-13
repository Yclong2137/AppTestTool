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
     * 是否为常用类型
     *
     * @param clazz 类型
     */
    public static boolean isCommonType(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isAnnotation()) {
            return true;
        }
        if (clazz.isEnum()) {
            return true;
        }
        if (clazz.isPrimitive()) {
            return true;
        }
        if (clazz == String.class) {
            return true;
        }
        if (clazz == Boolean.class) {
            return true;
        }
        if (clazz == Character.class) {
            return true;
        }
        if (clazz == Byte.class) {
            return true;
        }
        if (clazz == Short.class) {
            return true;
        }
        if (clazz == Integer.class) {
            return true;
        }
        if (clazz == Long.class) {
            return true;
        }
        if (clazz == Float.class) {
            return true;
        }
        if (clazz == Double.class) {
            return true;
        }
        if (clazz == Void.class) {
            return true;
        }
        if (clazz.isArray()) {
            return true;
        }

        return false;
    }

    /**
     * 是否为对象类型
     *
     * @param clazz
     * @return
     */
    public static boolean isInstance(Class<?> clazz) {
        if (clazz == null) return false;
        int mod = clazz.getModifiers();
        if (Modifier.isInterface(mod)) {
            return false;
        }
        if (Modifier.isAbstract(mod)) {
            return false;
        }
        return !isCommonType(clazz);
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
        }
        pw.flush();
        return sw.toString();
    }


    /**
     * 格式化类的属性类型
     *
     * @param clazz
     * @return
     */
    public static String formatClassFieldTypes(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(clazz.getName());
        builder.append("]");
        List<Field> fields = getClassSelfFields(clazz);
        for (Field field : fields) {
            builder.append(field.getType());
            builder.append(",");
        }
        return builder.toString();
    }

    /**
     * 获取类本身的私有成员变量
     *
     * @param clazz
     * @return
     */
    public static List<Field> getClassSelfFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getDeclaringClass() == Object.class) continue;
                fieldList.add(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldList;
    }


    public static Class<?> getGenericTypeClass(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
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
        System.out.println("findMethodAnnotation() called with: bridgeMethod = [" + bridgeMethod + "], annotationType = [" + annotationType + "],has " + has);
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
