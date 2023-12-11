package com.example.movieapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlayYTView extends WebView {
    private final String BASE_URL = "https://www.youtube.com/embed/";
    private final String MIME_TYPE = "text/html";
    private final String ENCODING = "UTF-8";
    private boolean isAutoPlay = false;
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";

    public PlayYTView(@NonNull Context context) {
        super(context);
    }

    public PlayYTView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadVideoID(String videoID) {
        String url = BASE_URL + videoID + "?&autoplay=" + (isAutoPlay ? 1 : 0);
        String data = "<iframe width=\"100%\" height=\"100%\" src=\"" + url + "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"autoplay;\" allowfullscreen></iframe>";
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setAllowContentAccess(true);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (isAutoPlay) {
            this.getSettings().setUserAgentString(USER_AGENT);
        }
        this.getSettings().setLoadsImagesAutomatically(true);
        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(webViewClient);
        this.loadData(data, MIME_TYPE, ENCODING);
        isPlaying = true;
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    };

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public void onPrevious() {
        if (canGoBack()) {

        }
    }


    boolean isPlaying = false;

    public void onPauseVideo() {
        this.isPlaying = false;
        this.onPause();
    }

    public void onPlayVideo() {
        this.isPlaying = true;
        this.onResume();
    }

    public void changeState() {
        if (isPlaying) {
            onPauseVideo();
        } else {
            onPlayVideo();
        }
    }

    public String getState() {
        return isPlaying ? "Playing" : "Pause";
    }

    private boolean isPlaying() {
        return isPlaying;
    }


    public void onForward() {
        if (canGoForward()) {

        }
    }
}
