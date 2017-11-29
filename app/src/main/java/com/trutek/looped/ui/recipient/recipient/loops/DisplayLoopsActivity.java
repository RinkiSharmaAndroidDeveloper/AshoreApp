package com.trutek.looped.ui.recipient.recipient.loops;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.ILoopService;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.recipient.RecipientDashBoardFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class DisplayLoopsActivity extends BaseAppCompatActivity {

    @Inject
    ILoopService loopService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.header) TextView header;
    @BindView(R.id.recycler_view_loop) RecyclerView recyclerViewLoops;

    private RecipientModel recipient;
    private List<LoopModel> loops;
    private LoopsAdapter loopsAdapter;

    public static void start(Context context, RecipientModel recipient) {
        Intent intent = new Intent(context, DisplayLoopsActivity.class);
        intent.putExtra("recipientModel", recipient);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_loops;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
        initAdapter();

        getRecipientLoop();
    }

    private void initFields(){
        recipient = (RecipientModel) getIntent().getSerializableExtra("recipientModel");
        loops = new ArrayList<>();

        if(recipient != null && recipient.name != null){
            header.setText(getString(R.string.recipient_loop_text, recipient.getName().toUpperCase()));
        }

    }

    private void initAdapter(){

        loopsAdapter = new LoopsAdapter(loops);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewLoops.setLayoutManager(linearLayoutManager);
        recyclerViewLoops.setAdapter(loopsAdapter);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.plus_icon:
                InviteFromLoopActivity.start(this, recipient);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.plus_icon_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
        unbinder = null;
    }

    public void getRecipientLoop() {
        PageInput input = new PageInput();
        input.query.add("recipientId", recipient.getServerId());
        input.query.add("status", "active");
        loops.addAll(loopService.search(input).items);
        loopsAdapter.notifyDataSetChanged();
    }
}
