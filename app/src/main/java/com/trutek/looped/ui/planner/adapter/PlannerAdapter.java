package com.trutek.looped.ui.planner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PlannerAdapter extends StickyAdapter<RecyclerView.ViewHolder> implements
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planner, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        ActivityModel model = getItem(position);

        TextView name = (TextView) view.findViewById(R.id.text_view_activity_name);
        TextView date = (TextView) view.findViewById(R.id.text_view_activity_date);

        name.setText(model.subject);
        date.setText(DateHelper.stringifyTime(model.dueDate));
    }

    @Override
    public long getHeaderId(int position) {
        ActivityModel model = getItem(position);
        if(model.dueDate == null){
            return 0;
        }
        return DateUtils.toShortDateLong(model.dueDate.getTime()/1000);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_planner, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        ActivityModel model = getItem(position);
        TextView header = (TextView) view.findViewById(R.id.text_view_header);

        if(model.dueDate != null)
            header.setText(DateUtils.toTodayTomorrowFullMonthDate(model.dueDate.getTime()));
    }
}
