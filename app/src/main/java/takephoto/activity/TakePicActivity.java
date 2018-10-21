package takephoto.activity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import takephoto.app.TakePhotoActivity;
import takephoto.model.TImage;
import takephoto.model.TResult;
import takephoto.simpActivity.CustomHelper;


public class TakePicActivity extends TakePhotoActivity {
    private CustomHelper customHelper;
    private List<String> fileNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customHelper= CustomHelper.of();
        fileNameList=new ArrayList<>();
    }

    public void pickPhoto(int width,int height){
        customHelper.setM_widthHeight(width,height);
        pickPhoto();
    }

    public void takePhoto(int width,int height){
        customHelper.setM_widthHeight(width,height);
        takePhoto();
    }

    public void pickPhoto() {
        customHelper.setPicBySelect(getTakePhoto(),1,true);
    }

    public void pickPhoto(int limit,boolean isCorp) {
        customHelper.setPicBySelect(getTakePhoto(),limit,isCorp);
    }
    public void takePhoto(boolean isCorp) {
        customHelper.setPicByTake(getTakePhoto(),isCorp);
    }
    public void takePhoto() {
        customHelper.setPicByTake(getTakePhoto(),true);
    }

    public void upLoadFile(String filePath){

    }

    public void upLoadFile(List<String> filePath){

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

    private void showImg(ArrayList<TImage> images) {
        fileNameList.clear();
        for (int i = 0; i <images.size() ; i++) {
            fileNameList.add(images.get(i).getPath());
        }
        upLoadFile(fileNameList);
        if (images.size()==1)
            upLoadFile(images.get(0).getPath());
    }


}
