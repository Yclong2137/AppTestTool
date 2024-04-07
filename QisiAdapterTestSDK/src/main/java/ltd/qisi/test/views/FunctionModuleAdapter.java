package ltd.qisi.test.views;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ltd.qisi.test.R;
import ltd.qisi.test.bean.ModuleInfo;

public class FunctionModuleAdapter extends RecyclerView.Adapter<FunctionModuleAdapter.VH> {

    private final List<ModuleInfo> functionModuleInfoList = new ArrayList<>();


    public void setData(List<ModuleInfo> functions) {
        this.functionModuleInfoList.clear();
        this.functionModuleInfoList.addAll(functions);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_name_button, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(functionModuleInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return functionModuleInfoList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView button;

        public VH(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor("#3B4550"));
            drawable.setCornerRadius(50);
            itemView.setBackground(drawable);
        }

        public void bind(ModuleInfo moduleInfo) {
            Class<?> moduleClass = moduleInfo.getModuleClass();
            if (moduleClass != null && moduleClass.isAnnotationPresent(Deprecated.class)) {
                button.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else {
                button.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            }
            String displayLabel = moduleInfo.getModuleName();
            if (moduleClass != null) {
                String className;
                if (displayLabel != null && !displayLabel.equals(className = moduleClass.getSimpleName())) {
                    displayLabel += "\n" + "(" + className + ")";
                }
            }
            button.setText(displayLabel);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FuncActivity.start(itemView.getContext(), moduleInfo);
                }
            });
        }

    }

}
