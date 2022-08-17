package com.android.example.projectarcamera.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.android.example.projectarcamera.R;
import com.android.example.projectarcamera.databinding.ActivityCameraBinding;
import com.android.example.projectarcamera.viewmodel.CameraViewModel;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class CameraActivity extends AppCompatActivity {
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding instance for activity gallery
        ActivityCameraBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        //view model instance
        CameraViewModel viewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        //ar fragment reference
        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        //loading the camera and ready it for placing 3d object(cube in this case)
        assert arFragment != null;
        loadAR(arFragment);

        //observer for handling if user wants to see the gallery
        viewModel.isGalleryCLicked.observe(this, navigate -> {
            if (navigate) {
                startActivity(new Intent(this, GalleryActivity.class));
                viewModel.doneGalleryNavigation();
            }
        });

        //observer to observe when user clicks on capture
        viewModel.imageCapture.observe(this, isCapture -> {
            if (isCapture) {
                //get the surface view from ar fragment
                SurfaceView surfaceView = (SurfaceView) arFragment.getArSceneView();
                bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

                //copy the pixels of the surface view to the bitmap object
                PixelCopy.request(surfaceView, bitmap, (copyResult)-> { }, new Handler());

                //handle the visibility and ar fragment
                binding.constraintLayoutCapture.setVisibility(View.GONE);
                binding.constraintLayoutUpload.setVisibility(View.VISIBLE);
                arFragment.getArSceneView().pause();

                //set the copied pixels to the image view for uploading purpose
                binding.imageView.setImageBitmap(bitmap);
            } else {
                binding.constraintLayoutCapture.setVisibility(View.VISIBLE);
                try {
                    arFragment.getArSceneView().resume();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }
                binding.constraintLayoutUpload.setVisibility(View.GONE);
            }
        });

        //observer for uploading image to firebase
        viewModel.isUploadCLicked.observe(this, isClicked -> {
            if (isClicked){
                binding.uploadProgressBar.setVisibility(View.VISIBLE);
                binding.uploadButton.setEnabled(false);

                //upload image to firebase
                viewModel.uploadImageBitmap(bitmap, this);
            } else {
                binding.uploadProgressBar.setVisibility(View.GONE);
                binding.uploadButton.setEnabled(true);
            }
        });

        //observer for showing correct toast message depending on the upload status
        viewModel.isSuccessfullyUploaded.observe(this, isUploaded -> {
            if (isUploaded){
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                viewModel.captureDoneOrFailed();
            } else {
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                viewModel.uploadDoneOrFailed();
            }
        });
    }

    //method for loading the camera and make it ready for placing 3d object(cube in this case)
    private void loadAR(ArFragment arFragment) {
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            //create the material for the model
            MaterialFactory
                    .makeOpaqueWithColor(this, new Color(android.graphics.Color.BLUE))
                    .thenAccept(material -> {
                        //create the renderable for the model
                        ModelRenderable renderable = ShapeFactory
                                .makeCube(new Vector3(0.3f,0.3f, 0.3f),new Vector3(0f,0.3f, 0f), material);

                        //create the node and and add the redenrable to it
                        AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
                        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                        node.setParent(anchorNode);
                        node.setRenderable(renderable);
                        node.select();
                        arFragment.getArSceneView().getScene().addChild(anchorNode);
                    });
        });
    }
}