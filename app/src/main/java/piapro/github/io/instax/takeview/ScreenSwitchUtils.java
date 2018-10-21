package piapro.github.io.instax.takeview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ScreenSwitchUtils {

    private static final String TAG = ScreenSwitchUtils.class.getSimpleName();

    private volatile static ScreenSwitchUtils mInstance;

    private Activity mActivity;


    private boolean isPortrait = true;

    public static final int ORIENTATION_HEAD_IS_UP = 0;
    public static final int ORIENTATION_HEAD_IS_LEFT = 1;
    public static final int ORIENTATION_HEAD_IS_RIGHT = 2;
    public static final int ORIENTATION_HEAD_IS_DOWN = 3;

    private int orientationState = ORIENTATION_HEAD_IS_UP;
    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;

    private SensorManager sm1;
    private Sensor sensor1;
    private OrientationSensorListener1 listener1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 88888:
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135) {
                        orientationState = ORIENTATION_HEAD_IS_RIGHT;
                    } else if (orientation > 135 && orientation < 225) {
                        orientationState = ORIENTATION_HEAD_IS_DOWN;
                    } else if (orientation > 225 && orientation < 315) {
                        if (isPortrait) {
                            Log.d(TAG, "Switch to landscape");
                            //### mActivity.setRequestedOrientation(0);
                            isPortrait = false;
                        }
                        orientationState = ORIENTATION_HEAD_IS_LEFT;
                    } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                        if (!isPortrait) {
                            Log.d(TAG, "Switch to vertical screen");
                            //### mActivity.setRequestedOrientation(1);
                            isPortrait = true;
                        }
                        orientationState = ORIENTATION_HEAD_IS_UP;
                    }
                    break;
                default:
                    break;
            }

        }

        ;
    };

    /**
     *
     **/
    public static ScreenSwitchUtils init(Context context) {
        if (mInstance == null) {
            synchronized (ScreenSwitchUtils.class) {
                if (mInstance == null) {
                    mInstance = new ScreenSwitchUtils(context);
                }
            }
        }
        return mInstance;
    }

    private ScreenSwitchUtils(Context context) {
        Log.d(TAG, "init orientation listener");

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(mHandler);


        sm1 = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener1 = new OrientationSensorListener1();
    }

    /**
     *
     */
    public void start(Activity activity) {
        Log.d(TAG, "start orientation listener");
        mActivity = activity;
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     *
     */
    public void stop() {
        Log.d(TAG, "stop orientation listener");
        sm.unregisterListener(listener);
        sm1.unregisterListener(listener1);
    }

    /**
     *
     */
    public void toggleScreen() {
        sm.unregisterListener(listener);
        sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);
        if (isPortrait) {
            isPortrait = false;

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            isPortrait = true;

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public boolean isPortrait() {
        return this.isPortrait;
    }

    public int getOrientationState() {
        return orientationState;
    }

    /**
     *
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {

                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (rotateHandler != null) {
                rotateHandler.obtainMessage(88888, orientation, 0).sendToTarget();
            }
        }
    }

    public class OrientationSensorListener1 implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public OrientationSensorListener1() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {

                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation > 225 && orientation < 315) {
                if (!isPortrait) {
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                if (isPortrait) {
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            }
        }
    }
}