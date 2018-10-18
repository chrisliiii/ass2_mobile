package piapro.github.io.instax.FavoriteComponents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import piapro.github.io.instax.R;

public class YourselfFragment extends Fragment {
    private static final String TAG = "This is the yourself Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yourself, container,false);
        return view;
    }
}