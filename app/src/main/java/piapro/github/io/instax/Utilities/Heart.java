package piapro.github.io.instax.Utilities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;


public class Heart {

    private static final String TAG = "Heart";

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView whiteHeart, redHeart;

    public Heart(ImageView whiteHeart, ImageView redHeart) {
        this.whiteHeart = whiteHeart;
        this.redHeart = redHeart;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: toggle heart.");

        AnimatorSet animationSet =  new AnimatorSet();


        if(redHeart.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggle red heart off.");
            redHeart.setScaleX(0.1f);
            redHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(redHeart, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(redHeart, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            redHeart.setVisibility(View.GONE);
            whiteHeart.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        else if(redHeart.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling red heart on.");
            redHeart.setScaleX(0.1f);
            redHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(redHeart, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(redHeart, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERATE_INTERPOLATOR);

            redHeart.setVisibility(View.VISIBLE);
            whiteHeart.setVisibility(View.GONE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        animationSet.start();

    }
}
