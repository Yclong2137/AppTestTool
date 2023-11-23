package ltd.qisi.test.model;


import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.annotaitons.MockField;
import ltd.qisi.test.annotaitons.MockMethod;
import ltd.qisi.test.MockClient;
import ltd.qisi.test.items.EnumTypeItem;
import ltd.qisi.test.items.ParameterTypeItem;
import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.MethodInvokeInfo;
import ltd.qisi.test.bean.ParameterInfo;
import ltd.qisi.test.event.MethodSpecChangeEvent;
import ltd.qisi.test.items.BodyTypeItem;
import ltd.qisi.test.items.InterfaceTypeItem;

/**
 * @author Yclong
 */
public class MethodSpec {


    public static MethodSpec create(Object caller, Method method) {
        return new MethodSpec(caller, method);
    }

    /**
     * 参数
     */
    private final Object[] args;
    /**
     * 调用者
     */
    public Object caller;
    /**
     * 方法
     */
    public Method method;
    /**
     * 参数映射类型
     */
    private final ParameterTypeItem<?>[] typeItems;

    /**
     * 参数类型
     */
    private final Class<?>[] parameterTypes;
    /**
     * 注解
     */
    private final MockMethod mockMethod;

    private final Deprecated deprecatedAnnotation;


    private final Class<?> returnType;

    public final SpannableStringBuilder reqText = new SpannableStringBuilder();

    public final SpannableStringBuilder respText = new SpannableStringBuilder();

    /**
     * 是否展开
     */
    private boolean isExpand = true;

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 搜索关键字
     */
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    /**
     * 执行结果
     */
    private boolean isPass;


    public boolean isPass() {
        return isPass;
    }

    /**
     * 方法是否执行过
     */
    private boolean isExecuted;


    public boolean isExecuted() {
        return isExecuted;
    }

    public Object[] getArgs() {
        return args;
    }

    private MethodSpec(Object caller, Method method) {
        this.caller = caller;
        this.method = method;
        keyword += method.getName();
        this.returnType = method.getReturnType();
        mockMethod = Utils.findMethodAnnotation(method, MockMethod.class);
        deprecatedAnnotation = Utils.findMethodAnnotation(method, Deprecated.class);
        if (mockMethod != null) {
            keyword += mockMethod.desc();
        }
        parameterTypes = method.getParameterTypes();
        args = new Object[parameterTypes.length];
        typeItems = new ParameterTypeItem[parameterTypes.length];
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            ParameterInfo parameterInfo = new ParameterInfo();
            Class<?> parameterType = parameterTypes[i];
            parameterInfo.parameterType = parameterType;
            Annotation[] parameterAnnotationArr = parameterAnnotations[i];
            parameterInfo.mockField = findMockAnnotation(parameterAnnotationArr, MockField.class);
            parameterInfo.mockBody = findMockAnnotation(parameterAnnotationArr, MockBody.class);
            try {
                //增加对数组类型处理
                if (parameterInfo.mockBody != null || parameterType.isArray()) {
                    BodyTypeItem bodyTypeItem = new BodyTypeItem(parameterInfo);
                    typeItems[i] = bodyTypeItem;
                } else if (parameterType.isInterface()) {//处理接口类型参数
                    InterfaceTypeItem typeItem = new InterfaceTypeItem(parameterInfo, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getDeclaringClass() == Object.class) {
                                //重写equals方法，保证返回为true（由于没有被代理对象，无法实现自定义equals方法）
                                if ("equals".equals(method.getName())) {
                                    return Boolean.TRUE;
                                }
                                return method.invoke(this, args);
                            }
                            EventBus.getDefault().post(MethodInvokeInfo.create(method, args));
                            return null;
                        }
                    });
                    typeItems[i] = typeItem;
                } else if (parameterType.isEnum()) {
                    typeItems[i] = new EnumTypeItem(parameterInfo);
                } else {
                    typeItems[i] = MockClient.getTypeItem(parameterType, parameterInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
                respText.clear();
                respText.append("失败 ").append(MockClient.getTextFormatter().format(e));
                respText.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

    }


    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * 查找注解
     *
     * @param annotations 注解
     * @return 目标注解
     */
    private <T extends Annotation> T findMockAnnotation(Annotation[] annotations, Class<T> annotationClazz) {
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == annotationClazz) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }


    public MockMethod getMockMethod() {
        return mockMethod;
    }


    public Deprecated getDeprecatedAnnotation() {
        return deprecatedAnnotation;
    }

    /**
     * 参数类型
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * 获取映射参数类型
     */
    public ParameterTypeItem<?>[] getTypeItems() throws Exception {
        return typeItems;
    }

    /**
     * 校验参数结果
     */
    private boolean verifyParameter(boolean notify) {
        StringBuilder builder = new StringBuilder();
        //检查参数是否匹配
        boolean healthy = true;
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            ParameterTypeItem<?> typeItem = typeItems[i];
            if (!parameterType.isInterface() && typeItem == null) {
                healthy = false;
                builder.append("parameter type(");
                builder.append(parameterType);
                builder.append(") is lost, please call registerParameterType()");
            }
        }
        if (healthy) {
            return true;
        }
        respText.clear();
        respText.append("失败 ").append(MockClient.getTextFormatter().format(builder.toString()));
        respText.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        if (notify) {
            EventBus.getDefault().post(new MethodSpecChangeEvent(this));
        }
        return false;
    }

    /**
     * 执行方法
     */
    public Object invoke(boolean notify) {
        if (method != null) {
            try {
                //校验参数
                if (!verifyParameter(notify)) {
                    isPass = false;
                    return null;
                }
                for (int i = 0; i < typeItems.length; i++) {
                    ParameterTypeItem<?> typeItem = typeItems[i];
                    Object arg = typeItem.getValue();
                    Object defVal;
                    if (arg == null && (defVal = typeItem.defaultValue()) != null) {
                        args[i] = defVal;
                    } else {
                        args[i] = arg;
                    }
                }
                reqText.clear();
                reqText.append(MockClient.getTextFormatter().format(args));
                EventBus.getDefault().post(new MethodSpecChangeEvent(this));
                Object resp = method.invoke(caller, args);
                respText.clear();
                respText.append("成功 ").append(String.valueOf(resp));
                respText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                EventBus.getDefault().post(new MethodSpecChangeEvent(this));
                isPass = true;
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
                respText.clear();
                respText.append("失败 ").append(MockClient.getTextFormatter().format(e));
                respText.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                EventBus.getDefault().post(new MethodSpecChangeEvent(this));
                isPass = false;
            } finally {
                isExecuted = true;
            }
        }
        return null;
    }


}
