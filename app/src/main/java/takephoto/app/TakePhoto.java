package takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import takephoto.compress.CompressConfig;
import takephoto.model.CropOptions;
import takephoto.model.MultipleCrop;
import takephoto.model.TException;
import takephoto.model.TResult;
import takephoto.model.TakePhotoOptions;
import takephoto.permission.PermissionManager;


public interface TakePhoto {

    void onPickMultiple(int limit);

    void onPickMultipleWithCrop(int limit, CropOptions options);

    void onPickFromDocuments();

    void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options);

    void onPickFromGallery();

    void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);


    void onPickFromCapture(Uri outPutUri);

    void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options);


    void onCrop(Uri imageUri, Uri outPutUri, CropOptions options)throws TException;

    void onCrop(MultipleCrop multipleCrop, CropOptions options)throws TException;
    void permissionNotify(PermissionManager.TPermissionType type);

    void onEnableCompress(CompressConfig config, boolean showCompressDialog);


    void setTakePhotoOptions(TakePhotoOptions options);
    void onCreate(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    interface TakeResultListener {
        void takeSuccess(TResult result);

        void takeFail(TResult result, String msg);

        void takeCancel();
    }
}