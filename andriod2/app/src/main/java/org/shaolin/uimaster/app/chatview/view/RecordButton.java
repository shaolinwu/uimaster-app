package org.shaolin.uimaster.app.chatview.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.shaolin.uimaster.app.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by wushaol on 2017/7/11.
 */
public class RecordButton extends Button {

    private String fileRoot = null;

    private String fileName = null;

    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 1000;// 2s
    private long startTime;

    private Dialog recordIndicator;

    private static int[] res = { R.drawable.mic_2, R.drawable.mic_3,
            R.drawable.mic_4, R.drawable.mic_5,  R.drawable.mic_6 };

    private static ImageView view;

    private MediaRecorder recorder;

    private ObtainDecibelThread thread;

    private Handler volumeHandler;


    public RecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RecordButton(Context context) {
        this(context, null);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setSavePath(String root) {
        this.fileRoot = root;
        File f = new File(root);
        if(!f.exists()) {
            f.mkdirs();
        } else if( !f.isDirectory() && f.canWrite() ){
            f.delete();
            f.mkdirs();
        }
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (fileRoot == null)
            return false;

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                try {
                    initDialogAndStartRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
                finishRecord();
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                cancelRecord();
                break;
        }

        return true;
    }

    private void initDialogAndStartRecord() throws IOException {

        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),
                R.style.like_toast_dialog_style);
        view = new ImageView(getContext());
        view.setImageResource(R.drawable.mic_2);
        recordIndicator.setContentView(view, new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recordIndicator.setOnDismissListener(onDismiss);
        WindowManager.LayoutParams lp = recordIndicator.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        startRecording();
        recordIndicator.show();
    }

    private void finishRecord() {
        stopRecording();
        recordIndicator.dismiss();

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
            File file = new File(fileRoot, fileName);
            file.delete();
            return;
        }

        File file = new File(fileRoot, fileName);
        if (finishedListener != null) {
            finishedListener.onFinishedRecord(file);
        }
    }

    private void cancelRecord() {
        stopRecording();
        recordIndicator.dismiss();

        Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
        File file = new File(fileRoot, fileName);
        file.delete();
    }

    private void startRecording() throws IOException {
        this.fileName = "r" + System.currentTimeMillis() + ".amr";
        File f = new File(fileRoot, fileName);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(f.getAbsolutePath());

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        thread = new ObtainDecibelThread();
        thread.start();

    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (f < 26)
                        volumeHandler.sendEmptyMessage(0);
                    else if (f < 32)
                        volumeHandler.sendEmptyMessage(1);
                    else if (f < 38)
                        volumeHandler.sendEmptyMessage(2);
                    else
                        volumeHandler.sendEmptyMessage(3);
                }
            }
        }
    }

    private DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    static class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            view.setImageResource(res[msg.what]);
        }
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(File audioFile);
    }

}