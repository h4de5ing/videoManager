package com.android.videomanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<VideoBean> mDataS;
    private AlertDialog mDialog;
    private static SimpleDateFormat DATE_FORMAT_DATE = null;
    private VideoAdapter mVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDataS = new ArrayList<>();
        DATE_FORMAT_DATE = new SimpleDateFormat(getResources().getString(R.string.formatdate));
        initData();
        ListView mListView = findViewById(R.id.list);
        mVideoAdapter = new VideoAdapter(this, mDataS);
        mListView.setAdapter(mVideoAdapter);
        mListView.setOnItemClickListener(new VideoOnItemClick());
        mListView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(20);
        emptyView.setText(getString(R.string.empty_album_video));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(emptyView);
        mListView.setEmptyView(emptyView);
    }

    class VideoOnItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(mDataS.get(position).baseUri, "video/*");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "打开失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    class VideoOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            View v = View.inflate(MainActivity.this, R.layout.video_dialog, null);
            TextView tv_delete = v.findViewById(R.id.delete);
            TextView tv_info = v.findViewById(R.id.info);
            tv_delete.setOnClickListener(new VideoOnClick(position));
            tv_info.setOnClickListener(new VideoOnClick(position));
            mDialog = new AlertDialog.Builder(MainActivity.this).create();
            mDialog.setTitle(mDataS.get(position).videoName);
            mDialog.setView(v);
            mDialog.show();
            return true;
        }
    }

    class VideoOnClick implements View.OnClickListener {
        int mPosition;

        public VideoOnClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mDialog.isShowing()) mDialog.dismiss();
            int id = v.getId();
            if (id == R.id.delete) deleteVideo(mDataS.get(mPosition).videopath, mPosition);
            else if (id == R.id.info) showVideoInfo(mPosition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mDataS.clear();
        getLoaderManager().restartLoader(0, null, mVideoLoader);
    }


    private void deleteVideo(final String filepath, final int position) {
        new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.delete)).setMessage(getString(R.string.operate_delete_file_ask, mDataS.get(position).videoName)).setNegativeButton(getString(R.string.common_text_ok), (dialog, which) -> delete(filepath, position)).setPositiveButton(getString(R.string.common_text_cancel), null).show();
    }

    private void showVideoInfo(int position) {
        View vi = View.inflate(MainActivity.this, R.layout.video_info, null);
        TextView tv_title = vi.findViewById(R.id.info_title);
        TextView tv_mk_time = vi.findViewById(R.id.info_mk_time);
        TextView tv_length = vi.findViewById(R.id.info_length);
        TextView tv_size = vi.findViewById(R.id.info_size);
        TextView tv_path = vi.findViewById(R.id.info_path);
        tv_title.setText(getString(R.string.title) + " : " + mDataS.get(position).videoName);
        tv_mk_time.setText(getString(R.string.time) + " : " + mDataS.get(position).videodatetime);
        tv_length.setText(getString(R.string.duration) + " : " + mDataS.get(position).videoLength);
        tv_size.setText(getString(R.string.file_size) + " : " + mDataS.get(position).videoSize);
        tv_path.setText(getString(R.string.path) + " : " + mDataS.get(position).videopath);
        new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.details)).setView(vi).setPositiveButton(getString(R.string.common_text_ok), null).create().show();
    }

    private void delete(String absoluteFilepath, int position) {
        getContentResolver().delete(MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA + "=?", new String[]{absoluteFilepath});
        mDataS.remove(position);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> mVideoLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Uri uu = MediaStore.Video.Media.getContentUri("external");
            return new CursorLoader(MainActivity.this, uu, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor cursor) {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    while (cursor.moveToNext()) {
                        VideoBean bean = new VideoBean();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = false;
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                        Uri baseUri = Uri.parse("content://media/external/video/media");
                        bean.baseUri = Uri.withAppendedPath(baseUri, "" + id);
                        String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        Bitmap smallBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                        Bitmap bigBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        Bitmap createBigBitmap = ThumbnailUtils.extractThumbnail(bigBitmap, 341, 256);
                        long aLong = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long elongate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                        bean.videosmallthumbnail = smallBitmap;
                        bean.videobigthumbnail = createBigBitmap;
                        bean.videopath = videoPath;
                        bean.videoName = display_name;
                        bean.videoSize = Formatter.formatFileSize(MainActivity.this, aLong);
                        bean.videoLength = TimeUtils.formatDuration(MainActivity.this, Integer.parseInt(duration) / 1000);
                        bean.videodatetime = DATE_FORMAT_DATE.format(new Date(elongate));
                        mDataS.add(bean);
                    }
                }
                cursor.close();
            }
            mVideoAdapter.notifyDataSetChanged();
        }


        @Override
        public void onLoaderReset(Loader loader) {
        }
    };
}
