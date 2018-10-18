package piapro.github.io.instax.LoginComponents;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import piapro.github.io.instax.HomeComponents.HomeActivity;
import piapro.github.io.instax.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText lEmail;
    private EditText lPassword;
    private TextView lPleaseWait;

    private Context lContext;
    private ProgressBar lProgressBar;

    //firebase
    private FirebaseAuth lAuth;
    private FirebaseAuth.AuthStateListener lAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmail = (EditText) findViewById(R.id.email_input);
        lPassword = (EditText) findViewById(R.id.password_input);
        lProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        lPleaseWait = (TextView) findViewById(R.id.waitPlease);

        lContext = LoginActivity.this;
        Log.d(TAG, "onCreate: start.");

        lPleaseWait.setVisibility(View.GONE);
        lProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        initfb();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: check whether string is null");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }


    // firebase part
    private void initfb(){

        //initialize the button for logging in
        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempt to log in.");

                String email = lEmail.getText().toString();
                String password = lPassword.getText().toString();

                if(isStringNull(email) && isStringNull(password)){
                    Toast.makeText(lContext, "need to fill out all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    lProgressBar.setVisibility(View.VISIBLE);
                    lPleaseWait.setVisibility(View.VISIBLE);

                    lAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    FirebaseUser user = lAuth.getCurrentUser();

                                    // handle sign_in failure
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                                Toast.LENGTH_SHORT).show();
                                        lProgressBar.setVisibility(View.GONE);
                                        lPleaseWait.setVisibility(View.GONE);
                                    }
                                    else{
                                        try{
                                            if(user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: success. email is verified.");
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(lContext, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                                lProgressBar.setVisibility(View.GONE);
                                                lPleaseWait.setVisibility(View.GONE);
                                                lAuth.signOut();
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }
                                    }

                                    // ...
                                }
                            });
                }

            }
        });

        TextView linkSignUp = (TextView) findViewById(R.id.jump_register);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //if users are already logged in
        if(lAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //set up firebase
    private void setupFirebaseAuth(){
        
        Log.d(TAG, "setupFirebaseAuth: set up firebase auth.");

        lAuth = FirebaseAuth.getInstance();

        lAuthListener = new FirebaseAuth.AuthStateListener() {
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
        lAuth.addAuthStateListener(lAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lAuthListener != null) {
            lAuth.removeAuthStateListener(lAuthListener);
        }
    }

}
