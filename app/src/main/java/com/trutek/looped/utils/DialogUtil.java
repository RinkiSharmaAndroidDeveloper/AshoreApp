package com.trutek.looped.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;

import java.util.ArrayList;


/**
 * Created by Amrit on 07/11/16.
 */
public class DialogUtil {

    static ProgressDialog progressDialog;
    public static int OPTION_REPORT_ABUSE = 1;
    public static int OPTION_UNFRIEND = 2;
    public static int OPTION_CANCEL = 3;
    public static int OPTION_DELETE = 4;

   /* public static void showAddRecipientDialog(FragmentActivity fragmentActivity, String text,
                                              final AsyncResult<Boolean> createRecipient, boolean isCanceledOnTouchOutside) {
        final Dialog addRecipient = new Dialog(fragmentActivity);
        addRecipient.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addRecipient.setContentView(R.layout.dialog_add_recipient);
        addRecipient.setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        TextView textView_title, textView_skip;
        Button button_new, button_existing;

        Typeface avenirNextRegular = Typeface.createFromAsset(fragmentActivity.getAssets(), Constants.AvenirNextRegular);

        textView_title = (TextView) addRecipient.findViewById(R.id.dialog_add_recipient_textView_title);
        textView_skip = (TextView) addRecipient.findViewById(R.id.dialog_add_recipient_textView_skip);

        button_new = (Button) addRecipient.findViewById(R.id.dialog_add_recipient_button_new);
        button_existing = (Button) addRecipient.findViewById(R.id.dialog_add_recipient_button_existing);

        if (null != text) {
            textView_title.setText(text);
        }

        textView_title.setTypeface(avenirNextRegular);
        textView_skip.setTypeface(avenirNextRegular);
        button_new.setTypeface(avenirNextRegular);
        button_existing.setTypeface(avenirNextRegular);

        fragmentActivity.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecipient.success(true);
                addRecipient.dismiss();
            }
        });

        button_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecipient.success(false);
                addRecipient.dismiss();

            }
        });

        textView_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRecipient.error("");
                addRecipient.dismiss();
            }
        });

        addRecipient.show();

    }
*/
    public static void showDiscoverDialog(FragmentActivity activity, final AsyncNotify callBack) {
        final Dialog discoverDialog = new Dialog(activity);
        discoverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        discoverDialog.setCanceledOnTouchOutside(false);
        discoverDialog.setContentView(R.layout.discoverpopup);

        Button button_discover;
        Button button_skip;
        final TextView text_line_one, text_line_two, text_line_three;

        button_discover = (Button) discoverDialog.findViewById(R.id.popup_discover_button);
        text_line_one = (TextView) discoverDialog.findViewById(R.id.text_popup_discover_line_one);
        text_line_two = (TextView) discoverDialog.findViewById(R.id.text_popup_discover_line_two);
        text_line_three = (TextView) discoverDialog.findViewById(R.id.text_popup_discover_line_three);
        button_skip = (Button) discoverDialog.findViewById(R.id.popup_button_skip);

        Typeface avenirNextRegular = Typeface.createFromAsset(activity.getAssets(), Constants.AvenirNextRegular);
        button_discover.setTypeface(avenirNextRegular);
        button_skip.setTypeface(avenirNextRegular);
        text_line_one.setTypeface(avenirNextRegular);
        text_line_two.setTypeface(avenirNextRegular);
        text_line_three.setTypeface(avenirNextRegular);
        discoverDialog.show();
        button_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.success();
                discoverDialog.dismiss();
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.error("");
                discoverDialog.dismiss();
            }
        });
    }

    public static void showInviteDialog(FragmentActivity fragmentActivity, final AsyncNotify callback) {

        final Dialog inviteDialog = new Dialog(fragmentActivity);
        inviteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        inviteDialog.setCanceledOnTouchOutside(false);
        inviteDialog.setContentView(R.layout.invite_pop_up);

        Button button_invite;
        Button button_skip;
        final TextView text_line_one, text_line_two, text_line_three;

        button_invite = (Button) inviteDialog.findViewById(R.id.popup_invite_button);
        text_line_one = (TextView) inviteDialog.findViewById(R.id.text_popup_invite_line_one);
        text_line_two = (TextView) inviteDialog.findViewById(R.id.text_popup_invite_line_two);
        text_line_three = (TextView) inviteDialog.findViewById(R.id.text_popup_invite_line_three);
        button_skip = (Button) inviteDialog.findViewById(R.id.invite_button_skip);

        Typeface avenirNextRegular = Typeface.createFromAsset(fragmentActivity.getAssets(), Constants.AvenirNextRegular);
        button_invite.setTypeface(avenirNextRegular);
        button_skip.setTypeface(avenirNextRegular);
        text_line_one.setTypeface(avenirNextRegular);
        text_line_two.setTypeface(avenirNextRegular);
        text_line_three.setTypeface(avenirNextRegular);
        button_skip.setTypeface(avenirNextRegular);

        inviteDialog.show();

        button_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.success();
                inviteDialog.dismiss();

            }
        });
        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.error("");
                inviteDialog.dismiss();

            }
        });

    }

    public static void showGenderDialog(FragmentActivity fragmentActivity, final AsyncResult<String> callback) {

        final Dialog genderDialog = new Dialog(fragmentActivity);
        genderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        genderDialog.setCanceledOnTouchOutside(false);
        genderDialog.setContentView(R.layout.custom_gender_list);
        final TextView textMale, textFemale, textNone, textOthers;
        textMale = (TextView) genderDialog.findViewById(R.id.text_male);
        textFemale = (TextView) genderDialog.findViewById(R.id.text_female);
        textNone = (TextView) genderDialog.findViewById(R.id.text_none);
        textOthers = (TextView) genderDialog.findViewById(R.id.text_others);
        Typeface lato_regular = Typeface.createFromAsset(fragmentActivity.getAssets(), Constants.AvenirNextRegular);
        textMale.setTypeface(lato_regular);
        textFemale.setTypeface(lato_regular);
        textNone.setTypeface(lato_regular);
        textOthers.setTypeface(lato_regular);
        genderDialog.show();

        textMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.success("Male");
                genderDialog.dismiss();
            }
        });
        textFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.success("Female");
                genderDialog.dismiss();
            }
        });
        textNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.success("None");
                genderDialog.dismiss();
            }
        });
        textOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.success("Other");
                genderDialog.dismiss();
            }
        });

    }

    public static void showLogDialog(FragmentActivity fragmentActivity, HealthParameterModel healthParameterModel,
                                     @Nullable HealthChartLogsModel healthChartLogsModel, final AsyncResult<Integer> callback) {
        final Dialog logDialog = new Dialog(fragmentActivity, R.style.Theme_CustomDialog);

        logDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logDialog.setContentView(R.layout.dialog_health_param_log);

        TextView textView_title, textView_done, textView_unit;
        ImageView imageView_delete, imageView_star;

        final EditText editText_reading;
        Typeface avenirNextRegular = Typeface.createFromAsset(fragmentActivity.getAssets(), Constants.AvenirNextRegular);
        textView_title = (TextView) logDialog.findViewById(R.id.dialog_hpl_textView_title);
        textView_unit = (TextView) logDialog.findViewById(R.id.dialog_hpl_textView_unit);
        textView_done = (TextView) logDialog.findViewById(R.id.dialog_hpl_textView_done);
        editText_reading = (EditText) logDialog.findViewById(R.id.dialog_hpl_editText_reading);
        imageView_delete = (ImageView) logDialog.findViewById(R.id.dialog_hpl_imageView_delete);//TODO imageView_delete
        imageView_star = (ImageView) logDialog.findViewById(R.id.dialog_hpl_icon_star);

        if (null != healthChartLogsModel) {
            editText_reading.setText(String.valueOf(healthChartLogsModel.getValue()));
            editText_reading.setSelection(editText_reading.getText().length());
        }
        textView_title.setTypeface(avenirNextRegular);
        textView_unit.setTypeface(avenirNextRegular);
        textView_done.setTypeface(avenirNextRegular);
        editText_reading.setTypeface(avenirNextRegular);
        textView_unit.setText(healthParameterModel.getUnits().get(0));
        textView_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText_reading.getText().toString().isEmpty()) {
                    callback.success(0);
                } else {
                    callback.success(Integer.valueOf(editText_reading.getText().toString()));
                }
                logDialog.dismiss();
            }
        });
        logDialog.show();

    }

    public static void showAlertDialog(@NonNull FragmentActivity fragmentActivity, @NonNull String titleText, @NonNull String message, @Nullable final AsyncNotify notifyCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
        builder.setTitle(titleText)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(null != notifyCallback){
                            notifyCallback.success();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(null != notifyCallback){
                            notifyCallback.error("");
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showOptionsDialog(FragmentActivity fragmentActivity,
                                         final AsyncResult<Integer> result,
                                         ArrayList<Integer> optionsToBeHide){

        final Dialog dialog = new Dialog(fragmentActivity,R.style.Theme_CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if(null != window) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.setContentView(R.layout.dialog_option);

        TextView textView_reportAbuse, textView_unFriend, textView_cancel, textView_delete;

        textView_reportAbuse = (TextView) dialog.findViewById(R.id.dialog_option_textView_reportAbuse);
        textView_unFriend = (TextView) dialog.findViewById(R.id.dialog_option_textView_unFriend);
        textView_delete = (TextView) dialog.findViewById(R.id.dialog_option_textView_delete);
        textView_cancel = (TextView) dialog.findViewById(R.id.dialog_option_textView_cancel);

        Typeface avenirRegular = Typeface.createFromAsset(fragmentActivity.getAssets(),Constants.AvenirNextRegular);
        textView_reportAbuse.setTypeface(avenirRegular);
        textView_unFriend.setTypeface(avenirRegular);
        textView_cancel.setTypeface(avenirRegular);

        if(optionsToBeHide.contains(OPTION_REPORT_ABUSE)){
            textView_reportAbuse.setVisibility(View.GONE);
        }

        if(optionsToBeHide.contains(OPTION_UNFRIEND)){
            textView_unFriend.setVisibility(View.GONE);
        }

        if (optionsToBeHide.contains(OPTION_DELETE)){
            textView_delete.setVisibility(View.GONE);
        }

        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.success(OPTION_CANCEL);
                dialog.dismiss();
            }
        });

        textView_unFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.success(OPTION_UNFRIEND);
                dialog.dismiss();

            }
        });

        textView_reportAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.success(OPTION_REPORT_ABUSE);
                dialog.dismiss();
            }
        });

        textView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.success(OPTION_DELETE);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public static void showProgress(String message, FragmentActivity fragmentActivity){

        try {

            progressDialog = new ProgressDialog(fragmentActivity);
            if(null != message) {
                progressDialog.setMessage(message);
            }else{
                progressDialog.setMessage("Please wait...");
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static void hideProgress(){
        try{
            if(null != progressDialog){
                progressDialog.dismiss();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
