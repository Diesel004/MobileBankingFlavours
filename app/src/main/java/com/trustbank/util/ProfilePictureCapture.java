package com.trustbank.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trustbank.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

public class ProfilePictureCapture {

    Context context;
    ImageView imageViewProfile;
    private Uri camPhotoPath;
    Activity activity;

    public ProfilePictureCapture(Context context, ImageView imageViewProfile) {
        this.context = context;
        this.activity = (Activity) context;
        this.imageViewProfile = imageViewProfile;
    }

    public void loadExistingProfile() {
        File fileProfile = TrustFileUtils.createFilePath(context, AppConstants.PROFILE_NAME, AppConstants.PROFILE_IMAGE);
        setProfileImage(fileProfile);
    }

    public void setProfileImage(File proFile) {
        if (proFile.exists()) {

            Glide.with(context).load(proFile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.ic_profile).into(imageViewProfile);
        }
    }


    /*-------------------------------- select image method ----------------------------------------*/
    public void selectTheImage() {

        try {
            String[] items = new String[]{"Take photo", "Choose from Gallery", "Clear"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Select Image");
            builder.setAdapter(adapter, (dialog, item) -> {

                // pick from camera
                if (item == 0) {
                    try {
                        startCameraForPhoto();
                    } catch (Exception e) {
                        TrustMethods.message(context, e.getMessage());
                        e.printStackTrace();
                    }
                } else if (item == 1) {
                    // pick from file
                    Intent intent = new Intent();

                    intent.setType("image//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    activity.startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), AppConstants.PICK_FROM_FILE_GALLERY);
                } else if (item == 2) {
                    imageViewProfile.setImageResource(R.drawable.ic_profile);
                    File fileProfile = TrustFileUtils.createFilePath(context, AppConstants.PROFILE_NAME,
                            AppConstants.PROFILE_IMAGE);
                    if (fileProfile.exists()) {
                        fileProfile.delete();
                    }
                }
            });
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
            TrustMethods.message(context, e.getMessage());
        }

    }

    public void startCameraForPhoto() {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/MPassbook/Statements/AccountStatement"+ System.currentTimeMillis() +".pdf";
                /*String outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/Img_Profile.jpeg";
                File file = new File(outpath);
                camPhotoPath = Uri.fromFile(file);*/

                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Img_Profile");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "ProfileImg");
                camPhotoPath = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                if (camPhotoPath == null)
                    throw new IOException("Failed to create new MediaStore record.");

                resolver.openOutputStream(camPhotoPath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, camPhotoPath);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                activity.startActivityForResult(intent, AppConstants.PICK_FROM_CAMERA_Photo);
            } else {

                File file = TrustFileUtils.createFilePath(context, AppConstants.PROFILE_NAME,
                        AppConstants.PROFILE_IMAGE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    camPhotoPath = Uri.fromFile(file);
                } else {
                    camPhotoPath = FileProvider.getUriForFile(context, AppConstants.IMAGE_FILE_AUTHORITIES, file);
                }


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camPhotoPath);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            activity.startActivityForResult(intent, AppConstants.PICK_FROM_CAMERA_Photo);

        }

        } catch (Exception e) {
            e.printStackTrace();
            TrustMethods.message(context, e.getMessage());
        }
    }



    public void takeImageToActivity(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.PICK_FROM_CAMERA_Photo:
                if (camPhotoPath != null) {
                    CropImage.activity(camPhotoPath).start(activity);
                } else {
                    selectTheImage();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    final File file = FileUtil.from(activity, result.getUri());
                    RwFileCopy.copyFileOrDirectory(file.getPath(), new File(Environment.getExternalStorageDirectory(), AppConstants.PROFILE_IMAGE).getPath());
                    setProfileImage(file);
                } catch (Exception e) {
                    TrustMethods.message(activity, e.toString());
                }

                break;
            case AppConstants.PICK_FROM_FILE_GALLERY:
                try {
                    File galleryImage = FileUtil.from(activity, data.getData());
                   setProfileImage(galleryImage);

                    CropImage.activity(data.getData())
                            .start(activity);
                } catch (Exception e) {
                    TrustMethods.message(activity, e.toString());
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
