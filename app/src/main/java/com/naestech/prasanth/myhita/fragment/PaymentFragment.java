package com.naestech.prasanth.myhita.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.activity.BaseActivity;

public class PaymentFragment extends Fragment {

    RadioGroup paymentGroup;
    FrameLayout cardFrame;
    RadioButton card, cash;
    LinearLayout payll;
    TextView pay;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_payment, container, false);
        paymentGroup = view.findViewById(R.id.payment_group);
        card = view.findViewById(R.id.card_payment);
        cash = view.findViewById(R.id.cash_on_delivery);
        cardFrame = view.findViewById(R.id.card_frame);
        payll = view.findViewById(R.id.pay_ll);
        pay = view.findViewById(R.id.total_pay);
        Double amount = ((BaseActivity) getActivity()).getTotalPrice();
        pay.append(amount + "");

        payll.setOnClickListener(v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            ft.replace(R.id.content_frame, new ConfirmFragment());
            ft.commit();
        });
        paymentGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = view.findViewById(checkedId);
            if (radioButton.getId() == R.id.cash_on_delivery) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                ft.replace(R.id.content_frame, new ConfirmFragment());
                ft.commit();
            }
            // Toast.makeText(getContext(),radioButton.getText()+"",Toast.LENGTH_LONG).show();
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Payment");
    }
}
