package piapro.github.io.instax.ShareComponents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import piapro.github.io.instax.ProfileComponents.AccountActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.FilePaths;
import piapro.github.io.instax.Utilities.FileSearch;
import piapro.github.io.instax.Utilities.GridImageAdapter;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

    private static final int NUM_GRID_COLUMNS = 3;

    //tools
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar gProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String gAppend = "file:/";
    private String gSelectedImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        galleryImage = (ImageView) view.findViewById(R.id.gallery_view);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        directorySpinner = (Spinner) view.findViewById(R.id.spinner_directory);
        gProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        gProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

        Log.d(TAG, "onCreateView: start.");

        ImageView shareClose = (ImageView) view.findViewById(R.id.closeShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });


        TextView nextScreen = (TextView) view.findViewById(R.id.next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if(isRootTask()){
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.chosen_image), gSelectedImage);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                    intent.putExtra(getString(R.string.chosen_image), gSelectedImage);
                    intent.putExtra(getString(R.string.backTo_fragment), getString(R.string.edit_profile));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        init();

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

    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_gridimage, gAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setImage(imgURLs.get(0), galleryImage, gAppend);
            gSelectedImage = imgURLs.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, gAppend);
                gSelectedImage = imgURLs.get(position);
            }
        });

    }


    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: set image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                gProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                gProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                gProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                gProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}

