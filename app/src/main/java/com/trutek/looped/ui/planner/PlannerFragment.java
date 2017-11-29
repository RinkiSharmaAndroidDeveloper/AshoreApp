package com.trutek.looped.ui.planner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.planner.adapter.DividerDecoration;
import com.trutek.looped.ui.planner.adapter.PlannerAdapter;
import com.trutek.looped.ui.planner.decorator.EventDecorator;
import com.trutek.looped.utils.DateUtils;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlannerFragment extends BaseV4Fragment {

    @Inject
    IActivityService _activityService;

    @BindView(R.id.recycler_view_community_planner)
    RecyclerView recyclerView;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;

    private PlannerAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private ArrayList<ActivityModel> activities,isActivitiesPost;
    private LinearLayoutManager layoutManager;
    private Tracker mTracker;
    public PlannerFragment() {
        // Required empty public constructor
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    // TODO: Rename and change types and number of parameters
    public static PlannerFragment newInstance() {
        PlannerFragment fragment = new PlannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

           /* activities.clear();
            loadAgenda();*/
            activities.clear();
            getDataFromServer();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(Constants.PLANNER_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        initAdapter();
        getDataFromServer();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_PLANNER));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activities = new ArrayList<>();
        isActivitiesPost = new ArrayList<>();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_planner, container, false);
        activateButterKnife(view);

        initListeners();

        initCalender();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_PLANNER));
        App application = (App) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.PLANNER_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        return view;
    }

    private void initCalender() {
        Calendar calendar = Calendar.getInstance();
        calendarView.setSelectedDate(calendar.getTime());
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                scrollToDate(adapter.getPositionFromDate(date.getDate()));
            }
        });
    }

    private void initListeners() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Go to the Activity")
                                .setAction("Move from planner to activity")
                                .build());
                        ActivityModel activity = activities.get(position);
                        DisplayActivity.start(getActivity(), activity);
                    }
                })
        );
    }

    private void initAdapter() {
        adapter = new PlannerAdapter();
        layoutManager = new LinearLayoutManager(getActivity());
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
    }

    private void scrollToDate(int position) {
        int pos;
        pos = position;
        if (pos>=0) {
            Collections.swap(activities, 0, pos);
            activities = SortListElement(activities);
            adapter.notifyDataSetChanged();
        } else {
        }
        layoutManager.smoothScrollToPosition(recyclerView, null, 0);
    }

    public ArrayList<ActivityModel> SortListElement(ArrayList<ActivityModel> activities) {
        ActivityModel a, b;
        ArrayList<ActivityModel> activityModels;
        activityModels = activities;
        activityModels.get(0).dueDate = activities.get(0).dueDate;
        for (int i = 1; i < activityModels.size(); i++) {
            for (int j = activityModels.size() - 1; j > i; j--) {
                a = activityModels.get(i);
                b = activityModels.get(j);
                if (a.dueDate.compareTo(b.dueDate) > 0) {
                    ActivityModel temp = activityModels.get(i);
                    activityModels.set(i, activityModels.get(j));
                    activityModels.set(j, temp);
                }

            }

        }
        return activityModels;
    }

    private void getDataFromServer() {
        activities.clear();
        isActivitiesPost.clear();
        isActivitiesPost.addAll(_activityService.getPlannerActivity());
        for (ActivityModel activityModel :isActivitiesPost){
            if(activityModel.type.contains("event"))
            {
                activities.add(activityModel);
            }
        }
        adapter.notifyDataSetChanged();
        Collections.sort(activities, new Comparator<ActivityModel>() {
            @Override
            public int compare(ActivityModel lhs, ActivityModel rhs) {
                return lhs.dueDate.compareTo(rhs.dueDate);
            }
        });

        adapter.setListStickyAdapter(activities);
        if (activities.size() == 0) {
            ToastUtils.longToast(R.string.empty_planner);
        }

        new SelectDotes(activities).executeOnExecutor(Executors.newSingleThreadExecutor());
       /*activityService.plannerActivities(new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (ActivityModel activity : activityPage.items) {
                            if(activity.type != null && activity.dueDate != null && activity.type.equalsIgnoreCase(ActivityModel.Type.event.name())) {
                                activities.add(activity);
                            }
                        }

                        Collections.sort(activities, new Comparator<ActivityModel>() {
                            @Override
                            public int compare(ActivityModel lhs, ActivityModel rhs) {
                                return lhs.dueDate.compareTo(rhs.dueDate);
                            }
                        });

                        adapter.setList(activities);
                        if(activities.size() == 0){
                            ToastUtils.longToast(R.string.empty_planner);
                        }

                        new SelectDotes(activities).executeOnExecutor(Executors.newSingleThreadExecutor());
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
        });*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.drawer_text_planner));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        // mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class SelectDotes extends AsyncTask<Void, Void, List<CalendarDay>> {

        private ArrayList<ActivityModel> activities;

        public SelectDotes(ArrayList<ActivityModel> activities) {

            this.activities = activities;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            ArrayList<CalendarDay> dates = new ArrayList<>();
            Date previousDate = null;

            for (ActivityModel activity : activities) {
                if (previousDate == null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(activity.dueDate);
                    previousDate = activity.dueDate;
                    dates.add(CalendarDay.from(cal));
                } else {
                    if (!DateUtils.isSameDay(previousDate, activity.dueDate)) {
                        dates.add(CalendarDay.from(activity.dueDate));
                        previousDate = activity.dueDate;
                    }
                }
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            calendarView.addDecorator(new EventDecorator(0xff0ccdaa, calendarDays));
        }
    }

}
