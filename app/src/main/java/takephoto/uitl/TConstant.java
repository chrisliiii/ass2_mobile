package takephoto.uitl;

import android.content.Context;


public class TConstant {



    public final static int RC_CROP = 1001;

    public final static int RC_PICK_PICTURE_FROM_CAPTURE_CROP = 1002;

    public final static int RC_PICK_PICTURE_FROM_CAPTURE = 1003;

    public final static int RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL = 1004;

    public final static int RC_PICK_PICTURE_FROM_DOCUMENTS_CROP = 1005;

    public final static int RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL = 1006;

    public final static int RC_PICK_PICTURE_FROM_GALLERY_CROP = 1007;

    public final static int RC_PICK_MULTIPLE = 1008;


    public final static int PERMISSION_REQUEST_TAKE_PHOTO = 2000;

    public final static String getFileProviderName(Context context){
        return context.getPackageName()+".fileprovider";
    }
 }