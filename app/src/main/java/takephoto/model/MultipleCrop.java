package takephoto.model;

import android.app.Activity;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import takephoto.uitl.TImageFiles;
import takephoto.uitl.TUtils;


public class MultipleCrop {
    private ArrayList<Uri> uris;
    private ArrayList<Uri> outUris;
    private ArrayList<TImage> tImages;
    public boolean hasFailed;
    public static MultipleCrop of(ArrayList<Uri> uris, Activity activity) throws TException {
        return new MultipleCrop(uris,activity);
    }
    public static MultipleCrop of(ArrayList<Uri> uris, ArrayList<Uri> outUris){
        return new MultipleCrop(uris,outUris);
    }
    private MultipleCrop(ArrayList<Uri> uris, Activity activity) throws TException {
        this.uris=uris;
        ArrayList<Uri> outUris=new ArrayList<>();
        for (Uri uri:uris){
            outUris.add(Uri.fromFile(TImageFiles.getTempFile(activity,uri)));
        }
        this.outUris=outUris;
        this.tImages= TUtils.getTImagesWithUris(outUris);
    }
    private MultipleCrop(ArrayList<Uri> uris, ArrayList<Uri> outUris) {
        this.uris=uris;
        this.outUris=outUris;
        this.tImages= TUtils.getTImagesWithUris(outUris);
    }

    public ArrayList<Uri> getUris() {
        return uris;
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris = uris;
    }

    public ArrayList<Uri> getOutUris() {
        return outUris;
    }

    public void setOutUris(ArrayList<Uri> outUris) {
        this.outUris = outUris;
    }

    public ArrayList<TImage> gettImages() {
        return tImages;
    }

    public void settImages(ArrayList<TImage> tImages) {
        this.tImages = tImages;
    }


    public Map setCropWithUri(Uri uri, boolean cropped){
        if(!cropped)hasFailed=true;
        int index=outUris.indexOf(uri);
        tImages.get(index).setCropped(cropped);
        Map result=new HashMap();
        result.put("index",index);
        result.put("isLast",index==outUris.size()-1? true:false);
        return result;
    }
}
