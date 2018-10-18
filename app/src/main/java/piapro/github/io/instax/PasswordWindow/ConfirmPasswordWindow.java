package piapro.github.io.instax.PasswordWindow;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import piapro.github.io.instax.R;

/**
 * Created by User on 7/10/2017.
 */

public class ConfirmPasswordWindow extends DialogFragment {

    private static final String TAG = "ConfirmPasswordWindow";

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener cOnConfirmPasswordListener;


    //vars
    TextView cPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.firebase_confirm_password, container, false);
        cPassword = (TextView) view.findViewById(R.id.field_confirm_password);

        Log.d(TAG, "onCreateView: start.");

        TextView confirmDialog = (TextView) view.findViewById(R.id.confirm_button);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming.");

                String password = cPassword.getText().toString();
                if(!password.equals("")){
                    cOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }else{
                    Toast.makeText(getActivity(), "you must enter a password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView cancelDialog = (TextView) view.findViewById(R.id.cancel_button);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: close this window");
                getDialog().dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            cOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}




