package ltd.qisi.test.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ltd.qisi.test.R;

public class MethodElementView extends FrameLayout {

    protected final LinearLayout viewContainer;

    private final TextView labelView;

    public MethodElementView(@NonNull Context context) {
        this(context, null);
    }

    public MethodElementView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MethodElementView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.test_item_method_element, this);
        viewContainer = findViewById(R.id.container);
        labelView = findViewById(R.id.label_view);
    }

    public void setLabel(String label) {
        labelView.setText(label);
    }


    public void setTooltipText(@Nullable CharSequence tooltipText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            labelView.setTooltipText(tooltipText);
        }
    }


    public void setParameterView(View view, LinearLayout.LayoutParams lp) {
        viewContainer.addView(view, 1, lp);
    }

}
