package piapro.github.io.instax.FavoriteComponents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import piapro.github.io.instax.R;

public class FollowedFragment extends Fragment {
    private static final String TAG = "This is the followed Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container,false);
        return view;
    }
}