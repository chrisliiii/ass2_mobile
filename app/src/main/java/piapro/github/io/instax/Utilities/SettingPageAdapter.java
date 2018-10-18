package piapro.github.io.instax.Utilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> sFList = new ArrayList<>();
    private final HashMap<Fragment, Integer> sFragment = new HashMap<>();
    private final HashMap<String, Integer> sFNumbers = new HashMap<>();
    private final HashMap<Integer, String> sFNames = new HashMap<>();

    public SettingPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return sFList.get(position);
    }

    @Override
    public int getCount() {
        return sFList.size();
    }

    public void addFrag(Fragment fragment, String fName){
        sFList.add(fragment);
        sFragment.put(fragment, sFList.size()-1);
        sFNumbers.put(fName, sFList.size()-1);
        sFNames.put(sFList.size()-1, fName);
    }

    // return fragment number by name
    public Integer getFNumber(String fName){
        if(sFNumbers.containsKey(fName)){
            return sFNumbers.get(fName);
        }else{
            return null;
        }
    }

    // return fragment number by fragment
    public Integer getFNumber(Fragment fragment){
        if(sFNumbers.containsKey(fragment)){
            return sFNumbers.get(fragment);
        }else{
            return null;
        }
    }

    // return fragment name by number
    public String getFName(Integer fNumber){
        if(sFNames.containsKey(fNumber)){
            return sFNames.get(fNumber);
        }else{
            return null;
        }
    }
}
