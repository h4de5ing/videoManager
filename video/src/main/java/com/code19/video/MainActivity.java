package com.code19.video;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    boolean mListMode = true;
    List<VideoBean> mDatas;
    private ListView mListView;
    private GridView mGridView;
    private AlertDialog mDialog;
    private VideoAdapter mVideoAdapter;
    private ModeVideoAdapter mModeVideoAdapter;
    private static SimpleDateFormat DATE_FORMAT_DATE = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDatas = new ArrayList<VideoBean>();
        DATE_FORMAT_DATE = new SimpleDateFormat(getResources().getString(R.string.formatdate));
        initData();
        mListView = (ListView) findViewById(R.id.list);
        mGridView = (GridView) findViewById(R.id.gridview);
        mVideoAdapter = new VideoAdapter(this, mDatas);
        mModeVideoAdapter = new ModeVideoAdapter(this, mDatas);
        mListView.setAdapter(mVideoAdapter);
        mGridView.setAdapter(mModeVideoAdapter);
        mListView.setOnItemClickListener(new VideoOnItemClick());
        mGridView.setOnItemClickListener(new VideoOnItemClick());
        mListView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
        mGridView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
        TextView emptyview = new TextView(this);
        emptyview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyview.setGravity(Gravity.CENTER);
        emptyview.setTextSize(20);
        emptyview.setText(getString(R.string.empty_album_video));
        emptyview.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(emptyview);
        mListView.setEmptyView(emptyview);
        mGridView.setEmptyView(emptyview);
    }

    class VideoOnItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "video/*";
            Uri uri = Uri.parse("file://" + mDatas.get(position).videopath);
            intent.setDataAndType(uri, type);
            startActivity(intent);
        }
    }

    class VideoOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            View v = View.inflate(MainActivity.this, R.layout.video_dialog, null);
            TextView tv_share = (TextView) v.findViewById(R.id.share);
            TextView tv_delete = (TextView) v.findViewById(R.id.delete);
            TextView tv_info = (TextView) v.findViewById(R.id.info);
            tv_share.setOnClickListener(new VideoOnClick(position));
            tv_delete.setOnClickListener(new VideoOnClick(position));
            tv_info.setOnClickListener(new VideoOnClick(position));
            mDialog = new AlertDialog.Builder(MainActivity.this).create();
            mDialog.setTitle(mDatas.get(position).videoName);
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
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            switch (v.getId()) {
                case R.id.share:
                    shareVideo(mDatas.get(mPosition).videopath);
                    break;
                case R.id.delete:
                    deleteVideo(mDatas.get(mPosition).videopath, mPosition);
                    break;
                case R.id.info:
                    showVideoInfo(mPosition);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        if (mListMode) {
            mListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        } else {
            mGridView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void initData() {
        //getLoaderManager().initLoader(1000, null, mVideoLoader);
        mDatas.clear();
        getLoaderManager().restartLoader(0, null, mVideoLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem test = menu.add(0, 1, 0, "列表风格");
        test.setIcon(R.drawable.mode_list);
        test.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (mListMode) {
                mListMode = false;
                item.setIcon(R.drawable.mode_grid);
                mListView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(new ModeVideoAdapter(this, mDatas));
                mGridView.setOnItemClickListener(new VideoOnItemClick());
                mGridView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
            } else {
                mListMode = true;
                item.setIcon(R.drawable.mode_list);
                mGridView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setAdapter(new VideoAdapter(this, mDatas));
                mListView.setOnItemClickListener(new VideoOnItemClick());
                mListView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareVideo(String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + filePath);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }

    private void deleteVideo(final String filepath, final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.operate_delete_file_ask, mDatas.get(position).videoName))
                .setNegativeButton(getString(R.string.common_text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(filepath, position);
                    }
                })
                .setPositiveButton(getString(R.string.common_text_cancel), null)
                .show();
    }

    private void showVideoInfo(int position) {
        View vi = View.inflate(MainActivity.this, R.layout.video_info, null);
        TextView tv_title = (TextView) vi.findViewById(R.id.info_title);
        TextView tv_mk_time = (TextView) vi.findViewById(R.id.info_mk_time);
        TextView tv_length = (TextView) vi.findViewById(R.id.info_length);
        TextView tv_size = (TextView) vi.findViewById(R.id.info_size);
        TextView tv_path = (TextView) vi.findViewById(R.id.info_path);
        tv_title.setText(getString(R.string.title) + " : " + mDatas.get(position).videoName);
        tv_mk_time.setText(getString(R.string.time) + " : " + mDatas.get(position).videodatetime);
        tv_length.setText(getString(R.string.duration) + " : " + mDatas.get(position).videoLength);
        tv_size.setText(getString(R.string.file_size) + " : " + mDatas.get(position).videoSize);
        tv_path.setText(getString(R.string.path) + " : " + mDatas.get(position).videopath);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.details))
                .setView(vi)
                .setPositiveButton(getString(R.string.common_text_ok), null)
                .create()
                .show();
    }

    private void delete(String absoluteFilepath, int position) {
        getContentResolver().delete(MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA + "=?", new String[]{absoluteFilepath});
        mDatas.remove(position);
        changeMode();
    }


    private void changeMode() {
        if (mListMode) {
            mGridView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(new VideoAdapter(this, mDatas));
            mListView.setOnItemClickListener(new VideoOnItemClick());
            mListView.setOnItemLongClickListener(new VideoOnItemLongClickListener());
        } else {
            mListView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mGridView.setAdapter(new ModeVideoAdapter(this, mDatas));
            mGridView.setOnItemClickListener(new VideoOnItemClick());
            mGridView.setOnItemLongClickListener(new VideoOnItemLongClickListener());

        }
    }

    private LoaderManager.LoaderCallbacks mVideoLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

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
                        String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        Bitmap smallBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                        Bitmap bigBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        Bitmap createBigBitmap = ThumbnailUtils.extractThumbnail(bigBitmap, 341, 256);
                        long aLong = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long longdate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                        bean.videosmallthumbnail = smallBitmap;
                        bean.videobigthumbnail = createBigBitmap;
                        bean.videopath = videoPath;
                        bean.videoName = display_name;
                        bean.videoSize = Formatter.formatFileSize(MainActivity.this, aLong);
                        bean.videoLength = TimeUtils.formatDuration(MainActivity.this, Integer.parseInt(duration) / 1000);
                        bean.videodatetime = DATE_FORMAT_DATE.format(new Date(longdate));
                        mDatas.add(bean);
                    }
                }
                cursor.close();
            }
            changeMode();
        }


        @Override
        public void onLoaderReset(Loader loader) {
        }
    };
}
