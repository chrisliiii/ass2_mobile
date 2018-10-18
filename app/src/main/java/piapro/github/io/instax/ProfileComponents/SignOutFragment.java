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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import piapro.github.io.instax.LoginComponents.LoginActivity;
import piapro.github.io.instax.R;

public class SignOutFragment extends Fragment{

    private static final String TAG = "SignOutFragment";

    private FirebaseAuth sAuth;
    private FirebaseAuth.AuthStateListener sAuthListener;

    private ProgressBar sProgressBar;
    private TextView sSignout, sSigningOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container,false);

        sSignout = (TextView) view.findViewById(R.id.confirm_signout);
        sProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        sSigningOut = (TextView) view.findViewById(R.id.signing_out);
        Button btnConfirmSignout = (Button) view.findViewById(R.id.botton_confirm_signout);

        sProgressBar.setVisibility(View.GONE);
        sSigningOut.setVisibility(View.GONE);

        setupFirebaseAuth();

        btnConfirmSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out.");
                sProgressBar.setVisibility(View.VISIBLE);
                sSigningOut.setVisibility(View.VISIBLE);

                sAuth.signOut();
                getActivity().finish();
            }
        });

        return view;
    }

    //firebase

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        sAuth = FirebaseAuth.getInstance();

        sAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        sAuth.addAuthStateListener(sAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sAuthListener != null) {
            sAuth.removeAuthStateListener(sAuthListener);
        }
    }
}
