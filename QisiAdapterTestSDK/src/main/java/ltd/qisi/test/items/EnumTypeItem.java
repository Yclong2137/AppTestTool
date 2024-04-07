package ltd.qisi.test.items;

import java.util.ArrayList;
import java.util.List;

import ltd.qisi.test.bean.ParameterInfo;

/**
 * 通用枚举
 */
public class EnumTypeItem extends SelectionTypeItem<Object> {

    private final List<Entry> entries = new ArrayList<>();

    public EnumTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
        Class<?> parameterType = parameterInfo.parameterType;
        if (parameterType.isEnum()) {
            Enum<?>[] enumConstants = (Enum<?>[]) parameterType.getEnumConstants();
            for (Enum<?> enumConstant : enumConstants) {
                entries.add(new Entry(enumConstant.name(), enumConstant));
            }
        }
    }

    /**
     * 输入结果值
     */
    @Override
    public Object getValue() throws Exception {
        return entries.get(getSelectedIndex()).data;
    }

    /**
     * 填充数据
     */
    @Override
    protected List<Entry> fillEntries() {
        return entries;
    }
}
