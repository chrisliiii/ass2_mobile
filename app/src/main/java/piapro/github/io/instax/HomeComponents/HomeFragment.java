package piapro.github.io.instax.HomeComponents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.MainfeedListAdapter;
import piapro.github.io.instax.FirebaseModels.Photo;
import piapro.github.io.instax.FirebaseModels.Comment;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ArrayList<Photo> hPhotos;
    private ArrayList<Photo> hListedPhotos;
    private ArrayList<String> hFollowing;
    private ListView hListView;
    private MainfeedListAdapter hAdapter;
    private int hResult;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        hPhotos = new ArrayList<>();
        hFollowing = new ArrayList<>();
        hListView = (ListView) view.findViewById(R.id.listView);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getFollowing();
        }

        return view;
    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: search for post of following users");

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

        Query query = dbref
                .child(getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: find user: " +
                            singleSnapshot.child(getString(R.string.fd_user_id)).getValue());

                    hFollowing.add(singleSnapshot.child(getString(R.string.fd_user_id)).getValue().toString());
                }
                hFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                //get the photos
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: get post photos");
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < hFollowing.size(); i++){
            final int count = i;
            Query query = dbref
                    .child(getString(R.string.db_user_photos))
                    .child(hFollowing.get(i))
                    .orderByChild(getString(R.string.fd_user_id))
                    .equalTo(hFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

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
                        hPhotos.add(photo);
                    }
                    if(count >= hFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){

        hListedPhotos = new ArrayList<>();

        if(hPhotos != null){
            try{
                Collections.sort(hPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = hPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                hResult = 10;
                for(int i = 0; i < iterations; i++){
                    hListedPhotos.add(hPhotos.get(i));
                }

                hAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_userfeed_list, hListedPhotos);
                hListView.setAdapter(hAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: display more photos");

        try{

            if(hPhotos.size() > hResult && hPhotos.size() > 0){

                int iterations;
                if(hPhotos.size() > (hResult + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = hPhotos.size() - hResult;
                }

                //add the new photos to the paginated results
                for(int i = hResult; i < hResult + iterations; i++){
                    hListedPhotos.add(hPhotos.get(i));
                }
                hResult = hResult + iterations;
                hAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}
