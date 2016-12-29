# 安卓简单音乐播放器实现seekBar

标签 ： 安卓

---

## 目录结构
- java
    - com.example.musicplayer
        - AudioInfo
        - DisPlayActivity 
        - DisPlayModel  
        - MainActivity
- res
    - drawable
        - btn_audio_list_normal.png
    - layout
        - activity_main
        - displayList  
        - music_item   
    
> 手机内存卡根目录如果有音乐，运行时就直接填写音乐路径如`/sdcard/Hoaprox.mp3`
  或者直接播放网络上的音乐，如`http://guotianyu.cn:211/Hoaprox.mp3`
*播放网络音乐需要在清单文件中添加权限*
[Andorid Studio在清单文件中添加网络权限](https://www.zybuluo.com/tianyu-211/note/612196)
    
*[这还有个不正经的音乐播放器](https://git.coding.net/tianyu211/MusicPlayer.git)*

![效果图](https://i.niupic.com/images/2016/12/25/ZEWKF7.png)    
### AudioInfo
>   用于保存数据，Get/Set方法

    public class AudioInfo {
        private String path;//显示歌曲的路径
        private String name;//显示歌曲的名字
        private String artist;//显示歌曲的作曲家名字
        private int duration;//歌曲时长
    
        public String getPath() {
            return path;
        }
    
        public void setPath(String path) {
            this.path = path;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getArtist() {
            return artist;
        }
    
        public void setArtist(String artist) {
            this.artist = artist;
        }
    
        public int getDuration() {
            return duration;
        }
    
        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

### MainActivity
> 主程序类

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

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //查找控件
            et_path = (EditText) findViewById(R.id.et_path);
            bt_pause = (Button) findViewById(R.id.bt_pause);
            bar = (SeekBar) findViewById(R.id.bar);
            //这里比较重要，给list赋值
            list=DisplayModel.getAudioInfo(this);
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
    
        //点击列表按钮
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
    
        //接受子activity返回的结果时调用的方法
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode>=0){
    //            position = resultCode; //???要这个干啥??
                //设置返回的路径
                path = list.get(resultCode).getPath();
                //把路径在EditText展示
                et_path.setText(path);
            }
        }
    
        //播放按钮
        public void play(View view){
            System.out.println(path);
            /**
             * 这里有个无法修复的BUG，就是多次点击播放会出现多重唱
             * 是因为在真正的项目开发中，播放停止功能逻辑都是写在后台服务的onCreate()中
             * 而后台服务的onCreate()只会执行一次，所以不会出现这种情况，这里不用管就好
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
        //销毁这些玩意
        @Override
        protected void onDestroy() {
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
            super.onDestroy();
        }
    }

###DisPlayListActivity
>有关列表界面的方法

    public class DisplayListActivity extends Activity {
        private ListView lv;
        private ArrayList<AudioInfo>list;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.displaylist);
            lv = (ListView) findViewById(R.id.lv);
            list = DisplayModel.getAudioInfo(this);//指定当前文件加载的布局文件
            //设置数据适配器
            lv.setAdapter(new MyAdapter());
            //设置值一个监听器
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(DisplayListActivity.this,MainActivity.class);
                    setResult(position, intent);
                    finish();
                }
            });
        }
    
        /**
         * 继承BaseAdapter方法需要重写
         * 主要两个是
         * getCount() //返回列表个数
         * getView(int position, View convertView, ViewGroup parent)//返回每个位置条目的view对象
         * getItem(int position)//根据位置获得对象
         * getItemId(int position) //根据位置获得对象id
         * 自动重写这些方法需要选中 BaseAdapter 然后alt+enter 选择Implement methods
         */
        class MyAdapter extends BaseAdapter{
            //配置数据适配器
    
            @Override
            public int getCount() {//控制listview条目数量
                return list.size();
            }
    
            @Override
            public Object getItem(int position) {
                return list.get(position);
            }
    
            @Override
            public long getItemId(int position) {
                return position;
            }
    
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //控制listview的内容
                View view = View.inflate(DisplayListActivity.this,R.layout.music_item,null);
                TextView musicname = (TextView) view.findViewById(R.id.musicname);
                TextView musicduration = (TextView) view.findViewById(R.id.musicduration);
                musicname.setText(list.get(position).getName());
    
                System.out.println(position);
    
                int time = list.get(position).getDuration();//歌曲时长，以毫秒为计量单位
                time = time/1000;//毫秒换成秒
                int minute = time/60;//秒换成分
                int second = time%60;//秒
                String duration = String.format("%02d:%02d",minute,second);//输出格式
                musicduration.setText(duration);
                // return view
                return view;
            }
        }
    }

###DisPlayModel
>给模型设置数据的方法

    public class DisplayModel {
        public static ArrayList<AudioInfo>getAudioInfo(Context context){
            //ArrayList<AudioInfo>存放对象，返回值是ArrayList
            ArrayList<AudioInfo>list = new ArrayList<AudioInfo>();
    
            ContentResolver resolver = context.getContentResolver();//访问别人的数据库，使用四大组件provider
    
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //
            //System.out.println(uri);
            String []projection={MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION};
            //
    //        for(int i=0;i<projection.length;i++) {
    //            System.out.println(projection[i]);
    //        }
            Cursor cursor = resolver.query(uri,projection,null,null,null);
    
            while(cursor.moveToNext()){
                //一次次把数据放进集合里
                AudioInfo music = new AudioInfo();
                music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                music.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                list.add(music);
            }
            cursor.close();
            return list;
    
        }
    }
    
### activity_main.xml
> 主界面视图

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:id="@+id/activity_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="@dimen/activity_vertical_margin"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        android:orientation="vertical"
        tools:context="cn.guotianyu.musicplayer.MainActivity">
    
        <LinearLayout
            android:layout_width="match_parent"
                android:layout_height="wrap_content">
            <EditText
                android:id="@+id/et_path"
                android:layout_width="0dp"
                android:layout_weight="1.5"
                android:layout_height="wrap_content"
                android:hint="请输入要播放的音乐路径" />
    
        </LinearLayout>
        <SeekBar
            android:id="@+id/bar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:layout_marginLeft="20dp"
            android:layout_marginRight="20dp"/>
        <LinearLayout
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginLeft="15dp"
            android:layout_marginTop="15dp">
            <Button
                android:onClick="play"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="播放"/>
            <Button
                android:id="@+id/bt_pause"
                android:onClick="pause"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="暂停"/>
        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginLeft="15dp"
            android:layout_marginTop="15dp">
            <Button
                android:onClick="replay"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="重播"/>
            <Button
                android:onClick="stop"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="停止"/>
    
        </LinearLayout>
    
    </LinearLayout>
    
###displaylist.xml
>列表视图，就一个ListView

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ListView
            android:id="@+id/lv"
            android:layout_width="match_parent"
            android:layout_height="50dp">
    
        </ListView>
    
    </LinearLayout>
    
###music_item.xml
>自定义ListView单元格样式

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:orientation="horizontal"
        android:background="#000000">
        <TextView
            android:id="@+id/musicname"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="歌曲名"
            android:textColor="#ffffff"
            android:layout_weight="1.5"
            android:layout_marginLeft="30dp"/>
        <TextView
            android:id="@+id/musicduration"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="时长"
            android:layout_marginRight="20dp"
            android:textColor="#ffffff"/>
    
    </LinearLayout>
