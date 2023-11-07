package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * double/Double参数类型
 *
 * @author Yclong
 */
public class DoubleTypeItem extends InputTypeItem<Double> {


    public DoubleTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public Double getValue() throws Exception {
        return Double.parseDouble(getInputText());
    }

    @Override
    public Double defaultValue() {
        return 0d;
    }
}
