package piapro.github.io.instax.LoginComponents;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import piapro.github.io.instax.FirebaseModels.User;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.MethodFirebase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private String email, username, password;
    private EditText rEmail, rPassword, rUsername;

    private Context rContext;

    private TextView loadPleaseWait;
    private Button buttonRegister;
    private ProgressBar rProgressBar;

    //firebase
    private FirebaseAuth rAuth;
    private FirebaseAuth.AuthStateListener rAuthListener;
    private MethodFirebase methodFirebase;
    private FirebaseDatabase rFirebaseDatabase;
    private DatabaseReference rRef;

    private String append = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rContext = RegisterActivity.this;
        methodFirebase = new MethodFirebase(rContext);

        Log.d(TAG, "onCreate: start.");

        initTools();
        setupFirebaseAuth();
        initReg();
    }

    private void initReg(){
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = rEmail.getText().toString();
                username = rUsername.getText().toString();
                password = rPassword.getText().toString();

                if(checkInputs(email, username, password)){
                    rProgressBar.setVisibility(View.VISIBLE);
                    loadPleaseWait.setVisibility(View.VISIBLE);

                    methodFirebase.registerNewEmail(email, password, username);
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password){
        Log.d(TAG, "checkInputs: check if inputs has null values.");
        if(email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(rContext, "All fields need to filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //initialize tools
    private void initTools(){
        Log.d(TAG, "initTools: Initializing Widgets.");
        rEmail = (EditText) findViewById(R.id.email_input);
        rUsername = (EditText) findViewById(R.id.username_input);
        buttonRegister = (Button) findViewById(R.id.button_register);
        rProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        loadPleaseWait = (TextView) findViewById(R.id.loadWaitPlease);
        rPassword = (EditText) findViewById(R.id.password_input);
        rContext = RegisterActivity.this;
        rProgressBar.setVisibility(View.GONE);
        loadPleaseWait.setVisibility(View.GONE);

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    // firebase part
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_users))
                .orderByChild(getString(R.string.fd_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        append = rRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";
                mUsername = username + append;

                //add new user to the database
                methodFirebase.addNewUser(email, mUsername, "", "", "");

                Toast.makeText(rContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                rAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set up firebase authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: set up firebase authentication.");

        rAuth = FirebaseAuth.getInstance();
        rFirebaseDatabase = FirebaseDatabase.getInstance();
        rRef = rFirebaseDatabase.getReference();

        rAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    rRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

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
        rAuth.addAuthStateListener(rAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (rAuthListener != null) {
            rAuth.removeAuthStateListener(rAuthListener);
        }
    }
}
