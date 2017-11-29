package com.trutek.looped.ui.recipient.healthparameter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrit on 02/12/16.
 */
public class AddHealthParameterAdapter extends RecyclerView.Adapter<AddHealthParameterAdapter.ViewHolder> {

    final static String ERROR_ALREADY_SELECTED = "Health parameter already selected";
    List<ItemRow> _itemRows;

    List<HealthParameterModel> mHealthParameterModels;
    List<HealthParameterModel> mFilteredHealthParameterModels;
    AsyncResult<HealthParameterModel> mListenerAddHealthParameter;

    private HealthParameterFilter mFilter;


    public AddHealthParameterAdapter(List<HealthParameterModel> healthParameterModels,
                                     List<HealthParameterModel> filteredHealthParameterModels,
                                     AsyncResult<HealthParameterModel> listenerAddHealthParameter) {

        mHealthParameterModels = healthParameterModels;
        mFilteredHealthParameterModels = filteredHealthParameterModels;
        mListenerAddHealthParameter = listenerAddHealthParameter;
        mFilter = new HealthParameterFilter(this);

        _itemRows = new ArrayList<>();

        ItemRow row = new ItemRow();
        if(mFilteredHealthParameterModels.size()>0){
            _itemRows.add(row);
        }

        for(HealthParameterModel item: mFilteredHealthParameterModels){
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
        if(mFilteredHealthParameterModels.size()>0){
            _itemRows.add(row);
        }

        for(HealthParameterModel item: mFilteredHealthParameterModels){
            if(!row.add(item)) {
                row = new ItemRow();
                row.add(item);
                _itemRows.add(row);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ItemRow row = _itemRows.get(position);

        if(row.item1 != null){

            if(row.item1.isSelected){
                parameterSelected(holder.item1);
            } else {
                parameterUnSelected(holder.item1);
            }

            holder.item1.setText(row.item1.getName());
            holder.item1.setVisibility(View.VISIBLE);

            holder.item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != mListenerAddHealthParameter){
                        if(!row.item1.isSelected) {
                            row.item1.setSelected(true);
                            mListenerAddHealthParameter.success(row.item1);
                        }else{
                            mListenerAddHealthParameter.error(ERROR_ALREADY_SELECTED);
                        }
                    }
                }
            });
        } else {
            holder.item1.setVisibility(View.GONE);
        }

        if(row.item2 != null){
            if(row.item2.isSelected){
                parameterSelected(holder.item2);
            } else {
                parameterUnSelected(holder.item2);
            }
            holder.item2.setText(row.item2.getName());
            holder.item2.setVisibility(View.VISIBLE);

            holder.item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != mListenerAddHealthParameter){
                        if(!row.item2.isSelected) {
                            row.item2.setSelected(true);
                            mListenerAddHealthParameter.success(row.item2);
                        }else{
                            mListenerAddHealthParameter.error(ERROR_ALREADY_SELECTED);
                        }
                    }
                }
            });
        } else {
            holder.item2.setVisibility(View.GONE);
        }

        if(row.item3 != null){

            if(row.item3.isSelected){
                parameterSelected(holder.item3);
            } else {
                parameterUnSelected(holder.item3);
            }

            holder.item3.setText(row.item3.getName());
            holder.item3.setVisibility(View.VISIBLE);

            holder.item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != mListenerAddHealthParameter){
                        if(!row.item3.isSelected) {
                            row.item3.setSelected(true);
                            mListenerAddHealthParameter.success(row.item3);
                        }else{
                            mListenerAddHealthParameter.error(ERROR_ALREADY_SELECTED);
                        }
                    }
                }
            });
        } else {
            holder.item3.setVisibility(View.GONE);
        }
    }

    private void parameterSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.color.dark_gray);
        checkBox.setTextColor(Color.WHITE);
    }

    private void parameterUnSelected(CheckBox checkBox){
        checkBox.setBackgroundResource(R.drawable.border_interest_unclicked);
        checkBox.setTextColor(Color.BLACK);

    }

    @Override
    public int getItemCount() {
        return _itemRows.size();
    }

    public HealthParameterFilter getFilter() {
        return mFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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

    private class ItemRow {
        private int maxLength = 30;
        HealthParameterModel item1;
        HealthParameterModel item2;
        HealthParameterModel item3;

        private int getLength(){
            int length = 0;
            if(item1 != null) {
                length += item1.getName().length();
            }

            if(item2 != null) {
                length += item2.getName().length();
            }

            if(item3 != null) {
                length += item3.getName().length();
            }

            return length;
        }

        private boolean canAdd(HealthParameterModel item) {
            if(item3 != null){
                return false;
            }

            if(getLength() + item.getName().length() >  maxLength)
                return false;
            else {

                return true;
            }
        }

        public boolean add(HealthParameterModel item){
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

    public class HealthParameterFilter extends Filter {
        private AddHealthParameterAdapter mAdapter;

        private HealthParameterFilter(AddHealthParameterAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredHealthParameterModels.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                mFilteredHealthParameterModels.addAll(mHealthParameterModels);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final HealthParameterModel healthParameterModel : mHealthParameterModels) {
                    if (healthParameterModel.getName().toLowerCase().startsWith(filterPattern)) {
                        mFilteredHealthParameterModels.add(healthParameterModel);
                    }
                }
            }
            System.out.println("Count Number " + mFilteredHealthParameterModels.size());
            results.values = mFilteredHealthParameterModels;
            results.count = mFilteredHealthParameterModels.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<HealthParameterModel>) results.values).size());
            this.mAdapter.setModified();
        }
    }
}
