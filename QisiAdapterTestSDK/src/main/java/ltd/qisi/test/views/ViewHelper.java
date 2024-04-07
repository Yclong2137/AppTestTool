package ltd.qisi.test.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ltd.qisi.test.Utils;
import ltd.qisi.test.annotaitons.MockField;
import ltd.qisi.test.bean.ParameterInfo;

public final class ViewHelper {


    private ViewHelper() {
    }

    /**
     * 创建子ItemView
     *
     * @param context 上下文
     * @param info    参数信息
     * @param pIndex  参数位置
     */
    @SuppressLint("DefaultLocale")
    public static View createSubItemView(Context context, ParameterInfo info, int pIndex, View itemView, ViewGroup.LayoutParams itemViewLp) {
        LinearLayout rootView = new LinearLayout(context);
        TextView labelView = new TextView(context);
        labelView.setTextColor(Color.WHITE);
        labelView.setTextSize(17);
        MockField mockField = info.mockField;
        if (mockField == null) {
            labelView.setText(String.format("\t\u3000 参数%d：", pIndex));
        } else {
            labelView.setText(String.format("\t%s：", mockField.name()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                labelView.setTooltipText(mockField.remarks());
            }
        }
        labelView.setSingleLine(true);
        labelView.setEllipsize(TextUtils.TruncateAt.END);
        labelView.setGravity(Gravity.CENTER | Gravity.END);
        LinearLayout.LayoutParams labelLP = new LinearLayout.LayoutParams(Utils.dp2px(120), itemViewLp.height);
        labelLP.setMargins(0, 6, 0, 6);
        rootView.addView(labelView, labelLP);
        rootView.addView(itemView, itemViewLp);

        return rootView;
    }

}
