package takephoto.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import takephoto.uitl.TFileUtils;


public class CompressImageUtil{
	private CompressConfig config;
	private Context context;
	Handler mhHandler = new Handler();
	public CompressImageUtil(Context context, CompressConfig config) {
		this.context=context;
		this.config=config==null? CompressConfig.ofDefaultConfig():config;
	}
	public void compress(String imagePath, CompressListener listener) {
		if (config.isEnablePixelCompress()){
			try {
				compressImageByPixel(imagePath,listener);
			} catch (FileNotFoundException e) {
				listener.onCompressFailed(imagePath, String.format("Image compression failed,%s",e.toString()));
				e.printStackTrace();
			}
		}else {
			compressImageByQuality(BitmapFactory.decodeFile(imagePath),imagePath,listener);
		}
	}

	private void compressImageByQuality(final Bitmap bitmap, final String imgPath, final CompressListener listener){
		if(bitmap==null){
			sendMsg(false,imgPath,"Pixel compression failure,bitmap is null",listener);
			return;
		}
		new Thread(new Runnable() {//开启多线程进行压缩处理
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int options = 100;
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				while (baos.toByteArray().length >config.getMaxSize()) {
					baos.reset();
					options -= 5;
					if(options<=5)options=5;
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
					if(options==5)break;
				}
//				if(bitmap!=null&&!bitmap.isRecycled()){
//					bitmap.recycle();
//				}
				try {
					File thumbnailFile=getThumbnailFile(new File(imgPath));
					FileOutputStream fos = new FileOutputStream(thumbnailFile);
					fos.write(baos.toByteArray());
					fos.flush();
					fos.close();
					sendMsg(true, thumbnailFile.getPath(),null,listener);
				} catch (Exception e) {
					sendMsg(false,imgPath,"Mass compression failure",listener);
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void compressImageByPixel(String imgPath, CompressListener listener) throws FileNotFoundException {
		if(imgPath==null){
			sendMsg(false,imgPath,"The file to be compressed does not exist",listener);
			return;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int width = newOpts.outWidth;
		int height = newOpts.outHeight;
		float maxSize =config.getMaxPixel();
		int be = 1;
		if (width >= height && width > maxSize) {
			be = (int) (newOpts.outWidth / maxSize);
			be++;
		} else if (width < height && height > maxSize) {
			be = (int) (newOpts.outHeight / maxSize);
			be++;
		}
		newOpts.inSampleSize =be;
		newOpts.inPreferredConfig = Config.ARGB_8888;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		if (config.isEnableQualityCompress()){
			compressImageByQuality(bitmap,imgPath,listener);
		}else {
			File thumbnailFile=getThumbnailFile(new File(imgPath));
			bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(thumbnailFile));

			listener.onCompressSuccess(thumbnailFile.getPath());
		}
	}

	private void sendMsg(final boolean isSuccess, final String imagePath, final String message, final CompressListener listener){
		mhHandler.post(new Runnable() {
			@Override
			public void run() {
				if (isSuccess){
					listener.onCompressSuccess(imagePath);
				}else{
					listener.onCompressFailed(imagePath,message);
				}
			}
		});
	}
	private File getThumbnailFile(File file){
		if (file==null||!file.exists())return file;
		return TFileUtils.getPhotoCacheDir(context,file);
	}

	public interface CompressListener {

		void onCompressSuccess(String imgPath);


		void onCompressFailed(String imgPath, String msg);
	}
}
