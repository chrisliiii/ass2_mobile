package takephoto.compress;




import java.util.ArrayList;

import takephoto.model.TImage;


public interface CompressImage {
    void compress();


    interface CompressListener {

        void onCompressSuccess(ArrayList<TImage> images);


        void onCompressFailed(ArrayList<TImage> images, String msg);
    }
}
