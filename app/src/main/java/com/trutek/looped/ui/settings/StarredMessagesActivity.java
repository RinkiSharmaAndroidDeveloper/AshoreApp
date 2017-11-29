package com.trutek.looped.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.settings.Adapter.StarredMessagesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 2/19/2017.
 */
public class StarredMessagesActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    ICommentService _commentService;

    ImageView back_arrow;
    StarredMessagesAdapter mAdapter;
    RecyclerView recyclerView;
    ProfileModel profile;
    CommentModel commentModel;
    List<CommentModel> commentModelList = new ArrayList<>();


    @Override
    protected int getContentResId() {
        return R.layout.activty_starred_messages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        getDataFromLocal();
        initAdapter();
        back_arrow.setOnClickListener(this);
    }

    public void init() {
        back_arrow = (ImageView) findViewById(R.id.starred_back);
        recyclerView = (RecyclerView) findViewById(R.id.starred_messages_recycler_view);

    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initAdapter() {
        mAdapter = new StarredMessagesAdapter(commentModelList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void getDataFromLocal() {
        commentModelList = _commentService.getLocalComment(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.starred_back):
                finish();
                break;
        }

    }
}
