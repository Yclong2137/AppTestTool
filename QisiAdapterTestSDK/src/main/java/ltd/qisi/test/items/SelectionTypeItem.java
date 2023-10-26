package ltd.qisi.test.items;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ltd.qisi.test.ParameterTypeItem;
import ltd.qisi.test.bean.ParameterInfo;

import java.util.List;

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
    public View getView(Context context) {
        spinner = new Spinner(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.GRAY);
        background.setCornerRadius(6);
        spinner.setBackground(background);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, fillData());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        hookView(spinner);
        return spinner;
    }

    /**
     * 填充数据
     */
    protected abstract List<String> fillData();

    protected void hookView(Spinner spinner) {

    }


    /**
     * 选中文本
     */
    protected String getSelectedText() {
        return spinner.getSelectedItem().toString();
    }
}
