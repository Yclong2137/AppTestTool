package ltd.qisi.test.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

public class SearchInputView extends androidx.appcompat.widget.AppCompatEditText {

    private static final long LIMIT = 1000;

    private OnTextChangedListener mListener;
    private String                mStartText = "";// 记录开始输入前的文本内容
    private Runnable              mAction    = new Runnable() {
        @Override
        public void run() {
            if (mListener != null) {
                // 判断最终和开始前是否一致
                if (mStartText!=null&&!mStartText.equals(getText().toString())) {
                    mStartText = getText().toString();// 更新 mStartText
                    mListener.onTextChanged(mStartText);
                }
            }
        }
    };

    public SearchInputView(Context context) {
        super(context);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(15);
        drawable.setColor(Color.GRAY);
        setBackground(drawable);
    }

    /**
     * 在 LIMIT 时间内连续输入不触发文本变化
     */
    public void setOnTextChangedListener(OnTextChangedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onTextChanged(final CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        // 移除上一次的回调
        removeCallbacks(mAction);
        postDelayed(mAction, LIMIT);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mAction);
    }

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

}
