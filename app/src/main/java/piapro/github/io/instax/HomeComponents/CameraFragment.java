package piapro.github.io.instax.HomeComponents;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import piapro.github.io.instax.R;
import piapro.github.io.instax.utils.BitmapUtils;
import piapro.github.io.instax.view.fliter_lib.ColorView;
import takephoto.app.TakePhotoFragment;
import takephoto.model.TImage;
import takephoto.model.TResult;
import takephoto.simpActivity.CustomHelper;

public class CameraFragment extends TakePhotoFragment implements View.OnClickListener {
    private static final String TAG = "This is the Camera Fragment";
    private Button Button1,Button2,Button3,Button4,Button5,Button6,Button11,Button22;
    private ColorView colorView = null;
    private float colorArray[] = null;
    private Bitmap bitmap = null;
    public Bitmap mImage = null;

    private CustomHelper customHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container,false);
        inintView(view);
        inintdata();
        return view;
    }

    private void inintdata() {
    }

    private void inintView(View view) {

        customHelper= CustomHelper.of();

        Button1 = (Button) view.findViewById(R.id.Button1);
        Button1.setOnClickListener(this);

        Button11 = (Button) view.findViewById(R.id.Button11);
        Button11.setOnClickListener(this);

        Button22 = (Button) view.findViewById(R.id.Button22);
        Button22.setOnClickListener(this);

        Button2 = (Button) view. findViewById(R.id.Button2);
        Button2.setOnClickListener(this);

        Button3 = (Button) view. findViewById(R.id.Button3);
        Button3.setOnClickListener(this);

        Button4 = (Button)  view.findViewById(R.id.Button4);
        Button4.setOnClickListener(this);

        Button5 = (Button)  view.findViewById(R.id.Button5);
        Button5.setOnClickListener(this);

        Button6 = (Button) view. findViewById(R.id.Button6);
        Button6.setOnClickListener(this);

        colorView = (ColorView) view. findViewById(R.id.myColorView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button1:
                colorArray=new float[]{
                        0.33F,	0.59F,	0.11F,	0,	0,
                        0.33F,	0.59F,	0.11F,	0,	0,
                        0.33F,	0.59F,	0.11F,	0,	0,
                        0,	0,	0,	1,	0
                };
                colorView.setColorArray(colorArray);
                break;
            case R.id.Button2:
                colorArray=new float[]{

                        -1,	0,	0,	1,	1,
                        0,	-1,	0,	1,	1,
                        0,	0,	-1,	1,	1,
                        0,	0,	0,	1,	0
                };
                colorView.setColorArray(colorArray);
                break;
            case R.id.Button3:
                colorArray=new float[]{
                        0.393F,	0.769F,	0.189F,	0,	0,
                        0.349F,	0.686F,	0.168F,	0,	0,
                        0.272F,	0.534F,	0.131F,	0,	0,
                        0,		0,		0,		1,	0
                };
                colorView.setColorArray(colorArray);
                break;
            case R.id.Button4:
                colorArray=new float[]{
                        1.5F, 1.5F, 1.5F, 0, -1,
                        1.5F, 1.5F, 1.5F, 0, -1,
                        1.5F, 1.5F, 1.5F, 0, -1,
                        0,    0,    0,    1, 0
                };
                colorView.setColorArray(colorArray);
                break;
            case R.id.Button5:
                colorArray=new float[]{
                        1.438F,  -0.122F, -0.016F, 0, -0.03F,
                        -0.062F, 1.378F,  -0.016F, 0, 0.05F,
                        -0.062F, -0.122F, 1.483F,  0, -0.02F,
                        0,       0,       0,       1, 0
                };
                colorView.setColorArray(colorArray);
                break;
            case R.id.Button6:
                colorArray=new float[]{
                        1, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 1, 0,
                };
                colorView.setColorArray(colorArray);
                break;
                case R.id.Button11:
                    customHelper.setPicByTake(getTakePhoto(),false);
                break;
                case R.id.Button22:
                    customHelper.setPicBySelect(getTakePhoto(),1,false);
                break;
        }

    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result,msg);
    }
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    public void setmImage(Bitmap mImage) {
        if (mImage != null) {
            this.mImage = mImage;
        }
    }

    private void showImg(ArrayList<TImage> images) {
      bitmap=  BitmapUtils.autoFitSizePic(new File(images.get(0).getPath()));
        colorView.setBitmap(bitmap);
    }
}