package com.code19.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2016/4/21 021.
 */
public class VideoAdapter extends BaseAdapter {
    List<VideoBean> mList = new ArrayList<VideoBean>();
    Context mContext;

    public VideoAdapter(Context context, List<VideoBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? 0 : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.list_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.length = (TextView) convertView.findViewById(R.id.tv_length);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VideoBean bean = mList.get(position);
        holder.icon.setImageBitmap(bean.videosmallthumbnail);
        holder.name.setText(bean.videoName);
        holder.size.setText(bean.videoSize);
        holder.length.setText(bean.videoLength);
        return convertView;
    }


    class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView size;
        public TextView length;
    }
}
