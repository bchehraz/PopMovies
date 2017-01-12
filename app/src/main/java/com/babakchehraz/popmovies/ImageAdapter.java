package com.babakchehraz.popmovies;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 1/1/2017.
 */

public class ImageAdapter<T> extends ArrayAdapter<T> {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private int mResource;
    private int mFieldId;

    public ImageAdapter(Context context, int resource, int imageViewResourceId, List<T> objects) {
        super(context, resource, imageViewResourceId, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        mFieldId = imageViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;

        if (convertView == null) {
            Resources r = Resources.getSystem();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, r.getDisplayMetrics());

            imageView = new ImageView(super.getContext());
            imageView.setLayoutParams(new GridView.LayoutParams((int)(px*0.65),(int)(px)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(16,16,16,16);
        } else {
            imageView = (ImageView) convertView;
        }

        T item = getItem(position);
        if (item instanceof Integer) {
            imageView.setImageResource((Integer)item);
        } else if (item instanceof String){
            Picasso.with(super.getContext()).load(item.toString()).into(imageView);
        }

        return imageView;
    }

    public void clear() {
        super.clear();
    }

    public void add(T item) {
        super.add(item);
    }
}