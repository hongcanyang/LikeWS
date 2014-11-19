package com.feibo.testmediaplayer.manager;

import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.feibo.testmediaplayer.manager.MediaCombinationCreater.MediaCombination;
import com.feibo.testmediaplayer.manager.MediaCombinationCreater.MediaPlayerStateListener;
import com.feibo.testmediaplayer.memory.BaseMemoryCache;
import com.feibo.testmediaplayer.memory.SoftMemoryCache;

public class MediaPlayerManager {

    private static final String TAG = MediaPlayerManager.class.getSimpleName();

    private MediaStateListener listener;

    private static BaseMemoryCache<MediaCombination> cache = new SoftMemoryCache<MediaCombination>();

    private static MediaPlayerManager manager;

    public static MediaPlayerManager getInstance() {
        if (manager == null) {
            manager = new MediaPlayerManager();
        }
        return manager;
    }

    public void hideVideoCanvas(String path) {
        MediaCombination combination = getFromCache(path);
        if (combination == null) {
            return;
        }
        MediaPlayer mediaPlayer = combination.getMediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    public void showVideoCanvas(FrameLayout frameLayout, Context context, final String path) {
        MediaCombination mediaCombination = getFromCache(path);
        if (mediaCombination != null) {
            MediaPlayer mediaPlayer = mediaCombination.getMediaPlayer();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return;
            }
            useExitCanvas(frameLayout, mediaCombination);
            if (!mediaCombination.isPrepared()) {
                return;
            }
            playVideo(mediaPlayer);
            return;
        }

        mediaCombination = createMediaCombination(context, path);
        if (mediaCombination == null) {
            return;
        }
        TextureView textureView = mediaCombination.getTextureView();
        if (textureView == null) {
            return;
        }
        addVideoCanvas(frameLayout, path, mediaCombination, textureView);
    }

    private void playVideo(MediaPlayer mediaPlayer) {
        stopAllMediaPlayer();
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    private void useExitCanvas(FrameLayout frameLayout, MediaCombination mediaCombination) {
        TextureView textureView = mediaCombination.getTextureView();
        if (textureView.getParent() != frameLayout || frameLayout.getChildCount() == 0) {
            ViewGroup viewGroup = (ViewGroup) textureView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(textureView);
            }
            frameLayout.addView(textureView);
        }
    }

    private void addVideoCanvas(FrameLayout frameLayout, final String path, MediaCombination mediaCombination,
            TextureView textureView) {
        frameLayout.removeAllViews();
        cache.put(path, mediaCombination);
        if (textureView.getParent() != null) {
            ((ViewGroup)textureView.getParent()).removeView(mediaCombination.getTextureView());
        }
        frameLayout.addView(textureView);
    }

    private MediaCombination createMediaCombination(Context context, final String path) {
        MediaCombination mediaCombination;
        mediaCombination = MediaCombinationCreater.create(context, path, addMediaPlayerStateListener(path));
        if (mediaCombination == null) {
            return null;
        }
        return mediaCombination;
    }

    private MediaPlayerStateListener addMediaPlayerStateListener(final String path) {
        return new MediaPlayerStateListener() {
            @Override
            public void onViewDestory(String key) {
                MediaCombination combition = getFromCache(path);
                if (combition != null && combition.getMediaPlayer() != null) {
                    combition.getMediaPlayer().pause();
                }
            }

            @Override
            public void onViewAvailable(Surface surface, String key) {
                MediaCombination combition = getFromCache(key);
                if (combition != null && combition.getMediaPlayer() != null) {
                    combition.getMediaPlayer().setSurface(surface);
                }
            }

            @Override
            public void onPrepared(String key) {
                MediaCombination combition = cache.get(key);
                if (combition == null || combition.getMediaPlayer() == null) {
                    return;
                }
                stopAllMediaPlayer();
                combition.setPrepared(true);
                combition.getMediaPlayer().start();
            }

            @Override
            public void onError(String key) {
                cache.remove(key);
                if (listener != null) {
                    listener.onError(path);
                }
            }

            @Override
            public void onCompletion(String key) {
                MediaCombination combition = cache.get(key);
                if (combition == null || combition.getMediaPlayer() == null) {
                    return;
                }
                MediaPlayer mp = combition.getMediaPlayer();
                mp.seekTo(0);
                mp.pause();
                if (listener != null) {
                    listener.onCompletion(key);
                }

            }
        };
    }

    private void stopAllMediaPlayer() {
        Collection<String> keys = cache.keys();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            MediaCombination combination = cache.get(key);
            MediaPlayer mediaPlayer = combination.getMediaPlayer();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }
    }

    public boolean isPlaying(String path) {
        MediaCombination combition = getFromCache(path);
        if (combition != null && combition.getMediaPlayer() != null) {
            return combition.getMediaPlayer().isPlaying();
        }
        return false;
    }

    private MediaCombination getFromCache(String path) {
        if (path.isEmpty()) {
            return null;
        }
        MediaCombination combination = cache.get(path);
        return combination;
    }

    public void setMediaStateListener(MediaStateListener listener) {
        this.listener = listener;
    }

    public static interface MediaStateListener {
        void onPrepare(int progress);

        void onStart();

        void onCompletion(String path);

        void onError(String path);
    }
}
