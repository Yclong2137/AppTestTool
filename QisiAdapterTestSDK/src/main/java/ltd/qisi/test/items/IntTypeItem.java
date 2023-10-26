package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * int/Integer 参数类型
 */
public class IntTypeItem extends InputTypeItem<Integer> {



    public IntTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public Integer getValue() throws Exception {
        return Integer.parseInt(getInputText());
    }
}
