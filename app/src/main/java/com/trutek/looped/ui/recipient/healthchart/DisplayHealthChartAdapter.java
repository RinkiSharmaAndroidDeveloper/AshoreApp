package com.trutek.looped.ui.recipient.healthchart;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.DateHelper;

import java.util.List;

/**
 * Created by Amrit on 01/12/16.
 */
public class DisplayHealthChartAdapter extends RecyclerView.Adapter<DisplayHealthChartAdapter.ViewHolder> {


    List<HealthChartModel> mHealthParameterModels;
    OnActionListener<HealthChartModel> mCreateHealthChartLogListener;
    OnActionListener<HealthChartModel> mSelectedHealthChartListener;

    public DisplayHealthChartAdapter(List<HealthChartModel> healthParameterModels,
                                     OnActionListener<HealthChartModel> selectedHealthChartListener,
                                     OnActionListener<HealthChartModel> createHealthChartLogListener) {
        mHealthParameterModels = healthParameterModels;
        mSelectedHealthChartListener = selectedHealthChartListener;
        mCreateHealthChartLogListener = createHealthChartLogListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_health_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HealthChartModel healthChartModel = mHealthParameterModels.get(position);
        List<HealthChartLogsModel> chartLogsModels = healthChartModel.getLogs();
        holder.textView_healthParamName.setText(healthChartModel.getHealthParam().getName());

        if (chartLogsModels.size() > 0) {
            showLogs(holder, chartLogsModels);
        } else {
            hideLogs(holder);
        }

        holder.imageView_addNewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreateHealthChartLogListener.notify(healthChartModel);
            }
        });

        holder.textView_healthParamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedHealthChartListener.notify(healthChartModel);
            }
        });

        holder.imageView_line1_nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView_healthParamName.performClick();
            }
        });

        holder.imageView_line2_nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView_healthParamName.performClick();
            }
        });

        holder.imageView_line3_nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView_healthParamName.performClick();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mHealthParameterModels.size();
    }

    public void modified() {
        notifyDataSetChanged();
    }

    void hideLogs(ViewHolder holder) {
        holder.linearLayout_logs.setVisibility(View.GONE);
    }

    void showLogs(ViewHolder holder, List<HealthChartLogsModel> chartLogsModels) {
        holder.linearLayout_logs.setVisibility(View.VISIBLE);
        for (int i = 0; i < chartLogsModels.size(); i++) {
            switch (i) {
                case 0:
                    holder.linearLayout_logs_line1.setVisibility(View.VISIBLE);
                    holder.textView_healthParam_line1_dateTime.setText(DateHelper.stringify(chartLogsModels.get(0).getCreate_At(), "MM/dd/yyyy, hh:mm aa"));
                    holder.textView_healthParam_line1_reading.setText(String.valueOf(chartLogsModels.get(0).getValue()));
                    break;
                case 1:
                    holder.linearLayout_logs_line2.setVisibility(View.VISIBLE);
                    holder.textView_healthParam_line2_dateTime.setText(DateHelper.stringify(chartLogsModels.get(1).getCreate_At(), "MM/dd/yyyy, hh:mm aa"));
                    holder.textView_healthParam_line2_reading.setText(String.valueOf(chartLogsModels.get(1).getValue()));
                    break;
                case 2:
                    holder.linearLayout_logs_line3.setVisibility(View.VISIBLE);
                    holder.textView_healthParam_line3_dateTime.setText(DateHelper.stringify(chartLogsModels.get(2).getCreate_At(), "MM/dd/yyyy, hh:mm aa"));
                    holder.textView_healthParam_line3_reading.setText(String.valueOf(chartLogsModels.get(2).getValue()));
                    break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_healthParamName, textView_healthParam_line1_dateTime, textView_healthParam_line1_reading,
                textView_healthParam_line2_dateTime, textView_healthParam_line2_reading,
                textView_healthParam_line3_dateTime, textView_healthParam_line3_reading;
        ImageView imageView_addNewLog,imageView_line1_nextArrow,imageView_line2_nextArrow,imageView_line3_nextArrow;

        LinearLayout linearLayout_logs, linearLayout_logs_line1, linearLayout_logs_line2, linearLayout_logs_line3;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_healthParamName = (TextView) itemView.findViewById(R.id.item_dhc_textView_name);
            textView_healthParam_line1_dateTime = (TextView) itemView.findViewById(R.id.item_dhc_textView_line1_dateTime);
            textView_healthParam_line1_reading = (TextView) itemView.findViewById(R.id.item_dhc_textView_line1_reading);
            textView_healthParam_line2_dateTime = (TextView) itemView.findViewById(R.id.item_dhc_textView_line2_dateTime);
            textView_healthParam_line2_reading = (TextView) itemView.findViewById(R.id.item_dhc_textView_line2_reading);
            textView_healthParam_line3_dateTime = (TextView) itemView.findViewById(R.id.item_dhc_textView_line3_dateTime);
            textView_healthParam_line3_reading = (TextView) itemView.findViewById(R.id.item_dhc_textView_line3_reading);

            linearLayout_logs = (LinearLayout) itemView.findViewById(R.id.item_dhc_linearLayout_logs);
            linearLayout_logs_line1 = (LinearLayout) itemView.findViewById(R.id.item_dhc_linearLayout_logs_line1);
            linearLayout_logs_line2 = (LinearLayout) itemView.findViewById(R.id.item_dhc_linearLayout_logs_line2);
            linearLayout_logs_line3 = (LinearLayout) itemView.findViewById(R.id.item_dhc_linearLayout_logs_line3);

            imageView_addNewLog = (ImageView) itemView.findViewById(R.id.item_dhc_imageView_add);
            imageView_line1_nextArrow = (ImageView)  itemView.findViewById(R.id.item_dhc_imageView_line1_nextArrow);
            imageView_line2_nextArrow = (ImageView)  itemView.findViewById(R.id.item_dhc_imageView_line2_nextArrow);
            imageView_line3_nextArrow = (ImageView)  itemView.findViewById(R.id.item_dhc_imageView_line3_nextArrow);
            setFonts(itemView);


        }

        void setFonts(View itemView) {
            Typeface avenirNextRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), Constants.AvenirNextRegular);
            textView_healthParamName.setTypeface(avenirNextRegular);
            textView_healthParam_line1_dateTime.setTypeface(avenirNextRegular);
            textView_healthParam_line1_reading.setTypeface(avenirNextRegular);
            textView_healthParam_line2_dateTime.setTypeface(avenirNextRegular);
            textView_healthParam_line2_reading.setTypeface(avenirNextRegular);
            textView_healthParam_line3_dateTime.setTypeface(avenirNextRegular);
            textView_healthParam_line3_reading.setTypeface(avenirNextRegular);
        }
    }


}
