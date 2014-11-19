package com.feibo.testmediaplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.feibo.testmediaplayer.adapter.VideoAdapter;
import com.feibo.testmediaplayer.adapter.VideoAdapter.ViewHolder;
import com.feibo.testmediaplayer.bean.MediaInfo;
import com.feibo.testmediaplayer.manager.MediaPlayerManager;
import com.feibo.testmediaplayer.manager.MediaPlayerManager.MediaStateListener;

public class ListVidioActivity extends Activity {

    private static final String TAG = ListVidioActivity.class.getSimpleName();
    private static final int SHOW_VIDEO = 0;

    private ListView listView;
    private List<MediaInfo> list;
    private VideoAdapter videoAdapter;
    private int currentPosition;
    private int playingPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SHOW_VIDEO) {
                updateListview(currentPosition, true);
            }
        }
    };

    private void delayForPlayVideo(long delayTime) {
        Message msg = handler.obtainMessage();
        msg.what = 0;
        handler.sendMessageDelayed(msg, delayTime);
    }

    private void updateListview(int itemPosition, boolean isPlaying) {
        int visiblePosition = listView.getFirstVisiblePosition();
        if (itemPosition - visiblePosition >= 0) {
            View view = listView.getChildAt(itemPosition - visiblePosition);
            if (view == null) {
                return;
            }
            int top = view.getTop();
            int hight = view.getHeight();
            if (Math.abs(top) * 2 > hight) {
                itemPosition += 1;
            }
            if (itemPosition >= listView.getAdapter().getCount()) {
                itemPosition = listView.getAdapter().getCount() - 1;
            }
            view = listView.getChildAt(itemPosition - visiblePosition);
            if (view == null) {
                return;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                return;
            }
            videoAdapter.updateItem(holder, itemPosition, isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        listView = (ListView) findViewById(R.id.video_list);
        videoAdapter = new VideoAdapter(this);

        List<MediaInfo> listInfos = getMediaInfos();
        videoAdapter.setData(listInfos);
        listView.setAdapter(videoAdapter);

        setOnScrollListener();
        setMediaPlayerListener();
    }

    private void setOnScrollListener() {
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    delayForPlayVideo(300);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                handler.removeMessages(SHOW_VIDEO);
                currentPosition = firstVisibleItem;
                if (firstVisibleItem != playingPosition) {
                    MediaInfo info = list.get(playingPosition);
                    MediaPlayerManager.getInstance().hideVideoCanvas(info.getVideoUrl());
                }
            }
        });
    }

    private void setMediaPlayerListener() {
        MediaPlayerManager.getInstance().setMediaStateListener(new MediaStateListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPrepare(int progress) {

            }

            private void updatePointItem(String path, boolean isPlaying) {
                for (int i = 0; i < list.size(); i++) {
                    MediaInfo info = list.get(i);
                    if (info.getVideoUrl().endsWith(path)) {
                        currentPosition = i;
                        updateListview(i, isPlaying);
                    }
                }
            }

            @Override
            public void onError(String path) {
                updatePointItem(path, false);
                updateListview(currentPosition, true);
            }

            @Override
            public void onCompletion(String path) {
                updatePointItem(path, true);
            }
        });
    }

    private List<MediaInfo> getMediaInfos() {
        list = new ArrayList<MediaInfo>();

        MediaInfo info1 = new MediaInfo();
        info1.setVideoUrl("http://101.71.72.17/vweishi.tc.qq.com/1008_30733e0922c1450da667e039ca809c2e.f30.mp4?vkey=F7A1849BEB0AC3E24E7A8E05C6D355CDD18BFED43495936DE3A04A121E5A2866CA7C69D9C9DE22B4&sha=bb72c85fce2cf953f78218a1dee0f78fa8b8ca44");
        list.add(info1);

        MediaInfo info2 = new MediaInfo();
        info2.setVideoUrl("http://101.71.72.17/vweishi.tc.qq.com/1008_6317f546b16846f2b11c373c69b80b7c.f30.mp4?vkey=2426C2F4D1704228A5D7E9B63BE656BDCAAD18B0C863B98F8163A908070D954E9CA45DB131149554&sha=11d558ae92a128f25");
        list.add(info2);

        MediaInfo info3 = new MediaInfo();
        info3.setVideoUrl("http://101.71.72.12/vweishi.tc.qq.com/1008_ef7f936a2f8a48deb453bb4ac9e49723.f30.mp4?vkey=205789E620DBC4FD9213EEB36C556C533F14451A3A081C7F1F728EC5C916161D7764E06892CDDB54&sha=b06a6a038cfade0b7");
        list.add(info3);

        MediaInfo info4 = new MediaInfo();
        info4.setVideoUrl("http://101.71.72.17/vweishi.tc.qq.com/1008_e42a9230906044f59054a64d93dad950.f30.mp4?vkey=7DF41B4027E9D50AC527618D0D6D0C07880339E991ACEEE4A74B471732BDA4F1E4709038876DA6D3&sha=c2243db4245ea3e96");
        list.add(info4);

        return list;
    }
}
