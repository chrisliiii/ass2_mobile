package takephoto.uitl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import takephoto.model.TException;
import takephoto.model.TExceptionType;


public class TUriParse {
    private static final String TAG = IntentUtils.class.getName();


    public static Uri convertFileUriToFileProviderUri(Context context, Uri uri){
        if(uri==null)return null;
        if(ContentResolver.SCHEME_FILE.equals(uri.getScheme())){
            return getUriForFile(context,new File(uri.getPath()));
        }
        return uri;

    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, TConstant.getFileProviderName(context), file);
    }


    public static Uri getTempUri(Context context){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File file=new File(Environment.getExternalStorageDirectory(), "/images/"+timeStamp + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return getUriForFile(context,file);
    }


    public static String parseOwnUri(Context context, Uri uri){
        if(uri==null)return null;
        String path;
        if(TextUtils.equals(uri.getAuthority(), TConstant.getFileProviderName(context))){
            path=new File(uri.getPath().replace("camera_photos/","")).getAbsolutePath();
        }else {
            path=uri.getPath();
        }
        return path;
    }

    public static String getFilePathWithUri(Uri uri, Activity activity)throws TException {
        if(uri==null){
            Log.w(TAG,"uri is null,activity may have been recovered?");
            throw new TException(TExceptionType.TYPE_URI_NULL);
        }
        File picture=getFileWithUri(uri,activity);
        String picturePath=picture==null? null:picture.getPath();
        if (TextUtils.isEmpty(picturePath))throw new TException(TExceptionType.TYPE_URI_PARSE_FAIL);
        if (!TImageFiles.checkMimeType(activity, TImageFiles.getMimeType(activity,uri)))throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        return picturePath;
    }

    public static File getFileWithUri(Uri uri, Activity activity) {
        String picturePath = null;
        String scheme=uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if(columnIndex>=0){
                picturePath = cursor.getString(columnIndex);
            }else if(TextUtils.equals(uri.getAuthority(), TConstant.getFileProviderName(activity))){
                picturePath=parseOwnUri(activity,uri);
            }
            cursor.close();
        }else if (ContentResolver.SCHEME_FILE.equals(scheme)){
            picturePath=uri.getPath();
        }
        return TextUtils.isEmpty(picturePath)? null:new File(picturePath);
    }

    public static String getFilePathWithDocumentsUri(Uri uri, Activity activity) throws TException {
        if(uri==null){
            Log.e(TAG,"uri is null,activity may have been recovered?");
            return null;
        }
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())&&uri.getPath().contains("document")){
            File tempFile = TImageFiles.getTempFile(activity,uri);
            try {
                TImageFiles.inputStreamToFile(activity.getContentResolver().openInputStream(uri),tempFile);
                return tempFile.getPath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new TException(TExceptionType.TYPE_NO_FIND);
            }
        }else {
            return getFilePathWithUri(uri,activity);
        }
    }
}
