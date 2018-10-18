package piapro.github.io.instax.Utilities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import piapro.github.io.instax.FavoriteComponents.FavoriteActivity;
import piapro.github.io.instax.HomeComponents.HomeActivity;
import piapro.github.io.instax.ProfileComponents.ProfileActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.SearchComponents.SearchActivity;
import piapro.github.io.instax.ShareComponents.ShareActivity;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHp"; //See logt

    public static void bottomNavigationViewSetup(BottomNavigationViewEx bottomNavigationViewEx){

        Log.d(TAG, "bottomNavigationViewSetup: set up bottom navigation view");
        // Unable all unnecessary animation features
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

    }

    //Setup method that navigating between activities
    public static void enableNavigation(final Context context, final Activity callActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.ic_home:
                        Intent intentHome = new Intent(context, HomeActivity.class);//ACTIVITY=0
                        context.startActivity(intentHome);
                        callActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;

                    case R.id.ic_search:
                        Intent intentSearch = new Intent(context, SearchActivity.class);//ACTIVITY=1
                        context.startActivity(intentSearch);
                        callActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;

                    case R.id.ic_add:
                        Intent intentShare = new Intent(context, ShareActivity.class);//ACTIVITY=2
                        context.startActivity(intentShare);
                        callActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;

                    case R.id.ic_favorite:
                        Intent intentFavorite = new Intent(context, FavoriteActivity.class);//ACTIVITY=3
                        context.startActivity(intentFavorite);
                        callActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;

                    case R.id.ic_profile:
                        Intent intentProfile = new Intent(context, ProfileActivity.class);//ACTIVITY=4
                        context.startActivity(intentProfile);
                        callActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;

                }
                return false;
            }
        });
    }

}
