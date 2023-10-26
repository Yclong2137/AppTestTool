package ltd.qisi.test.items;

import java.util.ArrayList;
import java.util.List;

import ltd.qisi.test.bean.ParameterInfo;

public class BooleanTypeItem extends SelectionTypeItem<Boolean> {


    public BooleanTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }

    @Override
    public Boolean getValue() throws Exception {
        return Boolean.valueOf(getSelectedText());
    }

    @Override
    protected List<String> fillData() {
        List<String> items = new ArrayList<>();
        items.add("true");
        items.add("false");
        return items;
    }
}
