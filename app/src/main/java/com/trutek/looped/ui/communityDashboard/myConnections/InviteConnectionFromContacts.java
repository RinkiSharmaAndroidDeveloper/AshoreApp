package com.trutek.looped.ui.communityDashboard.myConnections;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CommunityStep2Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.InviteFromContactFragment;
import com.trutek.looped.ui.home.HomeActivity;

/**
 * Created by Rinki on 3/10/2017.
 */
public class InviteConnectionFromContacts extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView imageBack;

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_connection_from_contacts;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intView();
        imageBack.setOnClickListener(this);
    }

    public void intView() {

        addFragmentWithoutStackEntry(R.id.load_contacts_fragment, InviteFromContactFragment.newInstance(), "contactsFragment");
        imageBack = (ImageView) findViewById(R.id.contacts_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.contacts_back):
                finish();
                break;
        }
    }
}
