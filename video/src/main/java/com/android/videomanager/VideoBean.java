package com.android.videomanager;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Gh0st on 2016/4/21 021.
 */
public class VideoBean {
    public Bitmap videosmallthumbnail;  //小缩略图
    public Bitmap videobigthumbnail;   //大缩略图
    public String videopath;//视频地址
    public String videoName;  //视频名称
    public String videoSize;  //视频大小
    public String videoLength;  //视频时长
    public String videodatetime;//视频的创建时间
    public Uri baseUri; //文件的Uri
}
