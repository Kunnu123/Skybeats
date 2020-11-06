package com.skybeats.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class ImageUtils {

    public static void loadImage(Context context,String filePath, int placeholder, ImageView imageView) {
        if (!((AppCompatActivity) context).isFinishing()) {
            Glide.with(context)
                    .load(filePath)
                    .apply(new RequestOptions().placeholder(placeholder).error(placeholder))
                    .into(imageView);
        }

    }
}
