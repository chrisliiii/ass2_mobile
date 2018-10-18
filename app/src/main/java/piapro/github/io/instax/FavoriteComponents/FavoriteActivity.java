package piapro.github.io.instax.FavoriteComponents;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.BottomNavigationViewHelper;


public class FavoriteActivity extends AppCompatActivity{
    private static final String TAG = "FavoriteActivity";
    private static final int ACTIVITY = 3;

    private Context mContext = FavoriteActivity.this;

    @Override
    //Alt+Insert open Generate Method
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        Log.d(TAG, "onCreate: Started");

        bottomNavigationViewSetup();
    }

    /**
     * Setup Bottom Navigation View
     */
    private void bottomNavigationViewSetup(){
        Log.d(TAG, "bottomNavigationViewSetup: Bottom Navigation View Setting up");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNaviBar);
        BottomNavigationViewHelper.bottomNavigationViewSetup(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this, bottomNavigationViewEx);//Parse the context
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY);
        menuItem.setChecked(true);
    }

}


