package piapro.github.io.instax.Utilities;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class FormatImageView extends AppCompatImageView{

    public FormatImageView(Context context) {

        super(context);
    }

    public FormatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FormatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
