package com.example.number7.smarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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


public class SettingServer extends Activity {
     EditText IP_Adress,Port,timmedLogout;
    Button Save;

    public static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
    Button btnCancel;
    public static final String KEY_SERVER = "http://smart-home-control.besaba.com/ShowAll_IPServer.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_server);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String ip_server;
        btnCancel = (Button) findViewById(R.id.btn_Cancel_server);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivityIntent() == null) {

                    onBackPressed();

                } else {
                    NavUtils.navigateUpFromSameTask(SettingServer.this);
                }
            }
        });
        IP_Adress = (EditText)findViewById(R.id.editText_IPServer);
        Port = (EditText)findViewById(R.id.editText_Port);
        timmedLogout = (EditText) findViewById(R.id.editText_TimedLogout);
        Save = (Button)findViewById(R.id.btn_settingPage_save);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result;
                result = Integer.parseInt(timmedLogout.getText().toString());
           if(result<=20){
               new IPServer().execute();
           }else {
               Toast.makeText(getApplicationContext(), "กรุณากรอกเวลาน้อกว่า 20 นาที", Toast.LENGTH_SHORT).show();
               //timmedLogout.setText("");
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

            IP_Adress.setText(json_data.getString("ip_server"));
            //status1.setText(json_data.getString("status"));
            Port.setText(json_data.getString("port"));
            timmedLogout.setText(json_data.getString("TimedLogout"));
            ip_server = json_data.getString("id");


            //status2.setText(json_data1.getString("status"));


            //status4.setText(json_data4.getString("status"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private class IPServer extends AsyncTask<String,String,Boolean>
    {
        final AlertDialog.Builder ad = new AlertDialog.Builder(SettingServer.this);
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(SettingServer.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
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
                ad.setTitle("แจ้ง! ");
                ad.setIcon(R.drawable.ic_warning_amber_36dp);
                ad.setPositiveButton("ตกลง", null);
                ad.setMessage("Error in Network Connection");
                ad.show();
                //SettingErrorMsg.setText("Error in Network Connection");
            }
        }
    }





    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String id,ip,port,TimmedLogout;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();
            //inputUsername = (EditText) findViewById(R.id.uname);
            //inputPassword = (EditText) findViewById(R.id.pword);


            id = "1".toString();
            ip = IP_Adress.getText().toString();
            port = Port.getText().toString();
            TimmedLogout = timmedLogout.getText().toString();


            // password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(SettingServer.this);
           // pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("กำลังบันทึก ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.IP_Server(id,ip,port,TimmedLogout);

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
                       // pDialog.setTitle("Getting Data");
                        pDialog.setMessage("บันทึกการเปลี่ยนแปลง...");

                        Toast.makeText(getApplicationContext(), ("บันทึกเรียบร้อย"), Toast.LENGTH_LONG).show();

                        if (getParentActivityIntent() == null) {

                            onBackPressed();
                        } else {
                            NavUtils.navigateUpFromSameTask(SettingServer.this);
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
                    Toast.makeText(getApplicationContext(),"Error occured in Update device name",Toast.LENGTH_SHORT).show();
                   //SettingErrorMsg.setText("Error occured in Update device name");
                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }}
    public void NetAsync(View view) {
        new IPServer().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_server, menu);
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
