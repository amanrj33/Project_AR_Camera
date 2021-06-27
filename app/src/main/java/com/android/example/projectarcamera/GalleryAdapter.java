package com.android.example.projectarcamera;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends ArrayAdapter<String> {
    ArrayList<String> listOfImages;
    Context context;

    //constructor
    public GalleryAdapter(@NonNull Context context, @LayoutRes int resourceId, @NonNull ArrayList<String> images){
        super(context,resourceId, images);
        listOfImages = images;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ImageView imageView;
        View itemView = convertView;
        if (itemView == null)
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false);

        //get the current image url and load to image view
        String currentImage = getItem(position);
        imageView = itemView.findViewById(R.id.imageView);
        Glide.with(context).load(currentImage).into(imageView);
        return itemView;
    }

    @Override
    public int getCount() {
        return listOfImages.size();
    }
}
