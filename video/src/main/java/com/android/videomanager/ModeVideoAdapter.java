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
public class ModeVideoAdapter extends BaseAdapter {
    private Context mContext;
    private List<VideoBean> mList;

    public ModeVideoAdapter(Context context, List<VideoBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
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
            convertView = View.inflate(mContext, R.layout.mode_list_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.mode_iv_icon);
            holder.name = (TextView) convertView.findViewById(R.id.mode_tv_name);
            holder.size = (TextView) convertView.findViewById(R.id.mode_tv_size);
            holder.length = (TextView) convertView.findViewById(R.id.mode_tv_length);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VideoBean bean = mList.get(position);
        holder.icon.setImageBitmap(bean.videobigthumbnail);
        String tempName = null;
        if (bean.videoName.length() >= 12) {
            tempName = bean.videoName.substring(0, 12) + "...";
        } else {
            tempName = bean.videoName;
        }
        holder.name.setText(tempName);
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
