package takephoto.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import piapro.github.io.instax.R;
import takephoto.model.Album;


public class CustomAlbumSelectAdapter extends CustomGenericAdapter<Album> {
    public CustomAlbumSelectAdapter(Context context, ArrayList<Album> albums) {
        super(context, albums);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_album_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
            viewHolder.tv_pic_size = (TextView) convertView.findViewById(R.id.tv_pic_size);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
/*
        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;*/
        viewHolder.tv_pic_size.setText(arrayList.get(position).size+"");
        viewHolder.textView.setText(arrayList.get(position).name);
        Glide.with(context)
                .load(arrayList.get(position).cover).centerCrop().into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView tv_pic_size;
    }
}
