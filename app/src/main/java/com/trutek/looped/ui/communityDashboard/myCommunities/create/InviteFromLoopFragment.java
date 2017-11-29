package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.StepThree.InviteFromLoopAdapter;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InviteFromLoopFragment extends BaseV4Fragment {

    @Inject
    IConnectionService connectionService;

    @BindView(R.id.from_loop_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.create_community_button_next)Button button_next;

    private ArrayList<String> profileIds;

    private ArrayList<ConnectionModel> connections;
    private InviteFromLoopAdapter inviteFromLoopAdapter;

    private OnActionListener<ConnectionModel> onSelectedActionListener;
    private OnActionListener<ConnectionModel> onUnSelectedActionListener;

    private OnFragmentInteractionListener mListener;

    public InviteFromLoopFragment() {
        // Required empty public constructor
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    public static InviteFromLoopFragment newInstance() {
        InviteFromLoopFragment fragment = new InviteFromLoopFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileIds = new ArrayList<>();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_from_loop, container, false);
        activateButterKnife(view);
        initFields();
        setAdapter();

        getDataFromServer();
        return view;
    }

    private void initFields() {
//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Intent intent = new Intent(getActivity(), DisplayProfile.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("profileModel", connections.get(position));
//                        getActivity().startActivity(intent);
//                    }
//                })
//        );

        onSelectedActionListener = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connectionModel) {
                profileIds.add(connectionModel.getServerId());
            }
        };

        onUnSelectedActionListener = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connectionModel) {
                profileIds.remove(connectionModel.getServerId());
            }
        };

        setFonts();
    }

    private void setAdapter() {
        connections = new ArrayList<>();
        inviteFromLoopAdapter = new InviteFromLoopAdapter(connections, onSelectedActionListener, onUnSelectedActionListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(inviteFromLoopAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void getDataFromServer() {
        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inviteFromLoopAdapter.addList(connectionModels);
                        inviteFromLoopAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.create_community_button_next)
    public void nextClick(){

        Intent intent =new Intent(getContext().getApplicationContext(), MyCommunitiesActivity.class);
        startActivity(intent);

    }
}
