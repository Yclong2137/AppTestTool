### QisiAdapterTestSDK 说明
------

#### 1.用途

* 适配层功能链路测试

* 模拟上层应用对适配层的功能调用



#### 2.原理

1.通过反射获取目标类的方法和参数；

2.将方法集合UI化，并针对每一个方法对其参数列表进行UI化，

3.点击测试按钮收集参数信息进行回填后反射调用目标方法



#### 3.关键类解析

##### 1、注解

```java
/**
 * 用于模块类
 */
public @interface Mock {
    String moduleName();
}

```



```java
/**
 * 用于方法
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MockMethod {

    String desc();

}
```

```java
/**
 * 用于参数
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MockField {
    /**
     * 参数名称
     */
    String name();
    /**
     * 备注
     */
    String remarks() default "";

}
```

##### 

```java
/**
 * 用于结构体参数
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MockBody {

}
```

##### 2、方法抽象化

```java
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

    private Callback callback;
    /**
     * 参数类型
     */
    private final Class<?>[] parameterTypes;
    /**
     * 注解
     */
    private final MockMethod mockMethod;


    private MethodSpec(Object caller, Method method) {
        this.caller = caller;
        this.method = method;
        mockMethod = findMockAnnotation(method.getAnnotations(), MockMethod.class);
        parameterTypes = method.getParameterTypes();
        args = new Object[parameterTypes.length];
        typeItems = new ParameterTypeItem[parameterTypes.length];
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            ParameterInfo parameterInfo = new ParameterInfo();
            Class<?> parameterType = parameterTypes[i];
            parameterInfo.parameterType = parameterType;
            Annotation[] parameterAnnotationArr = parameterAnnotations[i];
            System.out.println(method.getName() + " ,参数类型：" + parameterType + " ,注解：" + Arrays.toString(parameterAnnotationArr));
            parameterInfo.mockField = findMockAnnotation(parameterAnnotationArr, MockField.class);
            parameterInfo.mockBody = findMockAnnotation(parameterAnnotationArr, MockBody.class);
            try {
                if (parameterType.isInterface()) {//处理接口类型参数
                    Object proxy = Proxy.newProxyInstance(parameterType.getClassLoader(), new Class[]{parameterType}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getDeclaringClass() == Object.class) {
                                return method.invoke(this, args);
                            }
                            EventBus.getDefault().post(MethodCallbackInfo.create(method, args));
                            return null;
                        }
                    });
                    InterfaceTypeItem typeItem = new InterfaceTypeItem(proxy, parameterInfo);
                    typeItems[i] = typeItem;
                } else if (parameterInfo.mockBody != null && Utils.isInstance(parameterType)) {
                    BodyTypeItem bodyTypeItem = new BodyTypeItem(parameterInfo);
                    typeItems[i] = bodyTypeItem;
                } else {
                    typeItems[i] = MockClient.getTypeItem(parameterType, parameterInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onFailure(MockClient.getTextFormatter().format(e));
            }
        }

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


    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public MockMethod getMockMethod() {
        return mockMethod;
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
            return typeItems;
        }
        if (callback != null)
            callback.onFailure(MockClient.getTextFormatter().format(builder.toString()));
        throw new IllegalAccessException(builder.toString());
    }

    /**
     * 执行方法
     */
    public Object invoke() {
        if (method != null) {
            try {
                for (int i = 0; i < typeItems.length; i++) {
                    ParameterTypeItem<?> typeItem = typeItems[i];
                    args[i] = typeItem.getValue();
                }
                if (callback != null)
                    callback.onReqChanged(MockClient.getTextFormatter().format(args));
                Object resp = method.invoke(caller, args);
                if (callback != null)
                    callback.onRespChanged(MockClient.getTextFormatter().format(resp));
                return resp;
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onFailure(MockClient.getTextFormatter().format(e));
            }
        }
        return null;
    }


    public interface Callback {

        void onReqChanged(String req);

        void onRespChanged(String resp);

        void onFailure(String msg);

    }

}

```



##### 3、参数抽象化

```java
/**
 * 参数类型化
 * @author Yclong
 */
public abstract class ParameterTypeItem<T> {


    protected ParameterInfo parameterInfo;

    public ParameterTypeItem(ParameterInfo parameterInfo) {
        this.parameterInfo = parameterInfo;
    }

    /**
     * 视图
     *
     * @param context 上下文
     */
    public abstract View getView(Context context);

    /**
     * 自定义View尺寸
     */
    protected LinearLayout.LayoutParams getLayoutParams() {
        return null;
    }

    /**
     * 输入结果值
     */
    public abstract T getValue() throws Exception;


    /**
     * 参数信息
     */
    public ParameterInfo getParameterInfo() {
        return parameterInfo;
    }
}

```

###### 1.输入型参数

```java
/**
 * 输入类型
 *
 * @author Yclong
 */
public abstract class InputTypeItem<T> extends ParameterTypeItem<T> {

    private EditText inputView;

    public InputTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }


    @Override
    public View getView(Context context) {
        inputView = new EditText(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.GRAY);
        background.setCornerRadius(6);
        inputView.setBackground(background);
        inputView.setPadding(Utils.dp2px(12), Utils.dp2px(2), Utils.dp2px(12), Utils.dp2px(2));
        inputView.setHint("请输入");
        inputView.setTextSize(18);
        hookView(inputView);
        return inputView;
    }

    protected void hookView(EditText inputView) {

    }

    /**
     * 输入文本
     */
    protected String getInputText() {
        return inputView.getText().toString();
    }
}
```

###### 2.选择型参数

```java
/**
 * 选择类型参数
 *
 * @author Yclong
 */
public abstract class SelectionTypeItem<T> extends ParameterTypeItem<T> {

    private Spinner spinner;


    public SelectionTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }


    @Override
    public View getView(Context context) {
        spinner = new Spinner(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.GRAY);
        background.setCornerRadius(6);
        spinner.setBackground(background);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, fillData());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        hookView(spinner);
        return spinner;
    }

    /**
     * 填充数据
     */
    protected abstract List<String> fillData();

    protected void hookView(Spinner spinner) {

    }


    /**
     * 选中文本
     */
    protected String getSelectedText() {
        return spinner.getSelectedItem().toString();
    }
}
```

###### 3.接口类型参数

```java
/**
 * 接口参数类型
 *
 * @author Yclong
 */
public class InterfaceTypeItem extends ParameterTypeItem<Object> {

    /**
     * 接口代理对象
     */
    private final Object proxy;

    public InterfaceTypeItem(Object proxy, ParameterInfo parameterInfo) {
        super(parameterInfo);
        this.proxy = proxy;
    }

    @Override
    public Object getValue() throws Exception {
        return proxy;
    }

    @Override
    public View getView(Context context) {
        return null;
    }
}
```

###### 4.结构体类型参数

```java
/**
 * 结构体参数类型
 *
 * @author Yclong
 */
public class BodyTypeItem extends InputTypeItem<Object> {

    /**
     * 对象嵌套深度
     */
    private static final int max_depth = 4;
    /**
     * 当前深度
     */
    private int depth;

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();


    private final Class<?> parameterType;

    public BodyTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
        this.parameterType = parameterInfo.parameterType;
    }

    @Override
    protected LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setText(createTemplate(parameterType));
    }

    @Override
    public Object getValue() throws Exception {
        return gson.fromJson(getInputText(), parameterType);
    }

    /**
     * 创建模版
     *
     * @param clazz
     * @return
     */
    private String createTemplate(Class<?> clazz) {
        if (clazz == null) return "";
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            Object obj = constructor.newInstance();
            initDefaultValue(obj);
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 初始化对象默认值
     *
     * @param obj 对象
     */
    private void initDefaultValue(Object obj) {
        try {
            if (depth > max_depth) return;
            List<Field> fields = Utils.getClassSelfFields(obj.getClass());
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                if (List.class.isAssignableFrom(fieldType)) {
                    System.out.println("这是List的子类");
                    field.set(obj, new ArrayList<>());
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    System.out.println("这是Map的子类");
                    field.set(obj, new HashMap<>());
                } else if (!fieldType.isInterface() && !Utils.isCommonType(fieldType)) {//对象类型
                    try {
                        Constructor<?> fieldTypeConstructor = fieldType.getConstructor();
                        fieldTypeConstructor.setAccessible(true);
                        Object o = fieldTypeConstructor.newInstance();
                        field.set(obj, o);
                        depth++;
                        initDefaultValue(o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

##### 4、使用

1、导入QisiAdapterTestSDK，app-test-plugin插件（自动扫描功能类以及实现方法排序）

2、  **功能类实现FunctionModuleInterface接口**并在功能类中使用 `@Mock` `@MockMethod`、`@MockField`、`@MockBody` 等注解进行标注（**可选**）

3、注册对应参数类型（**已实现常用基本类型、包装类型、接口类型和使用`@MockBody` 标记的结构体**）

```java
MockClient.registerParameterTypeItem(SeatPosition.class, SeatPositionTypeItem.class);
```







