package takephoto.uitl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageRotateUtil {

    public static ImageRotateUtil of(){
        return new ImageRotateUtil();
    }

    private ImageRotateUtil() {
    }


    public void correctImage(Context context, Uri path){

        String imagePath= TUriParse.parseOwnUri(context,path);
        int degree;
        if((degree=getBitmapDegree(imagePath))!=0){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            if(bitmap==null)return;
            Bitmap resultBitmap=rotateBitmapByDegree(bitmap,degree);
            if(resultBitmap==null)return;
            try {
                resultBitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(new File(imagePath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private int getBitmapDegree(String path) {
        int degree = 0;
        try {

            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    private Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;


        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {

            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
}
