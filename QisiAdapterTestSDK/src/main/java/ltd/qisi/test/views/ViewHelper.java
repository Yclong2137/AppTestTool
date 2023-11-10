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

import ltd.qisi.test.annotaitons.MockField;
import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.ParameterInfo;

public final class ViewHelper {


    private ViewHelper() {
    }

    /**
     * 创建子ItemView
     *
     * @param context  上下文
     * @param info     参数信息
     * @param pIndex   参数位置
     * @param minWidth 最小宽度
     */
    @SuppressLint("DefaultLocale")
    public static View createSubItemView(Context context, ParameterInfo info, int pIndex, int minWidth, View itemView, ViewGroup.LayoutParams itemViewLp) {
        LinearLayout rootView = new LinearLayout(context);
        TextView labelView = new TextView(context);
        labelView.setTextColor(Color.WHITE);
        labelView.setTextSize(17);
        MockField mockField = info.mockField;
        if (mockField == null) {
            labelView.setText(String.format("参数%d：", pIndex));
        } else {
            labelView.setText(String.format("%s：", mockField.name()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                labelView.setTooltipText(mockField.remarks());
            }
        }
        labelView.setSingleLine(true);
        labelView.setEllipsize(TextUtils.TruncateAt.END);
        labelView.setMinWidth(minWidth);
        labelView.setMaxWidth(Utils.dp2px(160));
        labelView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        LinearLayout.LayoutParams labelLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Utils.dp2px(32));
        labelLP.setMargins(0, Utils.dp2px(6), 0, Utils.dp2px(6));
        rootView.addView(labelView, labelLP);
        rootView.addView(itemView, itemViewLp);

        return rootView;
    }

}
