package piapro.github.io.instax.ProfileComponents;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import piapro.github.io.instax.FirebaseModels.SettingUser;
import piapro.github.io.instax.FirebaseModels.User;
import piapro.github.io.instax.FirebaseModels.UserAccountSettings;
import piapro.github.io.instax.PasswordWindow.ConfirmPasswordWindow;
import piapro.github.io.instax.R;
import piapro.github.io.instax.ShareComponents.ShareActivity;
import piapro.github.io.instax.Utilities.LoadUniversalImage;
import piapro.github.io.instax.Utilities.MethodFirebase;

public class EditProfileFragment extends Fragment
        implements ConfirmPasswordWindow.OnConfirmPasswordListener{

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth eAuth;
    private FirebaseAuth.AuthStateListener eAuthListener;
    private FirebaseDatabase eFirebaseDatabase;
    private DatabaseReference eRef;
    private MethodFirebase eMethodFirebase;
    private String userID;

    //EditProfile Fragment tools
    private EditText eDisplayName, eUsername, eWebsite, eDescription, eEmail, ePhoneNumber;
    private TextView eChangeProfilePhoto;
    private CircleImageView eProfilePhoto;
    private SettingUser eSettingUser;

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        //get authentication credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(eAuth.getCurrentUser().getEmail(), password);

        // let the user re-provide their sign-in credentials
        eAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            ///check to see whether the email is present in the database
                            eAuth.fetchProvidersForEmail(eEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getProviders().size() == 1){
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                eAuth.getCurrentUser().updateEmail(eEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    eMethodFirebase.updateEmail(eEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: "  +e.getMessage() );
                                        }
                                    }
                                }
                            });


                        }else{
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        eProfilePhoto = (CircleImageView) view.findViewById(R.id.photo_profile);
        eDisplayName = (EditText) view.findViewById(R.id.display_name);
        eUsername = (EditText) view.findViewById(R.id.username);
        eWebsite = (EditText) view.findViewById(R.id.web);
        eDescription = (EditText) view.findViewById(R.id.bio);
        eEmail = (EditText) view.findViewById(R.id.email);
        ePhoneNumber = (EditText) view.findViewById(R.id.phone);
        eChangeProfilePhoto = (TextView) view.findViewById(R.id.change_Photo_profile);
        eMethodFirebase = new MethodFirebase(getActivity());


        //setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.save_change);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });

        return view;
    }


    //get the data and upload to firebase
    private void saveProfileSettings(){
        final String displayName = eDisplayName.getText().toString();
        final String username = eUsername.getText().toString();
        final String website = eWebsite.getText().toString();
        final String description = eDescription.getText().toString();
        final String email = eEmail.getText().toString();
        final long phoneNumber = Long.parseLong(ePhoneNumber.getText().toString());


        //if the user changes their username
        if(!eSettingUser.getUser().getUsername().equals(username)){

            checkIfUsernameExists(username);
        }
        //if the user changes their email
        if(!eSettingUser.getUser().getEmail().equals(email)){

            // re-authenticate
            ConfirmPasswordWindow dialog = new ConfirmPasswordWindow();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_window));
            dialog.setTargetFragment(EditProfileFragment.this, 1);

        }


        if(!eSettingUser.getUserSettings().getDisplay_name().equals(displayName)){
            //update displayname
            eMethodFirebase.updateUserAccountSettings(displayName, null, null, 0);
        }
        if(!eSettingUser.getUserSettings().getWebsite().equals(website)){
            //update website
            eMethodFirebase.updateUserAccountSettings(null, website, null, 0);
        }
        if(!eSettingUser.getUserSettings().getDescription().equals(description)){
            //update description
            eMethodFirebase.updateUserAccountSettings(null, null, description, 0);
        }
        if(!eSettingUser.getUserSettings().getProfile_photo().equals(phoneNumber)){
            //update phoneNumber
            eMethodFirebase.updateUserAccountSettings(null, null, null, phoneNumber);
        }
    }



    // check whether username exists
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Check if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_users))
                .orderByChild(getString(R.string.fd_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    //add the username
                    eMethodFirebase.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(SettingUser userSettings){

        Log.d(TAG, "setProfileWidgets: data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: data retrieving from firebase database: " + userSettings.getUser().getEmail());
        Log.d(TAG, "setProfileWidgets: data retrieving from firebase database: " + userSettings.getUser().getPhone_number());

        eSettingUser = userSettings;

        UserAccountSettings settings = userSettings.getUserSettings();
        LoadUniversalImage.setImage(settings.getProfile_photo(), eProfilePhoto, null, "");
        eDisplayName.setText(settings.getDisplay_name());
        eUsername.setText(settings.getUsername());
        eWebsite.setText(settings.getWebsite());
        eDescription.setText(settings.getDescription());
        eEmail.setText(userSettings.getUser().getEmail());
        ePhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        eChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    //firebase part
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        eAuth = FirebaseAuth.getInstance();
        eFirebaseDatabase = FirebaseDatabase.getInstance();
        eRef = eFirebaseDatabase.getReference();
        userID = eAuth.getCurrentUser().getUid();

        eAuthListener = new FirebaseAuth.AuthStateListener() {
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


        eRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get user information from the database
                setProfileWidgets(eMethodFirebase.getUserSettings(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        eAuth.addAuthStateListener(eAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eAuthListener != null) {
            eAuth.removeAuthStateListener(eAuthListener);
        }
    }

}
