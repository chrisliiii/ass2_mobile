package piapro.github.io.instax.Utilities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import piapro.github.io.instax.R;
import piapro.github.io.instax.FirebaseModels.Comment;
import piapro.github.io.instax.FirebaseModels.Like;
import piapro.github.io.instax.FirebaseModels.Photo;
import piapro.github.io.instax.FirebaseModels.User;
import piapro.github.io.instax.FirebaseModels.UserAccountSettings;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";


    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth vAuth;
    private FirebaseAuth.AuthStateListener vAuthListener;
    private FirebaseDatabase vFirebaseDatabase;
    private DatabaseReference vRef;


    //tools
    private FormatImageView vPostImage;
    private BottomNavigationViewEx btmNaviView;
    private TextView vBackLabel, vCaption, vUsername, vTimestamp, vLikes, vComments;
    private ImageView vBackArrow, vEllipses, vHeartRed, vHeartWhite, vProfileImage, vComment;

    private Photo mPhoto;
    private int vActivityNumber = 0;
    private UserAccountSettings vUserAccountSettings;
    private GestureDetector vGestureDetector;
    private Heart vHeart;
    private Boolean vLikedByCurrentUser;
    private StringBuilder vUsers;
    private String vLikesString = "";
    private User vCurrentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        vPostImage = (FormatImageView) view.findViewById(R.id.post_image);
        btmNaviView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNaviBar);
        vBackArrow = (ImageView) view.findViewById(R.id.arrowBack);
        vBackLabel = (TextView) view.findViewById(R.id.backLabel);
        vCaption = (TextView) view.findViewById(R.id.content);
        vUsername = (TextView) view.findViewById(R.id.username);
        vTimestamp = (TextView) view.findViewById(R.id.post_time);
        vEllipses = (ImageView) view.findViewById(R.id.settings);
        vHeartRed = (ImageView) view.findViewById(R.id.like_red);
        vHeartWhite = (ImageView) view.findViewById(R.id.like_white);
        vProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
        vLikes = (TextView) view.findViewById(R.id.txt_likes);
        vComment = (ImageView) view.findViewById(R.id.chat_bubble);
        vComments = (TextView) view.findViewById(R.id.view_comments_link);

        vHeart = new Heart(vHeartWhite, vHeartRed);
        vGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        setupFirebaseAuth();
        setupBtmNaviView();

        return view;
    }

    private void init(){
        try{

            LoadUniversalImage.setImage(getPhotoFromBundle().getImage_path(), vPostImage, null, "");
            vActivityNumber = getActivityNumFromBundle();
            String photo_id = getPhotoFromBundle().getPhoto_id();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.db_photos))
                    .orderByChild(getString(R.string.fd_photo_id))
                    .equalTo(photo_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Photo newPhoto = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getString(R.string.fd_caption)).toString());
                        newPhoto.setTags(objectMap.get(getString(R.string.fd_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.fd_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.fd_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.fd_create_date)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.fd_image_path)).toString());

                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.fd_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);

                        mPhoto = newPhoto;

                        getCurrentUser();
                        getPhotoDetails();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isAdded()){
            init();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.fd_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.db_users))
                            .orderByChild(getString(R.string.fd_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: " +
                                        singleSnapshot.getValue(User.class).getUsername());

                                vUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                vUsers.append(",");
                            }

                            String[] splitUsers = vUsers.toString().split(",");

                            if(vUsers.toString().contains(vCurrentUser.getUsername() + ",")){//mitch, mitchell.tabian
                                vLikedByCurrentUser = true;
                            }else{
                                vLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1){
                                vLikesString = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                vLikesString = "Liked by " + splitUsers[0]
                                + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                vLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                + " and " + splitUsers[2];

                            }
                            else if(length == 4){
                                vLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            }
                            else if(length > 4){
                                vLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + vLikesString);
                            setupTools();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    vLikesString = "";
                    vLikedByCurrentUser = false;
                    setupTools();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_users))
                .orderByChild(getString(R.string.fd_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    vCurrentUser = singleSnapshot.getValue(User.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.db_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.fd_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(vLikedByCurrentUser &&
                                singleSnapshot.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            vRef.child(getString(R.string.db_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.fd_likes))
                                    .child(keyID)
                                    .removeValue();
///
                            vRef.child(getString(R.string.db_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.fd_likes))
                                    .child(keyID)
                                    .removeValue();

                            vHeart.toggleLike();
                            getLikesString();
                        }
                        //case2: The user has not liked the photo
                        else if(!vLikedByCurrentUser){
                            //add new like
                            addNewLike();
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = vRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        vRef.child(getString(R.string.db_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.fd_likes))
                .child(newLikeID)
                .setValue(like);

        vRef.child(getString(R.string.db_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.fd_likes))
                .child(newLikeID)
                .setValue(like);

        vHeart.toggleLike();
        getLikesString();
    }

    private void getPhotoDetails(){
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_user_account_settings))
                .orderByChild(getString(R.string.fd_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    vUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }



    private void setupTools(){
        String timestampDiff = getTimestampDifference();
        if(!timestampDiff.equals("0")){
                vTimestamp.setText(timestampDiff + " DAYS AGO");
            }else{
                vTimestamp.setText("TODAY");
        }
        LoadUniversalImage.setImage(vUserAccountSettings.getProfile_photo(), vProfileImage, null, "");
        vUsername.setText(vUserAccountSettings.getUsername());
        vLikes.setText(vLikesString);
        vCaption.setText(mPhoto.getCaption());

        if(mPhoto.getComments().size() > 0){
            vComments.setText("View all " + mPhoto.getComments().size() + " comments");
        }else{
            vComments.setText("");
        }

        vComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread");

                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);

            }
        });

        vBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        vComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);

            }
        });

        if(vLikedByCurrentUser){
            vHeartWhite.setVisibility(View.GONE);
            vHeartRed.setVisibility(View.VISIBLE);
            vHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return vGestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            vHeartWhite.setVisibility(View.VISIBLE);
            vHeartRed.setVisibility(View.GONE);
            vHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return vGestureDetector.onTouchEvent(event);
                }
            });
        }


    }



    private String getTimestampDifference(){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }


    private int getActivityNumFromBundle(){
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    // setup bottom navigation bar
    private void setupBtmNaviView(){
        Log.d(TAG, "setupBtmNaviView: setting up BottomNavigationView");
        BottomNavigationViewHelper.bottomNavigationViewSetup(btmNaviView);
        BottomNavigationViewHelper.enableNavigation(getActivity(),getActivity() , btmNaviView);
        Menu menu = btmNaviView.getMenu();
        MenuItem menuItem = menu.getItem(vActivityNumber);
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





















