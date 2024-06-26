package ltd.qisi.test.items;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
    protected View getView(Context context) {
        inputView = new EditText(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.GRAY);
        background.setCornerRadius(6);
        inputView.setBackground(background);
        inputView.setPadding(12, 2, 12, 2);
        inputView.setHint("请输入");
        inputView.setTextSize(18);
        inputView.clearFocus();
        hookView(inputView);
        Object value = null;
        if ((value = parameterInfo.value) != null || (value = defaultValue()) != null) {
            inputView.setText(String.valueOf(value));
        }
        return inputView;
    }

    protected void hookView(EditText inputView) {

    }

    @Override
    public LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(-1, 84);
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
