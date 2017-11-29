package com.trutek.looped.ui.authenticate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.ScheduleModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.authenticate.SignupLocationInterestActivity;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rinki on 1/19/2017.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<CategoryModel> categoryModelList;
    private AsyncResult<CategoryModel> asyncResult_selectedCategory;


    public CategoryAdapter(List<CategoryModel> categoryModelList, AsyncResult<CategoryModel> asyncResult_selectedCategory) {
        this.categoryModelList = categoryModelList;

        this.asyncResult_selectedCategory = asyncResult_selectedCategory;
    }
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signup_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, final int position) {
        final CategoryModel model = categoryModelList.get(position);
        holder.category_name.setText(model.getName());

        if(model.isSelected()){
            holder.imageView_select.setVisibility(View.VISIBLE);
        }else{
            holder.imageView_select.setVisibility(View.GONE);
        }

        switch (position % 4){
            case 0:holder.category_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_first));
                break;
            case 1: holder.category_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                break;
            case 2: holder.category_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                break;
            case 3: holder.category_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                break;
            default: holder.category_image.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_first));
                break;
        }

        holder.category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.isSelected()){
                    model.setSelected(false);
                    asyncResult_selectedCategory.error(model.getServerId());
                    holder.imageView_select.setVisibility(View.GONE);
                }else {
                    model.setSelected(true);
                    asyncResult_selectedCategory.success(model);
                    holder.imageView_select.setVisibility(View.VISIBLE);
                }
            }
        });


       /* if (model.getPicUrl() != null && !model.getPicUrl().isEmpty() && model.getPicUrl().contains("http")) {
            displayImageByUrl(model.getPicUrl(), holder);
        }

        holder.category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              asyncResult_selectedCategory.success(model);
            }
        });*/
    }
    private void displayImageByUrl(String publicUrl, ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(publicUrl, viewHolder.category_image,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }


    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public void addAllToAdapter(List<CategoryModel> categoryModels) {
        if (categoryModels!=null){
            categoryModelList.clear();
            categoryModelList.addAll(categoryModels);
            notifyDataSetChanged();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_name;
        MaskedImageView category_image;
        ImageView  imageView_select;

        Typeface avenirRegular;

        public ViewHolder(View itemView) {
            super(itemView);
            avenirRegular = Typeface.createFromAsset(itemView.getResources().getAssets(), Constants.AvenirNextRegular);
            category_image = (MaskedImageView) itemView.findViewById(R.id.category_image);
            category_name = (TextView) itemView.findViewById(R.id.category_name);
            imageView_select = (ImageView) itemView.findViewById(R.id.item_category_imageView_select);
            category_name.setTypeface(avenirRegular);

        }
    }
}
