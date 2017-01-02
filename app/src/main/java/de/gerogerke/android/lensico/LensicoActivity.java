package de.gerogerke.android.lensico;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.activities.ActivityAbout;
import de.gerogerke.android.lensico.activities.ActivityPhotoSubmit;
import de.gerogerke.android.lensico.bottomsheet.Bottomsheet;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.tabs.TabInstagramUser;
import de.gerogerke.android.lensico.tabs.ViewPagerAdapter;

public class LensicoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPagerAdapter adapter;
    TreeMap<String, Fragment> mTabs;
    InstagramAPI api;

    ViewGroup headerLayout;
    Menu menu;
    SubMenu subMenu;
    InstagramUser self;

    @Bind(R.id.main_loading_progress1)
    View spinner;

    @Bind(R.id.main_viewpager_container)
    ViewGroup viewPagerContainer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lensico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = (ViewGroup) navigationView.getHeaderView(0);
        menu = navigationView.getMenu();
        subMenu = menu.addSubMenu("People you follow");

        api = InstagramAPI
                .with(this);

        mTabs = new TreeMap<>();

        api.connect(new InstagramAPI.OAuthDialogListener() {
            @Override
            public void onComplete(String accessToken) {
                addTabs();
                setupDrawerHeader();
                spinner.setVisibility(View.GONE);
                viewPagerContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error");
                builder.setMessage("Oh oh, something went wrong!");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabs);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        tabs.setupWithViewPager(pager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), ActivityPhotoSubmit.class);
                myIntent.putExtra("centerX", fab.getX() + (fab.getWidth() / 2));
                myIntent.putExtra("centerY", fab.getY());
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lensico, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_about) {
            Intent myIntent = new Intent(getApplicationContext(), ActivityAbout.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            if(api.isAuthorized()) {
                api.resetAccessToken();
                //TODO: Handle being logged out
            }
        } else if (id == R.id.my_account) {
            Bottomsheet.openProfileBottomSheet(this, self.getUsername(), null);
        }

        final int pos = findFragmentPositionByName(item.getTitle() + "");
        if(pos >= 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    pager.setCurrentItem(pos, true);
                }
            });
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public int findFragmentPositionByName(String name) {
        int pos = -1;
        for(String title : mTabs.keySet()) {
            pos++;
            if(title.equalsIgnoreCase(name)) {
                return pos;
            }
        }
        return pos;
    }

    public void setupDrawerHeader() {
        final ViewGroup welcomeTextGroup = (ViewGroup) headerLayout.findViewById(R.id.drawer_header_welcome_text_group);
        final TextView nameTextView = (TextView) welcomeTextGroup.findViewById(R.id.drawer_header_welcome_text_name);
        api.getConnection().fetchUser("self", new TaskCallback() {
            @Override
            public void onSuccess(Object object) {
                self = (InstagramUser) object;
                if(self != null) {
                    nameTextView.setText(self.getUsername());
                    welcomeTextGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                //TODO: Show try again dialog. This self-fetch is vital for the app lifecycle!
            }
        });
    }

    public void addTabs() {
        api.getConnection().getFollows("self", new TaskCallback() {
            @Override
            public void onSuccess(Object object) {
                List<SimpleInstagramUser> follows = (List<SimpleInstagramUser>) object;
                for (SimpleInstagramUser follower : follows) {
                    addTab(follower.uName, TabInstagramUser.newInstance(follower));
                    subMenu.add(follower.uName);
                }
            }
        });
    }

    public void addTab(String title, Fragment fragment) {
        mTabs.put(title, fragment);
        adapter.notifyDataSetChanged();
        tabs.setupWithViewPager(pager);
    }

}