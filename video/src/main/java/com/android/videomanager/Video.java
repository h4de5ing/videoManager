package com.android.videomanager;

import android.graphics.Bitmap;
import android.net.Uri;

public class Video {
    public final Uri uri;
    public final String videoName;
    public final String duration;//时长
    public final String size;//文件大小
    public Bitmap videoThumbnail;//缩略图

    public Video(Uri uri, String name, String duration, String size, Bitmap videoThumbnail) {
        this.uri = uri;
        this.videoName = name;
        this.duration = duration;
        this.size = size;
        this.videoThumbnail = videoThumbnail;
    }
}
