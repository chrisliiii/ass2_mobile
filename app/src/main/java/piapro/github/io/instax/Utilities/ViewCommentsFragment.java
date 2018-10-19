package piapro.github.io.instax.Utilities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import piapro.github.io.instax.HomeComponents.HomeActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.FirebaseModels.Comment;
import piapro.github.io.instax.FirebaseModels.Photo;

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth vAuth;
    private FirebaseAuth.AuthStateListener vAuthListener;
    private FirebaseDatabase vFirebaseDatabase;
    private DatabaseReference vRef;

    //tools
    private ImageView vArrowBack;
    private ImageView vCheckMark;
    private EditText vComment;
    private ListView vListView;
    
    private Photo vPhoto;
    private ArrayList<Comment> vComments;
    
    private Context vContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_comment_view, container, false);
        
        vArrowBack = (ImageView) view.findViewById(R.id.arrowBack);
        vCheckMark = (ImageView) view.findViewById(R.id.post_comment);
        vComment = (EditText) view.findViewById(R.id.comment);
        vListView = (ListView) view.findViewById(R.id.listView);
        vComments = new ArrayList<>();
        
        vContext = getActivity();


        try{
            vPhoto = getPhotoFromBundle();
            setupFirebaseAuth();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        return view;
    }

    private void setupTools(){

        CommentFormatAdapter adapter = new CommentFormatAdapter(vContext,
                R.layout.layout_comment, vComments);
        vListView.setAdapter(adapter);

        vCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!vComment.getText().toString().equals("")){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addComment(vComment.getText().toString());

                    vComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                if(getActivityCallFromBundle().equals(getString(R.string.home_activity))){
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((HomeActivity)getActivity()).showLayout();
                }else{
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addComment(String newComment){
        Log.d(TAG, "addComment: adding new comment: " + newComment);

        String commentID = vRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photos part
        vRef.child(getString(R.string.db_photos))
                .child(vPhoto.getPhoto_id())
                .child(getString(R.string.fd_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos part
        vRef.child(getString(R.string.db_user_photos))
                .child(vPhoto.getUser_id())
                .child(vPhoto.getPhoto_id())
                .child(getString(R.string.fd_comments))
                .child(commentID)
                .setValue(comment);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }


    private String getActivityCallFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getString(getString(R.string.home_activity));
        }else{
            return null;
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
            }
        };

        if(vPhoto.getComments().size() == 0){
            vComments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(vPhoto.getCaption());
            firstComment.setUser_id(vPhoto.getUser_id());
            firstComment.setDate_created(vPhoto.getDate_created());
            vComments.add(firstComment);
            vPhoto.setComments(vComments);
            setupTools();
        }


        vRef.child(vContext.getString(R.string.db_photos))
                .child(vPhoto.getPhoto_id())
                .child(vContext.getString(R.string.fd_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = vRef
                                .child(vContext.getString(R.string.db_photos))
                                .orderByChild(vContext.getString(R.string.fd_photo_id))
                                .equalTo(vPhoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get(vContext.getString(R.string.fd_caption)).toString());
                                    photo.setTags(objectMap.get(vContext.getString(R.string.fd_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(vContext.getString(R.string.fd_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(vContext.getString(R.string.fd_user_id)).toString());
                                    photo.setDate_created(objectMap.get(vContext.getString(R.string.fd_create_date)).toString());
                                    photo.setImage_path(objectMap.get(vContext.getString(R.string.fd_image_path)).toString());


                                    vComments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(vPhoto.getCaption());
                                    firstComment.setUser_id(vPhoto.getUser_id());
                                    firstComment.setDate_created(vPhoto.getDate_created());
                                    vComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child(vContext.getString(R.string.fd_comments)).getChildren()){
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        vComments.add(comment);
                                    }

                                    photo.setComments(vComments);

                                    vPhoto = photo;

                                    setupTools();


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled.");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



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
