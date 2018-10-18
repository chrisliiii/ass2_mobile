package piapro.github.io.instax.HomeComponents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import piapro.github.io.instax.R;

public class CameraFragment extends Fragment {
    private static final String TAG = "This is the Camera Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container,false);
        return view;
    }
}