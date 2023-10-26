package ltd.qisi.test;

public interface TextFormatter {

    /**
     * 格式化
     *
     * @param o 源数据
     * @return 格式化数据
     */
    String format(Object o);

    /**
     * 格式化参数
     *
     * @param args 参数
     * @return
     */
    String format(Object[] args);

}
