package piapro.github.io.instax.Utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import piapro.github.io.instax.FirebaseModels.SettingUser;
import piapro.github.io.instax.FirebaseModels.UserAccountSettings;
import piapro.github.io.instax.ProfileComponents.AccountActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.FirebaseModels.Comment;
import piapro.github.io.instax.FirebaseModels.Like;
import piapro.github.io.instax.FirebaseModels.Photo;
import piapro.github.io.instax.FirebaseModels.User;

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    //firebase
    private FirebaseAuth vAuth;
    private FirebaseAuth.AuthStateListener vAuthListener;
    private FirebaseDatabase vFirebaseDatabase;
    private DatabaseReference vRef;



    //tools
    private TextView vPosts, vFollowers, vFollowing, vDisplayName, vUsername, vWebsite, vDescription,
            vFollow, vUnfollow;
    private ProgressBar vProgressBar;
    private CircleImageView vProfilePhoto;
    private GridView gridView;
    private ImageView vArrowBack;
    private BottomNavigationViewEx btmNaviView;
    private Context vContext;
    private TextView editProfile;


    private User vUser;
    private int vFollowersCount = 0;
    private int vFollowingCount = 0;
    private int vPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        vDisplayName = (TextView) view.findViewById(R.id.display_name);
        vUsername = (TextView) view.findViewById(R.id.username);
        vWebsite = (TextView) view.findViewById(R.id.web);
        vDescription = (TextView) view.findViewById(R.id.bio);
        vProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        vPosts = (TextView) view.findViewById(R.id.no_post);
        vFollowers = (TextView) view.findViewById(R.id.no_followers);
        vFollowing = (TextView) view.findViewById(R.id.no_following);
        vProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        btmNaviView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNaviBar);
        vFollow = (TextView) view.findViewById(R.id.follow);
        vUnfollow = (TextView) view.findViewById(R.id.unfollow);
        editProfile  = (TextView) view.findViewById(R.id.edit_profile);
        vArrowBack = (ImageView) view.findViewById(R.id.backArrow);
        vContext = getActivity();
        Log.d(TAG, "onCreateView: start.");


        try{
            vUser = getUserFromBundle();
            init();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: "  + e.getMessage() );
            Toast.makeText(vContext, "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        setupBtmNaviView();
        setupFirebaseAuth();

        isFollowing();
        getFollowingCount();
        getFollowersCount();
        getPostsCount();

        vFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: " + vUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(vUser.getUser_id())
                        .child(getString(R.string.fd_user_id))
                        .setValue(vUser.getUser_id());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_followers))
                        .child(vUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.fd_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();
            }
        });


        vUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + vUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(vUser.getUser_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_followers))
                        .child(vUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
                setUnfollowing();
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + vContext.getString(R.string.edit_profile));
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra(getString(R.string.activity_call), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        return view;
    }


    private void init(){

        //set the profile tools
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.db_user_account_settings))
                .orderByChild(getString(R.string.fd_user_id)).equalTo(vUser.getUser_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(UserAccountSettings.class).toString());

                    SettingUser settings = new SettingUser();
                    settings.setUser(vUser);
                    settings.setUserSettings(singleSnapshot.getValue(UserAccountSettings.class));
                    setProfileWidgets(settings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //get the users' profile photos
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.db_user_photos))
                .child(vUser.getUser_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Photo> photos = new ArrayList<Photo>();
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.fd_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.fd_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.fd_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.fd_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.fd_create_date)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.fd_image_path)).toString());

                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.fd_comments)).getChildren()){
                        Comment comment = new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    List<Like> likesList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.fd_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                setupImageGrid(photos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.fd_user_id)).equalTo(vUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersCount(){
        vFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_followers))
                .child(vUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    vFollowersCount++;
                }
                vFollowers.setText(String.valueOf(vFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(){
        vFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(vUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    vFollowingCount++;
                }
                vFollowing.setText(String.valueOf(vFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount(){
        vPostsCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_user_photos))
                .child(vUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    vPostsCount++;
                }
                vPosts.setText(String.valueOf(vPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        vFollow.setVisibility(View.GONE);
        vUnfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        vFollow.setVisibility(View.VISIBLE);
        vUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void setCurrentUsersProfile(){
        Log.d(TAG, "setFollowing: updating UI for showing this user their own profile");
        vFollow.setVisibility(View.GONE);
        vUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.VISIBLE);
    }

    private void setupImageGrid(final ArrayList<Photo> photos){
        //setup our image grid
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ArrayList<String> imgUrls = new ArrayList<String>();
        for(int i = 0; i < photos.size(); i++){
            imgUrls.add(photos.get(i).getImage_path());
        }
        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_gridimage,
                "", imgUrls);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY);
            }
        });
    }

    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.intent_user));
        }else{
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }


    private void setProfileWidgets(SettingUser userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUserSettings().getUsername());


        UserAccountSettings settings = userSettings.getUserSettings();

        LoadUniversalImage.setImage(settings.getProfile_photo(), vProfilePhoto, null, "");

        vDisplayName.setText(settings.getDisplay_name());
        vUsername.setText(settings.getUsername());
        vWebsite.setText(settings.getWebsite());
        vDescription.setText(settings.getDescription());
        vPosts.setText(String.valueOf(settings.getPosts()));
        vFollowing.setText(String.valueOf(settings.getFollowing()));
        vFollowers.setText(String.valueOf(settings.getFollowers()));
        vProgressBar.setVisibility(View.GONE);

        vArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

    }


    //setup bottom navigation bar
    private void setupBtmNaviView(){
        Log.d(TAG, "setupBtmNaviView: setting up BottomNavigationView");
        BottomNavigationViewHelper.bottomNavigationViewSetup(btmNaviView);
        BottomNavigationViewHelper.enableNavigation(vContext,getActivity() , btmNaviView);
        Menu menu = btmNaviView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY);
        menuItem.setChecked(true);
    }

    //firebase part
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        vAuth = FirebaseAuth.getInstance();
        vFirebaseDatabase = FirebaseDatabase.getInstance();
        vRef = vFirebaseDatabase.getReference();

        vAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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
        vAuth.addAuthStateListener(vAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (vAuthListener != null) {
            vAuth.removeAuthStateListener(vAuthListener);
        }
    }
}
