package com.feibo.testmediaplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.feibo.testmediaplayer.R;
import com.feibo.testmediaplayer.bean.MediaInfo;
import com.feibo.testmediaplayer.manager.MediaPlayerManager;

public class VideoAdapter extends BaseAdapter {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    private Context context;
    private List<MediaInfo> list;

    public VideoAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<MediaInfo> list) {
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View root;
        if (convertView != null) {
            root = convertView;
        } else {
            ViewHolder holder = new ViewHolder();
            root = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            holder.imageButton = (ImageButton) root.findViewById(R.id.item_img);
            holder.imageView = (ImageView) root.findViewById(R.id.item_ima);
            holder.frameLayout = (FrameLayout) root.findViewById(R.id.item_surfaceview_parent);
            root.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) root.getTag();

        holder.frameLayout.removeAllViews();
        holder.frameLayout.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.imageButton.setVisibility(View.VISIBLE);
        MediaPlayerManager.getInstance().stopMediaPlayer(getItem(position).getVideoUrl());

        holder.imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               updateItem(holder, position, true);
            }
        });
        return root;
    }

    public void updateItem(ViewHolder holder, int position, boolean isPlaying) {
        MediaInfo info = getItem(position);
        String path = info.getVideoUrl();
        MediaPlayerManager manager = MediaPlayerManager.getInstance();
        if (isPlaying) {
            manager.addMediaPlayer(holder.frameLayout, context, path);
            if (holder.frameLayout.getVisibility() == View.VISIBLE) {
                return;
            }
            holder.frameLayout.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.GONE);
        } else {
            manager.stopMediaPlayer(path);
            if (holder.frameLayout.getVisibility() == View.GONE) {
                return;
            }
            holder.frameLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageButton.setVisibility(View.VISIBLE);
        }
    }

    public static class ViewHolder {
        public ImageButton imageButton;
        public ImageView imageView;
        public FrameLayout frameLayout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MediaInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
