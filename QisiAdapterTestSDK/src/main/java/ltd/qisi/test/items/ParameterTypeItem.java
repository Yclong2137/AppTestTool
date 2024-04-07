package ltd.qisi.test.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ltd.qisi.test.annotaitons.MockField;
import ltd.qisi.test.bean.ParameterInfo;
import ltd.qisi.test.views.MethodElementView;

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
    protected abstract View getView(Context context);

    @SuppressLint("DefaultLocale")
    public MethodElementView getHostView(Context context) {
        View view;
        if ((view = getView(context)) != null) {
            MethodElementView elementView = new MethodElementView(context);
            LinearLayout.LayoutParams itemViewLp = getLayoutParams();
            if (itemViewLp == null) {
                itemViewLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 32);
            }
            itemViewLp.setMargins(0, 8, 0, 8);
            elementView.setParameterView(view, itemViewLp);
            return elementView;
        }

        return null;
    }

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
