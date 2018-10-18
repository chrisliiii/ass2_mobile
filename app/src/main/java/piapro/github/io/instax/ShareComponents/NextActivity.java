package piapro.github.io.instax.ShareComponents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import piapro.github.io.instax.Utilities.MethodFirebase;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.LoadUniversalImage;


public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener nAuthListener;
    private FirebaseDatabase nFirebaseDatabase;
    private DatabaseReference nRef;
    private MethodFirebase nMethodFirebase;
    private EditText nCaption;

    private String nAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        nMethodFirebase = new MethodFirebase(NextActivity.this);
        nCaption = (EditText) findViewById(R.id.caption) ;

        setupFirebaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.nextArrowBack);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: close the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.next);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempt to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = nCaption.getText().toString();

                if(intent.hasExtra(getString(R.string.chosen_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.chosen_image));
                    nMethodFirebase.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl,null);
                }
                else if(intent.hasExtra(getString(R.string.chosen_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.chosen_bitmap));
                    nMethodFirebase.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null,bitmap);
                }



            }
        });

        setImage();
    }


    private void setImage(){
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.photoShare);

        if(intent.hasExtra(getString(R.string.chosen_image))){
            imgUrl = intent.getStringExtra(getString(R.string.chosen_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            LoadUniversalImage.setImage(imgUrl, image, null, nAppend);
        }
        else if(intent.hasExtra(getString(R.string.chosen_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.chosen_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }
    }

    //firebase part
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        nAuth = FirebaseAuth.getInstance();
        nFirebaseDatabase = FirebaseDatabase.getInstance();
        nRef = nFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        nAuthListener = new FirebaseAuth.AuthStateListener() {
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


        nRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = nMethodFirebase.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(nAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (nAuthListener != null) {
            nAuth.removeAuthStateListener(nAuthListener);
        }
    }
}
