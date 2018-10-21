package piapro.github.io.instax.takeview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import piapro.github.io.instax.HomeComponents.HomeActivity;
import piapro.github.io.instax.R;

public class PreviewActivity extends Activity implements CropImageView.OnCropImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {
private  CropImageView id_iv_preview_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);


         id_iv_preview_photo = (CropImageView) this.findViewById(R.id.id_iv_preview_photo);
        id_iv_preview_photo.setOnSetImageUriCompleteListener(this);
        id_iv_preview_photo.setOnCropImageCompleteListener(this);
        ImageView id_iv_cancel = (ImageView) this.findViewById(R.id.id_iv_cancel);
        ImageView id_iv_ok = (ImageView) this.findViewById(R.id.id_iv_ok);

        id_iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.this.finish();
            }
        });
        id_iv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_iv_preview_photo.getCroppedImageAsync();
            }
        });

        Intent intent = this.getIntent();
        if (intent != null) {
            //byte [] bis=intent.getByteArrayExtra("bitmapByte");

            String filePath = intent.getStringExtra("filePath");
            // Toast.makeText(this, "filePath:"+filePath, Toast.LENGTH_SHORT).show();
            id_iv_preview_photo.setImageBitmap(getBitmapByUrl(filePath));
        } else {
            Toast.makeText(this, "Image loading error", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapByUrl(String url) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(url);
            bitmap = BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bitmap = null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fis = null;
            }
        }

        return bitmap;
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (result.getError() == null) {
            Intent intent = new Intent(PreviewActivity.this, HomeActivity.class);
            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
            if (result.getUri() != null) {
                intent.putExtra("URI", result.getUri());
            } else {

                HomeActivity.mImage =
                        id_iv_preview_photo.getCropShape() == CropImageView.CropShape.OVAL
                                ? CropImage.toOvalBitmap(result.getBitmap())
                                : result.getBitmap();
//                BitmapUtils.saveBitmap(mImage,)
            }
            startActivity(intent);
        } else {
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }
}
