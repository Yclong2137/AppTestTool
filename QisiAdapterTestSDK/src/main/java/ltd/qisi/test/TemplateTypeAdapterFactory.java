package ltd.qisi.test;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理Null
 */
public class TemplateTypeAdapterFactory implements TypeAdapterFactory {

    public static final TemplateTypeAdapterFactory INSTANCE = new TemplateTypeAdapterFactory();

    public static final ConstructorConstructor sConstructor = new ConstructorConstructor(Collections.emptyMap());

    private static final Set<TypeToken<?>> sRecursiveTypeTokens = new HashSet<>();

    private TemplateTypeAdapterFactory() {

    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        final Class<? super T> rawType = typeToken.getRawType();
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, typeToken);
        if (Number.class.isAssignableFrom(rawType) || rawType.isPrimitive()) {
            MockClient.printLog("rawType = " + rawType + ", Primitive类型或Number派生的子类无需处理");
            return null;
        }
        if (rawType.isArray()) {
            MockClient.printLog("rawType = " + rawType + ", Array类型");
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    MockClient.printLog("rawType = " + rawType + ", Array类型, value = " + value);
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
        }
        if (rawType == String.class) {
            MockClient.printLog("rawType = " + rawType + ", String类型");
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
        if (List.class.isAssignableFrom(rawType)) {
            MockClient.printLog("rawType = " + rawType + ", List以及派生类型");
            return new TypeAdapter<T>() {

                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null || ((List) value).isEmpty()) {
                        T instance = createTemplateObject(typeToken);
                        if (instance != null) {
                            delegateAdapter.write(out, instance);
                            return;
                        }
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
        }
        if (Map.class.isAssignableFrom(rawType)) {
            MockClient.printLog("rawType = " + rawType + ", Map以及派生类型");
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) {
                        out.beginObject().endObject();
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
        if (Object.class.isAssignableFrom(rawType)) {
            MockClient.printLog("rawType = " + rawType + ", MockBody类型");
            return new TypeAdapter<T>() {

                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) {
                        T instance = createTemplateObject(typeToken);
                        if (instance != null) {
                            delegateAdapter.write(out, instance);
                            return;
                        }
                        out.beginObject().endObject();

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
        MockClient.printLog("rawType = " + rawType + ", 无需适配类型");

        return null;
    }

    /**
     * 创建模版对象
     *
     * @param typeToken
     * @param <T>
     * @return
     */
    private static <T> T createTemplateObject(TypeToken<T> typeToken) {
        if (typeToken == null) {
            return null;
        }

        Class<? super T> rawType = typeToken.getRawType();
        Type type = typeToken.getType();
        //排除一些系统对象类
        if (rawType.isArray() || rawType.isPrimitive() || Number.class.isAssignableFrom(rawType)) {
            return null;
        }
        try {

            ObjectConstructor<T> instanceConstructor = sConstructor.get(typeToken);
            T instance = instanceConstructor.construct();
            if (instance == null) {
                return null;
            }
            if (List.class.isAssignableFrom(rawType)) {
                Class<?> actualTypeArgument = Utils.getActualTypeArgument(type);
                if (actualTypeArgument != null) {
                    Object templateObject = createTemplateObject(TypeToken.get(actualTypeArgument));
                    if (templateObject != null) {
                        ((List) instance).add(templateObject);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
