package com.vincentramdhanie.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Ramdhanie on 7/11/15.
 */
public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    private final  String BASE_URL = "http://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE = "w185";


    private Context mContext;
    private List<String> posters;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public ImageAdapter(Context c, List<String> posters){
        this(c);
        this.posters = posters;

    }

    public int getCount() {
        return posters != null?posters.size():0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void clear(){
        posters = new ArrayList<String>();
    }

    public void addAll(List<String> uris){
        posters = uris;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d(LOG_TAG, "About to load the image");
        Picasso.with(mContext).load(String.format("%s/%s/%s",BASE_URL, IMAGE_SIZE, posters.get(position))).into(imageView);
        return imageView;
    }


}