package cn.guotianyu.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    /**
     * 路径
     */
    private EditText et_path;
    /**
     * 音乐播放器
     */
    MediaPlayer mediaPlayer;
    //播放路径
    private String path;
    //暂停
    private Button bt_pause;
    //拖动进度条
    private SeekBar bar;
    private int position = 0;
    private static ArrayList<AudioInfo> list;

    /**
     *
     */
    private Timer timer;
    private TimerTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_path = (EditText) findViewById(R.id.et_path);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bar = (SeekBar) findViewById(R.id.bar);

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                    int progress = mediaPlayer.getCurrentPosition();//得到当前进度
                    int total = mediaPlayer.getDuration();
                    bar.setMax(total);
                    bar.setProgress(progress);
                }

            }
        };
        timer.schedule(task,500,500);


        /**
         * 拖动进度条的事件监听需要实现SeekBar.OnSeekBarChangeListener接口
         * 调用SeekBar的setOnSeekBarChangeListener把该事件监听对象传递进去进行事件监听
         */
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //  onProgressChanged
            // 该方法拖动进度条进度改变的时候调用
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            //  onStartTrackingTouch方法
            // 该方法拖动进度条开始拖动的时候调用。
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //  onStopTrackingTouch
            // 该方法拖动进度条停止拖动的时候调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int position = bar.getProgress();
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(position);
                }
            }
        });
    }

//    public void click(View view){
//        switch (view.getId()){
//            case list:
//                Intent intent = new Intent(this,DisplayListActivity.class);
//                startActivityForResult(intent,1);
//                break;
//            default:
//                break;
//        }
//    }
//    //接受子activity返回的结果时调用的方法
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode>=0){
//            position=resultCode;
//            play(resultCode);
//        }
//    }

//    //歌曲清单中选择一首后调用的方法
//    public void play(int position){
//        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.reset();
//
//            try {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(list.get(position).getPath());
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                Toast.makeText(this,"播放失败！音乐可能不存在！",Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(list.get(position).getPath());
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                Toast.makeText(this,"播放失败！音乐可能不存在！",Toast.LENGTH_SHORT).show();
//            }
//        }
//        startupdateber();
//    }
//    private android.os.Handler handler = new android.os.Handler();
//    public void startupdateber(){
//        handler.post(r);
//    }
//    private Runnable r = new Runnable(){
//        //获取进度条当前的进度
//        @Override
//        public void run() {
//            int max = mediaPlayer.getDuration();
//            int currentPosition = mediaPlayer.getCurrentPosition();
//            bar.setMax(max);
//            bar.setProgress(currentPosition);
//            handler.postDelayed(r,100);
//        }
//
//    };


    //播放
    public void play(View view){
        path = et_path.getText().toString().trim();
        if(TextUtils.isEmpty(path)){
            Toast.makeText(this,"音频文件不能为空",Toast.LENGTH_SHORT).show();
        }else{
            try {
                //1.初始化mediaPlayer
                mediaPlayer = new MediaPlayer();
                //2.设置播放器的一些初始化参数
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(path);
                //3.准备播放音乐
                mediaPlayer.prepare();
                //4.开始播放
                mediaPlayer.start();
            } catch (IOException e) {
                Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT).show();
            }
        }

    }
    //暂停
    public void pause(View view){
        if("继续".equals(bt_pause.getText().toString())){
            mediaPlayer.start();
            bt_pause.setText("暂停");
            return;
        }
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            bt_pause.setText("继续");
        }
    }
    //重播
    public void replay(View view){
        /**
         * 这是在播放情况下
         */
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            return;
        }
        /**
         * 如果是暂停状态下
         */
        if(mediaPlayer!=null){
            //播放状态为0毫秒
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            bt_pause.setText("暂停");
        }
    }
    //停止
    public void stop(View view){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        bt_pause.setText("暂停");
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        task.cancel();
        timer = null;
        task = null;
        super.onDestroy();
    }
}
