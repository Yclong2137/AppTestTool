package ltd.qisi.test.items;

import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ltd.qisi.test.Utils;
import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.bean.ParameterInfo;

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
            .registerTypeAdapterFactory(NullListToEmptyFactory.INSTANCE)
            .create();


    private final Class<?> parameterType;

    static class NullListToEmptyFactory implements TypeAdapterFactory {

        public static final NullListToEmptyFactory INSTANCE = new NullListToEmptyFactory();

        private NullListToEmptyFactory() {

        }


        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            final Class<? super T> rawType = typeToken.getRawType();
            Type type = typeToken.getType();
            System.out.println("=====>>>>>>, type = " + type + ", rawType = " + rawType);
            final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, typeToken);
            if (List.class.isAssignableFrom(rawType)) {
                return new TypeAdapter<T>() {
                    @Override
                    public void write(JsonWriter out, T value) throws IOException {
                        if (value == null || ((List<?>) value).isEmpty()) {
                            out.beginArray().endArray();
                        } else {
                            delegateAdapter.write(out, value);
                        }
                    }

                    @Override
                    public T read(JsonReader in) throws IOException {
                        return delegateAdapter.read(in);
                    }
                };
            } else if (type instanceof GenericArrayType || type instanceof Class && ((Class<?>) type).isArray()) {
                return new TypeAdapter<T>() {
                    @Override
                    public void write(JsonWriter out, T value) throws IOException {
                        if (value == null) {
                            out.beginArray().endArray();
                        } else {
                            delegateAdapter.write(out, value);
                        }
                    }

                    @Override
                    public T read(JsonReader in) throws IOException {
                        return delegateAdapter.read(in);
                    }
                };
            } else if (rawType == String.class) {
                return new TypeAdapter<T>() {
                    @Override
                    public void write(JsonWriter out, T value) throws IOException {
                        if (value == null) {
                            out.value("");
                        } else {
                            delegateAdapter.write(out, value);
                        }
                    }

                    @Override
                    public T read(JsonReader in) throws IOException {
                        return delegateAdapter.read(in);
                    }
                };
            }
            return null;
        }
    }

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
    public Object defaultValue() {
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
