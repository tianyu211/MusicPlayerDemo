package cn.guotianyu.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by 郭天宇 on 2016/12/22.
 *
 * 鬼知道这个类里发生了什么
 */

public class DisplayModel {
    public static ArrayList<AudioInfo>getAudioInfo(Context context){
        //ArrayList<AudioInfo>存放对象，返回值是ArrayList
        ArrayList<AudioInfo>list = new ArrayList<AudioInfo>();

        ContentResolver resolver = context.getContentResolver();//

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String []projection={MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION};
        Cursor cursor = resolver.query(uri,projection,null,null,null);

        while(cursor.moveToNext()){
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
