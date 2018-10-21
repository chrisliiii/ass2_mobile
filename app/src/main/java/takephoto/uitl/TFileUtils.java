package takephoto.uitl;

import android.content.Context;
import android.util.Log;

import java.io.File;


public class TFileUtils {
    private static final String TAG="TFileUtils";
    private static String DEFAULT_DISK_CACHE_DIR = "takephoto_cache";
    public static File getPhotoCacheDir(Context context, File file) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File mCacheDir = new File(cacheDir,DEFAULT_DISK_CACHE_DIR);
            if (!mCacheDir.mkdirs() && (!mCacheDir.exists() || !mCacheDir.isDirectory())) {
                return file;
            }else {
                return new File(mCacheDir, file.getName());
            }
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return file;
    }
}
