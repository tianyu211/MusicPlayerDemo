package cn.guotianyu.musicplayer;

/**
 * Created by 郭天宇 on 2016/12/22.
 */

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
