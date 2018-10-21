package piapro.github.io.instax.ShareComponents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import piapro.github.io.instax.ProfileComponents.AccountActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.Permissions;
import takephoto.simpActivity.CustomHelper;
import piapro.github.io.instax.takeview.CameraActivity;

public class PhotoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PhotoFragment";

    private CustomHelper customHelper;
    private float colorArray[] = null;

    private Button  Button14, Button15;

    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int  CAMERA_REQUEST_CODE = 5;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        Log.d(TAG, "onCreateView: start.");

        inintView(view);

        Button btnLaunchCamera = (Button) view.findViewById(R.id.button_openCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launch camera.");

                if(((ShareActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if(((ShareActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])){
                        Log.d(TAG, "onClick: start camera");
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }else{
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    private void inintView(View view) {
        customHelper= CustomHelper.of();

        Button14 = (Button)view.findViewById(R.id.Button14);
        Button14.setOnClickListener(this);

        Button15 = (Button)view.findViewById(R.id.Button15);
        Button15.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button14:
                startActivity(new Intent(getActivity(),CameraActivity.class));
                break;

            case R.id.Button15:
                colorArray=new float[]{
                        1.438F,  -0.122F, -0.016F, 0, -0.03F,
                        -0.062F, 1.378F,  -0.016F, 0, 0.05F,
                        -0.062F, -0.122F, 1.483F,  0, -0.02F,
                        0,       0,       0,       1, 0
                };

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempt to navigate to final share screen.");

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            if(isRootTask()){
                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.chosen_bitmap), bitmap);
                    startActivity(intent);
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }else{
                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                    intent.putExtra(getString(R.string.chosen_bitmap), bitmap);
                    intent.putExtra(getString(R.string.backTo_fragment), getString(R.string.edit_profile));
                    startActivity(intent);
                    getActivity().finish();
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }

        }
    }
}

