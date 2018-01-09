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


public class SettingUser extends Activity {
    public static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    public static final String KEY_SERVER = "http://smart-home-control.besaba.com/ShowAll_User.php";
    EditText Username,Pass,ConfPass;
    String id_User,Emai_id;
    Button SaveUname,ChangPass,btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivityIntent() == null) {

                    onBackPressed();

                } else {
                    NavUtils.navigateUpFromSameTask(SettingUser.this);
                }
            }
        });
        Username = (EditText)findViewById(R.id.editTextUsername);
        Pass = (EditText)findViewById(R.id.editTextUserPass);
        ConfPass = (EditText)findViewById(R.id.editText2UserConfPass);
        //name4 = (EditText)findViewById(R.id.txteditname4);
        //ปุ่มตั้งค่าServer Intent to SettingServer.class


        //ttingErrorMsg = (TextView)findViewById(R.id.settingErrorMsg);

        SaveUname = (Button)findViewById(R.id.btnSaveUsername);
        ChangPass = (Button)findViewById(R.id.btnSavePassUserSetting);

        SaveUname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetCheck().execute();
            }
        });

        ChangPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Pass.getText().length() == 0)
                {

                    Toast.makeText(getApplicationContext(), ("กรุณากรอกรหัสผ่าน! "), Toast.LENGTH_LONG).show();
                    Pass.requestFocus();

                }
                else if(Pass.getText().length() == 0 || ConfPass.getText().length() == 0 ){

                    /*ad.setMessage("กรุณายืนยันรหัสผ่าน!");
                    ad.show();*/

                    Toast.makeText(getApplicationContext(),("กรุณายืนยันรหัสผ่าน!"), Toast.LENGTH_LONG).show();
                    ConfPass.requestFocus();
                }
                else if(!Pass.getText().toString().equals(ConfPass.getText().toString())){
                    /*ad.setMessage("รหัสผ่านไม่ตรงกัน! ");
                    ad.show();*/
                    Toast.makeText(getApplicationContext(),("รหัสผ่านไม่ตรงกัน! "), Toast.LENGTH_LONG).show();
                    ConfPass.requestFocus();
                }else {
                    new ProcessChangPass().execute();
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


            JSONObject json_data1 = jArray.getJSONObject(1);
            Username.setText(json_data1.getString("username"));
            //status2.setText(json_data1.getString("status"));
            id_User = json_data1.getString("uid").toString();
            Emai_id = json_data1.getString("email").toString();


        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }



    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(SettingUser.this);
           nDialog.setMessage("Loading..");
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
                final AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());

                ad.setTitle("Error! ");
                ad.setIcon(R.drawable.error);
                ad.setPositiveButton("Close", null);
                ad.setMessage("Error in Network Connection ");
                ad.show();

                nDialog.dismiss();
                //registerErrorMsg.setText("Error in Network Connection");
            }
        }
    }





    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String id,uname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();
            //inputUsername = (EditText) findViewById(R.id.uname);
            //inputPassword = (EditText) findViewById(R.id.pword);
           // inputFirstName = (EditText) findViewById(R.id.fname);
            //inputLastName = (EditText) findViewById(R.id.lname);
            //inputUsername = (EditText) findViewById(R.id.uname);
           // inputEmail = (EditText) findViewById(R.id.email);
           // inputID = (TextView) findViewById(R.id.txt_id);

            id = id_User.toString();
            uname = Username.getText().toString();
            // password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(SettingUser.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Updateing ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.UpdateProfile(id, uname);

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
                        pDialog.setMessage("บันทึกการเปลี่ยนแปลง...");

                        Toast.makeText(getApplicationContext(), ("บันทึกเรียบร้อย"), Toast.LENGTH_LONG).show();


                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        /**
                         * Removes all the previous data in the SQlite database
                         **/

                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));

                        if (getParentActivityIntent() == null) {

                            onBackPressed();
                        } else {
                            NavUtils.navigateUpFromSameTask(SettingUser.this);
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
                    final AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());

                    ad.setTitle("Error! ");
                    ad.setIcon(R.drawable.error);
                    ad.setPositiveButton("Close", null);
                    ad.setMessage("Error occured in registration ");
                    ad.show();
                   // registerErrorMsg.setText("Error occured in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }

    private class ProcessChangPass extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String newpas,email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();




            newpas = Pass.getText().toString();
            email = Emai_id.toString();

            pDialog = new ProgressDialog(SettingUser.this);
            //pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("บันทึกการเปลี่ยนแปลง ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.chgPass(newpas, email);
            Log.d("Button", "Register");
            return json;


        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    // alert.setText("");
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);


                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
                        //alert.setText("Your Password is successfully changed.");
                        Toast.makeText(getApplicationContext(), "เปลี่ยนรหัสผ่านรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                        if (getParentActivityIntent() == null) {

                            onBackPressed();
                        } else {
                            NavUtils.navigateUpFromSameTask(SettingUser.this);
                        }

                        finish();

                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Invalid old Password.", Toast.LENGTH_SHORT).show();
                        // alert.setText("Invalid old Password.");
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error occured in changing Password.", Toast.LENGTH_SHORT).show();
                        // alert.setText("Error occured in changing Password.");
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();


            }

        }}



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_user, menu);
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
        }else if (id == android.R.id.home) {
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
