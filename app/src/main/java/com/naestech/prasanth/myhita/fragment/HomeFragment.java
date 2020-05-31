package com.naestech.prasanth.myhita.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.activity.MainActivity;
import com.naestech.prasanth.myhita.adapter.CategoryAdapter;
import com.naestech.prasanth.myhita.adapter.HomeSliderAdapter;
import com.naestech.prasanth.myhita.adapter.NewProductAdapter;
import com.naestech.prasanth.myhita.adapter.PopularProductAdapter;
import com.naestech.prasanth.myhita.helper.Data;
import com.naestech.prasanth.myhita.model.Category;
import com.naestech.prasanth.myhita.util.localstorage.REST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    Timer timer;
    int page_position = 0;
    Data data;
    private int dotscount;
    private ImageView[] dots;
    private List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView, nRecyclerView, pRecyclerView;
    private CategoryAdapter mAdapter;
    private NewProductAdapter nAdapter;
    private PopularProductAdapter pAdapter;
    ImageView imageView;

    private Integer[] images = {R.drawable.delivery, R.drawable.socaldistance, R.drawable.veg1, R.drawable.slider5};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        data = new Data();
        recyclerView = view.findViewById(R.id.category_rv);
   //    pRecyclerView = view.findViewById(R.id.popular_product_rv);
        nRecyclerView = view.findViewById(R.id.new_product_rv);

     /*  mAdapter = new CategoryAdapter(data.getCategoryList(), getContext(), "Home");
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/
        imageView=view.findViewById(R.id.wtsappbtn);
        imageView.setOnClickListener(view1 -> {
            String phoneNumberWithCountryCode = "+919393939150";
            String message = "Hi I would like to know About";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message))));
        });



        nAdapter = new NewProductAdapter(data.getNewList(), getContext(), "Home");
        RecyclerView.LayoutManager nLayoutManager = new GridLayoutManager(getContext(), 2);
        nRecyclerView.setLayoutManager(nLayoutManager);
        nRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nRecyclerView.setAdapter(nAdapter);

        setUpListFromDb();

       /* pAdapter = new PopularProductAdapter(data.getPopularList(), getContext(), "Home");
        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        pRecyclerView.setLayoutManager(pLayoutManager);
        pRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pRecyclerView.setAdapter(pAdapter);*/


        timer = new Timer();
        viewPager = view.findViewById(R.id.viewPager);

        sliderDotspanel = view.findViewById(R.id.SliderDots);

        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(getContext(), images);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduleSlider();

        return view;
    }


    public void scheduleSlider() {

       final Handler handler = new Handler();

        final Runnable update = () -> {
            if (page_position == dotscount) {
                page_position = 0;
            } else {
                page_position = page_position + 1;
            }
            viewPager.setCurrentItem(page_position, true);
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    public void onLetsClicked(View view) {
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
       // getActivity().setTitleColor(Color.parseColor("#9c9a9c"));
    }



    private void setUpListFromDb(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REST.CATEGORYITMES, response -> {
            Log.i("lrs",response);

            try {
                recyclerView.setVisibility(View.VISIBLE);
              //  recyclerView.setLayoutManager(new GridLayoutManager(getActivity()),"2");
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                recyclerView.setLayoutManager(mLayoutManager);
                JSONArray jarr = new JSONArray(response);

                List<Category> list = new ArrayList<>();

                for(int i = 0; i< jarr.length(); i++) {

                    Category adapter = new Category();
                    JSONObject json = jarr.getJSONObject(i);

                    adapter.setId(json.getString("category_id"));
                    adapter.setTitle(json.getString("category"));
                    adapter.setImage(json.getString("image_path"));
                    list.add(adapter);
                }

                //  recyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
                mAdapter = new CategoryAdapter(list,getContext(),"Home");
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                recyclerView.setVisibility(View.GONE);

                 Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(),"No Data",Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =new HashMap<>();

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);

    }
}
