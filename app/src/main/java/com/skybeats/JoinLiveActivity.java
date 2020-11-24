package com.skybeats;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.GetPointsModel;
import com.skybeats.retrofit.model.GiftModel;
import com.skybeats.stats.LocalStatsData;
import com.skybeats.stats.RemoteStatsData;
import com.skybeats.stats.StatsData;
import com.skybeats.ui.VideoGridContainer;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class JoinLiveActivity extends RtcBaseActivity {
    private static final String TAG = JoinLiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;

    private LinearLayout ll_Gift, ll_bottom_view, llmessage;
    private RecyclerView rvGift;
    ImageView ivSendGift, ivmessage, ivSendmessage;
    AppCompatEditText edt_message;
    private String recever_id = "";
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private ProgressDialog progressDialog;
    BottomSheetDialog bt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_live_room);
        initUI();
        initData();
    }

    private void initUI() {
        TextView roomName = findViewById(R.id.live_room_name);
        roomName.setText(config().getChannelName());
        roomName.setSelected(true);

        ll_Gift = findViewById(R.id.ll_Gift);
        ll_bottom_view = findViewById(R.id.ll_bottom_view);
        llmessage = findViewById(R.id.llmessage);
        rvGift = findViewById(R.id.rvGift);
        ivSendGift = findViewById(R.id.ivSendGift);
        ivmessage = findViewById(R.id.ivmessage);
        ivSendmessage = findViewById(R.id.ivSendmessage);
        edt_message = findViewById(R.id.edt_message);

        ivSendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt =new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);
                View bottom_sheet_lay = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_lay,null);
                progressBar = bottom_sheet_lay.findViewById(R.id.pdProgress);
                RecyclerView btrvGift = bottom_sheet_lay.findViewById(R.id.rvGift);
                bt.setContentView(bottom_sheet_lay);
                bt.show();
                if (AppClass.networkConnectivity.isNetworkAvailable()) {
                    progressBar.setVisibility(View.VISIBLE);
                    AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.GET_GIFT_LIST)
                            .setTag(this)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsObject(GiftModel.class, new ParsedRequestListener<GiftModel>() {
                                @Override
                                public void onResponse(GiftModel response) {
                                    hideProgress();
                                    if (response.isStatus().equalsIgnoreCase("200")) {
                                        if (response.getData().size() > 0) {
                                            progressBar.setVisibility(View.GONE);
//                                            ll_bottom_view.setVisibility(View.GONE);
//                                            ll_Gift.setVisibility(View.VISIBLE);
//                                            llmessage.setVisibility(View.GONE);
                                            GiftAdapter giftAdapter = new GiftAdapter(response.getData());
                                            GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 5);
                                            btrvGift.setLayoutManager(mLayoutManager);
                                            btrvGift.setItemAnimator(new DefaultItemAnimator());
                                            btrvGift.setAdapter(giftAdapter);
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                } else {
                    Toast.makeText(JoinLiveActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                }




            }
        });


        ivmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_bottom_view.setVisibility(View.GONE);
                ll_Gift.setVisibility(View.GONE);
                llmessage.setVisibility(View.VISIBLE);
            }
        });

        recever_id = getIntent().getStringExtra(WebAPI.RECEIVER_ID);

        ivSendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_message.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(JoinLiveActivity.this, "Please Enter Message", Toast.LENGTH_LONG).show();
                }else {
                    ll_Gift.setVisibility(View.GONE);
                    llmessage.setVisibility(View.GONE);
                    ll_bottom_view.setVisibility(View.VISIBLE);
//                    hideKeyboard(JoinLiveActivity.this,  edt_message);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    if (AppClass.networkConnectivity.isNetworkAvailable()) {
                        showProgress();
                        AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.SEND_MESSAGE)
                                .addBodyParameter(WebAPI.SENDER_ID, getMyPref().getUserData().getUser_id())
                                .addBodyParameter(WebAPI.RECEIVER_ID, recever_id)
                                .addBodyParameter(WebAPI.MESSAGE, edt_message.getText().toString().trim())
                                .setTag(this)
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
                                    @Override
                                    public void onResponse(BaseModel response) {
                                        hideProgress();
                                        if (response.isStatus().equalsIgnoreCase("200")) {
                                            edt_message.setText("");
                                            Toast.makeText(JoinLiveActivity.this, response.getResponse_msg(), Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(JoinLiveActivity.this, response.getResponse_msg(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        hideProgress();
                                    }
                                });

                    } else {
                        Toast.makeText(JoinLiveActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                    }
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
    }

    private void initUserIcon() {
        Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.fake_user_icon);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
        drawable.setCircular(true);
        ImageView iconView = findViewById(R.id.live_name_board_icon);
        iconView.setImageDrawable(drawable);
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
        statsManager().clearAllData();
    }

    public void onLeaveClicked(View view) {
        finish();
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

    public void showProgress() {
        progressDialog = new ProgressDialog(JoinLiveActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.MyViewHolder> {

        private List<GiftModel> giftModelList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout rlGift;
            public AppCompatImageView ivGift;
            public AppCompatTextView txtPoints;

            public MyViewHolder(View view) {
                super(view);
                rlGift = (LinearLayout) view.findViewById(R.id.rlGift);
                ivGift = (AppCompatImageView) view.findViewById(R.id.ivGift);
                txtPoints = (AppCompatTextView) view.findViewById(R.id.txtPoints);
            }
        }


        public GiftAdapter(List<GiftModel> giftModelList) {
            this.giftModelList = giftModelList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_gift, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            GiftModel giftModel = giftModelList.get(position);
            ImageUtils.loadImage(JoinLiveActivity.this, giftModel.getImage_name(), R.drawable.placeholder, holder.ivGift);

            holder.txtPoints.setText(giftModelList.get(position).getPoints());
            holder.rlGift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll_Gift.setVisibility(View.GONE);
                    ll_bottom_view.setVisibility(View.VISIBLE);
                    llmessage.setVisibility(View.GONE);

                    if (AppClass.networkConnectivity.isNetworkAvailable()) {
                        progressBar.setVisibility(View.VISIBLE);
                        AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.SEND_GIFT)
                                .addBodyParameter(WebAPI.SENDER_ID, getMyPref().getUserData().getUser_id())
                                .addBodyParameter(WebAPI.RECEIVER_ID, recever_id)
                                .addBodyParameter(WebAPI.GIFT_ID, giftModelList.get(position).getGift_id())
                                .setTag(this)
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
                                    @Override
                                    public void onResponse(BaseModel response) {
                                        progressBar.setVisibility(View.GONE);
                                        bt.dismiss();
                                        if (response.isStatus().equalsIgnoreCase("200")) {
                                            Toast.makeText(JoinLiveActivity.this, response.getResponse_msg(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(JoinLiveActivity.this, response.getResponse_msg(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                    } else {
                        Toast.makeText(JoinLiveActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return giftModelList.size();
        }
    }

    public static void hideKeyboard(Activity activity, AppCompatEditText edt_message) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_message.getWindowToken(), 0);
    }
}
