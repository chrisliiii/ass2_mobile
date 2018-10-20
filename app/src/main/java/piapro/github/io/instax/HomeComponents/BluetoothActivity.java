package piapro.github.io.instax.HomeComponents;
/**
 * @Author AohengMa 888788 Oct 15,2018
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import piapro.github.io.instax.R;
import piapro.github.io.instax.ShareComponents.NextActivity;
import piapro.github.io.instax.Utilities.MethodFirebase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";

    Button btn_Send,btn_Listen,btn_ListDevice,btn_Upload ;
    ListView listDevices;
    TextView status_view;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    ImageView imageView;

    BluetoothCommunication btCommunication;

    static final int STATE_LISTENING = 0;
    static final int STATE_CONNECTING = 1;
    static final int STATE_CONNECTED = 2;
    static final int STATE_CONNECTION_FAILED = 3;
    static final int STATE_MESSAGE_RECEIVED = 4;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "instax";
    private static final UUID DEVICE_UUID = UUID.fromString("10991D89-03EA-4838-99F8-0B34A6A9B3C8");
    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Bitmap bitmap;
    private BitmapDrawable bitmapDrawable;

    //firebase
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener nAuthListener;
    private FirebaseDatabase nFirebaseDatabase;
    private DatabaseReference nRef;
    private MethodFirebase nMethodFirebase;

    private int imageCount = 0;
    private Intent intent;
    private String imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*
        if(bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(),"ERROR: This device does not support Bluetooth",Toast.LENGTH_LONG).show();
        }else{
            if (!bluetoothAdapter.isEnabled()) {
                Intent enabledIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enabledIntent, REQUEST_ENABLE_BLUETOOTH);
                //implementListeners();
            }
            else {Toast.makeText(getApplicationContext(), "Please turn Bluetooth on first!", Toast.LENGTH_LONG).show();}
        }
    */
            if (!bluetoothAdapter.isEnabled()) {
                Intent enabledIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enabledIntent, REQUEST_ENABLE_BLUETOOTH);
            }

        implementListeners();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"Permission Granted ", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission NOT Granted ", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                }
        }
    }

    private void implementListeners() {
        //Request permission for accessing internal storage
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        }

        btn_ListDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] deviceCollection = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        deviceCollection[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceCollection);
                    listDevices.setAdapter(arrayAdapter);
                }
            }
        });



        btn_Listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothServer btServer = new BluetoothServer();
                btServer.start();
            }
        });


        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BluetoothClient bluetoothClient = new BluetoothClient(btArray[position]);
                bluetoothClient.start();
                status_view.setText("Connecting");

            }
        });

        //Upload photo action listener
        btn_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,RESULT_LOAD_IMAGE);
            }
        });


        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String caption = mCaption.setText("From Bluetooth").toString(); //show hash tag as BT
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test1);


                // Retrieve image from imageView to bitmap
                bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
                if(bitmapDrawable==null){
                    imageView.buildDrawingCache();
                    bitmap = imageView.getDrawingCache();
                    imageView.buildDrawingCache(false);
                }else
                {
                    bitmap = bitmapDrawable .getBitmap();
                }

                //Convert bitmap to stream
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
                byte[] imageBytes = stream.toByteArray();

                int subArraySize = 400;
                btCommunication.write(String.valueOf(imageBytes.length).getBytes());
                //Write to the receiver
                for(int i = 0; i < imageBytes.length; i += subArraySize){
                    byte[] tempArray;
                    tempArray = Arrays.copyOfRange(imageBytes, i, Math.min(imageBytes.length, i + subArraySize));
                    btCommunication.write(tempArray);
                }

            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageView.setDrawingCacheEnabled(true);
                        Bitmap imageBitmap = imageView.getDrawingCache();
                        if (imageBitmap != null) {
                            new saveImageTask().execute(imageBitmap);

                        }
                    }
                });
                builder.show();

                return true;
            }
        });




    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case STATE_LISTENING:
                    status_view.setText("Listening");
                    break;

                case STATE_CONNECTING:
                    status_view.setText("Connecting");
                    break;

                case STATE_CONNECTED:
                    status_view.setText("Connected");
                    break;

                case STATE_CONNECTION_FAILED:
                    status_view.setText("Connection failed");
                    break;

                case STATE_MESSAGE_RECEIVED:
                    status_view.setText("Message Received");
                    byte[] readBuffer = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(readBuffer, 0, msg.arg1);
                    imageView.setImageBitmap(bitmap);//upload image view




                    break;
            }
            return true;
        }
    });

    private void findViewByIdes() {
        btn_Listen = (Button) findViewById(R.id.btn_Listen);
        btn_ListDevice = (Button) findViewById(R.id.btn_ListDevice);
        btn_Send = (Button) findViewById(R.id.btn_Send);
        listDevices = (ListView) findViewById(R.id.list_devices);
        status_view = (TextView) findViewById(R.id.status_view);
        btn_Upload = (Button) findViewById(R.id.btn_Upload);
        imageView = (ImageView) findViewById(R.id.pic_view);

    }


    //Bluetooth client class
    private class BluetoothClient extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public BluetoothClient(BluetoothDevice device1) {
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                btCommunication = new BluetoothCommunication(socket);
                btCommunication.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    //Send and Receive data via Bluetooth
    private class BluetoothCommunication extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public BluetoothCommunication (BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream input = null;
            OutputStream output = null;

            try {
                input = bluetoothSocket.getInputStream();
                output = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = input;
            outputStream = output;
        }

        public void run(){

            byte[] buffer = null;
            int numberOfBytes = 0;
            int index = 0;
            boolean flag = true;

            while(true){
                if(flag){
                    try {
                        byte[] temp = new byte[inputStream.available()];
                        if(inputStream.read(temp) > 0){
                            numberOfBytes = Integer.parseInt(new String(temp,"UTF-8"));
                            buffer = new byte [numberOfBytes];
                            flag = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    try {
                        byte[] data = new  byte[inputStream.available()];
                        int numbers = inputStream.read(data);

                        System.arraycopy(data, 0, buffer, index, numbers);
                        index = index + numbers;

                        if(index == numberOfBytes){
                            handler.obtainMessage(STATE_MESSAGE_RECEIVED,numberOfBytes,-1, buffer).sendToTarget();
                            flag = true;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Setup Bluetooth Server Socket
    private class BluetoothServer extends Thread {
        private BluetoothServerSocket serverSocket;

        public BluetoothServer() {
            try {
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, DEVICE_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;
            while(socket == null){
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;

                    handler.sendMessage(message);

                }

                if(socket!=null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    //SEND/RECEIVE MSG LOGIC HERE
                    btCommunication = new BluetoothCommunication(socket);
                    btCommunication.start();
                    break;
                }

            }
        }

    }

    private class saveImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... params) {
            String result = getResources().getString(R.string.save_picture_failed);
            try {
                String externalStorage = Environment.getExternalStorageDirectory().toString();

                File file = new File(externalStorage + "/DCIM/Camera");
                if (!file.exists()) {
                    file.mkdirs();
                }

                File imageFile = new File(file.getAbsolutePath(),new Date().getTime()+".jpg");
                FileOutputStream outStream = null;
                outStream = new FileOutputStream(imageFile);
                Bitmap image = params[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                result = getResources().getString(R.string.save_picture_success,  file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            imageView.setDrawingCacheEnabled(false);
        }
    }

    //firebase part
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        nAuth = FirebaseAuth.getInstance();
        nFirebaseDatabase = FirebaseDatabase.getInstance();
        nRef = nFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        nAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        nRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = nMethodFirebase.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        //nAuth.addAuthStateListener(nAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (nAuthListener != null) {
            nAuth.removeAuthStateListener(nAuthListener);
        }
    }





}
