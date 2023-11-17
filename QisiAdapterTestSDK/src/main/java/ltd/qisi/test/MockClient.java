package ltd.qisi.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

import ltd.qisi.test.bean.ParameterInfo;
import ltd.qisi.test.items.ParameterTypeItem;


public final class MockClient {


    public static TextFormatter sTextFormatter;
    /**
     * 调试日志
     */
    public static boolean enableLog = true;


    public static final Gson sGson = new GsonBuilder()
            .serializeNulls()
            .create();


    private MockClient() {

    }

    /**
     * 打印日志
     *
     * @param msg 日志
     */
    public static void printLog(String msg) {
        if (!enableLog) return;
        System.out.println("--->>> " + msg);
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
            if (o instanceof Class) {
                return String.valueOf(o);
            }
            Class<?> clazz = o.getClass();
            try {
                if (Throwable.class.isAssignableFrom(clazz)) {
                    return Utils.getStackTraceString((Throwable) o);
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
            String result = null;
            try {
                result = sGson.toJson(args);
            } catch (Exception e) {
                //e.printStackTrace();
                result = Arrays.toString(args);
            }
            return result;
        }
    };


}
