package takephoto.model;

import android.net.Uri;

import java.io.Serializable;


public class TImage implements Serializable {
    private String path;
    private boolean cropped;
    private boolean compressed;
    public static TImage of(String path){
        return new TImage(path);
    }
    public static TImage of(Uri uri){
        return new TImage(uri);
    }
    private TImage(String path) {
        this.path = path;
    }
    private TImage(Uri uri) {
        this.path = uri.getPath();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
}
