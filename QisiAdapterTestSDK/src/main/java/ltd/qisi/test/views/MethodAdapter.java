package ltd.qisi.test.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import ltd.qisi.test.MockClient;
import ltd.qisi.test.R;
import ltd.qisi.test.Utils;
import ltd.qisi.test.annotaitons.MockField;
import ltd.qisi.test.annotaitons.MockMethod;
import ltd.qisi.test.event.MethodCountChangeEvent;
import ltd.qisi.test.event.MethodSpecChangeEvent;
import ltd.qisi.test.items.ParameterTypeItem;
import ltd.qisi.test.model.MethodSpec;

/**
 * @author Yclong
 */
public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.VH> {


    private List<MethodSpec> methodSpecs;

    private OnItemViewClickListener itemViewClickListener;


    public void setItemViewClickListener(OnItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }

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
        return VH.create(this, itemView);
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


        private final TextMethodElementView methodRemarksElement;
        private final TextMethodElementView methodSignatureElement;

        private MethodSpec methodSpec;

        private final LinearLayout parameterElement;

        private final Context context;

        private final TextMethodElementView reqResultElement;
        private final TextMethodElementView respResultElement;

        private static final String SUFFIX = "\t";
        private final WeakReference<MethodAdapter> adapterRef;

        VH(MethodAdapter adapter, @NonNull View itemView) {
            super(itemView);
            adapterRef = new WeakReference<>(adapter);
            this.context = itemView.getContext();
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.parseColor("#80333333"));
            background.setCornerRadius(8);
            itemView.setBackground(background);
            parameterElement = itemView.findViewById(R.id.element_container_parameter);
            methodRemarksElement = itemView.findViewById(R.id.element_method_remarks);
            methodRemarksElement.setLabel("方法描述：");
            methodSignatureElement = itemView.findViewById(R.id.element_method_signature);
            methodSignatureElement.setLabel("方法签名：");
            Button btnTest = itemView.findViewById(R.id.btn_test);
            reqResultElement = itemView.findViewById(R.id.element_req);
            reqResultElement.setLabel("请求参数：");
            respResultElement = itemView.findViewById(R.id.element_resp);
            respResultElement.setLabel("请求结果：");
            btnTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MethodAdapter methodAdapter = adapterRef.get();
                    if (methodAdapter != null && methodAdapter.itemViewClickListener != null) {
                        methodAdapter.itemViewClickListener.onItemViewClick(v, methodSpec);
                    }
                }
            });

        }

        static VH create(MethodAdapter adapter, View itemView) {
            return new VH(adapter, itemView);
        }

        public void bind(MethodSpec methodSpec, List<Object> payloads) {
            this.methodSpec = methodSpec;
            if (payloads != null && payloads.size() > 0) {
                Object payload = payloads.get(0);
                if (payload instanceof MethodSpecChangeEvent) {
                    reqResultElement.setText(methodSpec.reqText);
                    respResultElement.setText(methodSpec.respText);
                    return;
                }
            }
            reqResultElement.setText(methodSpec.reqText);
            respResultElement.setText(methodSpec.respText);

            MockMethod mockMethod = methodSpec.getMockMethod();
            if (mockMethod != null) {
                methodRemarksElement.setVisibility(View.VISIBLE);
                methodRemarksElement.setText(mockMethod.desc());
            } else {
                methodRemarksElement.setVisibility(View.GONE);
            }
            StringBuilder signatureText = new StringBuilder(methodSpec.method.getReturnType() + " " + methodSpec.method.getName());
            signatureText.append(" (");
            int length = methodSpec.getParameterTypes().length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    Object argType = methodSpec.getParameterTypes()[i];
                    signatureText
                            .append("\n")
                            .append(MockClient.getTextFormatter().format(argType));
                    if (i != length - 1) {
                        signatureText.append(",");
                    }
                }
                signatureText.append("\n").append(")");
            } else {
                signatureText.append(")");
            }
            methodSignatureElement.setText(signatureText);

            //处理废弃方法
            Deprecated deprecated = Utils.findMethodAnnotation(methodSpec.method, Deprecated.class);
            methodSignatureElement.setDeprecatedFlag(deprecated != null);
            if (methodSpec.isExpand()) {
                parameterElement.removeAllViews();
                try {
                    ParameterTypeItem<?>[] typeItems = methodSpec.getTypeItems();
                    for (int index = 0; index < typeItems.length; index++) {
                        ParameterTypeItem<?> typeItem = typeItems[index];
                        MethodElementView view;
                        if (typeItem != null && (view = typeItem.getHostView(context)) != null) {
                            MockField mockField = typeItem.getParameterInfo().mockField;
                            if (mockField == null) {
                                view.setLabel("参数" + index + "：");
                            } else {
                                view.setLabel(String.format("%s：", mockField.name()));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    view.setTooltipText(mockField.remarks());
                                }
                            }
                            parameterElement.addView(view);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                parameterElement.removeAllViews();
            }
        }


    }


    public interface OnItemViewClickListener {
        void onItemViewClick(View view, MethodSpec methodSpec);
    }


}
