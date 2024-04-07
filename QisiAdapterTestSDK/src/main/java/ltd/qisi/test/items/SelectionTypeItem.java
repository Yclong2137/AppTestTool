package ltd.qisi.test.items;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.ParameterInfo;

/**
 * 选择类型参数
 *
 * @author Yclong
 */
public abstract class SelectionTypeItem<T> extends ParameterTypeItem<T> {

    private Spinner spinner;


    public SelectionTypeItem(ParameterInfo parameterInfo) {
        super(parameterInfo);
    }


    @Override
    protected View getView(Context context) {
        spinner = new Spinner(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.GRAY);
        background.setCornerRadius(6);
        spinner.setBackground(background);
        List<Entry> entries = fillEntries();
        List<String> items = new ArrayList<>();
        int defaultItemIndex = 0;
        T defaultValue = defaultValue();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            items.add(entry.name);
            if (Objects.equals(defaultValue, entry.data)) {
                defaultItemIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultItemIndex);
        hookView(spinner);
        return spinner;
    }


    /**
     * 填充数据
     */
    protected abstract List<Entry> fillEntries();


    protected void hookView(Spinner spinner) {

    }

    @Override
    public LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(-1, 84);
    }


    /**
     * 选中文本
     */
    protected String getSelectedText() {
        return spinner.getSelectedItem().toString();
    }

    protected int getSelectedIndex() {
        return spinner.getSelectedItemPosition();
    }

    public class Entry {

        public String name;

        public T data;

        public Entry(String name, T data) {
            this.name = name;
            this.data = data;
        }
    }
}
