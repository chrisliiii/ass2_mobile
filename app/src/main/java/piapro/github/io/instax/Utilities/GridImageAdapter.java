package piapro.github.io.instax.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import piapro.github.io.instax.R;

public class GridImageAdapter extends ArrayAdapter<String>{

    private Context gContext;
    private LayoutInflater gInflater;
    private int layoutRes;
    private String gAppend;
    private ArrayList<String> imageURLs;

    public GridImageAdapter(Context gContext, int layoutRes, String gAppend, ArrayList<String> imageURLs) {

        super(gContext, layoutRes, imageURLs);
        gInflater = (LayoutInflater) gContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gContext = gContext;
        this.layoutRes = layoutRes;
        this.gAppend = gAppend;
        this.imageURLs = imageURLs;
    }

    private static class ViewHolder{

        ProgressBar gProgressBar;
        FormatImageView gridImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder gridHolder;
        if(convertView == null){
            convertView = gInflater.inflate(layoutRes, parent, false);
            gridHolder = new ViewHolder();
            gridHolder.gProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            gridHolder.gridImage = (FormatImageView) convertView.findViewById(R.id.gridImage);

            convertView.setTag(gridHolder);
        }
        else{
            gridHolder = (ViewHolder) convertView.getTag();
        }

        String imageURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(gAppend + imageURL, gridHolder.gridImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

                if(gridHolder.gProgressBar != null){
                    gridHolder.gProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                if(gridHolder.gProgressBar != null){
                    gridHolder.gProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                if(gridHolder.gProgressBar != null){
                    gridHolder.gProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                if(gridHolder.gProgressBar != null){
                    gridHolder.gProgressBar.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }
}
