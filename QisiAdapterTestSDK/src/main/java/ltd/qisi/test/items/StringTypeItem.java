package ltd.qisi.test.items;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * @author Yclong
 */
public class StringTypeItem extends InputTypeItem<String> {


    public StringTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }


    @Override
    public String getValue() throws Exception {
        return getInputText();
    }

    @Override
    public String defaultValue() {
        return "";
    }
}
