package piapro.github.io.instax.HomeComponents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import piapro.github.io.instax.LoginComponents.LoginActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.BottomNavigationViewHelper;
import piapro.github.io.instax.Utilities.LoadUniversalImage;
import piapro.github.io.instax.Utilities.MainfeedListAdapter;
import piapro.github.io.instax.Utilities.TabsPagerAdapter;
import piapro.github.io.instax.Utilities.ViewCommentsFragment;
import piapro.github.io.instax.FirebaseModels.Photo;

public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.middleContentViewer + ":" + hViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;

    private Context hContext = HomeActivity.this;
    private CameraFragment cameraFragment= new CameraFragment();
    public static Bitmap mImage = null;

    //tools
    private ViewPager hViewPager;
    private FrameLayout hFrameLayout;
    private RelativeLayout hRelativeLayout;

    //firebase
    private FirebaseAuth hAuth;
    private FirebaseAuth.AuthStateListener hAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: start activity home.");
        hViewPager = (ViewPager) findViewById(R.id.middleContentViewer);
        hFrameLayout = (FrameLayout) findViewById(R.id.container);
        hRelativeLayout = (RelativeLayout) findViewById(R.id.rllayoutParent);

        setupFirebaseAuth();

        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();

    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.viewComments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        hRelativeLayout.setVisibility(View.GONE);
        hFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        hRelativeLayout.setVisibility(View.VISIBLE);
        hFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(hFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }


    private void initImageLoader(){
        LoadUniversalImage universalImageLoader = new LoadUniversalImage(hContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mImage!=null){
            cameraFragment.setmImage(mImage);
        }
    }

    // add three fragment
    private void setupViewPager(){
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new DirectFragment()); //index 2
        hViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.topTabs);
        tabLayout.setupWithViewPager(hViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setText(getString(R.string.app_name));
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_direct);
    }

    //bottom navigation bar set up
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNaviBar);
        BottomNavigationViewHelper.bottomNavigationViewSetup(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(hContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    //firebase part
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(hContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    //set up firebase authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        hAuth = FirebaseAuth.getInstance();

        hAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        hAuth.addAuthStateListener(hAuthListener);
        hViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(hAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (hAuthListener != null) {
            hAuth.removeAuthStateListener(hAuthListener);
        }
    }

}
