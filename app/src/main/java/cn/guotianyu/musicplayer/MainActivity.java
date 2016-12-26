package cn.guotianyu.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //路径
    private EditText et_path;
    //音乐播放器
    MediaPlayer mediaPlayer;
    //播放路径
    private String path;
    //暂停
    private Button bt_pause;
    //拖动进度条
    private SeekBar bar;
    private int position = 0;
    //可以理解成是AuduiInfo类型的数组
    private static ArrayList<AudioInfo> list;
    //计时器
    private Timer timer;
    private TimerTask task;

    private EditText et_image;
    private ImageView iv;

    //这里导包需要导入andoird.os中的包
    private Handler handler = new Handler(){
        //在这里ctrl+o可以快速重写方法，选择handleMessage

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //用Bitmap接收msg
            Bitmap bitmap = (Bitmap) msg.obj;
            iv.setImageBitmap(bitmap);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //查找控件
        et_path = (EditText) findViewById(R.id.et_path);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bar = (SeekBar) findViewById(R.id.bar);

        et_image = (EditText) findViewById(R.id.et_image);
        iv = (ImageView) findViewById(R.id.iv);
        //计时
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

    //点击列表
    public void click(View view){
        switch (view.getId()){
            case R.id.list:
                Intent intent = new Intent(this,DisplayListActivity.class);
                //startActivityForResult( )
                //当程序执行到这段代码的时候，假若从T1Activity跳转到下一个Text2Activity，
                //而当这个Text2Activity调用了finish()方法以后，程序会自动跳转回T1Activity，
                //并调用前一个T1Activity中的onActivityResult( )方法。
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
    }
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
//    public void play(View view){
//        path = et_path.getText().toString().trim();
//        if(TextUtils.isEmpty(path)){
//            Toast.makeText(this,"音频文件不能为空",Toast.LENGTH_SHORT).show();
//        }else{
//            try {
//                //1.初始化mediaPlayer
//                mediaPlayer = new MediaPlayer();
//                //2.设置播放器的一些初始化参数
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDataSource(path);
//                //3.准备播放音乐
//                mediaPlayer.prepare();
//                //4.开始播放
//                mediaPlayer.start();
//            } catch (IOException e) {
//                Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    //播放按钮
    public void play(View view){
        path = et_path.getText().toString().trim();
        //判断是否输入了路径
        if(TextUtils.isEmpty(path)) {
            Toast.makeText(this,"路径不能为空",Toast.LENGTH_SHORT).show();
        }else{
            /**
             * 这里有个无法修复的BUG，就是多次点击播放会出现多重唱
             * 是因为在真正的项目开发中，播放停止功能逻辑都是写在后台服务的onCreate()中
             * 而后台服务的onCreate()只会执行一次，所以不会出现这种情况
             */
            try {
                //1.初始化mediaPlayer
                mediaPlayer = new MediaPlayer();
                //2.设置播放器的一些初始化参数

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置媒体流类型，根据播放类型分配内存
//            mediaPlayer.setDataSource(this,"http://guotianyu.cn:211/a.mp3");//这里可以设置播放网络音乐
                mediaPlayer.setDataSource(path);//设置播放音乐路径
                //3.准备播放音乐
                /**
                 * 本地音频
                 * mediaPlayer.prepare();
                 * 在主线程准备播放，如果主线程不执行完毕，下面就不执行
                 * 所以如果播放网络音乐，准备的时间过长
                 * 就容易出现 ANR （application not respond）程序无响应
                 */
                /**
                 * 网络音频
                 * mediaPlayer.prepareAsync();
                 * 是指在子线程准备播放,即使子线程不执行完毕，也会执行下一步
                 * 这样造成的问题就是，如果音乐没有下载完毕，那么仍然执行下一步
                 * 导致音乐无法播放
                 */
                mediaPlayer.prepareAsync();
                /**
                 * 所以设置一个setOnPreparedListener
                 * 设置一个监听器
                 */
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    //一旦监听器准备好，就调用onPrepared方法
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //4.播放音乐
                        mediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                Toast.makeText(this,"音乐无法播放",Toast.LENGTH_SHORT).show();
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
        bar.setProgress(0);

    }

    //查看图片
    public void image(View view){
        final String path = et_image.getText().toString().trim();
        if(TextUtils.isEmpty(path)){
            Toast.makeText(this,"图片路径不能为空",Toast.LENGTH_SHORT).show();
        }else {
            //安卓4.0以后的UI修改只能在子线程进行
            //所以新建一个子线程
            //new Thread(){}.start();
            new Thread(){
                @Override
                public void run() {
                    //下载网络上的图片，显示到imageView里
                    try {
                        //1.创建URL对象
                        URL url = new URL(path);
                        //2.通过url对象打开http连接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //conn默认采用GET方式获取连接
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept","text/php,*.png,*/*");
                        int code = conn.getResponseCode();
                        //200 ok 404文件不存在或者503服务器内部错误
                        if(code == 200){
                            //得到服务器返回的数据流
                            InputStream is = conn.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            //iv.setImageBitmap(bitmap);
                            //由于子线程不可以直接修改ui
                            //这里是先获取数据后，调用主线程的handler发消息去更新ui
                            Message msg = new Message();
                            //把获取到的bitmap放到msg盒子里
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        }else {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
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
