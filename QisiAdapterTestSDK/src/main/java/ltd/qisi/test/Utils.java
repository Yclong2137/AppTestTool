package ltd.qisi.test;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ltd.qisi.test.model.MethodSpec;

/**
 * @author Yclong
 */
public final class Utils {

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    private Utils() {

    }

    /**
     * 适配屏幕方案
     *
     * @param activity
     * @param application
     */
    public static void setCustomDensity(Activity activity, final Application application) {
        //通过资源文件getResources类获取DisplayMetrics
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            //保存之前density值
            sNoncompatDensity = appDisplayMetrics.density;
            //保存之前scaledDensity值，scaledDensity为字体的缩放因子，正常情况下和density相等，但是调节系统字体大小后会改变这个值
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            //监听设备系统字体切换
            application.registerComponentCallbacks(new ComponentCallbacks() {

                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        //调节系统字体大小后改变的值
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                public void onLowMemory() {

                }
            });
        }

        float targetDensity = appDisplayMetrics.widthPixels / 1080f;
        //通过计算之前scaledDensity和density的比获得scaledDensity值
        float targetScaleDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        int targetDensityDpi = (int) (160 * targetDensity);
        //设置系统density值
        appDisplayMetrics.density = targetDensity;
        //设置系统scaledDensity值
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        //设置系统densityDpi值
        appDisplayMetrics.densityDpi = targetDensityDpi;

        //获取当前activity的DisplayMetrics
        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        //设置当前activity的density值
        activityDisplayMetrics.density = targetDensity;
        //设置当前activity的scaledDensity值
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        //设置当前activity的densityDpi值
        activityDisplayMetrics.densityDpi = targetDensityDpi;
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

    /**
     * 获取方法配置集
     *
     * @param caller
     * @return
     */
    public static List<MethodSpec> getMethodSpecs(Object caller) {
        Class<?> clazz = caller.getClass();
        Method[] methods;
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            //排除Object类中方法
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            //排除FunctionModuleInterface中定义的方法
            if (method.getDeclaringClass() == FunctionModuleInterface.class) {
                continue;
            }
            //排除静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            result.add(method);
        }
        methods = result.toArray(new Method[0]);

        List<MethodSpec> methodSpecs = new ArrayList<>();

        List<String> sortedMethodNames = null;
        if (caller instanceof FunctionModuleInterface) {
            sortedMethodNames = ((FunctionModuleInterface) caller).sortedMethodNames;
            sortedMethod(sortedMethodNames, methods);
        }
        for (Method method : methods) {
            methodSpecs.add(MethodSpec.create(method));
        }
        return methodSpecs;
    }

    /**
     * 排序方法
     *
     * @param methodNames 有序方法名称集合
     * @param methods     无需方法集合
     */
    private static void sortedMethod(List<String> methodNames, Method[] methods) {
        if (methodNames == null || methods == null) return;
        //已排序区间|待排序区间
        int sortedIndex = 0;//排序索引
        for (int i = 0; i < methodNames.size(); i++) {
            int j = findMethod(methodNames.get(i), sortedIndex, methods);
            if (j != -1) {
                swap(sortedIndex, j, methods);
                sortedIndex += 1;
            }
        }
    }

    private static int findMethod(String name, int startIndex, Method[] methods) {
        for (int i = startIndex; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static void swap(int i, int j, Method[] methods) {
        Method tmp = methods[i];
        methods[i] = methods[j];
        methods[j] = tmp;
    }


}
