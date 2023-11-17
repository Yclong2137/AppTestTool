package ltd.qisi.test.items;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.ParameterInfo;

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
        inputView.setText(String.valueOf(formatText()));
        return inputView;
    }

    protected void hookView(EditText inputView) {

    }


    /**
     * 格式化文本
     */
    public Object formatText() {
        Class<?> parameterType = parameterInfo.parameterType;
        if (parameterType != null) {
            if (parameterType.isPrimitive() || Number.class.isAssignableFrom(parameterType)) {
                return defaultValue();
            }
        }
        return null;
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
