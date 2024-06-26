package ltd.qisi.test.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ltd.qisi.test.R;
import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.MethodInvokeInfo;
import ltd.qisi.test.event.MethodCountChangeEvent;
import ltd.qisi.test.model.MethodSpec;

/**
 * @author Yclong
 */
public class MockView extends FrameLayout {

    private static final String TAG = "MockView";

    private MethodAdapter methodAdapter;

    private MethodInvokeAdapter methodInvokeAdapter;

    /**
     * 待测目标
     */
    private Object caller;

    /**
     * 是否包含废弃
     */
    private boolean hasDeprecated;

    /**
     * 是否包含执行错误
     */
    private boolean hasError;

    /**
     * 关键字
     */
    private String key;

    /**
     * 元数据
     */
    private final List<MethodSpec> metaMethodSpecs = new ArrayList<>();

    private final List<MethodSpec> methodSpecs = new ArrayList<>();

    RecyclerView rvLeft, rvRight;

    TextView tvMethodArea;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService mWorkExecutor = Executors.newCachedThreadPool();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MockView(Context context) {
        this(context, null);
    }

    MockView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    MockView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setTextSize(36);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        LayoutInflater.from(context).inflate(R.layout.test_view_mock, this);
        initMethodView(context);
        initMethodCallbackView(context);
    }


    private void initMethodCallbackView(Context context) {
        rvLeft = findViewById(R.id.rv_method_callback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvLeft.setLayoutManager(layoutManager);
        methodInvokeAdapter = new MethodInvokeAdapter();
        rvLeft.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
                outRect.bottom = 12;
            }
        });
        rvLeft.setAdapter(methodInvokeAdapter);
    }

    private void initMethodView(Context context) {
        rvRight = findViewById(R.id.rv_methods);
        tvMethodArea = findViewById(R.id.tv_method_area);
        findViewById(R.id.btn_test_auto).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTest();
            }
        });
        CheckBox checkBox = findViewById(R.id.rb_deprecated);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hasDeprecated = isChecked;
                mExecutor.execute(new FilterResultRunnable());
            }
        });
        hasDeprecated = checkBox.isChecked();
        CheckBox errorCbView = findViewById(R.id.rb_error);
        errorCbView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hasError = isChecked;
                mExecutor.execute(new FilterResultRunnable());
            }
        });
        hasError = errorCbView.isChecked();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvRight.setLayoutManager(layoutManager);
        methodAdapter = new MethodAdapter();
        methodAdapter.setItemViewClickListener(new MethodAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view, MethodSpec methodSpec) {
                if (methodSpec != null) methodSpec.invoke(caller, true);
            }
        });
        rvRight.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 12;
                outRect.right = 12;
                outRect.top = 12;
                outRect.bottom = 12;
            }

            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                int itemCount = parent.getChildCount();
                for (int index = 0; index < itemCount; index++) {
                    View child = parent.getChildAt(index);
                    int right = child.getRight() - parent.getPaddingLeft();
                    int top = parent.getTop() + child.getTop();
                    int radius = 32;
                    int cx = right - radius;
                    int cy = top + radius;
                    mPaint.setColor(Color.RED);
                    c.drawCircle(cx, cy, radius, mPaint);
                    String text = String.valueOf(parent.getChildAdapterPosition(child) + 1);
                    mPaint.setColor(Color.WHITE);
                    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                    float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
                    c.drawText(text, cx, cy + distance, mPaint);
                }
            }
        });
        rvRight.setItemViewCacheSize(5);
        rvRight.setAdapter(methodAdapter);
        SearchInputView searchInputView = findViewById(R.id.search_view);
        searchInputView.setOnTextChangedListener(new SearchInputView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                key = text;
                mExecutor.execute(new FilterResultRunnable());
            }
        });
    }

    /**
     * 自动化测试所有接口
     */
    private void autoTest() {
        for (MethodSpec methodSpec : methodSpecs) {
            mWorkExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    methodSpec.invoke(caller, true);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMethodCallback(MethodInvokeInfo info) {
        if (methodInvokeAdapter != null) {
            methodInvokeAdapter.addItem(info);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMethodCountChangeEvent(MethodCountChangeEvent event) {
        if (tvMethodArea != null) {
            String text = String.format("方法区域（%d)", event.count);
            tvMethodArea.setText(text);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow() called");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow() called");
        EventBus.getDefault().unregister(this);
        mExecutor.shutdownNow();
    }

    /**
     * 关联对象
     *
     * @param caller
     */
    public void assignObject(Object caller) {
        if (caller == null) return;
        this.caller = caller;
        mExecutor.execute(new HandleMethodsRunnable());
    }

    /**
     * 顾虑错误结果
     */
    private final class FilterResultRunnable implements Runnable {

        @Override
        public void run() {
            methodSpecs.clear();
            for (MethodSpec methodSpec : metaMethodSpecs) {
                //过滤废弃方法
                if (!hasDeprecated && methodSpec.getDeprecatedAnnotation() != null) {
                    continue;
                }
                //过滤执行错误
                if (hasError && (methodSpec.isPass() || !methodSpec.isExecuted())) {
                    continue;
                }
                //过滤关键字
                String keyword = methodSpec.getKeyword();
                if (key != null && keyword != null && !keyword.toLowerCase().contains(key.toLowerCase())) {
                    continue;
                }
                methodSpecs.add(methodSpec);
            }
            notifyDataChanged();
        }
    }


    /**
     * 处理数据
     */
    private class HandleMethodsRunnable implements Runnable {

        @Override
        public void run() {
            metaMethodSpecs.clear();
            metaMethodSpecs.addAll(Utils.getMethodSpecs(caller));
            methodSpecs.addAll(metaMethodSpecs);
            new FilterResultRunnable().run();
        }
    }

    /**
     * 通知数据刷新
     */
    private void notifyDataChanged() {
        post(new Runnable() {
            @Override
            public void run() {
                if (methodAdapter != null)
                    methodAdapter.setData(methodSpecs);
            }
        });
    }


}
