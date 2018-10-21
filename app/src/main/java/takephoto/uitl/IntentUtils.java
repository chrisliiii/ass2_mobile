package takephoto.uitl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import takephoto.activity.AlbumActivity;
import takephoto.contents.Constants;
import takephoto.model.CropOptions;
import takephoto.model.TContextWrap;


public class IntentUtils {
    private static final String TAG = IntentUtils.class.getName();


    public static Intent getPickMultipleIntent(TContextWrap contextWrap, int limit){
        Intent intent = new Intent(contextWrap.getActivity(), AlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit>0? limit:1);
        return intent;
    }


    public static Intent getCropIntentWithOtherApp(Uri targetUri, Uri outPutUri, CropOptions options) {
        boolean isReturnData = TUtils.isReturnData();
        Log.w(TAG, "getCaptureIntentWithCrop:isReturnData:" + (isReturnData ? "true" : "false"));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(targetUri, "image/*");
        intent.putExtra("crop", "true");
        if (options.getAspectX()*options.getAspectY()>0){
            intent.putExtra("aspectX", options.getAspectX());
            intent.putExtra("aspectY", options.getAspectY());
        }
        if (options.getOutputX()*options.getOutputY()>0){
            intent.putExtra("outputX", options.getOutputX());
            intent.putExtra("outputY", options.getOutputY());
        }
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("return-data", isReturnData);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        return intent;
    }


    public static Intent getCaptureIntent(Uri outPutUri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        return intent;
    }

    public static Intent getPickIntentWithGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");
        return intent;
    }

    public static Intent getPickIntentWithDocuments() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }
}
