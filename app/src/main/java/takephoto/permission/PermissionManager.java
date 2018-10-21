package takephoto.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import piapro.github.io.instax.R;
import takephoto.app.TakePhoto;
import takephoto.model.InvokeParam;
import takephoto.model.TContextWrap;
import takephoto.uitl.TConstant;


public class PermissionManager {
    public enum TPermission {
        STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        CAMERA(Manifest.permission.CAMERA);
        String stringValue;

        TPermission(String stringValue) {
            this.stringValue = stringValue;
        }

        public String stringValue() {
            return stringValue;
        }
    }

    public enum TPermissionType {
        GRANTED("Authorized"),
        DENIED("Unauthorized"),
        WAIT("Wait for authorization"),
        NOT_NEED("No authorization"),
        ONLY_CAMERA_DENIED("No photo permissions"),
        ONLY_STORAGE_DENIED("No access to SD card.");
        String stringValue;

        TPermissionType(String stringValue) {
            this.stringValue = stringValue;
        }

        public String stringValue() {
            return stringValue;
        }
    }
    private final static String[]methodNames={
            "onPickFromCapture",
            "onPickFromCaptureWithCrop",
            "onPickMultiple",
            "onPickMultipleWithCrop",
            "onPickFromDocuments",
            "onPickFromDocumentsWithCrop",
            "onPickFromGallery",
            "onPickFromGalleryWithCrop",
            "onCrop"
    };

    public static TPermissionType checkPermission(@NonNull TContextWrap contextWrap, @NonNull Method method) {
        String methodName = method.getName();
        boolean contain=false;
        for(int i=0,j=methodNames.length;i<j;i++){
            if(TextUtils.equals(methodName,methodNames[i])){
                contain=true;
                break;
            }
        }
        if(!contain)return TPermissionType.NOT_NEED;

        boolean cameraGranted = true, storageGranted = ContextCompat.checkSelfPermission(contextWrap.getActivity(), TPermission.STORAGE.stringValue()) == PackageManager.PERMISSION_GRANTED ? true : false;

        if (TextUtils.equals(methodName, "onPickFromCapture") || TextUtils.equals(methodName, "onPickFromCaptureWithCrop")) {
            cameraGranted = ContextCompat.checkSelfPermission(contextWrap.getActivity(), TPermission.CAMERA.stringValue()) == PackageManager.PERMISSION_GRANTED ? true : false;
        }

        boolean granted = storageGranted && cameraGranted;
        if (!granted) {
            ArrayList<String> permissions = new ArrayList<>();
            if (!storageGranted) permissions.add(TPermission.STORAGE.stringValue());
            if (!cameraGranted) permissions.add(TPermission.CAMERA.stringValue());
            requestPermission(contextWrap,permissions.toArray(new String[permissions.size()]));
        }
        return granted ? TPermissionType.GRANTED : TPermissionType.WAIT;
    }

    public static void requestPermission(@NonNull TContextWrap contextWrap, @NonNull String[] permissions) {
        if(contextWrap.getFragment()!=null){
            contextWrap.getFragment().requestPermissions(permissions, TConstant.PERMISSION_REQUEST_TAKE_PHOTO);
        }else{
            ActivityCompat.requestPermissions(contextWrap.getActivity(), permissions, TConstant.PERMISSION_REQUEST_TAKE_PHOTO);
        }
    }

    public static TPermissionType onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == TConstant.PERMISSION_REQUEST_TAKE_PHOTO) {
            boolean cameraGranted = true, storageGranted = true;
            for (int i = 0, j = permissions.length; i < j; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (TextUtils.equals(TPermission.STORAGE.stringValue(),permissions[i])) {
                        storageGranted = false;
                    } else if (TextUtils.equals(TPermission.CAMERA.stringValue(),permissions[i])) {
                        cameraGranted = false;
                    }
                }
            }
            if (cameraGranted && storageGranted) return TPermissionType.GRANTED;
            if (!cameraGranted && storageGranted) return TPermissionType.ONLY_CAMERA_DENIED;
            if (!storageGranted && cameraGranted) return TPermissionType.ONLY_STORAGE_DENIED;
            if(!storageGranted&&!cameraGranted)return TPermissionType.DENIED;
        }
        return TPermissionType.WAIT;
    }
    public static void handlePermissionsResult(Activity activity, TPermissionType type, InvokeParam invokeParam, TakePhoto.TakeResultListener listener){
                String tip=null;
                switch (type){
                    case DENIED:
                        listener.takeFail(null,tip=activity.getResources().getString(R.string.tip_permission_camera_storage));
                        break;
                    case ONLY_CAMERA_DENIED:
                        listener.takeFail(null,tip=activity.getResources().getString(R.string.tip_permission_camera));
                        break;
                    case ONLY_STORAGE_DENIED:
                        listener.takeFail(null,tip=activity.getResources().getString(R.string.tip_permission_storage));
                        break;
                    case GRANTED:
                        try {
                            invokeParam.getMethod().invoke(invokeParam.getProxy(),invokeParam.getArgs());
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.takeFail(null,tip=activity.getResources().getString(R.string.tip_permission_camera_storage));
                        }
                        break;
            default:
                break;
        }
        if(tip!=null) Toast.makeText(activity,tip, Toast.LENGTH_LONG).show();

    }
}