package piapro.github.io.instax.FavoriteComponents;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.BottomNavigationViewHelper;

public class FavoriteActivity extends AppCompatActivity{

    private static final String TAG = "FavoriteActivity";
    private static final int ACTIVITY = 3;

    private Context fContext = FavoriteActivity.this;
    //private ListView fListView;

    @Override
    //Alt+Insert open Generate Method
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        //fListView = (ListView) findViewById(R.id.listView);
        Log.d(TAG, "onCreate: Started");

        btmNaviViewSetup();
    }


   //setup bottom navigation bar
    private void btmNaviViewSetup(){
        Log.d(TAG, "btmNaviViewSetup: Bottom Navigation View Setting up");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNaviBar);
        BottomNavigationViewHelper.bottomNavigationViewSetup(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(fContext,this, bottomNavigationViewEx);//Parse the context
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY);
        menuItem.setChecked(true);
    }

}


