package com.feibo.testmediaplayer.manager;

import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.feibo.testmediaplayer.memory.BaseMemoryCache;
import com.feibo.testmediaplayer.memory.SoftMemoryCache;

public class MediaPlayerManager {

    private static final String TAG = MediaPlayerManager.class.getSimpleName();

    private MediaStateListener listener;

    private static BaseMemoryCache<MediaCombination> cache = new SoftMemoryCache<MediaCombination>();
   // private Map<String, MediaCombination> cache = new HashMap<String, MediaPlayerManager.MediaCombination>();

    private static MediaPlayerManager manager;

    public static MediaPlayerManager getInstance() {
        if (manager == null) {
            manager = new MediaPlayerManager();
        }
        return manager;
    }

    private MediaCombination getFromCache(String path) {
        if (path.isEmpty()) {
            return null;
        }
        MediaCombination combination = cache.get(path);
        return combination;
    }

    public void stopMediaPlayer(String path) {
        MediaCombination combination = getFromCache(path);
        if (combination == null) {
            return;
        }
        if (combination.mediaPlayer.isPlaying()) {
            combination.mediaPlayer.pause();
            combination.mediaPlayer.seekTo(0);
        }
    }


    public void stopAllMediaPlayer() {
        Collection<String> keys = cache.keys();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            MediaCombination combination = cache.get(key);
            MediaPlayer mediaPlayer = combination.mediaPlayer;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }

        /*Iterator<Entry<String, MediaCombination>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, MediaCombination> next = it.next();
            MediaCombination combination = next.getValue();
            MediaPlayer mediaPlayer = combination.mediaPlayer;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }*/
    }

    public void addMediaPlayer(FrameLayout frameLayout, Context context, final String path) {
        MediaCombination mediaCombination = getFromCache(path);
        if (mediaCombination != null) {
            MediaPlayer mediaPlayer = mediaCombination.mediaPlayer;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return;
            }
            if (mediaCombination.textureView.getParent() != frameLayout || frameLayout.getChildCount() == 0) {
                ViewGroup viewGroup = (ViewGroup) mediaCombination.textureView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(mediaCombination.textureView);
                }
                frameLayout.addView(mediaCombination.textureView);
            }
            if (!mediaCombination.isPrepared) {
                return;
            }
            stopAllMediaPlayer();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            try {

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        frameLayout.removeAllViews();
        final TextureView textureView = new TextureView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textureView.setLayoutParams(params);

        createMediaPlayer(path, textureView);

        setListener(path, textureView);

        frameLayout.addView(textureView);
    }

    private void createMediaPlayer(final String path, final TextureView textureView) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();

            setOnPreparedListener(path, mediaPlayer);
            setOnCompletionListener(path, mediaPlayer);
            setOnErrorListener(path, mediaPlayer);
            setOnBufferingUpdateListener(mediaPlayer);

            MediaCombination combination = new MediaCombination(textureView, mediaPlayer);
            cache.put(path, combination);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnBufferingUpdateListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i(TAG, "percent : " + percent);
            }
        });
    }

    private void setOnErrorListener(final String path, MediaPlayer mediaPlayer) {
        mediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                cache.remove(path);
                if (listener != null) {
                    listener.onError(path);
                }
                return true;
            }
        });
    }

    private void setOnCompletionListener(final String path, MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mp.pause();
                if (listener != null) {
                    listener.onEnd(path);
                }
            }
        });
    }

    private void setOnPreparedListener(final String path, MediaPlayer mediaPlayer) {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                MediaCombination com = getFromCache(path);
                if (com != null) {
                    com.isPrepared = true;
                }
                stopAllMediaPlayer();
                mediaPlayer.start();
            }
        });
    }

    private void setListener(final String path, final TextureView textureView) {
        textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                MediaCombination combition = getFromCache(path);
                if (combition != null && combition.mediaPlayer != null) {
                    combition.mediaPlayer.pause();
                }
                return true;
            }

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                MediaCombination combition = getFromCache(path);
                Surface surface = new Surface(surfaceTexture);
                if (combition != null && combition.mediaPlayer != null) {
                    combition.mediaPlayer.setSurface(surface);
                }
            }
        });

    }

    public static class MediaCombination {
        public TextureView textureView;
        public MediaPlayer mediaPlayer;
        public boolean isPrepared = false;

        public MediaCombination(TextureView textureView, MediaPlayer mediaPlayer) {
            this.textureView = textureView;
            this.mediaPlayer = mediaPlayer;
        }
    }

    public void setMediaStateListener(MediaStateListener listener) {
        this.listener = listener;
    }

    public static interface MediaStateListener {
        void onPrepare(int progress);

        void onStart();

        void onEnd(String path);

        void onError(String path);
    }

    public boolean isPlaying(String path) {
        MediaCombination combition = getFromCache(path);
        if (combition != null && combition.mediaPlayer != null) {
            return combition.mediaPlayer.isPlaying();
        }
        return false;
    }
}
