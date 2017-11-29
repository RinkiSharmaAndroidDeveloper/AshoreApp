package com.trutek.looped.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.ui.activity.edit.EditActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;

import javax.inject.Inject;

/**
 * Created by Rinki on 4/18/2017.
 */
public class ContactUsActivity extends BaseAppCompatActivity implements View.OnClickListener{
    TextView done;
    EditText editText;
    ImageView back_arrow;
    @Inject
    IReportBugService reportBugService;
    @Override
    protected int getContentResId() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        back_arrow = (ImageView) findViewById(R.id.starred_back);
        done = (TextView) findViewById(R.id.contact_textView_done);
        editText = (EditText) findViewById(R.id.cotact_us_editText);

        back_arrow.setOnClickListener(this);
        done.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.starred_back):
                finish();
                break;
            case (R.id.contact_textView_done):
                if (editText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Write your review", Toast.LENGTH_LONG).show();
                } else {
                    ReportBugModel reportBug = new ReportBugModel();
                    reportBug.description = editText.getText().toString();
                    reportBug(reportBug);
                }
                break;
        }
    }
    private void reportBug(ReportBugModel model) {

        reportBugService.reportBug(model, new AsyncResult<ReportBugModel>() {
            @Override
            public void success(ReportBugModel reportBugModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Thanks");
                        finish();
                    }
                });
            }

            @Override
            public void error(final String error) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }
}
