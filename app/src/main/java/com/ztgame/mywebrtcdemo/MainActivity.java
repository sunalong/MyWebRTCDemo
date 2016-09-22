package com.ztgame.mywebrtcdemo;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import static android.R.attr.x;

public class MainActivity extends AppCompatActivity {
    private static final String LOCAL_MEDIA_STREAM_ID = "xiaomi_local_media_stream_id";
    private String TAG = "MainActivity";
    private static final String VIDEO_TRACK_ID = "xiaomi_video_track_id";
    private static final String AUDIO_TRACK_ID = "xiaomi_audio_track_id";
    VideoTrack localVideoTrack;
    PeerConnectionFactory peerConnectionFactory;
    AudioTrack localAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true, null);

        peerConnectionFactory = new PeerConnectionFactory();

        // Returns the number of camera devices
        int getDeviceCount = VideoCapturerAndroid.getDeviceCount();
        // Returns the front face device name
        String fontFacingDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        // Returns the back facing device name
        String backFacingDeviceName = VideoCapturerAndroid.getNameOfBackFacingDevice();
        // Creates a VideoCapturerAndroid instance for the device name
        VideoCapturerAndroid capturer = VideoCapturerAndroid.create(fontFacingDeviceName);
        Log.i(TAG, "getDeviceCount:" + getDeviceCount + "  fDN:" + fontFacingDeviceName + " bfdN:" + backFacingDeviceName);
        // First we create a VideoSource
        MediaConstraints videoConstraints = new MediaConstraints();
        VideoSource videoSource = peerConnectionFactory.createVideoSource(capturer, videoConstraints);
        // Once we have that, we can create our VideoTrack
        // Note that VIDEO_TRACK_ID can be any string that uniquely
        // identifies that video track in your application

        localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

        // First we create an AudioSource
        MediaConstraints audioConstraints = new MediaConstraints();
        AudioSource audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        // Once we have that, we can create our AudioTrack
        // Note that AUDIO_TRACK_ID can be any string that uniquely
        // identifies that audio track in your application
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        // To create our VideoRenderer, we can use the
        // included VideoRendererGui for simplicity
        // First we need to set the GLSurfaceView that it should render to
        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);
        // Then we set that view, and pass a Runnable
        // to run once the surface is ready
        VideoRendererGui.setView(videoView, null);
        VideoRenderer renderer = null;
        try {
            //width和Height由源码可知,必须为0到100,它表示占屏幕的百分比
            renderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.

        // We start out with an empty MediaStream object,
        // created with help from our PeerConnectionFactory
        // Note that LOCAL_MEDIA_STREAM_ID can be any string
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        // Now we can add our tracks.
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);
    }
}
