package com.android.videomanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gh0st on 2016/4/21 021.
 */
public class VideoAdapter extends BaseAdapter {
    private List<Video> mList;
    private Context mContext;

    public VideoAdapter(Context context, List<Video> list) {
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
            holder.icon = convertView.findViewById(R.id.iv_icon);
            holder.name = convertView.findViewById(R.id.tv_name);
            holder.size = convertView.findViewById(R.id.tv_size);
            holder.length = convertView.findViewById(R.id.tv_length);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        Video bean = mList.get(position);
        holder.icon.setImageBitmap(bean.videoThumbnail);
        holder.name.setText(bean.videoName);
        holder.size.setText(bean.size);
        holder.length.setText(bean.duration);
        return convertView;
    }


    class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView size;
        public TextView length;
    }
}
