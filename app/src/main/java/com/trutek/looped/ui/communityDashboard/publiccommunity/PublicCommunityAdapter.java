package com.trutek.looped.ui.communityDashboard.publiccommunity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amrit on 21/01/17.
 */
public class PublicCommunityAdapter extends RecyclerView.Adapter<PublicCommunityAdapter.ViewHolder> {

    private List<CommunityModel> communityModels;
    private List<CommunityModel> filteredCommunityModel;
    private AsyncResult<Integer> notify;
    private AsyncResult<CommunityModel> asyncResult_openCommunity;
    private PublicCommunityFilter mFilter;
    int clickedItemPosition = -1;
    public PublicCommunityAdapter(List<CommunityModel> communityModels,
                                  List<CommunityModel> filteredCommunityModel,
                                  AsyncResult<Integer> notify, AsyncResult<CommunityModel> asyncResult_openCommunity) {
        this.communityModels = communityModels;
        this.filteredCommunityModel = filteredCommunityModel;
        this.notify = notify;
        this.asyncResult_openCommunity = asyncResult_openCommunity;
        mFilter = new PublicCommunityFilter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_public_community,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final CommunityModel communityModel = filteredCommunityModel.get(position);

        holder.textView_communityName.setText(communityModel.getSubject());
        holder.textView_communityDescription.setText(communityModel.getBody());

        if(communityModel.isSelected){
            holder.button_join.setText(holder.itemView.getResources().getString(R.string.ipc_text_leave));
        }else{
            holder.button_join.setText(holder.itemView.getResources().getString(R.string.ipc_text_join));
        }

        if(communityModel.picUrl != null && !communityModel.picUrl.isEmpty() && communityModel.picUrl.contains("http")){
            displayImageByUrl(communityModel.picUrl, holder);
        } else {
            switch (position % 3){
                case 0:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
                case 1:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_third));
                    break;
                case 2:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_forth));
                    break;
                default:
                    holder.imageView_communityImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.background_round_color_second));
                    break;
            }
        }

        holder.button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify.success(pos);
               /* if(communityModel.isSelected){
                    communityModel.isSelected = false;
                    notify.success(pos);
                }else{
                    communityModel.isSelected = true;
                    notify.success(pos);
                }*/
            }
        });

        holder.imageView_communityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedItemPosition(pos);
                asyncResult_openCommunity.success(communityModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredCommunityModel.size();
    }
    public int getClickedItemPosition() {
        return clickedItemPosition;
    }
    public void setClickedItemPosition(int clickedItemPosition) {
        this.clickedItemPosition = clickedItemPosition;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        MaskedImageView imageView_communityImage;
        TextView textView_communityName,textView_communityDescription;
        Button button_join;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView_communityImage = (MaskedImageView) itemView.findViewById(R.id.ipc_imageView_communityImage);
            textView_communityName = (TextView) itemView.findViewById(R.id.ipc_textView_community_name);
            textView_communityDescription = (TextView) itemView.findViewById(R.id.ipc_textView_community_description);
            button_join = (Button) itemView.findViewById(R.id.ipc_button_join);
        }

    }

    private void displayImageByUrl(String publicUrl, final ViewHolder viewHolder) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(publicUrl, viewHolder.imageView_communityImage,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public PublicCommunityFilter getFilter() {
        return mFilter;
    }

    public class PublicCommunityFilter extends Filter {
        private PublicCommunityAdapter mAdapter;

        private PublicCommunityFilter(PublicCommunityAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredCommunityModel.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredCommunityModel.addAll(communityModels);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final CommunityModel model : communityModels) {
                    if (model.getSubject().toLowerCase().startsWith(filterPattern)) {
                        filteredCommunityModel.add(model);
                    }
                }
            }
            System.out.println("Count Number " + filteredCommunityModel.size());
            results.values = filteredCommunityModel;
            results.count = filteredCommunityModel.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((ArrayList<CommunityModel>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
