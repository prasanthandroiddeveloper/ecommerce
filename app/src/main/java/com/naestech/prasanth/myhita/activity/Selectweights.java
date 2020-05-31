package com.naestech.prasanth.myhita.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naestech.prasanth.myhita.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selectweights extends AppCompatActivity {
    Bundle bb;
    String id,Hid;
    Spinner spin;
    List<String> weightsArray, weightsid;
    ArrayAdapter<String> itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectweights);




        bb=getIntent().getExtras();
        if(bb != null)
        {
            id=bb.getString("id");
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show();

        }
    }

    private void getdata() {

        weightsArray = new ArrayList<>();
        weightsid = new ArrayList<>();
        weightsArray.clear();

        StringRequest stringRequest= new StringRequest(Request.Method.POST,"", response -> {



            try{
                JSONArray jarr = new JSONArray(response);

                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject json = jarr.getJSONObject(i);
                    weightsArray.add(json.getString("weights")) ;
                    weightsid.add(json.getString("weightsid"));

                }

                itemAdapter = new ArrayAdapter<>(Selectweights.this, android.R.layout.simple_spinner_item, weightsArray);
                spin.setAdapter(itemAdapter);

                spin.setOnItemClickListener((adapterView, view, position, l) -> {
                   
                    Hid=weightsArray.get(position);
                    Log.i("Hid",Hid);


                });

            }catch (JSONException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Selectweights.this);
        requestQueue.add(stringRequest);

    }
}
