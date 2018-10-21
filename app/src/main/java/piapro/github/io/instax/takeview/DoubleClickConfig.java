package piapro.github.io.instax.takeview;

import android.os.Handler;
import android.os.Message;
import android.view.View;

public class DoubleClickConfig {
    public interface OnDoubleClickListener {
        public void OnSingleClick(View v);

        public void OnDoubleClick(View v);
    }

    /**

     */
    public static void registerDoubleClickListener(View view, final OnDoubleClickListener listener) {
        if (listener == null) return;
        view.setOnClickListener(new View.OnClickListener() {
            private static final int DOUBLE_CLICK_TIME = 350;
            private boolean waitDouble = true;

            private Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    listener.OnSingleClick((View) msg.obj);
                }

            };


            public void onClick(final View v) {
                if (waitDouble) {
                    waitDouble = false;
                    new Thread() {

                        public void run() {
                            try {
                                Thread.sleep(DOUBLE_CLICK_TIME);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (!waitDouble) {

                                waitDouble = true;
                                Message msg = handler.obtainMessage();
                                msg.obj = v;
                                handler.sendMessage(msg);
                            }
                        }

                    }.start();
                } else {
                    waitDouble = true;
                    listener.OnDoubleClick(v);
                }
            }
        });
    }

}
