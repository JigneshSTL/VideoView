package com.solutelabs;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.io.File;
import java.io.IOException;

public class VideoView extends TextureView
        implements TextureView.SurfaceTextureListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mMediaPlayer;
    private Uri mSource;
    private boolean isPaused = false;
    private boolean isSquare = false;

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mMediaPlayer = mediaPlayer;
            mMediaPlayer.start();
        }
    };

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            mMediaPlayer = mediaPlayer;
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START && isPaused) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pause();
                    }
                }, 200);
            }
            return false;
        }
    };

    public VideoView(Context context) {
        this(context, null, 0);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);

        if (isInEditMode()) {
            return;
        }
    }

    public void setSource(int resId) {
        setSource(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resId));
    }

    public void setSource(String path) {
        setSource(new File(path));
    }

    public void setSource(File file) {
        if (file != null && file.exists())
            setSource(Uri.fromFile(file));
    }

    public void setSource(Uri source) {
        mSource = source;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isSquare) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            if (width < height) {
                this.setMeasuredDimension(width, width);
            } else if (width > height) {
                this.setMeasuredDimension(height, height);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // release resources on detach
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDetachedFromWindow();
    }

    /*
     * TextureView.SurfaceTextureListener
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(surface);
        startMedia();
    }

    private void startMedia() {
        if (mSource == null) return;
        try {
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setDataSource(getContext(), mSource);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void setVisible(boolean isVisible) {
        isPaused = !isVisible;
        if (isPaused) {
            pause();
        } else {
            start();
        }
    }

    public void toggle() {
        setVisible(!isPaused);
    }

    public void update() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            startMedia();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
//            mMediaPlayer.seekTo(0);
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mSource != null;
    }
}
