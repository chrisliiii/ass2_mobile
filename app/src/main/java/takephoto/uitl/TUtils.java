package takephoto.uitl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import piapro.github.io.instax.R;
import takephoto.model.CropOptions;
import takephoto.model.Image;
import takephoto.model.TContextWrap;
import takephoto.model.TException;
import takephoto.model.TExceptionType;
import takephoto.model.TImage;
import takephoto.model.TIntentWap;


public class TUtils {
    private static final String TAG = IntentUtils.class.getName();



    public static ArrayList<Uri> convertImageToUri(Context context, ArrayList<Image> images) throws TException {
        ArrayList<Uri> uris=new ArrayList();
        for(Image image:images){
            uris.add(FileProvider.getUriForFile(context, TConstant.getFileProviderName(context), new File(image.path)));
        }
        return uris;
    }

    public static ArrayList<TImage> getTImagesWithImages(ArrayList<Image> images){
        ArrayList<TImage> tImages=new ArrayList();
        for(Image image:images){
            tImages.add(TImage.of(image.path));
        }
        return tImages;
    }

    public static ArrayList<TImage> getTImagesWithUris(ArrayList<Uri> uris){
        ArrayList<TImage> tImages=new ArrayList();
        for(Uri uri:uris){
            tImages.add(TImage.of(uri));
        }
        return tImages;
    }

    public static void startActivityForResult(TContextWrap contextWrap, TIntentWap intentWap){
        if (contextWrap.getFragment()!=null){
            contextWrap.getFragment().startActivityForResult(intentWap.getIntent(),intentWap.getRequestCode());
        }else {
            contextWrap.getActivity().startActivityForResult(intentWap.getIntent(),intentWap.getRequestCode());
        }
    }

    public static void sendIntentBySafely(TContextWrap contextWrap, List<TIntentWap> intentWapList, int defaultIndex, boolean isCrop)throws TException {
        if (defaultIndex+1>intentWapList.size())throw new TException(isCrop? TExceptionType.TYPE_NO_MATCH_PICK_INTENT: TExceptionType.TYPE_NO_MATCH_CROP_INTENT);
        TIntentWap intentWap=intentWapList.get(defaultIndex);
        List result=contextWrap.getActivity().getPackageManager().queryIntentActivities(intentWap.getIntent(), PackageManager.MATCH_ALL);
        if (result.isEmpty()){
            sendIntentBySafely(contextWrap,intentWapList,++defaultIndex,isCrop);
        }else {
            startActivityForResult(contextWrap,intentWap);
        }
    }

    public static void captureBySafely(TContextWrap contextWrap, TIntentWap intentWap)throws TException {
        List result=contextWrap.getActivity().getPackageManager().queryIntentActivities(intentWap.getIntent(), PackageManager.MATCH_ALL);
        if (result.isEmpty()){
            Toast.makeText(contextWrap.getActivity(),contextWrap.getActivity().getResources().getText(R.string.tip_no_camera), Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NO_CAMERA);
        }else {
            startActivityForResult(contextWrap,intentWap);
        }
    }

    public static void cropWithOtherAppBySafely(TContextWrap contextWrap, Uri imageUri, Uri outPutUri, CropOptions options){
        Intent nativeCropIntent= IntentUtils.getCropIntentWithOtherApp(imageUri, outPutUri,options);
        List result=contextWrap.getActivity().getPackageManager().queryIntentActivities(nativeCropIntent, PackageManager.MATCH_ALL);
        if (result.isEmpty()){
            cropWithOwnApp(contextWrap,imageUri,outPutUri,options);
        }else {
            startActivityForResult(contextWrap,new TIntentWap(IntentUtils.getCropIntentWithOtherApp(imageUri, outPutUri,options), TConstant.RC_CROP));
        }
    }

    public static void cropWithOwnApp(TContextWrap contextWrap, Uri imageUri, Uri outPutUri, CropOptions options){

    }

    public static boolean isReturnData() {
        String release= Build.VERSION.RELEASE;
        int sdk= Build.VERSION.SDK_INT;
        Log.i(TAG,"release:"+release+"sdk:"+sdk);
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            if (manufacturer.toLowerCase().contains("lenovo")) {
                return true;
            }
        }

        return false;
    }

    public static ProgressDialog showProgressDialog(final Activity activity,
                                                    String... progressTitle) {
        if(activity==null||activity.isFinishing())return null;
        String title = activity.getResources().getString(R.string.tip_tips);
        if (progressTitle != null && progressTitle.length > 0)
            title = progressTitle[0];
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
