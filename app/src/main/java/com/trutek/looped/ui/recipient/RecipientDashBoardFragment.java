package com.trutek.looped.ui.recipient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IProviderService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.recipient.healthchart.DisplayHealthChartActivity;
import com.trutek.looped.ui.recipient.healthparameter.AddHealthParameterActivity;
import com.trutek.looped.ui.recipient.recipient.create.AddRecipientActivity;
import com.trutek.looped.ui.recipient.recipient.display.DisplayRecipientActivity;
import com.trutek.looped.ui.recipient.recipient.loops.DisplayLoopsActivity;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.ui.medicine.create.AddMedicineActivity;
import com.trutek.looped.ui.medicine.create.CreateMedicineActivity;

import java.util.ArrayList;

import javax.inject.Inject;

public class RecipientDashBoardFragment extends BaseV4Fragment implements View.OnClickListener {

    @Inject
    IRecipientService recipientService;

    @Inject
    IProfileService _ProfileService;
    @Inject
    IProviderService _ProviderService;


    private static final int REQUEST_DISPLAY = 1;

    private OnFragmentInteractionListener mListener;

    TextView textView_medication, textView_tracking, textView_budgeting, textView_notes,
            textView_history, textViewProfileName;
    ImageView imageView_medication, imageView_tracking, imageView_budgeting, imageView_notes,
            imageView_history;
    MaskedImageView imageView_profile;
    Button button_looped, button_provider;
    private RecipientModel recipient;
    private LinearLayout emptyLayout, mainLayout;

    BroadcastReceiver mRecipientBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recipient = getRecipientDetails();
            resolveEmptyContent();
        }
    };

    public RecipientDashBoardFragment() {
        // Required empty public constructor
    }

    public static RecipientDashBoardFragment newInstance(String param1, String param2) {
        RecipientDashBoardFragment fragment = new RecipientDashBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipient_dash_board, container, false);
        initViews(rootView);
        setFonts();
        listeners();
        createRecipientIfAny();
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void createRecipientIfAny() {
        RecipientModel model = recipientService.get(new PageQuery());
        if(model != null)
            recipientService.getRecipient(model.getServerId(), Constants.BROADCAST_RECIPIENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRecipientBroadcast, new IntentFilter(Constants.BROADCAST_RECIPIENT));
        recipient = getRecipientDetails();
        if(recipient != null){
            recipient.setProviders(getAllProviders(recipient.getId()));
        }
        resolveEmptyContent();
    }

    private ArrayList<ProviderModel> getAllProviders(Long recipientId) {
        return new ArrayList<ProviderModel>(_ProviderService.getProvidersLocally(recipientId));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.actionbar_text_recipient));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(mRecipientBroadcast, new IntentFilter(Constants.BROADCAST_RECIPIENT));
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRecipientBroadcast);
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRecipientBroadcast);
    }

    private void initViews(View rootView) {
        textView_medication = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_medication);
        textView_tracking = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_tracking);
        textView_budgeting = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_budgeting);
        textView_notes = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_notes);
        textView_history = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_history);
        textViewProfileName = (TextView) rootView.findViewById(R.id.recipient_dashboard_textView_profile_name);

        button_looped = (Button) rootView.findViewById(R.id.recipient_loop);
        button_provider = (Button) rootView.findViewById(R.id.recipient_dashboard_button_provider);

        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);
        imageView_profile = (MaskedImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_profile_pic);
        imageView_medication = (ImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_icon_medication);
        imageView_tracking = (ImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_icon_tracking);
        imageView_budgeting = (ImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_icon_budgeting);
        imageView_notes = (ImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_icon_notes);
        imageView_history = (ImageView) rootView.findViewById(R.id.recipient_dashboard_imageView_icon_history);
    }

    private void setFonts() {
        textView_medication.setTypeface(avenirNextRegular);
        textView_tracking.setTypeface(avenirNextRegular);
        textView_budgeting.setTypeface(avenirNextRegular);
        textView_notes.setTypeface(avenirNextRegular);
        textView_history.setTypeface(avenirNextRegular);
        textViewProfileName.setTypeface(avenirNextRegular);

        button_looped.setTypeface(avenirNextRegular);
        button_provider.setTypeface(avenirNextRegular);
    }

    private void listeners() {
        imageView_profile.setOnClickListener(this);
        textViewProfileName.setOnClickListener(this);
        button_looped.setOnClickListener(this);
        button_provider.setOnClickListener(this);
        imageView_medication.setOnClickListener(this);
        textView_medication.setOnClickListener(this);
        imageView_tracking.setOnClickListener(this);
        textView_tracking.setOnClickListener(this);
        imageView_budgeting.setOnClickListener(this);
        textView_budgeting.setOnClickListener(this);
        imageView_notes.setOnClickListener(this);
        textView_notes.setOnClickListener(this);
        imageView_history.setOnClickListener(this);
        textView_history .setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == imageView_profile.getId()) {
            Intent intent = new Intent(getActivity(), DisplayRecipientActivity.class);
            startActivityForResult(intent, REQUEST_DISPLAY);
        } else if (view.getId() == textViewProfileName.getId()) {
            imageView_profile.performClick();
        } else if (view.getId() == button_looped.getId()) {
            //TODO show looped list
        }else if(view.getId() == button_provider.getId()){
            Intent intent = new Intent(getActivity(),RecipientProviderActivity.class);
            intent.putExtra(Constants.MODEL_RECIPIENT,recipient);
            startActivity(intent);

        }else if(view.getId() == textView_medication.getId()){
            Intent intent = new Intent(getActivity(), CreateMedicineActivity.class);
            startActivity(intent);
        } else if (view.getId() == imageView_medication.getId()) {
            textView_medication.performClick();
        } else if (textView_tracking.getId() == view.getId()) {
            Intent intent = new Intent(getActivity(), DisplayHealthChartActivity.class);
            intent.putExtra(Constants.MODEL_RECIPIENT, recipient);
            startActivity(intent);
        } else if (view.getId() == imageView_tracking.getId()) {
            textView_tracking.performClick();
        } else if (view.getId() == textView_budgeting.getId()) {
            //TODO show budgeting activity
        } else if (view.getId() == imageView_budgeting.getId()) {
            textView_budgeting.performClick();
        } else if (view.getId() == textView_notes.getId()) {
            //TODO show notes activity
        } else if (view.getId() == imageView_notes.getId()) {
            textView_notes.performClick();
        } else if (view.getId() == textView_history.getId()) {
            //TODO show history activity
        } else if (view.getId() == imageView_history.getId()) {
            textView_history.performClick();
        }

    }
    private RecipientModel getRecipientDetails() {
        return recipientService.getLastRecipientFromLocal();
    }

    private void setRecipientData() {
        if (recipient != null) {
            textViewProfileName.setText(recipient.name);
        }

        if (recipient != null && recipient.picUrl != null && !recipient.picUrl.isEmpty() && recipient.picUrl.contains("http")) {
            displayImageByUrl(recipient.picUrl, imageView_profile);
        } else {
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_placeholder);
            imageView_profile.setImageBitmap(image);
        }

        if (recipient != null && recipient.name != null) {
            button_looped.setText(getString(R.string.recipient_loop_text, recipient.name.toUpperCase()));
            button_provider.setText(getString(R.string.recipient_provider_text, recipient.name.toUpperCase()));
        }
    }

    private void displayImageByUrl(String publicUrl, MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DISPLAY) {
//            setRecipientData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void resolveEmptyContent() {
        if (recipient == null) {
            mainLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            mainLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            setRecipientData();
        }

    }
}
