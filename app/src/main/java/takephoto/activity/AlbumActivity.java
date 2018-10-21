package takephoto.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import lib.SlidingMenu;
import piapro.github.io.instax.R;
import piapro.github.io.instax.view.TitleWidget;
import takephoto.adapter.CustomAlbumSelectAdapter;
import takephoto.adapter.CustomImageSelectAdapter;
import takephoto.contents.Constants;
import takephoto.model.Album;
import takephoto.model.Image;


public class AlbumActivity extends HelperActivity implements View.OnClickListener {
    SlidingMenu menu;


    private ArrayList<Image> images;
    private String album;

    private TextView errorDisplay;

    private ProgressBar progressBar;
    private GridView imselect_gridView;
    private TitleWidget menu_title;
    private TitleWidget title;
    private CustomImageSelectAdapter imageSelectAdapter;

    private int countSelected;


    private ArrayList<Album> albums;
    private ListView gridView;

    private TextView tv_look;

    private AppCompatButton acbtn_sure;
    private int in_dex;
    private CustomAlbumSelectAdapter adapter;
    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
    };
    private final String[] imgprojection = new String[]{ MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        setView(findViewById(R.id.layout_image_select));
        inintSildMenu();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        Constants.limit = intent.getIntExtra(Constants.INTENT_EXTRA_LIMIT, Constants.DEFAULT_LIMIT);
        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        title= (TitleWidget) findViewById(R.id.title);

        tv_look= (TextView) findViewById(R.id.tv_look);
        acbtn_sure= (AppCompatButton) findViewById(R.id.acbtn_sure);
        tv_look.setOnClickListener(this);
        acbtn_sure.setOnClickListener(this);
        errorDisplay.setVisibility(View.INVISIBLE);
//        title.setBackMiv(R.string.apps);
        title.setReturnListener(new TitleWidget.onReturnListener() {
            @Override
            public void onReturn(View paramView) {
                menu.showSecondaryMenu();
            }
        });
        title.setRighttv3("cancel");
        title.setSubmitListener(new TitleWidget.onSubmitListener() {
            @Override
            public void onSubmit(View paramView) {
                onBackPressed();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_select);
        imselect_gridView = (GridView) findViewById(R.id.grid_view_image_select);
        imselect_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection(position);
            }
        });
    }

    private void inintSildMenu() {
        menu=(SlidingMenu)findViewById(R.id.slidingmenulayout);

        menu.setMode(SlidingMenu.LEFT);

        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        menu.setBehindOffsetRes(R.dimen.item_height);

        menu.setFadeDegree(0.35f);

        menu.setOffsetFadeDegree(0.35f);

        //menu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_album_select, null);
        menu.setSecondaryMenu(view);
        gridView = (ListView) view.findViewById(R.id.grid_view_album_select);
        menu_title= (TitleWidget) view.findViewById(R.id.menu_title);
        menu_title.setBackVisibility(view.GONE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.toggle();
                if (!album.equals(albums.get(position).name)){
                    in_dex=position;
                    album = albums.get(position).name;
                    countSelected=0;
                    tv_look.setTextColor(Color.parseColor("#1Fffffff"));
                    acbtn_sure.setText("确定");
                    loadImages();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.PERMISSION_GRANTED: {
                        loadAlbums();
                        break;
                    }
                    case Constants.FETCH_STARTED: {
                        break;
                    }
                    case Constants.FETCH_COMPLETED: {
                        if (albums!=null&&albums.size()>0){


                        album = albums.get(in_dex).name;
                        loadImages();
                        if (adapter == null) {
                            adapter = new CustomAlbumSelectAdapter(getApplicationContext(), albums);
                            gridView.setAdapter(adapter);
                            gridView.setVisibility(View.VISIBLE);
//                            orientationBasedUI(getResources().getConfiguration().orientation);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        }
                        break;
                    }
                    case Constants.ERROR: {
                        break;
                    }
                    case Constants.FETCH_SELECTPIC: {
                        if (imageSelectAdapter == null) {
                            imageSelectAdapter = new CustomImageSelectAdapter(getApplicationContext(), images);
                            imselect_gridView.setAdapter(imageSelectAdapter);
                            imageSelectAdapter.setOnShowPicClickListener(new CustomImageSelectAdapter.OnSelectPicListener() {
                                @Override
                                public void onSelectPic(int position) {
                                    toggleSelection(position);
                                }
                            });
                            progressBar.setVisibility(View.INVISIBLE);
                            imselect_gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);
                        } else {
                            imageSelectAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {

            }
        };
        loadAlbums();
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
        checkPermission();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }
    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (imageSelectAdapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 3 : metrics.widthPixels / 5;
            imageSelectAdapter.setLayoutParams(size);
        }
        imselect_gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5);
    }

    private void loadImages() {
        startThread(new ImageLoaderRunnable());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (images!=null&&imageSelectAdapter!=null){
            imageSelectAdapter.setItem(images);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            if (imageSelectAdapter == null) {
                sendMessage(Constants.FETCH_STARTED);
            }

            File file;
            HashSet<String> selectedImages = new HashSet<>();
            if (images != null) {
                Image image;
                for (int i = 0, l = images.size(); i < l; i++) {
                    image = images.get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.path);
                    }
                }
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{ album }, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(Constants.ERROR);
                return;
            }

            int tempCountSelected = 0;
            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(path);
                    if (isSelected) {
                        tempCountSelected++;
                    }

                    file = new File(path);
                    if (file.exists()) {
                        temp.add(new Image(id, name, path, isSelected));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            sendMessage(Constants.FETCH_SELECTPIC, tempCountSelected);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopThread();
        getContentResolver().unregisterContentObserver(observer);
        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        albums = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
        images = null;
        //TODO
        if (imageSelectAdapter != null) {
            imageSelectAdapter.releaseResources();
        }
        imselect_gridView.setOnItemClickListener(null);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private void loadAlbums() {
        startThread(new AlbumLoaderRunnable());
    }

    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (adapter == null) {
                sendMessage(Constants.FETCH_STARTED);
            }

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null,null);
            if (cursor == null) {
                sendMessage(Constants.ERROR);
                return;
            }
            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
            HashSet<Long> albumSet = new HashSet<>();
            File file;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }
                    long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String album = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String image = cursor.getString(cursor.getColumnIndex(projection[2]));
//                    String size = cursor.getString(cursor.getColumnIndex(projection[3]));


                    int count = 10;
                    if (!albumSet.contains(albumId)) {
                        file = new File(image);
                        if (file.exists()) {
                            temp.add(new Album(album, image,""));
                            albumSet.add(albumId);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (albums == null) {
                albums = new ArrayList<>();
            }
            albums.clear();
            albums.addAll(temp);
            sendMessage(Constants.FETCH_COMPLETED);
        }
    }

    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(int what, int arg1) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.sendToTarget();
    }
    private void sendMessage(int what) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.sendToTarget();
    }

    @Override
    protected void permissionGranted() {
        Message message = handler.obtainMessage();
        message.what = Constants.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    @Override
    protected void hideViews() {
        gridView.setVisibility(View.INVISIBLE);
    }
    protected int getLayoutId() {
        return R.layout.activity_album;
    }


    private void toggleSelection(int position) {
        if (!images.get(position).isSelected && countSelected >= Constants.limit) {
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.limit_exceeded), Constants.limit),
                    Toast.LENGTH_SHORT)
                    .show();
        }else{
            images.get(position).isSelected = !images.get(position).isSelected;
            if (images.get(position).isSelected) {
                countSelected++;
            } else {
                countSelected--;
            }
            if (countSelected==0){
                acbtn_sure.setText("confirm");
                tv_look.setTextColor(Color.parseColor("#1Fffffff"));
            }else {
                acbtn_sure.setText("confirm"+countSelected);
                tv_look.setTextColor(Color.parseColor("#ffffff"));
            }
            imageSelectAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_look) {
            if (countSelected > 0) {
                Intent intent = new Intent(this, BrowsePicActivity.class);
                intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES, getSelected());
                startActivity(intent);
            }

        } else if (i == R.id.acbtn_sure) {
            sendIntent();

        }
    }

    private ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0, l = images.size(); i < l; i++) {
            if (images.get(i).isSelected) {
                selectedImages.add(images.get(i));
            }
        }
        return selectedImages;
    }

    private void sendIntent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES, getSelected());
        setResult(RESULT_OK, intent);
        finish();
    }
}
