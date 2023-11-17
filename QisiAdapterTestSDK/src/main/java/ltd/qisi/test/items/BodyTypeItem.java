package ltd.qisi.test.items;

import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ltd.qisi.test.TemplateTypeAdapterFactory;
import ltd.qisi.test.annotaitons.MockBody;
import ltd.qisi.test.bean.ParameterInfo;

/**
 * 结构体参数类型
 *
 * @author Yclong
 */
public class BodyTypeItem extends InputTypeItem<Object> {


    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(TemplateTypeAdapterFactory.INSTANCE)
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
                template = gson.toJson(null, pType);
                return;
            }
            template = gson.toJson(null, pType);
            return;
        }
        //处理数组类型
        template = gson.toJson(null, pType);
    }

    @Override
    public Object defaultValue() {
        return template;
    }


    @Override
    public LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    @Override
    public Object getValue() throws Exception {
        if (pType != null) {
            return gson.fromJson(getInputText(), pType);
        }
        return gson.fromJson(getInputText(), parameterType);
    }

}
