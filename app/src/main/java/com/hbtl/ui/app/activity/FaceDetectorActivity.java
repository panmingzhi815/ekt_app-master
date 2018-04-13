package com.hbtl.ui.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hbtl.beans.CrossCommonAppFaceDetectorBus;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.view.DrawFacesView;
import com.hbtl.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FaceDetectorActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FaceDetectorActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_CODE = 0x100;
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;
    @BindView(R.id.commonFaceScan_Btn) LinearLayout commonFaceScan_Btn;
    @BindView(R.id.facesRecognition_FL) FrameLayout facesRecognition_FL;
    @BindView(R.id.surface_View) SurfaceView surface_View;
    @BindView(R.id.faces_View) DrawFacesView faces_View;

    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        //    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //}
        setContentView(R.layout.app_face_detector_activity);
        mActivity = FaceDetectorActivity.this;
        ButterKnife.bind(this);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        appNav_ToolBar.setTitle("优图人脸识别窗口");
        appNav_ToolBar.setBackgroundColor(Color.parseColor("#60DD2C00"));

        setSupportActionBar(appNav_ToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 注册点击事件
        commonFaceScan_Btn.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                }
                return;
            }
        }

        // 打开人脸识别程序...
        openSurfaceView();
    }

    /**
     * 把摄像头的图像显示到SurfaceView
     */
    private int surfaceWidth = 0;
    private int surfaceHeight = 0;

    private void openSurfaceView() {
        mHolder = surface_View.getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mCamera == null) {
                    // 前置摄像头...
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    int cameraCount = Camera.getNumberOfCameras(); // 摄像头数量

                    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                        Camera.getCameraInfo(camIdx, cameraInfo);
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) { // 代表摄像头的方位,目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                            //mCamera = Camera.open(camIdx);
                        }
                    }

                    // 默认后置摄像头...
                    mCamera = Camera.open();
                    try {
                        mCamera.setFaceDetectionListener(new FaceDetectorListener());
                        mCamera.setPreviewDisplay(holder);
                        startFaceDetection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                surfaceWidth = width;
                surfaceHeight = height;
                //if (BuildConfig.DEBUG) ToastHelper.makeText(CoamApplicationLoader.appContextInstance, "surfaceChanged[width: " + surfaceWidth + "][height: " + surfaceHeight + "]...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();

                if (mHolder.getSurface() == null) {
                    // preview surface does not exist
                    Timber.e(TAG + "mHolder.getSurface() == null");
                    return;
                }

                try {
                    mCamera.stopPreview();

                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    Timber.e(TAG + "Error stopping camera preview: " + e.getMessage());
                }

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    int measuredWidth = surface_View.getMeasuredWidth();
                    int measuredHeight = surface_View.getMeasuredHeight();
                    setCameraParms(mCamera, measuredWidth, measuredHeight);
                    mCamera.startPreview();

                    startFaceDetection(); // re-start face detection feature

                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                    Timber.d(TAG + "Error starting camera preview: " + e.getMessage());
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                holder = null;
            }
        });
    }

    // 获取镜头图片...
    public void takeStillshot() {

        // 关闭人脸识别定位,防止识别相框突然消失...
        //mCamera.stopFaceDetection();

        Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
                Timber.d(TAG + "onShutter'd");
            }
        };

        Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Timber.d(TAG + "onPictureTaken - raw");
            }
        };

        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Timber.d(TAG + "onPictureTaken - jpeg");

                String captureFaceInfo = "Capture Face[centerX: " + captureFaceRect.centerX() + "][centerY: " + captureFaceRect.centerY() + "][width: " + captureFaceRect.width() + "][height: " + captureFaceRect.height() + "]"
                        + "[left: " + captureFaceRect.left + "][top: " + captureFaceRect.top + "][right: " + captureFaceRect.right + "][bottom: " + captureFaceRect.bottom + "]";
                //ToastHelper.makeText(mActivity, "[#][captureFaceInfo: " + captureFaceInfo + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                Timber.w("[captureFaceInfo: " + captureFaceInfo + "]");

                int centerX = captureFaceRect.centerX();
                int centerY = captureFaceRect.centerY();
                int left = captureFaceRect.left;
                int top = captureFaceRect.top;
                int width = captureFaceRect.width();
                int height = captureFaceRect.height();
                int _width = width * surfaceWidth / 2000;
                int _height = height * surfaceHeight / 2000;

                // [Android中人脸检测的基本实现](https://yuncnc.github.io/2017/03/04/Android/face_detection/)
                // 坐标轴转换 [-1000,-1000] -> [1000,1000]
                int _centerX = (centerX + 1000) * surfaceWidth / 2000;
                int _centerY = (centerY + 1000) * surfaceHeight / 2000;
                int _startX = _centerX - (_width / 2);
                int _startY = _centerY - (_height / 2);
                int _endX = _centerX + (_width / 2);
                int _endY = _centerY + (_height / 2);
                try {
                    Bitmap srcBmp, dstBmp;
                    srcBmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // 反转 90 度保存...
                    float degrees = 90f;
                    Matrix matrix = new Matrix();
                    matrix.reset();
                    matrix.postRotate(degrees);
                    dstBmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
                    //dstBmp = Bitmap.createBitmap(srcBmp, _startX, _startY, _width, _height, matrix, true);
                    if (!srcBmp.isRecycled()) {
                        srcBmp.recycle();
                    }

                    //TODO: Compress bitmap to file
                    File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "ACamera");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File saveFile = (new File(root, System.currentTimeMillis() + ".jpg"));
                    String saveFilePath = saveFile.getAbsolutePath();
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    // 图片压缩...
                    dstBmp.compress(Bitmap.CompressFormat.JPEG, 99, fos);
                    if (!dstBmp.isRecycled()) {
                        dstBmp.recycle();
                    }

                    CrossCommonAppFaceDetectorBus crossCommonAppFaceDetectorBus = new CrossCommonAppFaceDetectorBus();
                    crossCommonAppFaceDetectorBus.captureWay = "faceDetector";
                    crossCommonAppFaceDetectorBus.captureFacePath = saveFilePath;
                    // 推送 EventBus 事件消息...
                    EventBus.getDefault().post(crossCommonAppFaceDetectorBus);

                    // 结束 Activity 扫码窗口
                    finish();

//                    // 第二种保存文件的方法
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {}
//                    File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "ACamera");
//                    if (!root.exists()) {
//                        root.mkdirs();
//                    }
//                    FileOutputStream f = new FileOutputStream(new File(root, System.currentTimeMillis() + ".jpg"));
//                    int len1 = data.length;
//                    f.write(data, 0, len1);
//                    f.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //ToastHelper.makeText(mActivity, "图像保存完成...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();
                }
            }
        };

        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    /**
     * 获取拍照之后的尺寸
     */
//    private int getPictureSize(List<Size> sizes) {
//
//
//// 屏幕的宽度
//        int screenWidth = CoamApplicationLoader.appContextInstance.getMYIntance().widthPixels;
//        int index = -1;
//
//
//        for (int i = 0; i < sizes.size(); i++) {
//            if (Math.abs(screenWidth - sizes.get(i).width) == 0) {
//                index = i;
//                break;
//            }
//        }
//// 当未找到与手机分辨率相等的数值,取列表中间的分辨率
//        if (index == -1) {
//            index = sizes.size() / 2;
//        }
//
//
//        return index;
//    }

//    private void initViews() {
//        //surface_View = new SurfaceView(this);
//        //faces_View = new DrawFacesView(this);
//        //facesRecognition_FL.addView(surface_View, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        //facesRecognition_FL.addView(faces_View, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commonFaceScan_Btn: // 通用二维码扫码

                if (!ifCaptureFace) {
                    // 人脸图片抓取失败
                    ToastHelper.makeText(mActivity, "未识别到人脸图像,截取图像失败...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                    return;
                }

                // 人脸图片抓取完成
                ToastHelper.makeText(mActivity, "已识别到人脸图像,即将截取图像验证...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

                // 获取图像
                //takeStillshot();

                //Intent intent = new Intent();
                //intent.putExtra("result", result);
                //setResult(RESULT_OK, intent);
                break;
            default:
                Timber.i("iwwwwwwwwwwwwwwwwwwwwwwwww---");
                break;
        }
    }

    /**
     * 脸部检测接口
     */
    private boolean ifCaptureFace = false;// 是否抓取脸部头像标志位...
    private Rect captureFaceRect = null;

    private class FaceDetectorListener implements Camera.FaceDetectionListener {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Camera.Face face = faces[0];
                captureFaceRect = face.rect;
                ifCaptureFace = true;
//                if (BuildConfig.DEBUG) {
//                    String captureFaceInfo = "Location Face[centerX: " + captureFaceRect.centerX() + "][centerY: " + captureFaceRect.centerY() + "][width: " + captureFaceRect.width() + "][height: " + captureFaceRect.height() + "]"
//                            + "[left: " + captureFaceRect.left + "][top: " + captureFaceRect.top + "][right: " + captureFaceRect.right + "][bottom: " + captureFaceRect.bottom + "]";
//                    Timber.w("[captureFaceInfo: " + captureFaceInfo + "]");
//                    Timber.w(captureFaceInfo);
//                }

                Matrix matrix = updateFaceRect();
                faces_View.updateFaces(matrix, faces);
                //ToastHelper.makeText(mActivity, "捕获到人脸图像[captureFaceInfo: " + captureFaceInfo + "]...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();

                // 直接抓取图像
                takeStillshot();
            } else {
                // 只会执行一次
                Timber.e("【FaceDetectorListener】类的方法:【onFaceDetection】: " + "没有脸部");
                faces_View.removeRect();
                //captureFaceRect = null;
                ifCaptureFace = false;
                //ToastHelper.makeText(mActivity, "未捕获到人脸...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.WARNING).show();
            }
        }
    }

    /**
     * 该方法出自
     * http://blog.csdn.net/yanzi1225627/article/details/38098729/
     * http://bytefish.de/blog/face_detection_with_android/
     *
     * @param matrix             这个就不用说了
     * @param mirror             是否需要翻转,后置摄像头（手机背面）不需要翻转,前置摄像头需要翻转.
     * @param displayOrientation 旋转的角度
     * @param viewWidth          预览View的宽高
     * @param viewHeight
     */
    public void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
            int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height)
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    /**
     * 因为对摄像头进行了旋转,所以同时也旋转画板矩阵
     * 详细请查看{@link Camera.Face#rect}
     *
     * @return
     */
    private Matrix updateFaceRect() {
        Matrix matrix = new Matrix();
        Camera.CameraInfo info = new Camera.CameraInfo();
        // Need mirror for front camera.
        boolean mirror = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(surface_View.getWidth() / 2000f, surface_View.getHeight() / 2000f);
        matrix.postTranslate(surface_View.getWidth() / 2f, surface_View.getHeight() / 2f);
        return matrix;
    }

    public void startFaceDetection() {
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();
        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0) {
            // mCamera supports face detection, so can start it:
            mCamera.startFaceDetection();
        } else {
            Timber.e("【FaceDetectorActivity】类的方法:【startFaceDetection】: " + "不支持");
        }
    }

    /**
     * 在摄像头启动前设置参数
     *
     * @param camera
     * @param width
     * @param height
     */
    private void setCameraParms(Camera camera, int width, int height) {
        // 获取摄像头支持的pictureSize列表
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        // 从列表中选择合适的分辨率
        Camera.Size pictureSize = getProperSize(pictureSizeList, (float) height / width);
        if (null == pictureSize) {
            pictureSize = parameters.getPictureSize();
        }
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = pictureSize.width;
        float h = pictureSize.height;
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        surface_View.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = getProperSize(previewSizeList, (float) height / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
        parameters.setJpegQuality(100);
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            // 连续对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        camera.cancelAutoFocus();
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    private Camera.Size getProperSize(List<Camera.Size> pictureSizes, float screenRatio) {
        Camera.Size result = null;
        for (Camera.Size size : pictureSizes) {
            float currenRatio = ((float) size.width) / size.height;
            if (currenRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }
        if (null == result) {
            for (Camera.Size size : pictureSizes) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {
                    result = size;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected void inNewIntent(@NotNull Intent intent) {
        // TODO ...
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        // TODO ...
    }
}
