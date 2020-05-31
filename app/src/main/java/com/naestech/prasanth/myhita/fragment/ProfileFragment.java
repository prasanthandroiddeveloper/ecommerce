package com.naestech.prasanth.myhita.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.naestech.prasanth.myhita.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    SharedPreferences sharedPreferences;
    TextView name,email,mobile,address;
    String nm,em,mob,addrs;
    ImageView imageView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences= Objects.requireNonNull(getActivity()).getSharedPreferences("MySharedPref", MODE_PRIVATE);


        name = view.findViewById(R.id.nameTv);
        email = view.findViewById(R.id.emailTv);
        mobile = view.findViewById(R.id.mobTv);
        address = view.findViewById(R.id.addrsTv);

        nm=sharedPreferences.getString("name","");
        em=sharedPreferences.getString("email","");
        mob=sharedPreferences.getString("mobile","");
        addrs=sharedPreferences.getString("address","");

        name.setText(nm);
        email.setText(em);
        mobile.setText(mob);
        address.setText(addrs);
        imageView=view.findViewById(R.id.wtsappbtn);
        imageView.setOnClickListener(view1 -> {
            String phoneNumberWithCountryCode = "+919393939150";
            String message = "Hi I would like to know About";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message))));
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Profile");
    }
}
