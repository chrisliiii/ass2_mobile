package piapro.github.io.instax.view.fliter_lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import piapro.github.io.instax.R;


public class ColorView extends ImageView {

    private Paint myPaint = null;
    private Bitmap bitmap = null;
    private ColorMatrix myColorMatrix = null;
    private float[] colorArray = {
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 1, 0,};

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        myPaint = new Paint();

        canvas.drawBitmap(bitmap, 0, 0, myPaint);

        myColorMatrix = new ColorMatrix();

        myColorMatrix.set(colorArray);

        myPaint.setColorFilter(new ColorMatrixColorFilter(myColorMatrix));

        canvas.drawBitmap(bitmap, 0, 0, myPaint);
        invalidate();
    }


    public void setColorArray(float[] colorArray) {
        this.colorArray = colorArray;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}