package ltd.qisi.test.views;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ltd.qisi.test.FunctionModuleRegistry;
import ltd.qisi.test.MockClient;
import ltd.qisi.test.R;
import ltd.qisi.test.Utils;
import ltd.qisi.test.bean.ModuleInfo;


public class LauncherActivity extends ActivityBase {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setSubContentView(R.layout.test_activity_launcher);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = Utils.dp2px(24);
                outRect.right = Utils.dp2px(24);
            }
        });
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = Utils.dp2px(8);
                outRect.bottom = Utils.dp2px(8);
            }
        });
        FunctionModuleAdapter adapter = new FunctionModuleAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setData(FunctionModuleRegistry.getModuleInfoList());
    }


}
