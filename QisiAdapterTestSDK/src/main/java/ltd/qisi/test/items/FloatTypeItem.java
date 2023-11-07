package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * float/Float参数类型
 *
 * @author Yclong
 */
public class FloatTypeItem extends InputTypeItem<Float> {


    public FloatTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

    }

    @Override
    public Float getValue() throws Exception {
        return Float.parseFloat(getInputText());
    }

    @Override
    public Float defaultValue() {
        return 0f;
    }
}
