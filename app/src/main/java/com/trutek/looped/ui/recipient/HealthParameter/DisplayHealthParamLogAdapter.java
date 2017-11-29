package com.trutek.looped.ui.recipient.healthparameter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.utils.DialogUtil;

import org.w3c.dom.Text;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
/**
 * Created by Amrit on 04/12/16.
 */
public class DisplayHealthParamLogAdapter extends RecyclerView.Adapter<DisplayHealthParamLogAdapter.ViewHolder> {
    private List<HealthChartLogsModel> healthChartLogsModels;
    private OnActionListener<HealthChartLogsModel> healthParamLogValue;
    HealthChartModel mHealthChartModel;
    private Text textView_title;
    public DisplayHealthParamLogAdapter(List<HealthChartLogsModel> healthChartLogsModels,
                                        OnActionListener<HealthChartLogsModel> healthParamLogValue) {
        this.healthChartLogsModels = healthChartLogsModels;
        this.healthParamLogValue = healthParamLogValue;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_health_chart_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HealthChartLogsModel healthChartLogsModel = healthChartLogsModels.get(position);
        String reading = healthChartLogsModel.getValue() + " " + healthChartLogsModel.getUnit();
        holder.textView_reading.setText(reading);
        holder.textView_time.setText(DateHelper.stringify(healthChartLogsModel.getCreate_At(), "hh:mm aa"));
        holder.textView_createdBy.setText("");
        holder.imageView_nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthParamLogValue.notify(healthChartLogsModel);
            }
        });
    }
    public void modified() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return healthChartLogsModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_reading, textView_createdBy, textView_time;
        ImageView imageView_nextArrow;
        public ViewHolder(View itemView) {super(itemView);
            textView_reading = (TextView) itemView.findViewById(R.id.item_dhcl_textView_reading);
            textView_createdBy = (TextView) itemView.findViewById(R.id.item_dhcl_textView_createdBy);
            textView_time = (TextView) itemView.findViewById(R.id.item_dhcl_textView_time);
            imageView_nextArrow = (ImageView) itemView.findViewById(R.id.item_dhcl_imageView_nextArrow);
            setFonts(itemView);}

        void setFonts(View itemView) {
            Typeface avenirRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            textView_reading.setTypeface(avenirRegular);
            textView_time.setTypeface(avenirRegular);
            textView_createdBy.setTypeface(avenirRegular);
        }
    }


}
