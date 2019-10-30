package com.ghsoft.android.dmz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.androidquery.AQuery;
import com.ghsoft.android.dmz.util.ImageData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ImageActivity extends AppCompatActivity {

    static SwipeViewPager pager;
    AQuery aQuery;
    ArrayList<ImageData> data = new ArrayList<>();
    private ImageAdapter mAdapter;
    Context context;
    String image_json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);

        context = ImageActivity.this;
        aQuery = new AQuery(this);

        pager = (SwipeViewPager) findViewById(R.id.pager);

        Intent intent = getIntent();
        image_json = intent.getExtras().getString("image");

        Log.e("aaaaaaaaa",image_json);

        try{
            Log.e("cccccccccc","try들어왔어");
            JSONObject jsonObject = new JSONObject(image_json);
            String image = jsonObject.getString("src");
//            int cnt = jsonObject.getInt("cnt");
            JSONArray jsonArray = new JSONArray(image);
            for (int i = 0; i < jsonArray.length(); i++) {
                ImageData imageeData = new ImageData();
                imageeData.img = jsonArray.getString(i);
                data.add(imageeData);
                Log.e("dddddddddd",data+"");
            }
            mAdapter = new ImageAdapter(ImageActivity.this, data);
            pager.setAdapter(mAdapter);
//            pager.setCurrentItem(cnt-1);

        } catch (JSONException e) {
            Log.e("eeeeeeeee",e+"");

        }


    }
}
