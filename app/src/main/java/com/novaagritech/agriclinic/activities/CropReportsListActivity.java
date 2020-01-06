package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.CropReportAdapter;
import com.novaagritech.agriclinic.app.AppController;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivityCropReportsListBinding;
import com.novaagritech.agriclinic.modals.ReportDetails;
import com.novaagritech.agriclinic.utilities.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CropReportsListActivity extends AppCompatActivity {

    ActivityCropReportsListBinding binding;

    CropReportAdapter visitorAdapter;
    List<ReportDetails> list;

    String TAG="VISTOR_LIST";
    ProgressDialog pDialog;
    String user_id;

    MyAppPrefsManager myAppPrefsManager;
    ReportDetails visitorDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView ( CropReportsListActivity.this, R.layout.activity_crop_reports_list );

        setSupportActionBar ( binding.toolbar );


        myAppPrefsManager=new MyAppPrefsManager(this);
        user_id=myAppPrefsManager.getUserId();

        list = new ArrayList<ReportDetails>();
        visitorDetails =new ReportDetails();


        displayLoader();
        displayList();



        binding.actionImage.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                onBackPressed ( );
            }
        } );


    }

    private void displayLoader(){
        pDialog = new ProgressDialog(CropReportsListActivity.this);
        pDialog.setMessage("Loading.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }



    public void displayList(){
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("user_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = jsonBody.toString();
        Log.d(TAG, ""+mRequestBody);
        // Tag used to cancel the request
        String tag_json_obj = "pqlist";



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Urls.CROP_URL1, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());


                        try{
                            if (response.getString("status").equalsIgnoreCase("true"))
                            {

                                JSONArray jsonArray=response.getJSONArray("result");
                                for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                visitorDetails =new ReportDetails(jsonObject.getString("id"),
                                                                        jsonObject.getString("user_name"),
                                                                        jsonObject.getString("mobile"),
                                                                        jsonObject.getString("image"),
                                                                        jsonObject.getString("remarks"),
                                                                        jsonObject.getString("reply"),
                                                                        jsonObject.getString("created_on"),
                                                                        jsonObject.getString("modified_on"));

                                list.add(visitorDetails);
                                }
                                if (list.size()>0)
                                {
                                    binding.emptyRecordText.setVisibility(View.GONE);

                                }

                                visitorAdapter = new CropReportAdapter(CropReportsListActivity.this, list);
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recycle.getContext(), DividerItemDecoration.VERTICAL);
                                binding.recycle.addItemDecoration(dividerItemDecoration);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CropReportsListActivity.this);
                                binding.recycle.setLayoutManager(linearLayoutManager);
                                binding.recycle.setAdapter(visitorAdapter);
                                pDialog.dismiss();

                            }
                            else {
                                //Toast.makeText(VisitorListActivity.this, ""+response.getString("Mssg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(CropReportsListActivity.this, "Try Again...", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.dismiss();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");

                } catch (UnsupportedEncodingException uee) {


                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }

            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }





}
