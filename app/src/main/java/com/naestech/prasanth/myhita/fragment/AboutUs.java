package com.naestech.prasanth.myhita.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naestech.prasanth.myhita.R;


public class AboutUs extends Fragment {

    View v;
    TextView aboutTv,fbTv,contacttv,wtsaptv;
    Intent in;
    String fbid,phnno;

    public AboutUs() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment_about_us, container, false);
        aboutTv=v.findViewById(R.id.blogtv);
        fbTv=v.findViewById(R.id.facebooktv);
        contacttv=v.findViewById(R.id.contacttv);
        wtsaptv=v.findViewById(R.id.wtsapptv);
        aboutTv.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://myhitha.com/blog/");
             in = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(in);
        });


        fbTv.setOnClickListener(view -> {
            try {
                fbid="101200274924005";
                in = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + fbid));
                startActivity(in);
            } catch (Exception e) {
                in =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + fbid));
                startActivity(in);
            }
        });

        wtsaptv.setOnClickListener(view -> {
            String phoneNumberWithCountryCode = "+91 9393939150";
            String message = "Hi I would like to know About";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message))));
        });



        contacttv.setOnClickListener(view -> {
            try {
                phnno="9393939150";
                Uri call = Uri.parse("tel:" + phnno);
                in = new Intent(Intent.ACTION_DIAL, call);
                startActivity(in);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Could not Place a call now", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
