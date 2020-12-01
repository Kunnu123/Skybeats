package com.skybeats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.BroadCastModel;
import com.skybeats.stats.LocalStatsData;
import com.skybeats.stats.RemoteStatsData;
import com.skybeats.stats.StatsData;
import com.skybeats.ui.VideoGridContainer;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.ImageUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class LiveActivity extends RtcBaseActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;
    private LinearLayout llMain;
    private LinearLayout bottom_container;

    private Myreceiver reMyreceive;
    private IntentFilter filter;
    FloatingActionButton menu;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private AppCompatTextView txtUserCount;
    private String broadCastId = "0";
    private TextView txtDiamonds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        initUI();
        initData();

        reMyreceive = new Myreceiver();

        //typo mistake
        //Edited after pointing out by a reader, thanks
        // IntentFilter filter=new IntentFilter("sohail.aziz");

        filter = new IntentFilter("live.broadcast");

    }

    public class Myreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            flyEmoji(intent.getStringExtra("broadcast_url"), intent.getStringExtra("broadcast_type"), intent.getStringExtra("broadcast_message"), intent.getStringExtra("broadcast_user"), intent.getStringExtra("broadcast_user_image"));
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(reMyreceive, filter);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.setRequestProperty("User-agent", "Mozilla/4.0");

                connection.connect();
                InputStream input = connection.getInputStream();

                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Bitmap feed) {
            // TODO: check this.exception
            // TODO: do something with the feed

            ZeroGravityAnimation animation = new ZeroGravityAnimation();
            animation.setCount(1);
            animation.setScalingFactor(0.2f);
            animation.setOriginationDirection(Direction.BOTTOM);
            animation.setDestinationDirection(Direction.TOP);
            animation.setImage(feed);
            animation.setDuration(10000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                                               @Override
                                               public void onAnimationStart(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationRepeat(Animation animation) {

                                               }
                                           }
            );

            ViewGroup container = findViewById(R.id.main_container);

            animation.play(LiveActivity.this, container);
        }
    }

    public void flyEmoji(String url, String type, String message, String userName, String userImage) {
        txtUserCount.setText(String.valueOf(AppClass.liveUserCount));
        txtDiamonds.setText(String.valueOf(AppClass.gift_point));
        if (type.equalsIgnoreCase("new_gift")) {
            new RetrieveFeedTask().execute(url);
        } else if (type.equalsIgnoreCase("join_request")) {
            LinearLayout llMain = findViewById(R.id.llMain);
            LinearLayout llJoinedUser = findViewById(R.id.llJoinedUser);
            CircleImageView imageView = new CircleImageView(LiveActivity.this);
            LinearLayout.LayoutParams paramsiv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsiv.height = 80;
            paramsiv.width = 80;
            paramsiv.setMargins(5, 5, 5, 5);
            imageView.setLayoutParams(paramsiv);

            ImageUtils.loadImage(LiveActivity.this, userImage, R.drawable.fake_user_icon, imageView);
            llJoinedUser.addView(imageView);


            AppCompatTextView textView = new AppCompatTextView(LiveActivity.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(userName + "<font color='#EDD139'> joined the LIVE", Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(userName + "<font color='#EDD139'> joined the LIVE"));
            }
            textView.setTextColor(getResources().getColor(R.color.white));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(params);
            textView.setPadding(10, 10, 10, 10);
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_full_transparent));
            textView.setVisibility(textView.VISIBLE);
            textView.setAlpha(1.0f);
// Start the animation
            llMain.addView(textView);
//            textView.animate()
//                    .translationY(textView.getHeight())
//                    .setDuration(5000)
//                    .alpha(0.0f)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            textView.setVisibility(View.GONE);
//                        }
//                    });
        } else {
            LinearLayout llMain = findViewById(R.id.llMain);
            AppCompatTextView textView = new AppCompatTextView(LiveActivity.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml("<font color='#EDD139'> " + userName + " Says : </font>" + message, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml("<font color='#EDD139'> " + userName + " Says : </font>" + message));
            }
            textView.setPadding(10, 10, 10, 10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(params);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_full_transparent));
            textView.setVisibility(textView.VISIBLE);
            textView.setAlpha(1.0f);
// Start the animation
            llMain.addView(textView);
//            textView.animate()
//                    .translationY(textView.getHeight())
//                    .setDuration(5000)
//                    .alpha(0.0f)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            textView.setVisibility(View.GONE);
//                        }
//                    });
        }


    }

    private void initUI() {
        TextView roomName = findViewById(R.id.live_room_name);
        txtDiamonds = findViewById(R.id.txtDiamonds);
        TextView btnGoLive = findViewById(R.id.btnGoLive);
        txtUserCount = findViewById(R.id.txtUserCount);
        txtDiamonds.setText("0");
        TextView live_room_broadcaster_uid = findViewById(R.id.live_room_broadcaster_uid);
        llMain = findViewById(R.id.llMain);
        bottom_container = findViewById(R.id.bottom_container);
        menu = findViewById(R.id.menu);
//        roomName.setText(config().getChannelName());
        live_room_broadcaster_uid.setText("ID : " + config().getChannelName());
        roomName.setText(getMyPref().getUserData().getUser_name());
        roomName.setSelected(true);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottom_container.getVisibility() == View.VISIBLE) {
                    bottom_container.setVisibility(View.GONE);
                } else {
                    bottom_container.setVisibility(View.VISIBLE);
                }
            }
        });

        initUserIcon();

        int role = getIntent().getIntExtra(
                com.skybeats.Constants.KEY_CLIENT_ROLE,
                Constants.CLIENT_ROLE_AUDIENCE);
        boolean isBroadcaster = (role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                com.skybeats.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(role);
        if (isBroadcaster) startBroadcast();

        btnGoLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(LiveActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custo_gi);


                if (AppClass.networkConnectivity.isNetworkAvailable()) {
                    AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.ADD_USER_DETAILS)
                            .addBodyParameter(WebAPI.USER_ID, getMyPref().getUserData().getUser_id())
                            .addBodyParameter(WebAPI.SKYBEAT_ID, getMyPref().getUserData().getUser_id())
                            .addBodyParameter(WebAPI.TOKEN, AppClass.agoraToken)
                            .addBodyParameter(WebAPI.CHANNEL_NAME, getMyPref().getUserData().getUser_id())
                            .setTag(this)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsObject(BroadCastModel.class, new ParsedRequestListener<BroadCastModel>() {
                                @Override
                                public void onResponse(BroadCastModel response) {
                                    if (response.isStatus().equalsIgnoreCase("200")) {
                                        AppClass.broadcaseOrAudiance = "Broadcast";
                                        broadCastId = response.getData().getBrodcast_id();
                                        btnGoLive.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    anError.getResponse();
                                }
                            });

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 4000);
                dialog.show();
            }
        });
    }

    private void initUserIcon() {
//        Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.fake_user_icon);
//        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
//        drawable.setCircular(true);
        CircleImageView iconView = findViewById(R.id.live_name_board_icon);
        ImageUtils.loadImage(LiveActivity.this, getMyPref().getUserData().getProfile_image(), R.drawable.fake_user_icon, iconView);
//        iconView.setImageDrawable(drawable);
    }

    private void initData() {
        mVideoDimension = com.skybeats.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
        mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        mMuteAudioBtn.setActivated(false);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        AppClass.broadcaseOrAudiance = "";
        AppClass.liveUserCount = 0;
        AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.WS_STOP_BROADCAST)
                .addBodyParameter(WebAPI.USER_ID, getMyPref().getUserData().getUser_id())
                .addBodyParameter(WebAPI.BRODCAST_ID, broadCastId)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
                    @Override
                    public void onResponse(BaseModel response) {
                        Log.e("@@@@", response.isStatus());
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
        statsManager().clearAllData();
    }

    public void onLeaveClicked(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LiveActivity.this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.leave_message));
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LiveActivity.this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.leave_message));
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                com.skybeats.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void onMoreClicked(View view) {
        // Do nothing at the moment
    }

    public void onPushStreamClicked(View view) {
        // Do nothing at the moment
    }

    public void onMuteAudioClicked(View view) {
        if (!mMuteVideoBtn.isActivated()) return;

        rtcEngine().muteLocalAudioStream(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    public void onMuteVideoClicked(View view) {
        if (view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }
        view.setActivated(!view.isActivated());
    }
}
