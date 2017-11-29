package com.trutek.looped.ui.recipient.recipient.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.ArrayList;
import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder> {

    List<DiseaseModel> mDiseaseModels;
    List<DiseaseModel> mFilteredDiseaseModels;
    ArrayList<ItemRow> _itemRows;

    private DiseaseFilter mFilter;

    OnActionListener<DiseaseModel> mActionListenerSelect, mActionListenerDeselect;

    public DiseaseAdapter(List<DiseaseModel> mDiseaseModels,
                          List<DiseaseModel> mFilteredDiseaseModels,
                          OnActionListener<DiseaseModel> mActionListenerSelect,
                          OnActionListener<DiseaseModel> mActionListenerDeselect) {

        this.mDiseaseModels = mDiseaseModels;
        this.mFilteredDiseaseModels = mFilteredDiseaseModels;
        this.mActionListenerSelect = mActionListenerSelect;
        this.mActionListenerDeselect = mActionListenerDeselect;

        mFilter = new DiseaseFilter(this);

        _itemRows = new ArrayList<>();

        ItemRow row = new ItemRow();
        if(this.mFilteredDiseaseModels.size()>0){
            _itemRows.add(row);
        }

        for(DiseaseModel item: this.mFilteredDiseaseModels){
            if(!row.add(item)) {
                row = new ItemRow();
                row.add(item);
                _itemRows.add(row);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemRow row = _itemRows.get(position);

        if(row.item1 != null){
            if(row.item1.isSelected()){
                interestSelected(holder.checkBox_item1);
            } else {
                interestUnSelected(holder.checkBox_item1);
            }

            holder.checkBox_item1.setText(row.item1.getName());
            holder.checkBox_item1.setVisibility(View.VISIBLE);

            holder.checkBox_item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mActionListenerSelect !=  null && mActionListenerDeselect != null){
                        if (row.item1.isSelected()) {
                            interestUnSelected(holder.checkBox_item1);
                            row.item1.setSelected(false);
                            mActionListenerDeselect.notify(row.item1);
                        } else {
                            interestSelected(holder.checkBox_item1);
                            row.item1.setSelected(true);
                            mActionListenerSelect.notify(row.item1);
                        }
                    }
                }
            });
        } else {
            holder.checkBox_item1.setVisibility(View.GONE);
        }

        if(row.item2 != null){
            if(row.item2.isSelected()){
                interestSelected(holder.checkBox_item2);
            } else {
                interestUnSelected(holder.checkBox_item2);
            }
            holder.checkBox_item2.setText(row.item2.getName());
            holder.checkBox_item2.setVisibility(View.VISIBLE);

            holder.checkBox_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mActionListenerSelect !=  null && mActionListenerDeselect != null) {
                        if (row.item2.isSelected()) {
                            interestUnSelected(holder.checkBox_item2);
                            row.item2.setSelected(false);
                            mActionListenerDeselect.notify(row.item2);
                        } else {
                            interestSelected(holder.checkBox_item2);
                            row.item2.setSelected(true);
                            mActionListenerSelect.notify(row.item2);
                        }
                    }
                }
            });
        } else {
            holder.checkBox_item2.setVisibility(View.GONE);
        }

        if(row.item3 != null){
            holder.checkBox_item3.setChecked(row.item3.isSelected());
            if(row.item3.isSelected()){
                interestSelected(holder.checkBox_item3);
            } else {
                interestUnSelected(holder.checkBox_item3);
            }
            holder.checkBox_item3.setText(row.item3.getName());
            holder.checkBox_item3.setVisibility(View.VISIBLE);

            holder.checkBox_item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mActionListenerSelect !=  null && mActionListenerDeselect != null) {
                        if (row.item3.isSelected()) {
                            interestUnSelected(holder.checkBox_item3);
                            row.item3.setSelected(false);
                            mActionListenerDeselect.notify(row.item3);
                        } else {
                            interestSelected(holder.checkBox_item3);
                            row.item3.setSelected(true);
                            mActionListenerSelect.notify(row.item3);
                        }
                    }
                }
            });
        } else {
            holder.checkBox_item3.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return _itemRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox_item1, checkBox_item2, checkBox_item3;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox_item1 = (CheckBox) itemView.findViewById(R.id.checkbox_interest_item1);
            checkBox_item2 = (CheckBox) itemView.findViewById(R.id.checkbox_interest_item2);
            checkBox_item3 = (CheckBox) itemView.findViewById(R.id.checkbox_interest_item3);
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);

            checkBox_item1.setTypeface(typeface);
            checkBox_item2.setTypeface(typeface);
            checkBox_item3.setTypeface(typeface);
        }
    }

    private class ItemRow {
        private int maxLength = 30;
        DiseaseModel item1;
        DiseaseModel item2;
        DiseaseModel item3;

        private int getLength() {
            int length = 0;
            if (item1 != null) {
                length += item1.getName().length();
            }

            if (item2 != null) {
                length += item2.getName().length();
            }

            if (item3 != null) {
                length += item3.getName().length();
            }

            return length;
        }

        private boolean canAdd(DiseaseModel item) {
            if (item3 != null) {
                return false;
            }

            if (getLength() + item.getName().length() > maxLength)
                return false;
            else {

                return true;
            }
        }

        public boolean add(DiseaseModel item) {
            if (!canAdd(item)) {
                return false;
            }
            if (item1 == null) {
                item1 = item;
                return true;
            } else if (item2 == null) {
                item2 = item;
                return true;
            } else if (item3 == null) {
                item3 = item;
                return true;
            }
            return false;

        }
    }

    public void setModified() {

        _itemRows.clear();
        ItemRow row = new ItemRow();
        if (mFilteredDiseaseModels.size() > 0) {
            _itemRows.add(row);
        }

        for (DiseaseModel item : mFilteredDiseaseModels) {
            if (!row.add(item)) {
                row = new ItemRow();
                row.add(item);
                _itemRows.add(row);
            }
        }
        this.notifyDataSetChanged();
    }

    private void interestSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.color.dark_gray);
        checkBox.setTextColor(Color.WHITE);
    }

    private void interestUnSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.drawable.border_interest_unclicked);
        checkBox.setTextColor(Color.BLACK);

    }

    public DiseaseFilter getFilter() {
        return mFilter;
    }

    public class DiseaseFilter extends Filter {
        private DiseaseAdapter mAdapter;

        private DiseaseFilter(DiseaseAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredDiseaseModels.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                mFilteredDiseaseModels.addAll(mDiseaseModels);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final DiseaseModel interest : mDiseaseModels) {
                    if (interest.getName().toLowerCase().startsWith(filterPattern)) {
                        mFilteredDiseaseModels.add(interest);
                    }
                }
            }
            System.out.println("Count Number " + mFilteredDiseaseModels.size());
            results.values = mFilteredDiseaseModels;
            results.count = mFilteredDiseaseModels.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<DiseaseModel>) results.values).size());
            this.mAdapter.setModified();
        }
    }


}
