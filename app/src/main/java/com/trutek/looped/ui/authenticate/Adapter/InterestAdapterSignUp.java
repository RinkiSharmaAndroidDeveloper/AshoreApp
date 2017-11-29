package com.trutek.looped.ui.authenticate.Adapter;

import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rinki on 1/19/2017.
 */
public class InterestAdapterSignUp extends RecyclerView.Adapter<InterestAdapterSignUp.ViewHolder> {

    private List<InterestModel> interestModels;
    public  OnActionListener<InterestModel> onActionListener;
    public  OnActionListener<InterestModel> onDeSelectListener;

    ArrayList<InterestModel> interests = new ArrayList<>();
    InterestAdapter interestAdapter;
    ArrayList<Boolean> showMore = new ArrayList<>();

    public InterestAdapterSignUp(List<InterestModel> interestModels, OnActionListener<InterestModel> onActionListener,
                                 OnActionListener<InterestModel> onDeSelectListener) {

        this.interestModels = interestModels;
        this.onActionListener = onActionListener;
        this.onDeSelectListener = onDeSelectListener;
        setShowMore();
    }

    @Override
    public InterestAdapterSignUp.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signup_interest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InterestAdapterSignUp.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final InterestModel model = interestModels.get(pos);

        holder.textView_title.setText(model.getName());
        if(showMore.get(position)) {
            getLimitedItem(12, pos);
            holder.textView_more.setText("More");
        }else{
            getLimitedItem(-1,pos);
            holder.textView_more.setText("Less");
        }

        if(interests.size()<=0){
            holder.relativeLayout_footer.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.textView_default.setVisibility(View.VISIBLE);
        }else{
            holder.relativeLayout_footer.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.textView_default.setVisibility(View.GONE);
        }

        interestAdapter = new InterestAdapter(interests,interests,onActionListener,onDeSelectListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(interestAdapter);

        holder.textView_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showMore.get(pos)) {
                    getLimitedItem(-1,pos);
                    holder.textView_more.setText("More");
                    showMore.set(pos,false);
                }else {
                    getLimitedItem(12,pos);
                    holder.textView_more.setText("Less");
                    showMore.set(pos,true);

                }
                if(null !=interestAdapter) {
                    notifyItemChanged(pos);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return interestModels.size();
    }
    public void addAllToAdapter(List<InterestModel> interestModel) {
        if (interestModel != null) {
            interestModels.clear();
            interestModels.addAll(interestModel);
            setShowMore();
            notifyDataSetChanged();
        }
    }

    void getLimitedItem(int limit, int pos){
        interests.clear();
        if(limit == -1){
            interests.addAll(interestModels.get(pos).getInterests());
            return;
        }

        if(interestModels.size() == 0){
            return;
        }

        if(limit>interestModels.get(pos).getInterests().size()){
            limit = interestModels.get(pos).getInterests().size();
        }
        for (int i = 0; i<limit; i++){
            if(interestModels.get(pos).getInterests().size()>0) {
                interests.add(interestModels.get(pos).getInterests().get(i));
            }
        }
    }

    void setShowMore(){
        showMore.clear();
        for (InterestModel interestModel: interestModels) {
            showMore.add(true);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_title, textView_more,textView_default;
        ImageView imageView_more;
        RecyclerView recyclerView;
        RelativeLayout relativeLayout_footer;

        public ViewHolder(View itemView) {
            super(itemView);
            Typeface avenirRegular = Typeface.createFromAsset(itemView.getResources().getAssets(), Constants.AvenirNextRegular);
            textView_title = (TextView) itemView.findViewById(R.id.item_signup_interest_textView_cardTitle);
            textView_more = (TextView) itemView.findViewById(R.id.item_signup_interest_textView_more);
            imageView_more = (ImageView) itemView.findViewById(R.id.item_signup_interest_imageView_more);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.item_signup_interest_recyclerView);
            textView_default = (TextView) itemView.findViewById(R.id.item_signup_interest_textView_default);
            relativeLayout_footer = (RelativeLayout) itemView.findViewById(R.id.item_signup_interest_relativeLayout_footer);
            textView_title.setTypeface(avenirRegular);
            textView_more.setTypeface(avenirRegular);
            textView_default.setTypeface(avenirRegular);

        }
    }




}
