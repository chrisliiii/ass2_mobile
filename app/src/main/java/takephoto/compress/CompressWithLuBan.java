package takephoto.compress;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import takephoto.model.LubanOptions;
import takephoto.model.TImage;


public class CompressWithLuBan implements CompressImage {
    private ArrayList<TImage> images;
    private CompressListener listener;
    private Context context;
    private LubanOptions options;
    private ArrayList<File> files = new ArrayList<>();

    public CompressWithLuBan(Context context, CompressConfig config, ArrayList<TImage> images, CompressListener listener) {
        options=config.getLubanOptions();
        this.images = images;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void compress() {
        if (images == null || images.isEmpty()) {
            listener.onCompressFailed(images, " images is null");
            return;
        }
        for (TImage image : images) {
            if (image == null) {
                listener.onCompressFailed(images, " There are pictures of compress  is null.");
                return;
            }
            files.add(new File(image.getPath()));
        }
        if (images.size() == 1) {
            compressOne();
        } else {
            compressMulti();
        }
    }

    private void compressOne() {

    }

    private void compressMulti() {

    }

    private void handleCompressCallBack(List<File> files) {
        for (int i = 0, j = images.size(); i < j; i++) {
            TImage image=images.get(i);
            image.setCompressed(true);
            image.setPath(files.get(i).getPath());
        }
        listener.onCompressSuccess(images);
    }
}
