package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * short/Short参数类型
 *
 * @author Yclong
 */
public class ShortTypeItem extends InputTypeItem<Short> {


    public ShortTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public Short getValue() throws Exception {
        return Short.parseShort(getInputText());
    }

    @Override
    protected Short defaultValue() {
        return 0;
    }
}
