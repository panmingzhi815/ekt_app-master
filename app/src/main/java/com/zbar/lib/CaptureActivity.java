package com.zbar.lib;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.RelativeLayout;

import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import timber.log.Timber;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014年5月9日 下午12:25:31
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 扫描界面
 */
public class CaptureActivity extends BaseActivity implements Callback {

    protected CaptureActivityHandler handler;
    protected boolean hasSurface;
    protected InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    protected boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    protected boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    protected RelativeLayout mContainer = null;
    protected RelativeLayout mCropLayout = null;
    private boolean isNeedCapture = false;

//    @BindView(R.id.appNav_ToolBar)
//    Toolbar appNav_ToolBar;

    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    /**
     * Called when the activity is first created.
     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.menu_common_scan_qr_code_activity);
//        ButterKnife.bind(this);
//
//        // 初始化 CameraManager
//        CameraManager.init(getApplication());
//        hasSurface = false;
//        inactivityTimer = new InactivityTimer(this);
//
//        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
//        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
//
//        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
//        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
//        mAnimation.setDuration(1500);
//        mAnimation.setRepeatCount(-1);
//        mAnimation.setRepeatMode(Animation.REVERSE);
//        mAnimation.setInterpolator(new LinearInterpolator());
//        mQrLineView.setAnimation(mAnimation);
//
////        findViewById(R.id.navBack).setOnClickListener(new OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                // TODO Auto-generated method stub
////                finish();
////            }
////        });
//
//        appNav_ToolBar.setTitle("全功能扫码");
//        appNav_ToolBar.inflateMenu(R.menu.common_scan_qr_code_menu);
//        setSupportActionBar(appNav_ToolBar);
//
//        // 设置透明
////        appNav_ToolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(mContext, R.color.primary)));
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮
//        //getSupportActionBar().setHomeButtonEnabled(true);
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////            getSupportActionBar().setHomeButtonEnabled(true);
////            getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//                //finish();
//            }
//        });
//
//    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.common_scan_qr_code_menu, menu);//否则不会显示右侧 app_setting 下拉菜单
//        return true;
//    }
//
//    private MenuItem mHelpAction;
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        mHelpAction = menu.findItem(R.id.help);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    /**
//     * 菜单键点击的事件处理
//     */
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
//////        return super.onOptionsItemSelected(item);
////        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
////    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.help:
//                // TODO Auto-generated method stub
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


	/*boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}*/

//    @SuppressWarnings("deprecation")
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        if (hasSurface) {
//            initCamera(surfaceHolder);
//        } else {
//            surfaceHolder.addCallback(this);
//            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        }
//        playBeep = true;
//        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//            playBeep = false;
//        }
//        initBeepSound();
//        vibrate = true;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (handler != null) {
//            handler.quitSynchronously();
//            handler = null;
//        }
//        CameraManager.get().closeDriver();
//    }
//
//    @Override
//    protected void onDestroy() {
//        inactivityTimer.shutdown();
//        super.onDestroy();
//    }
    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        finish();

        // 连续扫描,不发送此消息扫描一次结束后就不能再次扫描
        // handler.sendEmptyMessage(R.id.restart_preview);
    }

    protected void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(true);


        } catch (IOException ioe) {
            Timber.e("#|@|EEEEEEEE[E-M]:" + ioe.getMessage());
            ioe.printStackTrace();
            Timber.e("eeeeeeeeeeeeeeeeeeeeeeee");
            return;
        } catch (RuntimeException e) {
            Timber.e("eeeeeeeeeeeee");
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    protected void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void inNewIntent(@NotNull Intent intent) {
        // TODO ...
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        // TODO ...
    }
}