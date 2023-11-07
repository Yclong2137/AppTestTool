package ltd.qisi.test.items;

import android.text.InputType;
import android.widget.EditText;

import ltd.qisi.test.bean.ParameterInfo;

public class LongTypeItem extends InputTypeItem<Long> {


    public LongTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    protected void hookView(EditText inputView) {
        inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public Long getValue() throws Exception {
        return Long.parseLong(getInputText());
    }

    @Override
    protected Long defaultValue() {
        return 0L;
    }
}
