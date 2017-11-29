package com.trutek.looped.ui.profile.create.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private List<ItemRow> _itemRows ;
    private List<TagModel> tags;
    private List<TagModel> filteredTags;
    private static OnActionListener<TagModel> onActionListener;
    private static OnActionListener<TagModel> onDeSelectListener;

    private TagFilter mFilter;

    private class ItemRow {
        private int maxLength = 30;
        TagModel item1;
        TagModel item2;
        TagModel item3;

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

        private boolean canAdd(TagModel item) {
            if(item3 != null){
                return false;
            }

            if(getLength() + item.name.length() >  maxLength)
                return false;
            else {

                return true;
            }
        }

        public boolean add(TagModel item){
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

    public TagAdapter(List<TagModel> tags,
                      List<TagModel> filteredTags,
                      OnActionListener<TagModel> onActionListener,
                      OnActionListener<TagModel> onDeSelectListener) {

        this.tags = tags;
        this.filteredTags = filteredTags;
        TagAdapter.onActionListener = onActionListener;
        TagAdapter.onDeSelectListener = onDeSelectListener;
        mFilter = new TagFilter(this);

        _itemRows = new ArrayList<>();

        ItemRow row = new ItemRow();
        if(filteredTags.size()>0){
            _itemRows.add(row);
        }

        for(TagModel item: filteredTags){
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
        if(filteredTags.size()>0){
            _itemRows.add(row);
        }

        for(TagModel item: filteredTags){
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

    public TagModel getInterestId(String interestName) {
        for (TagModel item : filteredTags) {
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
        checkBox.setBackgroundResource(R.color.dark_gray);
        checkBox.setTextColor(Color.WHITE);
    }

    private void interestUnSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.drawable.border_interest_unclicked);
        checkBox.setTextColor(Color.BLACK);

    }

    public TagFilter getFilter() {
        return mFilter;
    }

    public class TagFilter extends Filter {
        private TagAdapter mAdapter;

        private TagFilter(TagAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredTags.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredTags.addAll(tags);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final TagModel tag : tags) {
                    if (tag.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredTags.add(tag);
                    }
                }
            }
            System.out.println("Count Number " + filteredTags.size());
            results.values = filteredTags;
            results.count = filteredTags.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<TagModel>) results.values).size());
            this.mAdapter.setModified();
        }
    }
}
