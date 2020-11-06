package com.skybeats.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.adapters.ViewPagerAdapter;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.skybeats.R;
import com.skybeats.databinding.ActivityMainBinding;
import com.skybeats.databinding.FragmentHomeBinding;
import com.skybeats.retrofit.ApiCallInterface;
import com.skybeats.retrofit.DisposableCallback;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.SponserModel;
import com.skybeats.utils.AppClass;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ArrayList<SlideModel> imageList = new ArrayList<>();
    ImageSlider imageSlider;
    ProgressDialog progressDialog;
    List<SponserModel> sponserModelList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlider = root.findViewById(R.id.image_slider);
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

}