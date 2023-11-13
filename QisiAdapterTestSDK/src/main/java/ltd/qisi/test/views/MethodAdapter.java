package ltd.qisi.test.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import ltd.qisi.test.MockClient;
import ltd.qisi.test.items.ParameterTypeItem;
import ltd.qisi.test.R;
import ltd.qisi.test.Utils;
import ltd.qisi.test.annotaitons.MockMethod;
import ltd.qisi.test.event.MethodCountChangeEvent;
import ltd.qisi.test.event.MethodSpecChangeEvent;
import ltd.qisi.test.model.MethodSpec;

/**
 * @author Yclong
 */
public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.VH> {


    private List<MethodSpec> methodSpecs;

    public MethodAdapter() {
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MethodSpec> methodSpecs) {
        this.methodSpecs = methodSpecs;
        if (methodSpecs == null) return;
        EventBus.getDefault().post(new MethodCountChangeEvent(methodSpecs.size()));
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.test_item_method, parent, false);
        return VH.create(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (methodSpecs == null) return;
        holder.bind(methodSpecs.get(position), null);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (methodSpecs == null) return;
        holder.bind(methodSpecs.get(position), payloads);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChanged(MethodSpecChangeEvent event) {
        if (methodSpecs == null) return;
        int index = this.methodSpecs.indexOf(event.methodSpec);
        notifyItemChanged(index, event);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getItemCount() {
        if (methodSpecs == null) return 0;
        return methodSpecs.size();
    }

    public static class VH extends RecyclerView.ViewHolder {


        private final TextView methodNameTv;

        private MethodSpec methodSpec;

        private final LinearLayout viewContainer;

        private final Context context;

        private final Button btnTest;

        private final TextView tvReqResult;
        private final TextView tvRespResult;

        private static final String SUFFIX = "\t";


        VH(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.parseColor("#80333333"));
            background.setCornerRadius(8);
            itemView.setBackground(background);
            viewContainer = itemView.findViewById(R.id.v_container);
            methodNameTv = itemView.findViewById(R.id.tv_method_name);
            btnTest = itemView.findViewById(R.id.btn_test);
            tvReqResult = itemView.findViewById(R.id.tv_req_result);
            tvRespResult = itemView.findViewById(R.id.tv_resp_result);
            btnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doReq();
                }
            });

        }

        static VH create(View itemView) {
            return new VH(itemView);
        }

        public void bind(MethodSpec methodSpec, List<Object> payloads) {
            this.methodSpec = methodSpec;
            if (payloads != null && payloads.size() > 0) {
                Object payload = payloads.get(0);
                if (payload instanceof MethodSpecChangeEvent) {
                    tvReqResult.setText(methodSpec.reqText);
                    tvRespResult.setText(methodSpec.respText);
                    return;
                }
            }
            tvReqResult.setText(methodSpec.reqText);
            tvRespResult.setText(methodSpec.respText);
            StringBuilder builder = new StringBuilder();

            //builder.append("\t").append("方法签名：");
            MockMethod mockMethod = methodSpec.getMockMethod();
            if (mockMethod != null) {
                builder.append("\t").append("方法描述: ");
                builder.append(mockMethod.desc());
                builder.append("\n");
            }
            builder.append("\t").append("方法签名: ");
            builder.append("\t")
                    .append(methodSpec.method.getReturnType()).append(" ")
                    .append(methodSpec.method.getName())
                    .append(" (");
            int length = methodSpec.getParameterTypes().length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    Object argType = methodSpec.getParameterTypes()[i];
                    builder.append("\n\t\t\t\t\t\t\t\t").append(MockClient.getTextFormatter().format(argType));
                    if (i != length - 1) {
                        builder.append(",");
                    }
                }
                builder.append("\n\t\t\t\t\t\t\t").append(")");
            } else {
                builder.append(")");
            }
            //处理废弃方法
            Deprecated deprecated = Utils.findMethodAnnotation(methodSpec.method, Deprecated.class);
            if (deprecated != null) {
                methodNameTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else {
                methodNameTv.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            }
            methodNameTv.setText(builder.toString());
            if (methodSpec.isExpand()) {
                viewContainer.removeAllViews();
                try {
                    ParameterTypeItem<?>[] typeItems = methodSpec.getTypeItems();
                    for (int index = 0; index < typeItems.length; index++) {
                        ParameterTypeItem<?> typeItem = typeItems[index];
                        View view;
                        if (typeItem != null && (view = typeItem.getView(context)) != null) {
                            LinearLayout.LayoutParams lp = typeItem.getLayoutParams() == null ? new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(32)) : typeItem.getLayoutParams();
                            lp.setMargins(0, Utils.dp2px(6), Utils.dp2px(12), Utils.dp2px(6));
                            viewContainer.addView(ViewHelper.createSubItemView(context, typeItem.getParameterInfo(), index, Utils.dp2px(96), view, lp));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                viewContainer.removeAllViews();
            }
        }


        private void doReq() {
            methodSpec.invoke(true);
        }


    }


}
