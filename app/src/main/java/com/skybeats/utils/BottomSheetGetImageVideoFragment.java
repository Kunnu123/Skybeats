package com.skybeats.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.skybeats.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * use
 * <p>
 * new BottomSheetGetImageVideoFragment(activity, BottomSheetGetImageVideoFragment.GET_IMAGE, new BottomSheetGetImageVideoFragment.OnActivityResult() {
 *
 * @Override public void onSuccessResult(String resultant_file_path, final Bitmap thumbnail_bitmap) {
 * <p>
 * }
 * @Override public void onFailResult(String reason) {
 * <p>
 * }
 * }).show(getChildFragmentManager(), "");
 */
@SuppressWarnings("validFragment")
public class BottomSheetGetImageVideoFragment extends BottomSheetDialogFragment {
    public static final String GET_VIDEO_FROM_GALLERY = "GET_VIDEO_FROM_GALLERY";
    public static final String GET_VIDEO = "GET_VIDEO";
    public static final String GET_IMAGE = "GET_IMAGE";
    private static final String TAG = "BottomSheetGetDataFragment";
    /**
     * an {@link AppCompatActivity} that calls this fragment
     */
    private AppCompatActivity appCompatActivity;
    /**
     * required data it can be {@link BottomSheetGetImageVideoFragment#GET_VIDEO} or {@link BottomSheetGetImageVideoFragment#GET_IMAGE} or {@link BottomSheetGetImageVideoFragment#GET_VIDEO_FROM_GALLERY}
     */
    private String data_required;
    /**
     * object of interface to set result
     */
    private OnActivityResult onActivityResult;
    /**
     * resultant image uri from camera
     */
    private Uri imageFileUri;
    /**
     * resultant video uri from camera
     */
    private Uri videoFileUri;
    /**
     * resultant video path
     */
    private String imageFilePath = "";
    /**
     * resultant video path
     */
    private String videoFilePath = "";
    /**
     * resultant video thumbnail
     */
    private Bitmap videoThumbnail;
    /**
     * constant for image cropping activity result
     */
    public static final int IMAGE_CROP = 4;
    /**
     * constant for image result activity result
     */
    public static final int SELECT_PICTURE_REQUEST_CODE = 3;
    /**
     * constant for video result activity result
     */
    public static final int SELECT_VIDEO_REQUEST_CODE = 2;
    /**
     * constant for max video size
     */
    public static final int maxFileSize = 20 * 1024 * 1024;//20MB

    /**
     * constant for max video length
     */
    public static final int maxFileLength = 2 * 60;//2 min

    /**
     * A constructor
     *
     * @param appCompatActivity an Activity that calls this dialog fragment
     * @param data_required     required data it can be {@link BottomSheetGetImageVideoFragment#GET_VIDEO} or {@link BottomSheetGetImageVideoFragment#GET_IMAGE} or {@link BottomSheetGetImageVideoFragment#GET_VIDEO_FROM_GALLERY}
     * @param onActivityResult  object of interface to set result
     */
    public BottomSheetGetImageVideoFragment(AppCompatActivity appCompatActivity, String data_required, OnActivityResult onActivityResult) {
        this.appCompatActivity = appCompatActivity;
        this.onActivityResult = onActivityResult;
        this.data_required = data_required;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = new LinearLayout(appCompatActivity);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        startActivityCall();

    }

    private void startActivityCall() {

        new BottomSheetPermissionFragment(appCompatActivity, new BottomSheetPermissionFragment.OnPermissionResult() {
            @Override
            public void onPermissionAllowed() {
                if (data_required != null && data_required.equalsIgnoreCase(GET_VIDEO)) {
                    addVideo();
                } else if (data_required != null && data_required.equalsIgnoreCase(GET_IMAGE)) {
                    addImage();
                }
            }

            @Override
            public void onPermissionDenied() {
                BottomSheetGetImageVideoFragment.this.dismissAllowingStateLoss(); //user denied permission
            }
        }, BottomSheetPermissionFragment.READ_EXTERNAL_STORAGE,
                BottomSheetPermissionFragment.WRITE_EXTERNAL_STORAGE,
                BottomSheetPermissionFragment.CAMERA).show(getChildFragmentManager(), "");
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final boolean isCamera;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_VIDEO_REQUEST_CODE:

                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        isCamera = action != null && action.equals(MediaStore.ACTION_VIDEO_CAPTURE);
                    }

                    Uri selectedVideoUri = null;
                    if (isCamera) {
                        selectedVideoUri = videoFileUri;
                        Log.d("VIDEO URI", selectedVideoUri + "");

                        if (selectedVideoUri != null) {

                            videoFilePath = selectedVideoUri.getPath();

                            Log.d("VIDEO PATH", videoFilePath);

                            videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.MINI_KIND);

                            onActivityResult.onSuccessResult(videoFilePath, videoThumbnail); //success from camera
                            this.dismissAllowingStateLoss();
                        } else {
                            onActivityResult.onFailResult("");  //fail if selectedVideoUri is null for camera
                            this.dismissAllowingStateLoss();
                        }
                    } else {
                        selectedVideoUri = data == null ? null : data.getData();

                        Log.d("VIDEO URI", selectedVideoUri + "");

                        if (selectedVideoUri != null) {
                            videoFilePath = "";
                            int fileSize = 0;

                            InputStream inputStream = null;
                            try {
                                inputStream = appCompatActivity.getContentResolver().openInputStream(selectedVideoUri);
                                if (inputStream != null) {
                                    fileSize = inputStream.available();
                                }
                            } catch (Exception e) {
                                onActivityResult.onFailResult(""); //fail if exception occurs in input open stream for camera
                                this.dismissAllowingStateLoss();
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (fileSize >= maxFileSize) {
                                onActivityResult.onFailResult(getResources().getString(R.string.less_than_20mb)); //fail if file size is greater than 20 MB for gallery
                                this.dismissAllowingStateLoss();
                            } else {
                                try {
                                    try {
                                        videoFilePath = RealPathUtil.getRealPathFromURI(appCompatActivity, selectedVideoUri);
                                        Log.d("VIDEO PATH", videoFilePath);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onActivityResult.onFailResult(""); //fail if exception occurs for gallery
                                        this.dismissAllowingStateLoss();
                                    }

                                    if (!TextUtils.isEmpty(videoFilePath)) {
                                        videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                                        onActivityResult.onSuccessResult(videoFilePath, videoThumbnail);  //success from gallery
                                        this.dismissAllowingStateLoss();
                                    } else if (videoFileUri != null && !TextUtils.isEmpty(videoFileUri.getPath())) {
                                        videoFilePath = videoFileUri.getPath();
                                        videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                                        onActivityResult.onSuccessResult(videoFilePath, videoThumbnail);  //success from gallery
                                        this.dismissAllowingStateLoss();
                                    } else {
                                        onActivityResult.onFailResult(""); //fail if we don't have file path
                                        this.dismissAllowingStateLoss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    onActivityResult.onFailResult(""); //fail if exception occurs for gallery
                                    this.dismissAllowingStateLoss();
                                }
                            }
                        } else {
                            onActivityResult.onFailResult("");  //fail if request code not match or result code not RESULT_OK
                            this.dismissAllowingStateLoss();
                        }
                    }
                    break;
                case SELECT_PICTURE_REQUEST_CODE:
                    //result code is OK and activity result for picture request

                    //check if result is from camera or gallery

                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    Uri selectedImageUri = null;
                    if (isCamera) {
                        //code if request is from camera
                        selectedImageUri = imageFileUri;
                        Log.d("IMAGE URI", selectedImageUri + "");

                        if (selectedImageUri != null) {
                            try {
                                Bitmap bitmap = getThumbnailBitmap(selectedImageUri.getPath(), 1000);

                                Logger.d(TAG + "BITMAP WIDTH " + bitmap.getWidth());
                                Logger.d(TAG + "BITMAP HEIGHT " + bitmap.getHeight());

                                File f = new File(selectedImageUri.getPath());
                                FileOutputStream fos = new FileOutputStream(f);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                Log.d("IMAGE SAVED", selectedImageUri.getPath());
                                fos.flush();
                                fos.close();

                                SdcardUtils.ORIGINAL_IMAGE_PATH = selectedImageUri.getPath();

                                if (!SdcardUtils.ORIGINAL_IMAGE_PATH.endsWith(".gif")) {
                                    /*Intent intentCrop = new Intent(appCompatActivity, ImageCropActivity.class);
                                    startActivityForResult(intentCrop, IMAGE_CROP);
                                    appCompatActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/

                                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
                                    root.mkdirs();
                                    File f1 = new File(root.getAbsolutePath(), (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".jpg"));
                                    Uri destination = Uri.fromFile(f1);

                                    UCrop.of(selectedImageUri, destination)
                                            .withAspectRatio(1, 1)
                                            .withMaxResultSize(200, 200)
                                            .start(appCompatActivity);

                                    //  onActivityResult.onSuccessResult(SdcardUtils.ORIGINAL_IMAGE_PATH, null);
                                    this.dismissAllowingStateLoss();
                                } else {
                                    imageFilePath = SdcardUtils.ORIGINAL_IMAGE_PATH;
                                    BitmapFactory.Options bitmapOptions1 = new BitmapFactory.Options();
                                    File f1 = new File(imageFilePath);
                                    double bytes1 = f1.length();
                                    double scale1 = bytes1 / 320000;
                                    int s1 = (int) scale1;
                                    bitmapOptions1.inSampleSize = s1 / 2 + s1 % 2 * 2;
                                    Bitmap bitmap1 = BitmapFactory.decodeFile(imageFilePath, bitmapOptions1);
                                    onActivityResult.onSuccessResult(imageFilePath, bitmap1);
                                    this.dismissAllowingStateLoss();
                                }
                            } catch (Exception e) {
                                onActivityResult.onFailResult(""); //Exception occured while camera
                                this.dismissAllowingStateLoss();
                                e.printStackTrace();
                            }
                        } else {
                            onActivityResult.onFailResult(""); //selectedImageUri is null camera
                            this.dismissAllowingStateLoss();
                        }

                    } else {
                        selectedImageUri = data == null ? null : data.getData();

                        Log.d("IMAGE URI", selectedImageUri + "");

                        String output = imageFileUri.getPath();

                        Log.d("Output", output);

                        File f = new File(output.substring(0, output.lastIndexOf("/")));
                        f.delete();

                        try {
                            boolean isLoadedFromBitmap = false;
                            String fileName = "temp.jpg";
                            if (selectedImageUri == null && data.getExtras() != null) {
                                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                fileName = savebitmap(fileName, bitmap).getAbsolutePath();
                                selectedImageUri = Uri.parse(fileName);
                                isLoadedFromBitmap = true;
                            }
                            if (selectedImageUri != null) {

                                String imgFilePath = "";
                                if (isLoadedFromBitmap) {
                                    imgFilePath = fileName;
                                } else {
                                    imgFilePath = RealPathUtil.getRealPathFromURI(appCompatActivity, selectedImageUri);
                                }
                                Log.d("IMG PATH", imgFilePath);

                                Uri uri = null;

                                if (imgFilePath != null) {
                                    Bitmap bitmap = getThumbnailBitmap(imgFilePath, 1000);

                                    Logger.d(TAG + "BITMAP WIDTH " + bitmap.getWidth());
                                    Logger.d(TAG + "BITMAP HEIGHT " + bitmap.getHeight());

                                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
                                    root.mkdirs();
                                    File f1 = new File(root.getAbsolutePath(), (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".jpg"));
                                    uri = Uri.fromFile(f1);
                                    FileOutputStream fos = new FileOutputStream(uri.getPath());
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    Log.d("IMAGE SAVED", uri.getPath());
                                    fos.flush();
                                    fos.close();
                                }

                                if (uri != null) {
                                    if (!imgFilePath.toLowerCase().endsWith(".gif")) {

                                        if (imgFilePath != null) {
                                            Bitmap bitmap = getThumbnailBitmap(imgFilePath, 1000);

                                            Logger.d(TAG + "BITMAP WIDTH " + bitmap.getWidth());
                                            Logger.d(TAG + "BITMAP HEIGHT " + bitmap.getHeight());

                                            File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
                                            root.mkdirs();
                                            File f1 = new File(root.getAbsolutePath(), (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".jpg"));
                                            uri = Uri.fromFile(f1);
                                            FileOutputStream fos = new FileOutputStream(uri.getPath());
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                            Log.d("IMAGE SAVED", uri.getPath());
                                            fos.flush();
                                            fos.close();
                                        } else {
                                            onActivityResult.onFailResult(""); //uri is null Gallery
                                            this.dismissAllowingStateLoss();
                                        }
                                        if (uri != null) {

                                            SdcardUtils.ORIGINAL_IMAGE_PATH = uri.getPath();
                                            /*Intent intentCrop = new Intent(appCompatActivity, ImageCropActivity.class);
                                            startActivityForResult(intentCrop, IMAGE_CROP);
                                            appCompatActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
                                            File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
                                            root.mkdirs();
                                            File f1 = new File(root.getAbsolutePath(), (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".jpg"));
                                            Uri destination = Uri.fromFile(f1);

                                            UCrop.of(uri, destination)
                                                    .withAspectRatio(1, 1)
                                                    .withMaxResultSize(200, 200)
                                                    .start(appCompatActivity);
                                            // onActivityResult.onSuccessResult(SdcardUtils.ORIGINAL_IMAGE_PATH, null);
                                            this.dismissAllowingStateLoss();
                                        } else {
                                            onActivityResult.onFailResult(""); //uri is null Gallery
                                            this.dismissAllowingStateLoss();
                                        }
                                    } else {

                                        if (imgFilePath != null) {
                                            Bitmap bitmap = getThumbnailBitmap(imgFilePath, 1000);

                                            Logger.d(TAG + "BITMAP WIDTH " + bitmap.getWidth());
                                            Logger.d(TAG + "BITMAP HEIGHT " + bitmap.getHeight());

                                            File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
                                            root.mkdirs();
                                            File f1 = new File(root.getAbsolutePath(), (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".gif"));
                                            uri = Uri.fromFile(f1);
                                            FileOutputStream fos = new FileOutputStream(uri.getPath());
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                            Log.d("IMAGE SAVED", uri.getPath());
                                            fos.flush();
                                            fos.close();
                                        } else {
                                            onActivityResult.onFailResult(""); //uri is null Gallery
                                            this.dismissAllowingStateLoss();
                                        }
                                        if (uri != null) {
                                            imageFilePath = imgFilePath;
                                            BitmapFactory.Options bitmapOptions1 = new BitmapFactory.Options();
                                            File f1 = new File(imageFilePath);
                                            double bytes1 = f1.length();
                                            double scale1 = bytes1 / 320000;
                                            int s1 = (int) scale1;
                                            bitmapOptions1.inSampleSize = s1 / 2 + s1 % 2 * 2;
                                            Bitmap bitmap1 = BitmapFactory.decodeFile(imageFilePath, bitmapOptions1);
                                            onActivityResult.onSuccessResult(imageFilePath, bitmap1);
                                            this.dismissAllowingStateLoss();
                                        } else {
                                            onActivityResult.onFailResult(""); //uri is null Gallery
                                            this.dismissAllowingStateLoss();
                                        }
                                    }
                                } else {
                                    onActivityResult.onFailResult(""); //uri is null Gallery
                                    this.dismissAllowingStateLoss();
                                }

                            } else {
                                onActivityResult.onFailResult(""); //selectedImageUri is null Gallery
                                dismissAllowingStateLoss();
                            }

                        } catch (Exception e) {
                            onActivityResult.onFailResult(""); //exception occured in Gallery
                            this.dismissAllowingStateLoss();
                            e.printStackTrace();
                        }
                    }
                    break;
                case IMAGE_CROP:
                    imageFilePath = SdcardUtils.CROPED_IMAGE_PATH.getAbsolutePath();
                    BitmapFactory.Options bitmapOptions1 = new BitmapFactory.Options();
                    File f1 = new File(imageFilePath);
                    double bytes1 = f1.length();
                    double scale1 = bytes1 / 320000;
                    int s1 = (int) scale1;
                    bitmapOptions1.inSampleSize = s1 / 2 + s1 % 2 * 2;
                    Bitmap bitmap1 = BitmapFactory.decodeFile(imageFilePath, bitmapOptions1);
                    onActivityResult.onSuccessResult(imageFilePath, bitmap1);
                    this.dismissAllowingStateLoss();
                    break;

                case UCrop.REQUEST_CROP:

                    Uri resultUri = UCrop.getOutput(data);

                    onActivityResult.onSuccessResult(resultUri.getPath(), null);
                    break;
                default:
                    Logger.e(TAG + "FAIL");
                    onActivityResult.onFailResult(""); //fail if request code Doesn't match above
                    this.dismissAllowingStateLoss();
                    break;
            }
        } else {
            Logger.e(TAG + "FAIL");
            onActivityResult.onFailResult(""); //fail if result code is not RESULT_OK
            this.dismissAllowingStateLoss();
        }

    }

    private File savebitmap(String path, Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, path);
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, path);

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }


    public void addVideo() {

        // Determine Uri of camera video to save.
        final File root = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + getResources().getString(R.string.app_name)
                + File.separator
                + getResources().getString(R.string.toto_ride_videos)
                + File.separator);
        root.mkdirs();
        final File sdImageMainDirectory = new File(root.getAbsolutePath(),
                (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".mp4"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            videoFileUri = FileProvider.getUriForFile(appCompatActivity, appCompatActivity.getPackageName() + ".provider", sdImageMainDirectory);
        } else {
            videoFileUri = Uri.fromFile(sdImageMainDirectory);
        }


        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxFileLength);
        final PackageManager packageManager = appCompatActivity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("video/*");
        /*if (Build.VERSION.SDK_INT < 19) {
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        }
        else
        {
            galleryIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        }*/

        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);


        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.select_video));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_VIDEO_REQUEST_CODE);
    }


    public void addImage() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + getResources().getString(R.string.app_name)
                + File.separator
                + getResources().getString(R.string.toto_ride_image)
                + File.separator);
        root.mkdirs();
        final File sdImageMainDirectory = new File(root.getAbsolutePath(),
                (getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".jpg"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageFileUri = FileProvider.getUriForFile(appCompatActivity, appCompatActivity.getPackageName() + ".provider", sdImageMainDirectory);
        } else {
            imageFileUri = Uri.fromFile(sdImageMainDirectory);
        }
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = appCompatActivity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");

        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.select_image));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }


    private Bitmap getThumbnailBitmap(final String path, final int thumbnailSize) {
        Bitmap bitmap;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            bitmap = null;
        }

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();

        if (bounds.outHeight < 1000 && bounds.outWidth < 1000) {
            if (bounds.outHeight > bounds.outWidth) {
                opts.inSampleSize = originalSize / bounds.outHeight;
            } else {
                opts.inSampleSize = originalSize / bounds.outWidth;
            }
        } else {
            opts.inSampleSize = originalSize / thumbnailSize;
            opts.inSampleSize = originalSize / thumbnailSize;
        }

        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }


    public interface OnActivityResult {
        void onSuccessResult(String resultant_file_path, Bitmap thumbnail_bitmap);

        void onFailResult(String reason);
    }

}
