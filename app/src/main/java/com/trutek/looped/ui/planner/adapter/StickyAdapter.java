package com.trutek.looped.ui.planner.adapter;


import android.support.v7.widget.RecyclerView;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.msas.common.helpers.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public abstract class StickyAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<ActivityModel> activities = new ArrayList<>();
    private HashMap<String, Integer> headerMap = new HashMap<>();

    public void add(ActivityModel model) {
        activities.clear();
        activities.add(model);
        notifyDataSetChanged();
    }

    public void setListStickyAdapter(List<ActivityModel> items) {
        activities.clear();
        activities = items;
        for (int i = 0 ; i < items.size() ; i++ ){
            ActivityModel activity = items.get(i);
            if(!headerMap.containsKey(DateHelper.stringify(activity.dueDate)))
                headerMap.put(DateHelper.stringify(activity.dueDate), i);
        }

        notifyDataSetChanged();
    }

    public StickyAdapter(){
        setHasStableIds(true);
    }

    public ActivityModel getItem(int position) {
        return activities.get(position);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public int getPositionFromDate(Date date){
        return headerMap.get(DateHelper.stringify(date)) == null ? -1 : headerMap.get(DateHelper.stringify(date));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
