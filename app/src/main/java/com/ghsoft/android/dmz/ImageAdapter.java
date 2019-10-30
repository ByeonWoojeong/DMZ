package com.ghsoft.android.dmz;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ghsoft.android.dmz.util.ImageData;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<ImageData> _imagePaths;
    private LayoutInflater inflater;

public ImageAdapter(Activity activity, ArrayList<ImageData> imagePaths){

    this._activity = activity;
    this._imagePaths = imagePaths;
}

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    TouchImageView imgDisplay;
        ImageView can;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.list_item_viewpager,container,false);
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        can = (ImageView)viewLayout.findViewById(R.id.can);

        Glide.with(_activity).load(_imagePaths.get(position).img).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgDisplay);

        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageActivity) _activity).finish();
            }
        });

        Log.e("zzzzzzzzzzzz", position + "");

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
