package ltd.qisi.test.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import ltd.qisi.test.R;
import ltd.qisi.test.bean.MethodInvokeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yclong
 */
public class MethodInvokeAdapter extends RecyclerView.Adapter<MethodInvokeAdapter.VH> {

    private final List<MethodInvokeInfo> infoList = new ArrayList<>();



    public MethodInvokeAdapter() {
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_method_callback, parent, false);
        return VH.create(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(infoList.get(position));
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void addItem(MethodInvokeInfo info) {
        this.infoList.add(info);
        this.notifyItemInserted(infoList.size() - 1);
    }

    public static class VH extends RecyclerView.ViewHolder {


        static VH create(View itemView) {
            return new VH(itemView);
        }

        private TextView tvInfo;

        private VH(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tv_info);
        }

        void bind(MethodInvokeInfo info) {
            tvInfo.setText(info.method.getName() + ">>>" + Arrays.toString(info.args));
        }
    }

}
