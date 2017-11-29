package com.trutek.looped.ui.profile.create.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;

import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.InterestModel;

import java.util.ArrayList;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private ArrayList<ItemRow> _itemRows ;
    private ArrayList<InterestModel> interests;
    private ArrayList<InterestModel> filteredInterest;
    private OnActionListener<InterestModel> onActionListener;
    private OnActionListener<InterestModel> onDeSelectListener;

    private InterestFilter mFilter;

    private class ItemRow {
        private int maxLength = 30;
        InterestModel item1;
        InterestModel item2;
        InterestModel item3;

        private int getLength(){
            int length = 0;
            if(item1 != null) {
                length += item1.name.length();
            }

            if(item2 != null) {
                length += item2.name.length();
            }

            if(item3 != null) {
                length += item3.name.length();
            }

            return length;
        }

        private boolean canAdd(InterestModel item) {
            if(item3 != null){
                return false;
            }

            if(getLength() + item.name.length() >  maxLength)
                return false;
            else {

                return true;
            }
        }

        public boolean add(InterestModel item){
            if(!canAdd(item)) {
                return false;
            }
            if(item1 == null){
                item1 = item;
                return true;
            } else if (item2 == null){
                item2 = item;
                return true;
            } else if (item3 == null){
                item3 = item;
                return true;
            }
            return false;

        }
    }

    public InterestAdapter(ArrayList<InterestModel> interests,
                           ArrayList<InterestModel> filteredInterest,
                           OnActionListener<InterestModel> onActionListener,
                           OnActionListener<InterestModel> onDeSelectListener) {

        this.interests = interests;
        this.filteredInterest = filteredInterest;
        this.onActionListener = onActionListener;
        this.onDeSelectListener = onDeSelectListener;
        mFilter = new InterestFilter(this);

        _itemRows = new ArrayList<>();

        ItemRow row = new ItemRow();
        if(filteredInterest.size()>0){
            _itemRows.add(row);
        }

        for(InterestModel item: filteredInterest){
            if(!row.add(item)) {
                row = new ItemRow();
                row.add(item);
                _itemRows.add(row);
            }
        }
    }

    public void setModified(){

        _itemRows.clear();
        ItemRow row = new ItemRow();
        if(filteredInterest.size()>0){
            _itemRows.add(row);
        }

        for(InterestModel item: filteredInterest){
            if(!row.add(item)) {
                row = new ItemRow();
                row.add(item);
                _itemRows.add(row);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_interest, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final ItemRow row = _itemRows.get(position);

        if(row.item1 != null){
            if(row.item1.isSelected){
                interestSelected(viewHolder.item1);
            } else {
                interestUnSelected(viewHolder.item1);
            }

            viewHolder.item1.setText(row.item1.name);
            viewHolder.item1.setVisibility(View.VISIBLE);

            viewHolder.item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onActionListener !=  null && onDeSelectListener != null){
                        if (row.item1.isSelected) {
                            interestUnSelected(viewHolder.item1);
                            row.item1.isSelected = false;
                            onDeSelectListener.notify(row.item1);
                        } else {
                            interestSelected(viewHolder.item1);
                            row.item1.isSelected = true;
                            onActionListener.notify(row.item1);
                        }
                    }
                }
            });
        } else {
            viewHolder.item1.setVisibility(View.GONE);
        }

        if(row.item2 != null){
            if(row.item2.isSelected){
                interestSelected(viewHolder.item2);
            } else {
                interestUnSelected(viewHolder.item2);
            }
            viewHolder.item2.setText(row.item2.name);
            viewHolder.item2.setVisibility(View.VISIBLE);

            viewHolder.item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onActionListener !=  null && onDeSelectListener != null) {
                        if (row.item2.isSelected) {
                            interestUnSelected(viewHolder.item2);
                            row.item2.isSelected = false;
                            onDeSelectListener.notify(row.item2);
                        } else {
                            interestSelected(viewHolder.item2);
                            row.item2.isSelected = true;
                            onActionListener.notify(row.item2);
                        }
                    }
                }
            });
        } else {
            viewHolder.item2.setVisibility(View.GONE);
        }

        if(row.item3 != null){
            viewHolder.item3.setChecked(row.item3.isSelected);
            if(row.item3.isSelected){
                interestSelected(viewHolder.item3);
            } else {
                interestUnSelected(viewHolder.item3);
            }
            viewHolder.item3.setText(row.item3.name);
            viewHolder.item3.setVisibility(View.VISIBLE);

            viewHolder.item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onActionListener !=  null && onDeSelectListener != null) {
                        if (row.item3.isSelected) {
                            interestUnSelected(viewHolder.item3);
                            row.item3.isSelected = false;
                            onDeSelectListener.notify(row.item3);
                        } else {
                            interestSelected(viewHolder.item3);
                            row.item3.isSelected = true;
                            onActionListener.notify(row.item3);
                        }
                    }
                }
            });
        } else {
            viewHolder.item3.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return _itemRows.size();
    }

    public InterestModel getInterestId(String interestName) {
        for (InterestModel item : filteredInterest) {
            if (interestName.equals(item.name)) {
                return item;
            }
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox item1, item2, item3;

        public ViewHolder(View view) {
            super(view);

            item1 = (CheckBox) view.findViewById(R.id.checkbox_interest_item1);
            item2 = (CheckBox) view.findViewById(R.id.checkbox_interest_item2);
            item3 = (CheckBox) view.findViewById(R.id.checkbox_interest_item3);
            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), Constants.AvenirNextRegular);

            item1.setTypeface(typeface);
            item2.setTypeface(typeface);
            item3.setTypeface(typeface);
        }
    }

    private void interestSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.drawable.border_rounded_filled_image);
        checkBox.setTextColor(Color.parseColor("#ffffff"));
    }

    private void interestUnSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.drawable.border_rounded_corner_image);
        checkBox.setTextColor(Color.parseColor("#0ccdaa"));

    }

    public InterestFilter getFilter() {
        return mFilter;
    }

    public class InterestFilter extends Filter {
        private InterestAdapter mAdapter;

        private InterestFilter(InterestAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredInterest.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredInterest.addAll(interests);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final InterestModel interest : interests) {
                    if (interest.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredInterest.add(interest);
                    }
                }
            }
            System.out.println("Count Number " + filteredInterest.size());
            results.values = filteredInterest;
            results.count = filteredInterest.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<TagModel>) results.values).size());
            this.mAdapter.setModified();
        }
    }
}
