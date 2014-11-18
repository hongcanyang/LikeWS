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
                itemPosition--;
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
                    MediaPlayerManager.getInstance().stopMediaPlayer(info.getVideoUrl());
                }
            }
        });

        videoAdapter = new VideoAdapter(this);

        List<MediaInfo> listInfos = getMediaInfos();
        videoAdapter.setData(listInfos);
        listView.setAdapter(videoAdapter);

        MediaPlayerManager.getInstance().setMediaStateListener(new MediaStateListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPrepare(int progress) {

            }

            @Override
            public void onEnd(String path) {
                updatePointItem(path, true);
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
        });
    }

    private List<MediaInfo> getMediaInfos() {
        list = new ArrayList<MediaInfo>();

        MediaInfo info1 = new MediaInfo();
        info1.setVideoUrl("http://101.71.72.17/vweishi.tc.qq.com/1008_61ab8b772420486cb0a9ee647664c5e9.f20.mp4?vkey=D24FDDCB2B6E90BBBA5DC76DB6AED98BFE148A56C97266896E822284064143434F0B6851F4980E39&sha=d11b6aeb58051cdf0631b439e2d8922fea8ae345");
        list.add(info1);

        MediaInfo info2 = new MediaInfo();
        info2.setVideoUrl("http://101.71.72.9/vweishi.tc.qq.com/1008_8d2a86a58b65451dbf1412415b8321d0.f30.mp4?vkey=618F31A8537E48A6C60D9323F5D25C891613305C1B3710FF7319C20B938571FA1D1EAFB9197468F6&sha=dbec97ebe966a3e0ce092c64e58f0d0afaa92ea9");
        list.add(info2);

        MediaInfo info3 = new MediaInfo();
        info3.setVideoUrl("http://101.71.72.20/vweishi.tc.qq.com/1008_e6df7f657c8a4995986443c898520282.f30.mp4?vkey=B73D1204BE2C4BAE8323D41159FFEF8BDE08C63B845C2A0EAC0F7F34C88E7D348487665F18E1F984&sha=20be72921735b277a9ab0ae81fd1501307dadabd");
        list.add(info3);

        MediaInfo info4 = new MediaInfo();
        info4.setVideoUrl("http://101.71.72.21/vweishi.tc.qq.com/1008_e8689fc345434451b8e4e4c8889f8da2.f30.mp4?vkey=D62D0A7A43EA7D65D98AD19E10B865233ED25FC0F72E8A264C925BA98A2F4A47CC905724625FDA04&sha=f7abdfd39d43267b91f5bd0f9a1a9c033bafdd81");
        list.add(info4);

        MediaInfo info5 = new MediaInfo();
        info5.setVideoUrl("http://101.71.72.17/vweishi.tc.qq.com/1008_61ab8b772420486cb0a9ee647664c5e9.f20.mp4?vkey=D6B1C5FA2945FD70362970D459B45163974657C7E63BFFF38C46832297056F4E1EC2C3084B174DAA&sha=d11b6aeb58051cdf0631b439e2d8922fea8ae345");
        list.add(info5);

        return list;
    }
}
