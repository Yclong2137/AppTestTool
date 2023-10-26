package ltd.qisi.test.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Constructor;

import ltd.qisi.test.FunctionModuleInterface;
import ltd.qisi.test.bean.ModuleInfo;

public class FuncActivity extends ActivityBase {
    private static final String KEY_FUNCTION_MODULE_INFO = "_key_function_module_info";

    public static void start(Context context, ModuleInfo info) {
        Intent starter = new Intent(context, FuncActivity.class);
        starter.putExtra(KEY_FUNCTION_MODULE_INFO, info);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            ModuleInfo moduleInfo = (ModuleInfo) intent.getSerializableExtra(KEY_FUNCTION_MODULE_INFO);
            if (moduleInfo != null) {
                //设置模块名称
                setTitle(moduleInfo.getModuleName() + "(" + moduleInfo.getModuleClass().getSimpleName() + ")");
                MockView mockView = new MockView(this);
                setSubContentView(mockView);

                try {
                    Constructor<?> constructor = moduleInfo.getModuleClass().getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object o = constructor.newInstance();
                    mockView.assignObject(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "moduleInfo is null ", Toast.LENGTH_LONG).show();
            }
        }
    }
}
