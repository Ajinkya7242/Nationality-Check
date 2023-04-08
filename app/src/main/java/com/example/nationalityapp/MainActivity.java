package com.example.nationalityapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
   String api;
   EditText edtSearch;
   Button sendbtn;
  TextView cname1,prob1,cname2,prob2;
  ProgressBar progressBar;
  LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch=findViewById(R.id.edtSearch);
        sendbtn=findViewById(R.id.button);
        cname1=findViewById(R.id.txtName1);
        cname2=findViewById(R.id.txtName2);
        prob1=findViewById(R.id.txtProb1);
        prob2=findViewById(R.id.txtProb2);
        progressBar=findViewById(R.id.progressBar2);
        linearLayout=findViewById(R.id.llmain);
        linearLayout.setVisibility(View.GONE);
         if(!checkInternet(MainActivity.this)){
             builderDialog(MainActivity.this).show();
         }
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=edtSearch.getText().toString().trim();
                if(!name.isEmpty()){
                    edtSearch.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    getData(name);
                }

            }

        });

    }

    private boolean checkInternet(Context context) {

        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info=   manager.getActiveNetworkInfo();
        if(info!=null&&info.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if((mobile!=null && mobile.isConnectedOrConnecting()) || (wifi!=null && wifi.isConnectedOrConnecting())){
                    return true;
            }
            else{
                return false;
            }

        }
        else{
            return  false;
        }

    }

    public AlertDialog.Builder builderDialog(Context context){
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("User you are not connected to the internet!");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
            }
        });
        return  builder;
    }

    private void getData(String name) {
        api="https://api.nationalize.io/?name="+name;
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("api","OnResponsse:"+response.toString());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray countryArray = jsonObject.getJSONArray("country");

                    if(countryArray.length()>=2){
                        for(int i=0;i<2;i++){
                            JSONObject countryObject = countryArray.getJSONObject(i);
                            String countryId = countryObject.getString("country_id");
                            double probability = countryObject.getDouble("probability");
                            progressBar.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                            if(i==0){
                                cname1.setText(countryId);
                                prob1.setText(String.valueOf(probability));
                            }
                            else{
                                cname2.setText(countryId);
                                prob2.setText(String.valueOf(probability));
                            }

                        }
                    }
                    else if(countryArray.length()==1){
                        JSONObject countryObject = countryArray.getJSONObject(0);
                        String countryId = countryObject.getString("country_id");
                        double probability = countryObject.getDouble("probability");
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        cname1.setText(countryId);
                        prob1.setText(String.valueOf(probability));
                        cname2.setText("");
                        prob2.setText("");

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Response Not Found", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e("api","OnResponsse:"+error.getLocalizedMessage());
                Toast.makeText(MainActivity.this,"Check your internet connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header=new HashMap();
                header.put("X-RapidAPI-Key","be422ee9femshb5a1601ea01e3d1p14098cjsn316c90c31e30");
                header.put("X-RapidAPI-Host","nationalize-io.p.rapidapi.com");
                return header;
            }
        };

        requestQueue.add(stringRequest);
    }
}
