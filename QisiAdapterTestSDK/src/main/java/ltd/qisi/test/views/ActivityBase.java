package ltd.qisi.test.views;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ltd.qisi.test.R;
import ltd.qisi.test.Utils;


public abstract class ActivityBase extends AppCompatActivity {

    private TextView titleView;


    protected abstract void initView();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_base_layout);
        Utils.setCustomDensity(this, getApplication());
        initTopBar();
        initView();
    }

    protected void setSubContentView(View view) {
        LinearLayout rootContainer = (LinearLayout) findViewById(R.id.root_layout);
        if (rootContainer != null && view != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp.weight = 1;
            rootContainer.addView(view, lp);
        }
    }

    protected void setSubContentView(@LayoutRes int resId) {
        setSubContentView(getLayoutInflater().inflate(resId, null));
    }

    private void initTopBar() {
        titleView = findViewById(R.id.title);
        titleView.setText("智联3.0系统接口认证工具");
        findViewById(R.id.im_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置title
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v != null) v.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }


}
