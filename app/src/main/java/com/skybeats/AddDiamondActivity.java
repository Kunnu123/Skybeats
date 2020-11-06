package com.skybeats;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.skybeats.databinding.ActivityAddDiamondBinding;
import com.skybeats.databinding.ActivityDiamondBinding;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.GetPointsModel;
import com.skybeats.retrofit.model.SponserModel;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddDiamondActivity extends BaseActivity  implements PaymentResultListener {
    ActivityAddDiamondBinding binding;
    ProgressDialog progressDialog;
    public int selectedPosition = -1;
    public String selectedPoints = "";
    public String selectedPrice = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiamondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (AppClass.networkConnectivity.isNetworkAvailable()) {
            showProgress();
            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.GET_POINTS)
                    .setTag(this)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObject(GetPointsModel.class, new ParsedRequestListener<GetPointsModel>() {
                        @Override
                        public void onResponse(GetPointsModel response) {
                            hideProgress();
                            if (response.isStatus().equalsIgnoreCase("200")){
                                if (response.getData().size() > 0){
                                    PointsAdapter pointsAdapter = new PointsAdapter(response.getData());
                                    GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                                    binding.rvPoints.setLayoutManager(mLayoutManager);
                                    binding.rvPoints.setItemAnimator(new DefaultItemAnimator());
                                    binding.rvPoints.setAdapter(pointsAdapter);
                                }
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hideProgress();
                        }
                    });

        } else {
            Toast.makeText(AddDiamondActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition == -1){
                    Toast.makeText(AddDiamondActivity.this, "Please Select Any One !!", Toast.LENGTH_LONG).show();
                }else {
                    startPayment(selectedPrice, "INR");
                }
            }
        });

    }

    public void showProgress() {
        progressDialog = new ProgressDialog(AddDiamondActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void startPayment(String amount, String currency){
        Checkout checkout = new Checkout();
        String amountNew = amount;
        amountNew += ("00");

        try {
            JSONObject options = new JSONObject();
            options.put("name", getResources().getString(R.string.app_name));
            options.put("currency", currency);
            options.put("amount", amountNew);
//            options.put("image", CommonStrings.PAYMENT_LOGO_URL);
//            options.put("prefill", JSONObject("{email:" + "'" + AppPref.getUserEmail() + "\'}"));
            checkout.open(AddDiamondActivity.this, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPaymentSuccess(String s) {
        if (AppClass.networkConnectivity.isNetworkAvailable()) {
            showProgress();
            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.PURCHASE_POINTS)
                    .addBodyParameter(WebAPI.USER_ID, getMyPref().getUserData().getUser_id())
                    .addBodyParameter(WebAPI.POINTS, selectedPoints)
                    .setTag(this)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
                        @Override
                        public void onResponse(BaseModel response) {
                            hideProgress();
                            Toast.makeText(AddDiamondActivity.this, response.getResponse_msg(), Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onError(ANError anError) {
                            hideProgress();
                        }
                    });

        } else {
            Toast.makeText(AddDiamondActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.MyViewHolder> {

        private List<GetPointsModel> pointsModelList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public AppCompatTextView txtPoints, txtPrice;
            public AppCompatImageView ivPoint, ivCheck;

            public MyViewHolder(View view) {
                super(view);
                txtPoints = (AppCompatTextView) view.findViewById(R.id.txtPoints);
                txtPrice = (AppCompatTextView) view.findViewById(R.id.txtPrice);
                ivPoint = (AppCompatImageView) view.findViewById(R.id.ivPoint);
                ivCheck = (AppCompatImageView) view.findViewById(R.id.ivCheck);
            }
        }


        public PointsAdapter(List<GetPointsModel> pointsModelList) {
            this.pointsModelList = pointsModelList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_points, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (selectedPosition == position){
                holder.ivCheck.setVisibility(View.VISIBLE);
                holder.itemView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_trans));
            }else {
                holder.ivCheck.setVisibility(View.GONE);
                holder.itemView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_white));
            }
            GetPointsModel getPointsModel = pointsModelList.get(position);
            ImageUtils.loadImage(AddDiamondActivity.this, getPointsModel.getImage_name(), R.drawable.placeholder, holder.ivPoint);
            holder.txtPoints.setText("Diamonds : " + getPointsModel.getPoints());
            holder.txtPrice.setText("Price : " + getPointsModel.getPrice());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPosition == position){
                        selectedPosition = -1;
                        selectedPoints = "0";
                        selectedPrice = "0";
                    }else {
                        selectedPosition = position;
                        selectedPoints = getPointsModel.getPoints();
                        selectedPrice = getPointsModel.getPrice();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return pointsModelList.size();
        }
    }
}