package com.trutek.looped.ui.recipient.recipient.loops;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LoopModel;

import java.util.List;

public class LoopsAdapter  extends RecyclerView.Adapter<LoopsAdapter.ViewHolder> {

    private List<LoopModel> loops;

    public LoopsAdapter(List<LoopModel> loops){

        this.loops = loops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_loops, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LoopModel loop = loops.get(position);
        holder.name.setText(loop.name);
        holder.role.setText(loop.getRole());

    }

    @Override
    public int getItemCount() {
        return loops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, role;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.loop_name);
            role = (TextView) view.findViewById(R.id.loop_role);
        }
    }
}
