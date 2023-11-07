package ltd.qisi.test.items;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import ltd.qisi.test.bean.ParameterInfo;

/**
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
    public T defaultValue() {
        return null;
    }

    /**
     * 参数信息
     */
    public ParameterInfo getParameterInfo() {
        return parameterInfo;
    }
}
