### QisiAdapterTestSDK 说明
------

#### 1.用途

* 适配层功能链路测试

* 模拟上层应用对适配层的功能调用



#### 2.原理

1、通过插件自动收集功能测试类注册，并对功能模块进行方法排序收集

2、反射获取目标类的方法和参数；

3、将方法集合UI化，并针对每一个方法对其参数列表进行UI化，

4、点击测试按钮收集参数信息进行回填后反射调用目标方法



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

    /**
     * 方法描述
     */
    String desc();

    /**
     * 已通过扫描插件自动实现
     */
    @Deprecated int order() default Integer.MAX_VALUE;

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
	/**
     * 原始类型
     */
    Class<?> rawType() default Void.class;

    /**
     * 参数类型
     */
    Class<?>[] type() default {};

    /**
     * 模版（json格式）
     */
    String template() default "";
}
```





##### 2、参数抽象化

```java
/**
 * 参数类型化
 * @author Yclong
 */
public abstract class ParameterTypeItem<T> {

	/**
     * 参数信息
     */
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
    public LinearLayout.LayoutParams getLayoutParams() {
        return null;
    }

    /**
     * 输入结果值
     */
    public abstract T getValue() throws Exception;

    /**
     * 默认值
     */
    protected T defaultValue() {
        return null;
    }

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
        inputView.clearFocus();
        hookView(inputView);
        try {
            Object value;
            if ((value = parameterInfo.value) != null || (value = defaultValue()) != null) {
                inputView.setText(String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputView;
    }

    protected void hookView(EditText inputView) {

    }

    /**
     * 输入文本
     */
    protected String getInputText() {
        String value = inputView.getText().toString();
        parameterInfo.value = value;
        return value;
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
        List<Entry> entries = fillEntries();
        List<String> items = new ArrayList<>();
        int defaultItemIndex = 0;
        T defaultValue = defaultValue();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            items.add(entry.name);
            if (Objects.equals(defaultValue, entry.data)) {
                defaultItemIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultItemIndex);
        hookView(spinner);
        return spinner;
    }


    /**
     * 填充数据
     */
    protected abstract List<Entry> fillEntries();


    protected void hookView(Spinner spinner) {

    }


    /**
     * 选中文本
     */
    protected String getSelectedText() {
        return spinner.getSelectedItem().toString();
    }

    public class Entry {

        public String name;

        public T data;

        public Entry(String name, T data) {
            this.name = name;
            this.data = data;
        }
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
    /**
     * 参数类型
     */
    private Type pType;

    private String template;

    public BodyTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
        this.parameterType = parameterInfo.parameterType;
        pType = TypeToken.get(parameterType).getType();
        MockBody mockBody = parameterInfo.mockBody;
        if (mockBody != null) {
            Class<?> rawType = mockBody.rawType();
            Class<?>[] types = mockBody.type();
            template = mockBody.template();
            if (parameterType == rawType) {
                pType = TypeToken.getParameterized(rawType, types).getType();
                if ((template = formatTemplate(template, pType)) == null && List.class.isAssignableFrom(parameterType) && types.length > 0) {
                    template = createListTemplate(types[0]);
                }
            } else {
                if ((template = formatTemplate(template, parameterType)) == null) {
                    template = createTemplate(parameterType);
                }
            }
        }
    }

    /**
     * 格式化模版
     *
     * @param template
     * @param type
     * @return
     */
    private String formatTemplate(String template, Type type) {
        if (template == null || template.isEmpty() || type == null) {
            return null;
        }
        try {
            Object o = gson.fromJson(template, type);
            return gson.toJson(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    @Override
    protected Object defaultValue() {
        return template;
    }

    @Override
    public Object getValue() throws Exception {
        if (pType != null) {
            return gson.fromJson(getInputText(), pType);
        }
        return gson.fromJson(getInputText(), parameterType);
    }

    private String createListTemplate(Class<?> type) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(createTemplate(type));
        sb.append("]");
        return sb.toString();
    }

    /**
     * 创建模版
     *
     * @param type
     * @return
     */
    private String createTemplate(Class<?> type) {
        if (type == null || type.isInterface() || Modifier.isAbstract(type.getModifiers()))
            return "";
        try {
            Constructor<?> constructor = type.getConstructor();
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

1、准备

```
//导入插件
classpath 'ltd.qisi.adapterproxy:app-test-plugin:0.0.5-SNAPSHOT'

//使用插件
apply plugin: 'ltd.qisi.test'

appTest {
	//功能模块注册中心 （一般不改动）
    injectClassName 'ltd/qisi/test/FunctionModuleRegistry'
    //功能模块接口 （一般不改动）
    scanInterface 'ltd/qisi/test/FunctionModuleInterface'//一般不改动
    //匹配正则 （需要处理的class文件）
    includePatterns 'ltd/qisi/adapterproxy/.*Test$', 'ltd/qisi/test/FunctionModuleRegistry'
}

//导入测试SDK
implementation 'ltd.qisi.adapterproxy:app-test-core:0.0.1-SNAPSHOT'

```

2、  实现功能模块测试类

```java
//功能模块测试类
public class CarHealthManagerTest extends CarHealthManager 
    //实现功能模块接口
    implements FunctionModuleInterface{
    //接口参数类型
    @MockMethod(desc = "注册监听器")
    public void registerCarChangeListener(ICarVehicleHealthStateChangeListener listener){
        super.registerCarChangeListener(listener);
    }
    //枚举参数类型
    @MockMethod(desc = "获取XXX状态")
    public int getXXXState(@MockField(name="座椅位置") 
                           SeatPosition pos){
        ...
    }
    //基本类型和基本类型的包装类
    @MockMethod(desc = "获取XXX状态")
    public int getXXXXState1(@MockField(name="a")
                             int a,
                             short b,
                             long c,
                             double d，
                             boolean e,
                             Boolean f){
        ...
    }
    //普通对象（结构体）参数类型
    @MockMethod(desc = "获取XXX状态")
    public int getXXXState2(@MockField(name="用户")
                            @MockBody
                            User user){
        ...
    }
    
    //集合参数化类型
    @MockMethod(desc = "获取XXX状态")
    public int getXXXState3(@MockField(name="多用户") 
                            @MockBody(rawType = List.class,type={User.class})
                            List<User> userList){
        ...
    }
    
    ...
    
}
```



3、注册对应参数类型（**已实现常用基本类型、包装类型、接口类型和使用`@MockBody` 标记的结构体**）

```java
MockClient.registerParameterTypeItem(SeatPosition.class, SeatPositionTypeItem.class);
```







