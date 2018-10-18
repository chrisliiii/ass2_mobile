package piapro.github.io.instax.Utilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

//Storing Fragments for Top Toolbar Tabs
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "TabsPagerAdapter";
    private final List<Fragment> fragmentListManager = new ArrayList<>();//Store fragments


    public TabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentListManager.get(position); //Return the fragment index
    }

    @Override
    public int getCount() {
        return fragmentListManager.size();
    }

    public void addFragment(Fragment fragment){
        fragmentListManager.add(fragment);
    }
}
