package cn.guotianyu.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 郭天宇 on 2016/12/22.
 */

public class DisplayListActivity extends Activity {
    private ListView lv;
    private ArrayList<AudioInfo>list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaylist);
        lv = (ListView) findViewById(R.id.lv);
        list = DisplayModel.getAudioInfo(this);//指定当前文件加载的布局文件

        lv.setAdapter(new MyAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DisplayListActivity.this,MainActivity.class);
                setResult(position, intent);//书本71，resultCode结果码，一般使用0或1
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
