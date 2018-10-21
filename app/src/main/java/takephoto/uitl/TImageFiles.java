package takephoto.uitl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import piapro.github.io.instax.R;
import takephoto.model.TException;
import takephoto.model.TExceptionType;

public class TImageFiles {
    private static final String TAG = IntentUtils.class.getName();

    public static void writeToFile(Bitmap bitmap, Uri imageUri) {
        if (bitmap == null) return;
        File file = new File(imageUri.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
            bos.flush();
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) try {
                fos.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void inputStreamToFile(InputStream is, File file) throws TException {
        if (file==null){
            Log.i(TAG,"inputStreamToFile:file not be null");
            throw new TException(TExceptionType.TYPE_WRITE_FAIL);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 10];
            int i;
            while ((i = is.read(buffer)) != -1) {
                fos.write(buffer, 0, i);
            }
        } catch (IOException e) {
            Log.e(TAG,"InputStream error writing file:"+e.toString());
            throw new TException(TExceptionType.TYPE_WRITE_FAIL);
        } finally {
            try {
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static File getTempFile(Activity context, Uri photoUri)throws TException {
        String minType=getMimeType(context, photoUri);
        if (!checkMimeType(context,minType))throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        File filesDir=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!filesDir.exists())filesDir.mkdirs();
        File photoFile = new File(filesDir, UUID.randomUUID().toString() + "." +minType );
        return photoFile;
    }


    public static boolean checkMimeType(Context context, String minType) {
        boolean isPicture= TextUtils.isEmpty(minType)?false:".jpg|.gif|.png|.bmp|.jpeg|.webp|".contains(minType.toLowerCase())?true:false;
        if (!isPicture)
            Toast.makeText(context,context.getResources().getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show();
        return isPicture;
    }


    public static String getMimeType(Activity context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            //If scheme is a content
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(extension))extension= MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            if (TextUtils.isEmpty(extension))extension= MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }
        if(TextUtils.isEmpty(extension)){
            extension=getMimeTypeByFileName(TUriParse.getFileWithUri(uri,context).getName());
        }
        return extension;
    }
    public static String getMimeTypeByFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf("."),fileName.length());
    }
 }