package com.trutek.looped.utils.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.TypedValue;


import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.utils.DateUtilsCore;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.Utils.MimeType;
import com.trutek.looped.utils.StorageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageUtils {

    public static final int GALLERY_REQUEST_CODE = 111;
    public static final int CAMERA_REQUEST_CODE = 222;
    public static final int IMAGE_REQUEST_CODE = 333;

    private static final String TAG = ImageUtils.class.getSimpleName();
    private static final String CAMERA_FILE_NAME_PREFIX = "CAMERA_";
    private static final String CAMERA_FILE_EXT = ".jpg";
    private static final String CAMERA_FILE_NAME = CAMERA_FILE_NAME_PREFIX + DateUtilsCore.getCurrentTime() + CAMERA_FILE_EXT;
    private static final int AVATAR_SIZE = 110;

    private Activity activity;

    public ImageUtils(Activity activity) {
        this.activity = activity;
    }

    public static void startImagePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MimeType.IMAGE_MIME);
        activity.startActivityForResult(
                Intent.createChooser(intent, activity.getString(R.string.dlg_choose_image_from)),
                GALLERY_REQUEST_CODE);
    }

    public static void startImagePicker(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MimeType.IMAGE_MIME);
        fragment.startActivityForResult(
                Intent.createChooser(intent, fragment.getString(R.string.dlg_choose_image_from)),
                GALLERY_REQUEST_CODE);
    }

    public static void startCameraForResult(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            return;
        }

        File photoFile = getTemporaryCameraFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public static void startCameraForResult(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(App.getInstance().getPackageManager()) == null) {
            return;
        }

        File photoFile = getTemporaryCameraFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public static File getTemporaryCameraFile() {
        File storageDir = StorageUtil.getAppExternalDataDirectoryFile();
        File file = new File(storageDir, CAMERA_FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            ErrorUtils.logError(e);
        }
        return file;
    }

    public static File getLastUsedCameraFile() {
        File dataDir = StorageUtil.getAppExternalDataDirectoryFile();
        File[] files = dataDir.listFiles();
        List<File> filteredFiles = new ArrayList<>();
        for (File file : files) {
            if (file.getName().startsWith(CAMERA_FILE_NAME_PREFIX)) {
                filteredFiles.add(file);
            }
        }

        Collections.sort(filteredFiles);
        if (!filteredFiles.isEmpty()) {
            return filteredFiles.get(filteredFiles.size() - 1);
        } else {
            return null;
        }
    }

    public static String saveUriToFile(Uri uri) throws Exception {
        ParcelFileDescriptor parcelFileDescriptor = App.getInstance().getContentResolver()
                .openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        InputStream inputStream = new FileInputStream(fileDescriptor);
        BufferedInputStream bis = new BufferedInputStream(inputStream);

        File parentDir = StorageUtil.getAppExternalDataDirectoryFile();
        String fileName = String.valueOf(System.currentTimeMillis()) + CAMERA_FILE_EXT;
        File resultFile = new File(parentDir, fileName);

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(resultFile));

        byte[] buf = new byte[2048];
        int length;

        try {
            while ((length = bis.read(buf)) > 0) {
                bos.write(buf, 0, length);
            }
        } catch (Exception e) {
            throw new IOException("Can\'t save Storage API bitmap to a file!", e);
        } finally {
            parcelFileDescriptor.close();
            bis.close();
            bos.close();
        }

        return resultFile.getAbsolutePath();
    }

    public static File getCreatedFileFromUri(Uri uri) {
        String filePath;
        File file = null;
        try {
            if (uri != null) {
                filePath = ImageUtils.saveUriToFile(uri);
                file = new File(filePath);
            }
        } catch (Exception e) {
            ErrorUtils.logError(e);
        }
        return file;
    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File file) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 140;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(file.getAbsolutePath(),o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getExifInterfaceOrientation(String pathToFile) {
        int orientation = Constants.NOT_INITIALIZED_VALUE;

        try {
            ExifInterface exifInterface = new ExifInterface(pathToFile);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, Constants.NOT_INITIALIZED_VALUE);
        } catch (Exception e) {
            ErrorUtils.logError(e);
        }

        return orientation;
    }

    public static void checkForRotation(String imagePath) {
        Bitmap bitmap = getBitmapFromFile(imagePath);
        if (bitmap.getHeight() > bitmap.getWidth()) {
            rotateImage(bitmap, 90);
        }
    }

    public static Bitmap rotateImage(int orientation, Bitmap bitmap) {
        Bitmap rotatedBitmap;
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                rotatedBitmap = bitmap;
                break;
            default:
                rotatedBitmap = bitmap;
                break;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedImg;
    }

    public static Bitmap getBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath, getBitmapOption());
    }

    private static BitmapFactory.Options getBitmapOption() {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOptions.inDither = false;
        bitmapOptions.inPurgeable = true;
        bitmapOptions.inInputShareable = true;
        bitmapOptions.inTempStorage = new byte[32 * 1024];
        return bitmapOptions;
    }

    private static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                             ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
                dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
                dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                         ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, Constants.ZERO_INT_VALUE, srcRectLeft + srcRectWidth,
                        srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(Constants.ZERO_INT_VALUE, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(Constants.ZERO_INT_VALUE, Constants.ZERO_INT_VALUE, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(Constants.ZERO_INT_VALUE, Constants.ZERO_INT_VALUE, dstWidth,
                        (int) (dstWidth / srcAspect));
            } else {
                return new Rect(Constants.ZERO_INT_VALUE, Constants.ZERO_INT_VALUE,
                        (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(Constants.ZERO_INT_VALUE, Constants.ZERO_INT_VALUE, dstWidth, dstHeight);
        }
    }

    public static Drawable getRoundIconDrawable(Context context, Bitmap avatarBitmap) {
        int actionBarHeight = getActionBarHeight(context);
        return getRoundIconDrawable(avatarBitmap, actionBarHeight);
    }

    private static int getActionBarHeight(Context context) {
        TypedValue typedValue = new TypedValue();
        int actionBarHeight = AVATAR_SIZE;

        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }

        int margin = actionBarHeight / 10;

        return actionBarHeight - margin;
    }

    private static Drawable getRoundIconDrawable(Bitmap avatarBitmap, int size) {
        // TODO Remove freaking hardcoded values
        Resources res = App.getInstance().getResources();
        Bitmap scaledBitmap = createScaledBitmap(avatarBitmap, size, size, ScalingLogic.CROP);

        // create rounded image avatar
        Bitmap output = Bitmap
                .createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = App.getInstance().getResources().getColor(R.color.gray);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = 200;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        // draw two bitmaps on Canvas
        int width = output.getWidth();
        int height = output.getHeight();

        Bitmap finalImage = Bitmap.createBitmap(width, height, output.getConfig());
        canvas = new Canvas(finalImage);
        canvas.drawBitmap(output, 0, 0, null);

        return new BitmapDrawable(res, finalImage);
    }

    private enum ScalingLogic {
        CROP, FIT
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}