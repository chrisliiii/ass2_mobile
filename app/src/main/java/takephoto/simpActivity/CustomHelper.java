package takephoto.simpActivity;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

import takephoto.app.TakePhoto;
import takephoto.compress.CompressConfig;
import takephoto.model.CropOptions;
import takephoto.model.TakePhotoOptions;

public class CustomHelper{

    private int m_height=800;
    private int m_width=800;
    public static CustomHelper of(){
        return new CustomHelper();
    }
    private CustomHelper() {
    }

    public void setM_widthHeight(int width,int Height){
        m_height=Height;
        m_width=width;
    }



    public void setPicBySelect(TakePhoto takePhoto, int size, boolean isCrop){

        configCompress(takePhoto);
        configTakePhotoOpthion(takePhoto);
        int limit= size;
        if(isCrop){
            takePhoto.onPickMultipleWithCrop(limit,getCropOptions());
        }else {
            takePhoto.onPickMultiple(limit);
        }
    }


    public void setPicByTake(TakePhoto takePhoto, boolean isCrop){
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+ System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOpthion(takePhoto);
        if(isCrop){
            takePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());
        }else {
            takePhoto.onPickFromCapture(imageUri);
        }
    }
    private void configTakePhotoOpthion(TakePhoto takePhoto){
        if(true){
            takePhoto.setTakePhotoOptions(new TakePhotoOptions.Builder().setWithOwnGallery(true).create());
        }
    }
    private void configCompress(TakePhoto takePhoto){
        if(false){
            takePhoto.onEnableCompress(null,false);
            return ;
        }
        int maxSize= 102400;
        int width= 800;
        int height= 800;
        CompressConfig config;
        if(true){
            config=new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width>=height? width:height)
                    .create();
        }else {

        }
        takePhoto.onEnableCompress(config,false);

    }
    private CropOptions getCropOptions(){
        int height= m_height;
        int width= m_width;
        CropOptions.Builder builder=new CropOptions.Builder();


        builder.setWithOwnCrop(false);
        return builder.create();
    }

}
