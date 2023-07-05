package com.android.videomanager;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Video> mDataS;
    private VideoAdapter mVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDataS = new ArrayList<>();
        ListView mListView = findViewById(R.id.list);
        mVideoAdapter = new VideoAdapter(this, mDataS);
        mListView.setAdapter(mVideoAdapter);
        mListView.setOnItemClickListener((adapterView, view, position, id) -> {
            try {
                System.out.println("点击了:" + id);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(mDataS.get(position).uri, "video/*");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "打开失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(20);
        emptyView.setText(getString(R.string.empty_album_video));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).addView(emptyView);
        mListView.setEmptyView(emptyView);
        getLoaderManager().restartLoader(0, null, mVideoLoader);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> mVideoLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final Uri baseUri = MediaStore.Video.Media.getContentUri("external");

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, baseUri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor cursor) {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    while (cursor.moveToNext()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = false;
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                        Uri uri = Uri.withAppendedPath(baseUri, "" + id);
                        String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        Bitmap bigBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        Bitmap createBigBitmap = ThumbnailUtils.extractThumbnail(bigBitmap, 341, 256);
                        long aLong = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        String videoSize = Formatter.formatFileSize(MainActivity.this, aLong);
                        String videoDuration = TimeUtils.formatDuration(MainActivity.this, Integer.parseInt(duration) / 1000);
                        mDataS.add(new Video(uri, display_name, videoDuration, videoSize, createBigBitmap));
                        System.out.println("文件：" + display_name);
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
