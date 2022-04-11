package com.common.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.example.administrator.myapplication.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/** The type Screen shot utils. */
public class ScreenShotUtils {
    public static final  String APP_PIC                  = Environment.getExternalStorageDirectory().getAbsolutePath()
                                                           + File.separator + "picture";
    private static final int    REQUEST_SCREEN_SHOT_CODE = 10;

    private int screenDensity;
    private int screenWidth;
    private int screenHeight;

    // 授予应用程序捕获屏幕内容或记录系统音频的能力。授予的准确能力取决于MediaProjection的类型
    private MediaProjection        mediaProjection;
    // 管理获取到MediaProjection具体类型。
    private MediaProjectionManager mediaProjectionManager;
    // VirtualDisplay表示一个虚拟显示，显示的内容render到 createVirtualDisplay()参数的Surface。
    private VirtualDisplay         virtualDisplay;
    // ImageReader允许应用级别的直接访问render(渲染)到surface上面的图像数据。
    private ImageReader            imageReader;
    // Surface用来处理由屏幕合成器管理的raw buffer。
    // 在Andorid的窗口实现里，每一个Window其实都会对应一个Surface，
    // 而每个Activity都会持有一个Window。可以理解为所有view的绘制都会绘制到surface上面去
    private Surface                surface;

    private Intent mResultData = null;
    private String filePath;

    private static volatile ScreenShotUtils screenShotUtils;

    private ScreenShotUtils() {
        mediaProjectionManager =
                (MediaProjectionManager)
                        Utils.getApp().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public static ScreenShotUtils getInstance() {
        if (screenShotUtils == null) {
            synchronized (ScreenShotUtils.class) {
                if (screenShotUtils == null) {
                    screenShotUtils = new ScreenShotUtils();
                }
            }
        }
        return screenShotUtils;
    }

    /**
     * <b>截屏前需要进行权限申请</b><br>
     * 其中，需要使用startActivityForResult的唯一原因是，捕捉屏幕是需要用户确认权限才可以， 这个权限对应的对话框就是由{@link
     * MediaProjectionManager#createScreenCaptureIntent()
     * createScreenCaptureIntent()}创建的， 在用户点击允许之后，在onActivityResult得到确认码，才可以拿到MediaProjection对象。
     */
    public void requestCapturePermission() {

        PermissionScreenShotActivity.startRequest(Utils.getApp());
    }

    /**
     * Init. 在onActivityResult得到确认码时，赋予mResultData返回的intent
     *
     * @param mResultData the m result data
     */
    public void init(Intent mResultData) {
        this.mResultData = mResultData;
    }

    // 成功获得用户允许后获取MediaProjection对象。如果授权失败，则得到空对象。
    private void setUpMediaProjection() {
        if (mResultData == null) {
            requestCapturePermission();
            /*Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(Utils.getApp(), MainActivity.class);
            Utils.getApp().startActivity(intent);*/
        } else {
            if (mediaProjection == null) {
                mediaProjection =
                        getMediaProjectionManager()
                                .getMediaProjection(Activity.RESULT_OK, mResultData);
            }
        }
    }

    // 用Display获取屏幕尺寸要用真实的尺寸，使用getRealMetrics方法。
    // 如果使用getMetrics方法，得到的高度是缺少Navigaiton Bar的高度的。
    // 如果尺寸和屏幕不一致，最终得到的图像会是等比例缩放到屏幕大小的图像，然后空白的地方会显示黑边
    private void getWH() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager =
                (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        screenDensity = metrics.densityDpi;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    // 使用newInstance方法实例化一个ImageReader
    private void createImageReader() {
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
    }

    /** Begin screen shot. */
    public void beginScreenShot() {
        synchronized (this) {
            if (mResultData != null) {
                setUpMediaProjection();
                getWH();
                createImageReader();
                try {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(
                            () -> {
                                LogUtils.d("beginVirtual");
                                beginVirtual();
                            },
                            0
                                       );

                    handler.postDelayed(
                            () -> {
                                LogUtils.d("beginCapture");
                                beginCapture();
                            },
                            150
                                       );
                } catch (Exception e) {
                    LogUtils.e(e);
                }

            } else {
                requestCapturePermission();
            }
        }
    }

    public String beginScreenShot(String name) {
        synchronized (this) {
            if (mResultData != null) {
                setUpMediaProjection();
                getWH();
                createImageReader();
                try {

                    beginVirtual();

                    Thread.sleep(150);

                    return beginCapture(name);

                } catch (Exception e) {
                    LogUtils.e(e);
                    return null;
                }

            } else {
                requestCapturePermission();
            }
        }
        return null;
    }

    public void beginScreenShot(String fileName, Callback callback) {
        synchronized (this) {
            if (mResultData != null) {
                setUpMediaProjection();
                getWH();
                createImageReader();
                try {

                    beginVirtual();

                    beginCapture(fileName, callback);

                } catch (Exception e) {
                    LogUtils.e(e);
                }

            } else {
                requestCapturePermission();
            }
        }
    }

    private void beginVirtual() {
        if (null != mediaProjection) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void virtualDisplay() {
        // 获取可以用来为当前ImageReader生产Images的Surface对象。
        surface = imageReader.getSurface();
        // 创建一个VirtualDisplay用来捕获屏幕内容。
        virtualDisplay =
                mediaProjection.createVirtualDisplay(
                        "screen-mirror",
                        screenWidth,
                        screenHeight,
                        screenDensity,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        surface,
                        null,
                        null
                                                    );
    }

    private MediaProjectionManager getMediaProjectionManager() {
        if (mediaProjectionManager == null) {
            mediaProjectionManager =
                    (MediaProjectionManager)
                            Utils.getApp().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        return mediaProjectionManager;
    }

    public interface Callback {
        void getPath(String path);
    }

    public static Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private String beginCapture(String path) {
        Image acquireLatestImage = null;
        try {
            // 通过Surface发送给ImageReader的图像都放在队列中，由acquireLatestImage()或 acquireNextImage() 两个方法取出。
            acquireLatestImage = imageReader.acquireLatestImage();
        } catch (IllegalStateException e) {
            LogUtils.e(e);
        }

        if (acquireLatestImage == null) {
            return beginScreenShot(path);
        } else {
            /*SaveTask saveTask = new SaveTask();
            saveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, acquireLatestImage);*/

            String savePath = saveImg(path, acquireLatestImage);

            releaseVirtual();
            stopMediaProjection();

            return savePath;
        }
    }

    private void beginCapture() {
        Image acquireLatestImage = null;
        try {
            // 通过Surface发送给ImageReader的图像都放在队列中，由acquireLatestImage()或 acquireNextImage() 两个方法取出。
            acquireLatestImage = imageReader.acquireLatestImage();
        } catch (IllegalStateException e) {
            LogUtils.e(e);
        }

        if (acquireLatestImage == null) {
            beginScreenShot();
        } else {
            SaveTask saveTask = new SaveTask();
            saveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, acquireLatestImage);

            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            () -> {
                                releaseVirtual();
                                stopMediaProjection();
                            },
                            1000
                                );
        }
    }

    private void beginCapture(String name, Callback callback) {

        try {
            // 通过Surface发送给ImageReader的图像都放在队列中，由acquireLatestImage()或 acquireNextImage() 两个方法取出。
            imageReader.setOnImageAvailableListener(
                    new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = reader.acquireLatestImage();
                            if (image == null) {
                                beginScreenShot(name, callback);
                            } else {
                                /*SaveTask saveTask = new SaveTask();
                                saveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, acquireLatestImage);*/

                                String savePath = saveImg(name, image);
                                callback.getPath(savePath);

                                reader.setOnImageAvailableListener(null, null);
                                image.close();

                                releaseVirtual();
                                stopMediaProjection();
                            }
                        }
                    },
                    MAIN_HANDLER
                                                   );
        } catch (IllegalStateException e) {
            LogUtils.e(e);
        }
    }

    // 因为virtual display内容render到应用程序提供的surface，所以当进程终止时，它将会自动释放，
    // 并且所以剩余的窗口都会被强制删除。但是，你仍然需要在使用完后显式地调用release()方法
    private void releaseVirtual() {
        LogUtils.d("releaseVirtual");
        if (null != virtualDisplay) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
    }

    private void stopMediaProjection() {
        //        LogUtils.d("stopMediaProjection");
        if (null != mediaProjection) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    private String saveImg(String name, Image image) {
        if (null == image) {
            return null;
        }

        //        Image image = args[0];

        int width;
        int height;
        try {
            width = image.getWidth();
            height = image.getHeight();
        } catch (IllegalStateException e) {
            return null;
        }

        // 这部分将Image对象的字节流写进Bitmap里，但是Bitmap接收的是像素格式的。
        // 先获取图片的buffer数据，然后要把这一行buffer包含的图片宽高找出来。
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer    buffer = planes[0].getBuffer();
        // 每个像素的间距。获取pixelStride。因为是RGBA4个通道，所以每个像素的间距是4。
        int pixelStride = planes[0].getPixelStride();
        // 总的间距（得到每行的宽度rowStride）
        int rowStride = planes[0].getRowStride();
        // 因为内存对齐的缘故，所以buffer的宽度会有不同。用图片宽度×像素间距得到一个大概的宽度。
        // 然后拿获取得到的宽度减去计算出的宽度，找到内存对齐的padding。
        int rowPadding = rowStride - pixelStride * width;
        // 由于计算的padding还是把4通道展开一行的宽度，拿给图像就需要rowPadding / pixelStride统一单位和mWidth相加。
        Bitmap bitmap =
                Bitmap.createBitmap(
                        width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap =
                Bitmap.createBitmap(
                        bitmap,
                        0, /*去除顶部状态栏*/
                        SizeUtils.dp2px(24),
                        width,
                        height - SizeUtils.dp2px(24)
                                   );
        image.close();
        File fileImage = null;
        if (null != bitmap) {
            FileOutputStream fos = null;
            try {
                fileImage = new File(createFile(name));
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                } /* else {
                      return null;
                  }*/

                fos = new FileOutputStream(fileImage);
                boolean succ = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                if (succ) {
                    LogUtils.i("图片已保存至:" + fileImage.getAbsolutePath());
                }
                fos.flush();

            } catch (IOException e) {
//                LogUtils.e(e);
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        LogUtils.e(e);
                    }
                }
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }

        if (null != fileImage) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri    uri    = Uri.fromFile(new File(fileImage.getAbsolutePath()));
            intent.setData(uri);
            MyApplication.getContext().sendBroadcast(intent);
            return fileImage.getAbsolutePath();
        }
        return null;
    }

    // 输出目录
    private String createFile(String name) {
        File file = new File(APP_PIC);
        if (!file.exists()) {
            file.mkdirs();
        }
        /* String outDir =
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;*/
        return APP_PIC + File.separator + name + ".jpg";
    }

    // 保存图像任务
    private static class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... args) {
            if (null == args || 1 > args.length || null == args[0]) {
                return null;
            }

            Image image = args[0];

            int width;
            int height;
            try {
                width = image.getWidth();
                height = image.getHeight();
            } catch (IllegalStateException e) {
                return null;
            }

            // 这部分将Image对象的字节流写进Bitmap里，但是Bitmap接收的是像素格式的。
            // 先获取图片的buffer数据，然后要把这一行buffer包含的图片宽高找出来。
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer    buffer = planes[0].getBuffer();
            // 每个像素的间距。获取pixelStride。因为是RGBA4个通道，所以每个像素的间距是4。
            int pixelStride = planes[0].getPixelStride();
            // 总的间距（得到每行的宽度rowStride）
            int rowStride = planes[0].getRowStride();
            // 因为内存对齐的缘故，所以buffer的宽度会有不同。用图片宽度×像素间距得到一个大概的宽度。
            // 然后拿获取得到的宽度减去计算出的宽度，找到内存对齐的padding。
            int rowPadding = rowStride - pixelStride * width;
            // 由于计算的padding还是把4通道展开一行的宽度，拿给图像就需要rowPadding / pixelStride统一单位和mWidth相加。
            Bitmap bitmap =
                    Bitmap.createBitmap(
                            width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (null != bitmap) {
                FileOutputStream fos = null;
                try {
                    fileImage = new File(createFile());
                    if (!fileImage.exists()) {
                        boolean newFile = fileImage.createNewFile();
                        fos = new FileOutputStream(fileImage);
                        boolean succ = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        if (newFile && succ) {
                            //                            LogUtils.i("图片已保存至:" + fileImage.getAbsolutePath());
                        }
                        fos.flush();
                    }
                } catch (IOException e) {
                    //                    LogUtils.e(e);
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            //                            LogUtils.e(e);
                        }
                    }
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            }

            if (null != fileImage) {
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (getInstance().surface.isValid()) {
                //                LogUtils.d("surface release");
                getInstance().surface.release();
            }
        }

        // 输出目录
        private String createFile() {
            String outDir =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            return outDir + System.currentTimeMillis() + ".jpg";
        }
    }

    public static class PermissionScreenShotActivity extends Activity {

        public static void startRequest(final Context context) {
            Intent starter = new Intent(context, PermissionScreenShotActivity.class);
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(starter);
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            //            LogUtils.i("PermissionScreenShotActivity onCreate");
            getWindow()
                    .addFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            if (screenShotUtils == null) {
                super.onCreate(savedInstanceState);
                Log.e("PermissionUtils", "request permissions failed");
                finish();
                return;
            }

            super.onCreate(savedInstanceState);

            startActivityForResult(
                    getInstance().getMediaProjectionManager().createScreenCaptureIntent(),
                    REQUEST_SCREEN_SHOT_CODE
                                  );
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //            LogUtils.i("PermissionScreenShotActivity onActivityResult");
            if (requestCode == REQUEST_SCREEN_SHOT_CODE && resultCode == Activity.RESULT_OK) {
                //                LogUtils.i("request permissions ok");
                getInstance().init(data);
                finish();
            } else {
                //                LogUtils.e("request permissions failed");
                finish();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            finish();
            return true;
        }
    }
}
