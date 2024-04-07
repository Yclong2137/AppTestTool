package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * short/Short参数类型
 *
 * @author Yclong
 */
public class ByteTypeItem extends InputTypeItem<Byte> {


    public ByteTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public Byte getValue() throws Exception {
        return Byte.parseByte(getInputText());
    }

    @Override
    public Byte defaultValue() {
        return 0;
    }
}
