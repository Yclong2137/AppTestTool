package ltd.qisi.test.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextMethodElementView extends MethodElementView {

    TextView mTextView;

    public TextMethodElementView(@NonNull Context context) {
        this(context, null);
    }

    public TextMethodElementView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextMethodElementView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextView = new TextView(context);
        mTextView.setTextSize(17);
        mTextView.setTextColor(Color.WHITE);
        viewContainer.addView(mTextView);
    }


    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setDeprecatedFlag(boolean flag) {
        if (flag) {
            mTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            mTextView.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

}
