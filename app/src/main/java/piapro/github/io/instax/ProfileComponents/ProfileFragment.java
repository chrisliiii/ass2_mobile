package piapro.github.io.instax.ProfileComponents;

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
import android.support.v7.widget.Toolbar;

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
import piapro.github.io.instax.FirebaseModels.Like;
import piapro.github.io.instax.FirebaseModels.SettingUser;
import piapro.github.io.instax.FirebaseModels.UserAccountSettings;
import piapro.github.io.instax.FirebaseModels.Photo;
import piapro.github.io.instax.FirebaseModels.Comment;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.BottomNavigationViewHelper;
import piapro.github.io.instax.Utilities.GridImageAdapter;
import piapro.github.io.instax.Utilities.LoadUniversalImage;
import piapro.github.io.instax.Utilities.MethodFirebase;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    //firebase
    private FirebaseAuth pAuth;
    private FirebaseAuth.AuthStateListener pAuthListener;
    private FirebaseDatabase pFirebaseDatabase;
    private DatabaseReference pRef;
    private MethodFirebase pMethodFirebase;


    //tools
    private TextView pPosts, pFollowers, pFollowing, pDisplayName, pUsername, pWebsite, pDescription;
    private ProgressBar pProgressBar;
    private CircleImageView pProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx btmNaviView;

    private Context pContext;

    private int pFollowersCount = 0;
    private int pFollowingCount = 0;
    private int pPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pDisplayName = (TextView) view.findViewById(R.id.display_name);
        pUsername = (TextView) view.findViewById(R.id.username);
        pWebsite = (TextView) view.findViewById(R.id.web);
        pDescription = (TextView) view.findViewById(R.id.bio);
        pProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        pPosts = (TextView) view.findViewById(R.id.no_post);
        pFollowers = (TextView) view.findViewById(R.id.no_followers);
        pFollowing = (TextView) view.findViewById(R.id.no_following);

        pProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        profileMenu = (ImageView) view.findViewById(R.id.profile_menu);
        btmNaviView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNaviBar);
        pMethodFirebase = new MethodFirebase(getActivity());

        pContext = getActivity();

        Log.d(TAG, "onCreateView: stare.");


        setupBtmNaviView();
        setupToolbar();

        setupFirebaseAuth();
        setupGridView();

        getFollowersCount();
        getFollowingCount();
        getPostsCount();

        TextView editProfile = (TextView) view.findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to " + pContext.getString(R.string.edit_profile));
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra(getString(R.string.activity_call), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        return view;
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

    private void setupGridView(){
        Log.d(TAG, "setupGridView: Set up image grid view.");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        photo.setCaption(objectMap.get(getString(R.string.fd_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.fd_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.fd_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.fd_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.fd_create_date)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.fd_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.fd_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.fd_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        photo.setLikes(likesList);
                        photos.add(photo);
                    }catch(NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage() );
                    }
                }

                //setup image grid
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void getFollowersCount(){
        pFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    pFollowersCount++;
                }
                pFollowers.setText(String.valueOf(pFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(){
        pFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    pFollowingCount++;
                }
                pFollowing.setText(String.valueOf(pFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount(){
        pPostsCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    pPostsCount++;
                }
                pPosts.setText(String.valueOf(pPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(SettingUser userSettings) {

        UserAccountSettings settings = userSettings.getUserSettings();

        LoadUniversalImage.setImage(settings.getProfile_photo(), pProfilePhoto, null, "");

        pDisplayName.setText(settings.getDisplay_name());
        pUsername.setText(settings.getUsername());
        pWebsite.setText(settings.getWebsite());
        pDescription.setText(settings.getDescription());
        pProgressBar.setVisibility(View.GONE);
    }


    //setup tools
    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(pContext, AccountActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    // bottom navigation bar setup
    private void setupBtmNaviView(){
        Log.d(TAG, "setupBtmNaviView: set up BottomNavigationView");
        BottomNavigationViewHelper.bottomNavigationViewSetup(btmNaviView);
        BottomNavigationViewHelper.enableNavigation(pContext,getActivity() , btmNaviView);
        Menu menu = btmNaviView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY);
        menuItem.setChecked(true);
    }

    //firebase part
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        pAuth = FirebaseAuth.getInstance();
        pFirebaseDatabase = FirebaseDatabase.getInstance();
        pRef = pFirebaseDatabase.getReference();

        pAuthListener = new FirebaseAuth.AuthStateListener() {
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
            }
        };


        pRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(pMethodFirebase.getUserSettings(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        pAuth.addAuthStateListener(pAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (pAuthListener != null) {
            pAuth.removeAuthStateListener(pAuthListener);
        }
    }

}
