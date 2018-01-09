package com.example.number7.smarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.number7.smarthome.Library.DatabaseHandler;
import com.example.number7.smarthome.Library.UserFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class SettingDevices extends Activity {
    public static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    public static final String KEY_SERVER = "http://smart-home-control.besaba.com/TestJSON.php";
    EditText name1,name2,name3,name4;
    TextView SettingErrorMsg;
    Button SaveDname,btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_devices);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        name1 = (EditText)findViewById(R.id.txteditname1);
        name2 = (EditText)findViewById(R.id.txteditname2);
        name3 = (EditText)findViewById(R.id.txteditname3);
        name4 = (EditText)findViewById(R.id.txteditname4);
        //ปุ่มตั้งค่าServer Intent to SettingServer.class


        SettingErrorMsg = (TextView)findViewById(R.id.settingErrorMsg);

        SaveDname = (Button)findViewById(R.id.btnSaveDname);

        btnCancel = (Button) findViewById(R.id.btnCancel_device);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivityIntent() == null) {

                    onBackPressed();

                } else {
                    NavUtils.navigateUpFromSameTask(SettingDevices.this);
                }
            }
        });
        String returnString = "";
        InputStream is = null;
        String result = "";
        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
        //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
        //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_SERVER);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //ส่วนของการแปลงผลลัพธ์ให้อยู่ในรูปแบบของ String
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-11"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        //ส่วนของการแปลงข้อมูล JSON ออกมาในรูปแบบของข้อมูลทั่วไปเพื่อนำไปใช้
        try {
            //แสดงผลออกมาในรูปแบบของ JSON
//            SWname1.setText("JSON : /n" + result);

            JSONArray jArray = new JSONArray(result);
            //

            JSONObject json_data = jArray.getJSONObject(0);

            name1.setText(json_data.getString("device_name"));
            //status1.setText(json_data.getString("status"));


            JSONObject json_data1 = jArray.getJSONObject(1);
            name2.setText(json_data1.getString("device_name"));
            //status2.setText(json_data1.getString("status"));

            JSONObject json_data3 = jArray.getJSONObject(2);
            name3.setText(json_data3.getString("device_name"));
            //status3.setText(json_data3.getString("status"));

            JSONObject json_data4 = jArray.getJSONObject(3);
            name4.setText(json_data4.getString("device_name"));
            //status4.setText(json_data4.getString("status"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        SaveDname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetAsync(v);
            }
        });
    }


    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(SettingDevices.this);
           // nDialog.setMessage("Loading..");
           // nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                SettingErrorMsg.setText("Error in Network Connection");
            }
        }
    }





    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String Dname1,Dname2,Dname3,Dname4;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();
            //inputUsername = (EditText) findViewById(R.id.uname);
            //inputPassword = (EditText) findViewById(R.id.pword);
            name1 = (EditText) findViewById(R.id.txteditname1);
            name2 = (EditText) findViewById(R.id.txteditname2);
            name3 = (EditText) findViewById(R.id.txteditname3);
            name4 = (EditText) findViewById(R.id.txteditname4);


            Dname1 = name1.getText().toString();
            Dname2 = name2.getText().toString();
            Dname3 = name3.getText().toString();
            Dname4 = name4.getText().toString();
            // password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(SettingDevices.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("กำลังบันทึก ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.device_name(Dname1, Dname2, Dname3, Dname4);

            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/

            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    // registerErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                        //pDialog.setTitle("Getting Data");
                        pDialog.setMessage("โหลดข้อมูล...");

                        Toast.makeText(getApplicationContext(), ("บันทึกเรียบร้อย"), Toast.LENGTH_LONG).show();

                        if (getParentActivityIntent() == null) {

                            onBackPressed();
                        } else {
                            NavUtils.navigateUpFromSameTask(SettingDevices.this);
                        }

                        finish();
                    }

                    else if (Integer.parseInt(red) ==1){
                        pDialog.dismiss();
                        //ad.setMessage("User already exists");
                        Toast.makeText(getApplicationContext(), ("User already exists"), Toast.LENGTH_LONG).show();
                        //registerErrorMsg.setText("User already exists");
                    }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        // ad.setMessage("Invalid username id");
                        Toast.makeText(getApplicationContext(), ("Invalid username id"), Toast.LENGTH_LONG).show();
                        //.show();
                        //registerErrorMsg.setText("Invalid Email id");
                    }

                }


                else{
                    pDialog.dismiss();

                    SettingErrorMsg.setText("Error occured in Update device name");
                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == android.R.id.home){
            if (getParentActivityIntent() == null) {

                onBackPressed();
            } else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
