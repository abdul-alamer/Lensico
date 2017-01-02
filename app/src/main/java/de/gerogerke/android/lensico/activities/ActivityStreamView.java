package de.gerogerke.android.lensico.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.tabs.TabInstagramUser;

/**
 * Created by Deutron on 31.03.2016.
 * <p/>
 * Activity to host the TabInstagramUser-Fragment. Allows to see the Images of a User without a tab
 */
public class ActivityStreamView extends AppCompatActivity {

    //TODO: Get username / id passed,
    //      Set Title to Username
    //      Inflate Fragment

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        //Get User From Intent Bundle
        SimpleInstagramUser user = (SimpleInstagramUser) getIntent().getSerializableExtra("user");

        //Bind Butterknife
        ButterKnife.bind(this);

        //Set Support Actionbar and Title
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(user.uName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabInstagramUser userTab = TabInstagramUser.newInstance(user);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.stream_content, userTab);
        fragmentTransaction.commit();
    }
}
