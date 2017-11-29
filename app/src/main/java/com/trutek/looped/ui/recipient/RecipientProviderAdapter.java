package com.trutek.looped.ui.recipient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.List;

/**
 * Created by Sandy on 12/2/2016.
 */

public class RecipientProviderAdapter extends RecyclerView.Adapter<RecipientProviderAdapter.MyViewHolder> {

    private List<ProviderModel> providers;
    private OnActionListener<ProviderModel> contactSelectedActionListeners;
    private OnActionListener<ProviderModel> contactUnSelectedActionListeners;

    public RecipientProviderAdapter(List<ProviderModel> providers, OnActionListener<ProviderModel> contactSelectedActionListeners,
                                    OnActionListener<ProviderModel> contactUnSelectedActionListeners)
    {
        this.providers = providers;
        this.contactSelectedActionListeners = contactSelectedActionListeners;
        this.contactUnSelectedActionListeners = contactUnSelectedActionListeners;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient_provider,parent,false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProviderModel ProviderModel = providers.get(position);
        holder.textView_name.setText(ProviderModel.getName());
        holder.textView_number.setText(ProviderModel.getPhone());


    }

    @Override
    public int getItemCount() {
        return providers.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        TextView textView_number;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView_name = (TextView) itemView.findViewById(R.id.item_rp_textView_name);
            textView_number = (TextView) itemView.findViewById(R.id.item_rp_textView_number);
        }
    }


}
