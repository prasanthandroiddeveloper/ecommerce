package com.naestech.prasanth.myhita.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.naestech.prasanth.myhita.R;
import com.naestech.prasanth.myhita.model.UserAddress;
import com.naestech.prasanth.myhita.util.Utils;
import com.naestech.prasanth.myhita.util.localstorage.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class AddressFragment extends Fragment {

    Context context;
    TextView txt_pyment;
    Spinner citySpinner, stateSpinner;
    ArrayList<String> stringArrayState;
    ArrayList<String> stringArrayCity;
    String spinnerStateValue, _city, _name, _email, _mobile, _address, _state, _zip, userString;
    String nm,em,mob,addrs,zp;
    EditText name, email, mobile, address, state, zip;
    UserAddress userAddress;
    LocalStorage localStorage;
    Gson gson;
    SharedPreferences sharedPreferences;
    ImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_address, container, false);
        citySpinner = v.findViewById(R.id.citySpinner);
        stateSpinner = v.findViewById(R.id.stateSpinner);
        name = v.findViewById(R.id.sa_name);
        name.requestFocus();
        name.setFocusableInTouchMode(true);
        email = v.findViewById(R.id.sa_email);
        mobile = v.findViewById(R.id.sa_mobile);
        address = v.findViewById(R.id.sa_address);
        zip = v.findViewById(R.id.sa_zip);
        sharedPreferences= Objects.requireNonNull(getActivity()).getSharedPreferences("MySharedPref",
                MODE_PRIVATE);
        SharedPreferences.Editor adrs = sharedPreferences.edit();

        nm=sharedPreferences.getString("name","");
        em=sharedPreferences.getString("email","");
        mob=sharedPreferences.getString("mobile","");
        addrs=sharedPreferences.getString("address","");
        zp=sharedPreferences.getString("pin","");

        name.setText(nm);
        email.setText(em);
        mobile.setText(mob);
        address.setText(addrs);
        zip.setText(zp);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        imageView=v.findViewById(R.id.wtsappbtn);
        imageView.setOnClickListener(view1 -> {
            String phoneNumberWithCountryCode = "+919393939150";
            String message = "Hi I would like to know About";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message))));
        });

        init();
        txt_pyment = v.findViewById(R.id.txt_pyment);

        txt_pyment.setOnClickListener(v1 -> {
            _name = name.getText().toString();
            _email = email.getText().toString();
            _mobile = mobile.getText().toString();
            _address = address.getText().toString();
            _zip = zip.getText().toString();
            Pattern p = Pattern.compile(Utils.regEx);

            Matcher m = p.matcher(_email);

            if (_name.length() == 0) {
                name.setError("Enter Name");
                name.requestFocus();
            } else if (_email.length() == 0) {
                email.setError("Enter email");
                email.requestFocus();
            } else if (!m.find()) {
                email.setError("Enter Correct email");
                email.requestFocus();

            } else if (_mobile.length() == 0) {
                mobile.setError("Enter mobile Number");
                mobile.requestFocus();
            } else if (_mobile.length() < 10) {
                mobile.setError("Enter Corretct mobile Number");
                mobile.requestFocus();
            } else if (_address.length() == 0) {
                address.setError("Enter your Address");
                address.requestFocus();
            } else if (_zip.length() == 0) {
                zip.setError("Enter your Zip Code");
                zip.requestFocus();
            } else {
               /* userAddress = new UserAddress(_name, _email, _mobile, _address, _state, _city, _zip);
                String user_address = gson.toJson(userAddress);
                localStorage.setUserAddress(user_address);*/

               adrs.putString("name",_name);
               adrs.putString("email",_email);
               adrs.putString("mobile",_mobile);
               adrs.putString("address",_address);
               adrs.putString("pin",_zip);
               adrs.apply();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
               // ft.replace(R.id.content_frame, new PaymentFragment());
                ft.replace(R.id.content_frame, new ConfirmFragment());
                ft.commit();
            }


        });
        return v;
    }

    private void init() {
        stringArrayState = new ArrayList<>();
        stringArrayCity = new ArrayList<>();

        //set city adapter
        final ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getActivity(), R.layout.spinnertextview, stringArrayCity);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapterCity);
        if (userAddress != null) {
            int selectionPosition1 = adapterCity.getPosition(userAddress.getCity());
            citySpinner.setSelection(selectionPosition1);
        }

        //Get state json value from assets folder
        try {
            JSONObject obj = new JSONObject(loadJSONFromAssetState());
            JSONArray m_jArry = obj.getJSONArray("statelist");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                String state = jo_inside.getString("State");
                String id = jo_inside.getString("id");

                stringArrayState.add(state);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnertextview, stringArrayState);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
        if (userAddress != null) {
            int selectionPosition = adapter.getPosition(userAddress.getState());
            stateSpinner.setSelection(selectionPosition);
        }


        //state spinner item selected listner with the help of this we get selected value

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                String Text = stateSpinner.getSelectedItem().toString();


                spinnerStateValue = String.valueOf(stateSpinner.getSelectedItem());
                _state = spinnerStateValue;
                stringArrayCity.clear();

                try {
                    JSONObject obj = new JSONObject(loadJSONFromAssetCity());
                    JSONArray m_jArry = obj.getJSONArray("citylist");

                    for (int i = 0; i < m_jArry.length(); i++) {
                        JSONObject jo_inside = m_jArry.getJSONObject(i);
                        String state = jo_inside.getString("State");
                        String cityid = jo_inside.getString("id");

                        if (spinnerStateValue.equalsIgnoreCase(state)) {
                            _city = jo_inside.getString("city");
                            stringArrayCity.add(_city);
                        }

                    }

                    //notify adapter city for getting selected value according to state
                    adapterCity.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerCityValue = String.valueOf(citySpinner.getSelectedItem());
                Log.e("SpinnerCityValue", spinnerCityValue);

                _city = spinnerCityValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public String loadJSONFromAssetState() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("state.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadJSONFromAssetCity() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("cityState.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void onBackPressed() {
        assert getFragmentManager() != null;
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Address");
    }
}
