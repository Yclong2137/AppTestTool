package ltd.qisi.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import ltd.qisi.test.bean.ParameterInfo;


public final class MockClient {


    private static TextFormatter sTextFormatter;


    private static final Gson sGson = new GsonBuilder().serializeNulls().create();


    private MockClient() {

    }


    /**
     * 注册参数类型
     *
     * @param type          参数类型
     * @param typeItemClazz
     */
    public static void registerParameterTypeItem(Class<?> type, Class<? extends ParameterTypeItem<?>> typeItemClazz) {
        ParameterTypeItemRegistry.registerParameterType(type, typeItemClazz);
    }


    /**
     * 获取参数映射类型
     *
     * @param type 参数类型
     * @return 参数映射类型
     */
    public static ParameterTypeItem<?> getTypeItem(Class<?> type, ParameterInfo parameterInfo) {
        return ParameterTypeItemRegistry.getTypeItem(type, parameterInfo);
    }


    public static TextFormatter getTextFormatter() {
        if (sTextFormatter == null) {
            return (sTextFormatter = DEFAULT_TEXT_FORMATTER);
        }
        return sTextFormatter;

    }

    static TextFormatter DEFAULT_TEXT_FORMATTER = new TextFormatter() {
        @Override
        public String format(Object o) {
            if (o == null) return "";
            Class<?> clazz = o.getClass();

            try {
                if (Throwable.class.isAssignableFrom(clazz)) {
                    return Utils.getStackTraceString((Throwable) o);
                } else if (Utils.isCommonType(clazz)) {
                    return String.valueOf(o);
                } else {
                    return sGson.toJson(o);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return String.valueOf(o);
            }

        }

        /**
         * 格式化参数
         *
         * @param args 参数
         * @return
         */
        @Override
        public String format(Object[] args) {
            return Arrays.toString(args);
        }
    };


}
