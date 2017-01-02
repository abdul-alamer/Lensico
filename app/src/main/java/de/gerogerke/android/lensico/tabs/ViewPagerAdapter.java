package de.gerogerke.android.lensico.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.TreeMap;

/**
 * Created by Deutron on 24.12.2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    TreeMap<String, Fragment> mTabs;

    public ViewPagerAdapter(FragmentManager mFragmentManager, TreeMap<String, Fragment> mTabs) {
        super(mFragmentManager);
        this.mTabs = mTabs;
    }

    @Override
    public Fragment getItem(int mPosition) {
        return (Fragment) mTabs.values().toArray()[mPosition];
    }

    @Override
    public CharSequence getPageTitle(int mPosition) {
        return mTabs.keySet().toArray()[mPosition] + "";
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

}
