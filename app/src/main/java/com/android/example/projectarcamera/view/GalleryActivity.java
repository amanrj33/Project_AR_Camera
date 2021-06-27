package com.android.example.projectarcamera.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.android.example.projectarcamera.NetworkConnected;
import com.android.example.projectarcamera.GalleryAdapter;
import com.android.example.projectarcamera.R;
import com.android.example.projectarcamera.databinding.ActivityGalleryBinding;
import com.android.example.projectarcamera.viewmodel.GalleryViewModel;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding instance for activity gallery
        ActivityGalleryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        //view model instance
        GalleryViewModel viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        //back button behavior
        binding.backButton.setOnClickListener(view -> {
            onBackPressed();
        });

        //adapter instance
        galleryAdapter = new GalleryAdapter(this, R.layout.list_item_image, new ArrayList<>());
        binding.imagesGridView.setAdapter(galleryAdapter);

        //check the connectivity
        if (NetworkConnected.isConnected(this))
            viewModel.readImages();
        else viewModel.setErrorNetwork(true);

        //handling the error
        viewModel.errorNetwork.observe(this, error -> {
            if (error) {
                binding.textView.setVisibility(View.VISIBLE);
                binding.textView.setText(getString(R.string.not_connected_to_internet));
            } else {
                binding.textView.setVisibility(View.GONE);
            }
        });

        //handling the loader
        viewModel.showLoader.observe(this, show -> {
            if (show) {
                binding.progress.setVisibility(View.VISIBLE);
                binding.textView.setVisibility(View.VISIBLE);
                binding.textView.setText(getString(R.string.no_images));
            }
            else {
                binding.progress.setVisibility(View.GONE);
                binding.textView.setVisibility(View.GONE);
            }
        });

        //handling the images to be loaded in gallery
        viewModel.images.observe(this, list -> {
                    galleryAdapter.clear();
                    galleryAdapter.addAll(list);
                    galleryAdapter.notifyDataSetChanged();
                }
        );
    }


}