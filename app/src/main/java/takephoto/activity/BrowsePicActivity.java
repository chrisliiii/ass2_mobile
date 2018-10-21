package takephoto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import piapro.github.io.instax.R;
import takephoto.contents.Constants;
import takephoto.model.Image;


public class BrowsePicActivity extends AppCompatActivity {
    ViewPager viewPager;
    List<ImageView> imageViewList;
    private ArrayList<Image> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsepic);
        inView();
        inData();
    }
    public void inView() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        viewPager=(ViewPager)findViewById(R.id.viewpage);
        images= intent.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
    }
    public void inData() {
        imageViewList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                ImageView image = new ImageView(BrowsePicActivity.this);
                image.setBackgroundColor(0xff000000);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                Glide.with(this)
                        .load(images.get(i).path)
                       .centerCrop().into(image);

                image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageViewList.add(image);
            }
            viewPager.setAdapter(new PictureAdapter());
    }

    class PictureAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewList.get(position));
            return imageViewList.get(position);
        }
    }
    protected int getLayoutId() {
        return R.layout.activity_browsepic;
    }
}
