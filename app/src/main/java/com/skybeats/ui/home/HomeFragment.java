package com.skybeats.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.skybeats.Constants;
import com.skybeats.JoinLiveActivity;
import com.skybeats.MainActivity;
import com.skybeats.R;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.AllUserModel;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.SponserModel;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.ImageUtils;
import com.skybeats.utils.MyPref;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private static final int PERMISSION_REQ_CODE_AUDIANCE = 123;
    ArrayList<SlideModel> imageList = new ArrayList<>();
    ImageSlider imageSlider;
    RecyclerView rvUserList;
    ProgressDialog progressDialog;
    AppCompatImageView iv;
    List<SponserModel> sponserModelList = new ArrayList<>();
    MainActivity activity;
    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String recever_id = "";
    private MyPref myPref;

    public HomeFragment(MainActivity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlider = root.findViewById(R.id.image_slider);
        rvUserList = root.findViewById(R.id.rvUserList);
        iv = root.findViewById(R.id.iv);
        return root;
    }

    public void showProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        new RetrieveFeedTask().execute("");
        if (myPref == null) {
            myPref = new MyPref(getActivity());
        }

        if (AppClass.networkConnectivity.isNetworkAvailable()) {
            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.GET_USERS)
                    .setTag(this)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsObject(AllUserModel.class, new ParsedRequestListener<AllUserModel>() {
                        @Override
                        public void onResponse(AllUserModel response) {
                            if (response.isStatus().equalsIgnoreCase("200")) {
                                if (response.getData().size() > 0) {
                                   DataAdapter dataAdapter = new DataAdapter(response.getData());
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                    rvUserList.setLayoutManager(mLayoutManager);
                                    rvUserList.setItemAnimator(new DefaultItemAnimator());
                                    rvUserList.setAdapter(dataAdapter);
                                } else {
                                    rvUserList.setVisibility(View.GONE);
                                }

                            } else {
                                rvUserList.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            rvUserList.setVisibility(View.GONE);
                        }
                    });

        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

        if (AppClass.networkConnectivity.isNetworkAvailable()) {
            showProgress();
            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.SPONSER)
                    .setTag(this)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObject(SponserModel.class, new ParsedRequestListener<SponserModel>() {
                        @Override
                        public void onResponse(SponserModel response) {
                            hideProgress();
                            if (response.isStatus().equalsIgnoreCase("200")) {
                                if (response.getData().size() > 0) {
                                    imageSlider.setVisibility(View.VISIBLE);
                                    sponserModelList.clear();
                                    imageList.clear();
                                    for (int i = 0; i < response.getData().size(); i++) {
                                        sponserModelList.add(response.getData().get(i));
                                        imageList.add(new SlideModel(response.getData().get(i).getImage_name(), ScaleTypes.FIT));
                                    }
                                    imageSlider.setImageList(imageList, ScaleTypes.FIT);
                                    imageSlider.setItemClickListener(i -> {
                                        if (sponserModelList.get(i).getUrl().equalsIgnoreCase("")){
                                            Intent viewIntent =
                                                    new Intent("android.intent.action.VIEW",
                                                            Uri.parse("https://www.google.com/"));
                                            startActivity(viewIntent);
                                        }else {
                                            Intent viewIntent =
                                                    new Intent("android.intent.action.VIEW",
                                                            Uri.parse(sponserModelList.get(i).getUrl()));
                                            startActivity(viewIntent);
                                        }
                                    });
                                } else {
                                    imageSlider.setVisibility(View.GONE);
                                }

                            } else {
                                imageSlider.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            hideProgress();
                            imageSlider.setVisibility(View.GONE);
                        }
                    });

        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }


    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<AllUserModel> allUserModelList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public AppCompatTextView txtUser, txtNo,txtCountry,btnJoin;
            public CircleImageView ivUser;

            public MyViewHolder(View view) {
                super(view);
                txtUser = (AppCompatTextView) view.findViewById(R.id.txtUser);
                txtNo = (AppCompatTextView) view.findViewById(R.id.txtNo);
                txtCountry = (AppCompatTextView) view.findViewById(R.id.txtCountry);
                btnJoin = (AppCompatTextView) view.findViewById(R.id.btnJoin);
                ivUser = (CircleImageView) view.findViewById(R.id.ivUser);
            }
        }


        public DataAdapter(List<AllUserModel> allUserModelList) {
            this.allUserModelList = allUserModelList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_all_user, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            AllUserModel allUserModel = allUserModelList.get(position);
            ImageUtils.loadImage(getActivity(), allUserModel.getProfile_image(), R.drawable.placeholder, holder.ivUser);
            holder.txtUser.setText(allUserModel.getUser_name());
            holder.txtNo.setText(allUserModel.getMobile_no());
            holder.txtCountry.setText(allUserModel.getCountry());

//            if (allUserModel.getUser_name().equalsIgnoreCase("")){
//                holder.itemView.setVisibility(View.GONE);
//            }else {
//                holder.itemView.setVisibility(View.VISIBLE);
//            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(allUserModelList.get(position).getChannel_name().equalsIgnoreCase("") && allUserModelList.get(position).getToken().equalsIgnoreCase(""))){
                        recever_id = allUserModelList.get(position).getUser_id();
                        AppClass.agoraToken = allUserModelList.get(position).getToken();
                        activity.config().setChannelName(allUserModelList.get(position).getChannel_name());
                        if (AppClass.networkConnectivity.isNetworkAvailable()) {
                            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.WS_JOIN_USER)
                                    .addBodyParameter(WebAPI.LIVE_STREA_ID, recever_id)
                                    .addBodyParameter(WebAPI.JOIN_USER_ID, myPref.getUserData().getUser_id())
                                    .setTag(this)
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
                                        @Override
                                        public void onResponse(BaseModel response) {
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                        }
                                    });

                        }
                        AppClass.broadcaseOrAudiance = "Audience";
                        checkPermission();
                    }else {
                        Toast.makeText(getActivity(), "User is not LIVE !!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return allUserModelList.size();
        }
    }

    private void checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }

        if (granted) {
            gotoLiveActivity(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        } else {
            requestPermissions();
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQ_CODE_AUDIANCE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE_AUDIANCE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                activity.config().setChannelName(activity.getMyPref().getUserData().getUser_id());
                gotoLiveActivity(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
            } else {
                toastNeedPermissions();
            }
        }
    }

    private void toastNeedPermissions() {
        Toast.makeText(getActivity(), R.string.need_necessary_permissions, Toast.LENGTH_LONG).show();
    }

    private void gotoLiveActivity(int role) {
        Intent intent = new Intent(getActivity().getIntent());
        intent.putExtra(Constants.KEY_CLIENT_ROLE, role);
        intent.putExtra(WebAPI.RECEIVER_ID, recever_id);
        intent.setClass(getActivity().getApplicationContext(), JoinLiveActivity.class);
        startActivity(intent);
    }
}