package com.naestech.prasanth.myhita.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.activity.BaseActivity;
import com.naestech.prasanth.myhita.activity.CartActivity;
import com.naestech.prasanth.myhita.activity.MainActivity;
import com.naestech.prasanth.myhita.adapter.CheckoutCartAdapter;
import com.naestech.prasanth.myhita.model.Cart;
import com.naestech.prasanth.myhita.model.Order;
import com.naestech.prasanth.myhita.util.localstorage.Date_Picker_Dialog;
import com.naestech.prasanth.myhita.util.localstorage.LocalStorage;
import com.naestech.prasanth.myhita.util.localstorage.REST;
import com.naestech.prasanth.myhita.util.localstorage.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class ConfirmFragment extends Fragment {
    LocalStorage localStorage;
    List<Cart> cartList = new ArrayList<>();
    Gson gson;
    RecyclerView recyclerView;
    CheckoutCartAdapter adapter;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    TextView back, placeOrder,fromTv,timeTv;
    TextView total, shipping, totalAmount;
    Double _total, _shipping, _totalAmount,amount;
    ProgressDialog progressDialog;
    List<Order> orderList = new ArrayList<>();
    String orderNo,getTime,format = "";
    String id,nm,em,mob,addrs,Hid;
    StringBuilder stringBuilder=new StringBuilder();
    SharedPreferences sh;
    Bundle bun;
    RadioButton card, cash;
    long minDate = System.currentTimeMillis() - 1000;
    String FromDate,ToDate;
    LinearLayout tymlyt;
    TimePicker tmpckr;
    Calendar calendar;
    int hour,min;
    Button settime;
    Spinner spin;
    List<String> timeslots, timeids;
    ArrayAdapter<String> itemAdapter;



    public ConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);
        localStorage = new LocalStorage(getContext());
        recyclerView = view.findViewById(R.id.cart_rv);
        totalAmount = view.findViewById(R.id.total_amount);
        total = view.findViewById(R.id.total);
        shipping = view.findViewById(R.id.shipping_amount);
        back = view.findViewById(R.id.back);
        placeOrder = view.findViewById(R.id.place_order);
        progressDialog = new ProgressDialog(getContext());
        fromTv = view.findViewById(R.id.fromTV);
        timeTv = view.findViewById(R.id.timeTV);
        tymlyt = view.findViewById(R.id.timelyt);
        tmpckr = view.findViewById(R.id.tmpckr);
        settime = view.findViewById(R.id.settime);

        FromDate = Utils.DatetoStr(System.currentTimeMillis(),0);
        ToDate = Utils.DatetoStr(System.currentTimeMillis()+86400000L,0);
        calendar=Calendar.getInstance();
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        min=calendar.get(Calendar.MINUTE);


        card = view.findViewById(R.id.card_payment);
        cash = view.findViewById(R.id.cash_on_delivery);
        gson = new Gson();
        orderList = ((BaseActivity) getActivity()).getOrderList();
        Random rnd = new Random();
        orderNo = String.valueOf(100000 + rnd.nextInt(900000));
        setUpCartRecyclerview();
        if (orderList.isEmpty()) {
            id = "1";
        } else {
            id = String.valueOf(orderList.size() + 1);
        }
        sh= getActivity().getSharedPreferences("MySharedPref",
                MODE_PRIVATE);



        _total = ((BaseActivity) getActivity()).getTotalPrice();
        _shipping = 0.0;
        _totalAmount = _total + _shipping;
        total.setText("₹"+" "+_total + "");
        shipping.setText("₹"+" "+_total + "");
        totalAmount.setText("₹"+" "+_totalAmount + "");

        back.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CartActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        });

        placeOrder.setOnClickListener(v -> {

            showDeleteDialog();

        });

        fromTv.setOnClickListener(view1 -> {
            stdate();
        });

        timeTv.setOnClickListener(view12 -> {
            sttime();
        });

        bun=new Bundle();
       // showTime(hour, min);

        settime.setOnClickListener(view13 -> {
        hour = tmpckr.getCurrentHour();
        min = tmpckr.getCurrentMinute();
        showTime(hour, min);
        });
        getdata();
        return view;
    }

    private void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        timeTv.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
         getTime=timeTv.getText().toString();
        Toast.makeText(getActivity(), getTime, Toast.LENGTH_SHORT).show();
        Log.i("getTime",getTime);
        tymlyt.setVisibility(View.GONE);
    }

    public void showDeleteDialog() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())

                //set message, title, and icon
                .setTitle("Oder Confirm")
                .setMessage("Do you want to Place Order")
                .setIcon(R.drawable.close)

                .setPositiveButton("Yes", (dialog, whichButton) -> {
                    amount = ((BaseActivity) getActivity()).getTotalPrice();
                    List<Cart> cartlist = ((BaseActivity) getActivity()).getCartList();
                    nm=sh.getString("name","");
                    em=sh.getString("email","");
                    mob=sh.getString("mobile","");
                    addrs=sh.getString("address","");
                    Log.i("nms",nm);
                    for (int i = 0; i < cartlist.size(); i++) {
                        final Cart cart = cartList.get(i);
                        stringBuilder.append(cart.getTitle()).append("=").append(cart.getQuantity()+"_"+"kg"+"/");
                    }
                    Log.i("item_name",stringBuilder.toString());

                    if(amount>=200){
                    insertdata();
                    closeProgress();
                }else{ Toast.makeText(getActivity(), "Place Mininum Order of ₹ 200", Toast.LENGTH_SHORT).show();
                    }

                }
                )

                .setNegativeButton("No", (dialog, which) -> processinsertdata())
                .create();
        myQuittingDialogBox.show();

    }

    private void closeProgress() {
        Handler handler = new Handler();
        handler.postDelayed(() -> progressDialog.dismiss(), 3000); // 5000 milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        Order order = new Order(id, orderNo, currentDateandTime, "Rs. " + _totalAmount, "CONFIRMED");
        orderList.add(order);
        String orderString = gson.toJson(orderList);
        localStorage.setOrder(orderString);
        localStorage.deleteCart();

    }




    private void showCustomDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.success_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(dialog1 -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });
        // Set dialog title

        dialog.show();
    }

    private void setUpCartRecyclerview() {

        cartList = new ArrayList<>();
        cartList = ((BaseActivity) getContext()).getCartList();


        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        adapter = new CheckoutCartAdapter(cartList, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Confirm");
    }


    public void stdate() {

        minDate = System.currentTimeMillis() - 1000;

        new Date_Picker_Dialog(getActivity(),minDate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(sdate -> {

            FromDate = sdate;
            Calendar newcal = Calendar.getInstance();
            newcal.setTime(Utils.StrtoDate(0,sdate));
            newcal.add(Calendar.DATE, 1);

            minDate = newcal.getTimeInMillis();

            ToDate = Utils.DatetoStr(newcal.getTime(),0);
            fromTv.setText(FromDate);

        });
    }

    public void sttime() { tymlyt.setVisibility(View.VISIBLE); }

    private void insertdata(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REST.ITEMSORDERED, response -> {
            Log.i("dataresponse",response);

            SharedPreferences.Editor odsh = sh.edit();
            odsh.putString("orderid",orderNo);
            odsh.putString("res",response);
            odsh.putString("amnt", String.valueOf(_totalAmount));
            odsh.apply();

            FragmentTransaction ft = (getActivity()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            ft.replace(R.id.content_frame, new FinalOrder());
            ft.commit();

        }, error -> {

            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =new HashMap<>();

                params.put("order_id",orderNo);
                params.put("created_on",currentDateandTime);
                params.put("items_list",stringBuilder.toString());
                params.put("total_amount", String.valueOf(amount));
                params.put("customer_name", nm);
                params.put("contact_num", mob);
                params.put("address", addrs);
                params.put("city", "Tirupathi");
                params.put("status", "CONFIRMED");
                params.put("payment_type", "COD");
                params.put("email", em);
                params.put("view_status", "NOT_VISITED");
                params.put("order_dl_date", FromDate);
                params.put("order_di_time", getTime);
                Log.i("pameters", String.valueOf(params));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);

    }

    private void processinsertdata(){

        amount = ((BaseActivity) getActivity()).getTotalPrice();
        List<Cart> cartlist = ((BaseActivity) getActivity()).getCartList();
        nm=sh.getString("name","");
        em=sh.getString("email","");
        mob=sh.getString("mobile","");
        addrs=sh.getString("address","");
        Log.i("nms",nm);
        for (int i = 0; i < cartlist.size(); i++) {
            final Cart cart = cartList.get(i);
            stringBuilder.append(cart.getTitle()).append("=").append(cart.getQuantity()+"_"+"kg"+"/");
        }
        Log.i("item_name",stringBuilder.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REST.ITEMSORDERED, response -> {
            Log.i("dataresponse",response);
            //closeProgress();
            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.dismiss(), 3000); // 5000 milliseconds
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String currentDateandTime1 = sdf1.format(new Date());
            Order order = new Order(id, orderNo, currentDateandTime1, "Rs. " + _totalAmount, "PENDING");
            orderList.add(order);
            String orderString = gson.toJson(orderList);
            localStorage.setOrder(orderString);
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();

        }, error -> {
            Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =new HashMap<>();

                params.put("order_id",orderNo);
                params.put("created_on",currentDateandTime);
                params.put("items_list",stringBuilder.toString());
                params.put("total_amount", String.valueOf(amount));
                params.put("customer_name", nm);
                params.put("contact_num", mob);
                params.put("address", addrs);
                params.put("city", "Tirupathi");
                params.put("status", "PENDING");
                params.put("payment_type", "COD");
                params.put("email", em);
                params.put("view_status", "NOT_VISITED");
                Log.i("parers", String.valueOf(params));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);

    }

    private void getdata() {

        timeslots = new ArrayList<>();
        timeids = new ArrayList<>();
        timeslots.clear();

        StringRequest stringRequest= new StringRequest(Request.Method.POST,"", response -> {



            try{
                JSONArray jarr = new JSONArray(response);

                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject json = jarr.getJSONObject(i);
                    timeslots.add(json.getString("time")) ;
                    timeids.add(json.getString("weightsid"));

                }

                itemAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, timeslots);
                spin.setAdapter(itemAdapter);

                spin.setOnItemClickListener((adapterView, view, position, l) -> {

                    Hid= timeslots.get(position);
                    Log.i("Hid",Hid);


                });

            }catch (JSONException e){
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        },error -> {

        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params.put("uid", id);
                Log.i("hparams", String.valueOf(params));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}
