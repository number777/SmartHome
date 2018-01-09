package com.example.number7.smarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.number7.smarthome.Library.DatabaseHandler;
import com.example.number7.smarthome.Library.UserFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class HomeControl extends Activity implements OnClickListener, ActionBar.TabListener {
    Timer timer;
    TimerTask timerTask;

    public static final String KEY_SERVER = "http://smart-home-control.besaba.com/TestJSON.php";
    public static final String KEY_SERVER_IP = "http://smart-home-control.besaba.com/ip_json.php";
    public static final String KEY_SERVER_LEDCTRl = "http://smart-home-control.besaba.com/";
    private static String KEY_UID = "uid";
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    Button profile,logout,btnconnect,setting;
    ToggleButton ToggleButton1,ToggleButton2,ToggleButton3,ToggleButton4;
    TextView connect,status1,status2,status3,status4,SWname1,SWname2,SWname3,SWname4,txtShowIP;
    EditText edittxt_ipserver;
    String ip_adress,port,id_user;
    //ตัวแปร method insert
    String status,users_id,id;
    int TimedLogout;
    InputStream is=null;
    String result=null;
    String line=null;
    String IMEI_Number;
    int code;
    //String led_status = "Off",Did = "1";
    ImageView ImgStatusled1,ImgStatusled2,ImgStatusled3,ImgStatusled4;
    Fragment myF;
    String userName;
    Handler handler = new Handler();

    TimerTask timetask;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contol);




        //attempts.setText(Integer.toString(counter));
        setTitle("ควบคุมเครื่องใช้ไฟฟ้า");
        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("uname");
            // and get whatever type user account id is
            Log.e("Username",userName);
        }else {

        }


        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new NetCheck().execute();
        //CheckServer();
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //Show IMEI
        IMEI_Number = tm.getDeviceId();

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user =new HashMap<String, String>();
        user = db.getUserDetails();




        final TextView fname = (TextView)findViewById(R.id.txtfname);
        //final TextView lname = (TextView)findViewById(R.id.txtlname);
        txtShowIP = (TextView)findViewById(R.id.txt_ipserver);
         /*status1 = (TextView)findViewById(R.id.txtStatus1);
         status2 = (TextView)findViewById(R.id.txtStatus2);
         status3 = (TextView)findViewById(R.id.txtStatus3);
         status4 = (TextView)findViewById(R.id.txtStatus4);*/

        ToggleButton1 = (ToggleButton)findViewById(R.id.Togglebtn1);
        ToggleButton2 = (ToggleButton)findViewById(R.id.Togglebtn2);
        ToggleButton3 = (ToggleButton)findViewById(R.id.Togglebtn3);
        ToggleButton4 = (ToggleButton)findViewById(R.id.Togglebtn4);

        //ToggelButton
        ImgStatusled1 =(ImageView)findViewById(R.id.imageView_status1);
        ImgStatusled2 =(ImageView)findViewById(R.id.imageView_status2);
        ImgStatusled3 =(ImageView)findViewById(R.id.imageView_status3);
        ImgStatusled4 =(ImageView)findViewById(R.id.imageView_status4);

        SWname1 = (TextView)findViewById(R.id.txtnameSW1);
        SWname2 = (TextView)findViewById(R.id.txtnameSW2);
        SWname3 = (TextView)findViewById(R.id.txtnameSW3);
        SWname4 = (TextView)findViewById(R.id.txtnameSW4);


        fname.setText(user.get("uname"));
        id_user = user.get(KEY_UID).toString();
       // lname.setText(user.get("lname"));


        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("คำเตือน! ");
        ad.setIcon(R.drawable.ic_warning_amber_36dp);
        ad.setPositiveButton("ปิด", null);


        //profile = (Button)findViewById(R.id.btn_profile);
        //logout = (Button)findViewById(R.id.btn_logout);
        btnconnect = (Button)findViewById(R.id.Disconnect_sev);
        //setting = (Button)findViewById(R.id.btn_setting);

        connect = (TextView)findViewById(R.id.txtconnect);


        final String ip_control_device;
        InputStream is = null;
        String result = "";

        ///////////////////////////////////////
        ////     สวนของดึงIPมาแสดง         /////
        ///////////////////////////////////////

        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
            //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
            //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", "0"));

            //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(KEY_SERVER_IP);
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

                   ip_adress = json_data.getString("ip_server").toString();
                   port = json_data.getString("port").toString();
                   TimedLogout = json_data.getInt("TimedLogout");

                TextView PortServer = (TextView)findViewById(R.id.txt_portServer);
                   PortServer.setText(port);
                    txtShowIP.setText(ip_adress);



                    // txtShowIP.setText(json_data.getString("ip_server"));

               // txtShowIP.setText(json_data.getString("ip_server"));
                // status1.setText(json_data.getString("status"));


                ToggleButton1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(ToggleButton1.isChecked() == true){


                            commandArduino("http://"+ip_adress+":"+port+"/?led1=on");
                            ImgStatusled1.setImageResource(R.drawable.turn_on_off_power_24);
                            id = "1";
                            status = "1";
                            users_id = id_user;
                            insert(id,status,users_id,IMEI_Number);

                        }

                        else{
                            commandArduino("http://"+ip_adress+":"+port+"/?led1=off");
                            ImgStatusled1.setImageResource(R.drawable.turn_off_on_power_24);
                            id = "1";
                            status = "0";
                            users_id = id_user;

                            insert(id,status,users_id,IMEI_Number);
                        }
                    }
                });
                ToggleButton2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ToggleButton2.isChecked() == true){
                            commandArduino("http://"+ip_adress+":"+port+"/?led2=on");
                            ImgStatusled2.setImageResource(R.drawable.turn_on_off_power_24);
                            id = "2";
                            status = "1";
                            users_id = id_user;


                            insert(id,status,users_id,IMEI_Number);
                        }

                        else{
                            commandArduino("http://"+ip_adress+":"+port+"/?led2=off");
                            ImgStatusled2.setImageResource(R.drawable.turn_off_on_power_24);
                            id = "2";
                            status = "0";
                            users_id = id_user;

                            insert(id,status,users_id,IMEI_Number);
                        }
                    }
                });
                ToggleButton3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ToggleButton3.isChecked() == true){
                            commandArduino("http://"+ip_adress+":"+port+"/?led3=on");
                            ImgStatusled3.setImageResource(R.drawable.turn_on_off_power_24);
                            id = "3";
                            status = "1";
                            users_id = id_user;


                            insert(id,status,users_id,IMEI_Number);
                        }

                        else{
                            commandArduino("http://"+ip_adress+":"+port+"/?led3=off");
                            ImgStatusled3.setImageResource(R.drawable.turn_off_on_power_24);
                            id = "3";
                            status = "0";
                            users_id = id_user;

                            insert(id,status,users_id,IMEI_Number);
                        }
                    }
                });
                ToggleButton4.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ToggleButton4.isChecked() == true){
                            commandArduino("http://"+ip_adress+":"+port+"/?led4=on");
                            ImgStatusled4.setImageResource(R.drawable.turn_on_off_power_24);
                            id = "4";
                            status = "1";
                            users_id = id_user;


                            insert(id,status,users_id,IMEI_Number);
                        }

                        else{
                            commandArduino("http://"+ip_adress+":"+port+"/?led4=off");
                            ImgStatusled4.setImageResource(R.drawable.turn_off_on_power_24);
                            id = "4";
                            status = "0";
                            users_id = id_user;

                            insert(id,status,users_id,IMEI_Number);
                        }
                    }
                });

            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
                Log.e("ip",ip_adress);
            }

        ///////////////////////////////////////
        ////สวนของชื่ออุปกรณ์ เช็คสภานะอุปกรณ์/////
        ///////////////////////////////////////

        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
        //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
        //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990

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
            String KEY_STATUS1 = "status";
            String Status1 = json_data.getString(KEY_STATUS1);





            if(Integer.parseInt(Status1) == 1){

                ImgStatusled1.setImageResource(R.drawable.turn_on_off_power_24);
                ToggleButton1.setChecked(true);
//
            }else {

                ImgStatusled1.setImageResource(R.drawable.turn_off_on_power_24);
                ToggleButton1.setChecked(false);
            }
           SWname1.setText(json_data.getString("device_name"));


           // status1.setText(json_data.getString("status"));


            JSONObject json_data1 = jArray.getJSONObject(1);
            String KEY_STATUS2 = "status";
            String Status2 = json_data1.getString(KEY_STATUS2);

           //เช็คค่าของเครื่องใช้ไฟฟ้า ToggleButton2

            if(Integer.parseInt(Status2) == 1 ){

                ToggleButton2.setChecked(true);
                ImgStatusled2.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

                ToggleButton2.setChecked(false);
                ImgStatusled2.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname2.setText(json_data1.getString("device_name"));

            JSONObject json_data3 = jArray.getJSONObject(2);
            String KEY_STATUS3 = "status";
            String Status3 = json_data3.getString(KEY_STATUS3);

            if(Integer.parseInt(Status3) == 1){

                ToggleButton3.setChecked(true);
                ImgStatusled3.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

                ToggleButton3.setChecked(false);
                ImgStatusled3.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname3.setText(json_data3.getString("device_name"));

            JSONObject json_data4 = jArray.getJSONObject(3);
            String KEY_STATUS4 = "status";
            String Status4 = json_data4.getString(KEY_STATUS4);

           if(Integer.parseInt(Status4) == 1){

               ToggleButton4.setChecked(true);
               ImgStatusled4.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

               ToggleButton4.setChecked(false);
               ImgStatusled4.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname4.setText(json_data4.getString("device_name"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        //ส่งผลลัพธ์ไปแสดงใน txtResult
       // return returnString;



        //ToggleButton





        //ปุ่มเชื่อมต่อบอร์ด Arduino
        btnconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


               connect.setText("Disconnect");
                connect.setTextColor(Color.BLUE);

                ip_adress = "";
               ToggleButton1.setEnabled(false);
                ToggleButton2.setEnabled(false);
                ToggleButton3.setEnabled(false);
                ToggleButton4.setEnabled(false);


            }
        });
        Button refresh = (Button)findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckServer();
               new NetCheck().execute();
                refreshNameDevice();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

       int time = TimedLogout*60000;
        Log.e("tag","Timmer : "+TimedLogout);
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, time, 10000); //

    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "SmartHomeControlออกจากระบบ *หมดเวลาแล้ว*", duration);
                        toast.show();
                       new  ProcessLogout().execute();
                        Intent newActivity = new Intent(HomeControl.this,MainActivity.class);
                        newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(newActivity);
                        finish();
                        stoptimertask();
                    }
                });
            }
        };
    }



    public class ProcessLogout extends AsyncTask<String, String, JSONObject> {


        final AlertDialog.Builder ad = new AlertDialog.Builder(HomeControl.this);



        private ProgressDialog pDialog;

        //String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog = new ProgressDialog(HomeControl.this);
            //pDialog.setTitle("ติดต่อข้อมูล ...");
            pDialog.setMessage("กำลังออกจากระบบ ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.Logout(userName);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    String error = json.getString("error");
                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("กำลังโหลดข้อมูล...");
                        pDialog.setTitle("รับข้อมูล");

                        Intent newActivity = new Intent(HomeControl.this,MainActivity.class);
                        startActivity(newActivity);

                        finish();


                    }
                    else{
                        pDialog.dismiss();
                        ad.setTitle("ออกจากระบบ ");
                        //ad.setIcon(R.drawable.ic_warning_amber_36dp);
                        ad.setPositiveButton("ตกลง", null);
                        ad.setMessage(" ออกจากระบบไม่สำเร็จ โปรดลองใหม่อีกครั้ง");
                        ad.show();



                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void refreshNameDevice() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
            String KEY_STATUS1 = "status";
            String Status1 = json_data.getString(KEY_STATUS1);





            if(Integer.parseInt(Status1) == 1){

                ImgStatusled1.setImageResource(R.drawable.turn_on_off_power_24);
                ToggleButton1.setChecked(true);
//
            }else {

                ImgStatusled1.setImageResource(R.drawable.turn_off_on_power_24);
                ToggleButton1.setChecked(false);
            }
            SWname1.setText(json_data.getString("device_name"));


            // status1.setText(json_data.getString("status"));


            JSONObject json_data1 = jArray.getJSONObject(1);
            String KEY_STATUS2 = "status";
            String Status2 = json_data1.getString(KEY_STATUS2);

            //เช็คค่าของเครื่องใช้ไฟฟ้า ToggleButton2

            if(Integer.parseInt(Status2) == 1 ){

                ToggleButton2.setChecked(true);
                ImgStatusled2.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

                ToggleButton2.setChecked(false);
                ImgStatusled2.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname2.setText(json_data1.getString("device_name"));

            JSONObject json_data3 = jArray.getJSONObject(2);
            String KEY_STATUS3 = "status";
            String Status3 = json_data3.getString(KEY_STATUS3);

            if(Integer.parseInt(Status3) == 1){

                ToggleButton3.setChecked(true);
                ImgStatusled3.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

                ToggleButton3.setChecked(false);
                ImgStatusled3.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname3.setText(json_data3.getString("device_name"));

            JSONObject json_data4 = jArray.getJSONObject(3);
            String KEY_STATUS4 = "status";
            String Status4 = json_data4.getString(KEY_STATUS4);

            if(Integer.parseInt(Status4) == 1){

                ToggleButton4.setChecked(true);
                ImgStatusled4.setImageResource(R.drawable.turn_on_off_power_24);

            }else {

                ToggleButton4.setChecked(false);
                ImgStatusled4.setImageResource(R.drawable.turn_off_on_power_24);

            }

            SWname4.setText(json_data4.getString("device_name"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private void CheckServer() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("คำเตือน! ");
        ad.setIcon(R.drawable.ic_warning_amber_36dp);
        ad.setPositiveButton("ปิด", null);


        //profile = (Button)findViewById(R.id.btn_profile);
        //logout = (Button)findViewById(R.id.btn_logout);
        btnconnect = (Button)findViewById(R.id.Disconnect_sev);
        //setting = (Button)findViewById(R.id.btn_setting);

        connect = (TextView)findViewById(R.id.txtconnect);


        final String ip_control_device;
        InputStream is = null;
        String result = "";

        ///////////////////////////////////////
        ////     สวนของดึงIPมาแสดง         /////
        ///////////////////////////////////////

        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
        //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
        //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_SERVER_IP);
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

            final String ip_adress1 = json_data.getString("ip_server").toString();
            final String Getport = json_data.getString("port").toString();
            TextView Port = (TextView) findViewById(R.id.txt_portServer);
            Port.setText(Getport);
            txtShowIP.setText(ip_adress1);

        }catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());

        }
        }
////////////////////////


    public void insert(String id,String status,String users_id ,String IMEI_Number)
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("id",id));
        nameValuePairs.add(new BasicNameValuePair("status",status));
        nameValuePairs.add(new BasicNameValuePair("users_id",users_id));
        nameValuePairs.add(new BasicNameValuePair("IMEI_number",IMEI_Number));


        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://smart-home-control.besaba.com/update_status.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }


    public void commandArduino(String url){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            int statuscode = statusLine.getStatusCode();
            if(statuscode == 200){
                Log.e("Log","ส่งข้อมูลสำเร็จ");
            }else {
                Log.e("Log : ","ส่งข้อมูลไม่สำเร็จ");
            }


        } catch (Exception e) {
            Log.e("Log : ","ส่งข้อมูลไม่สำเร็จ");
            Log.d("InputStream", e.getLocalizedMessage());
            Log.e("ip",url);
        }

    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        ft.add(android.R.id.content,this.myF);
        ft.attach(this.myF);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

   public void onClick(View thisView) {

    }
    /*
    //บันทึก Status ของ LED
    public class ProcessSaveStatus extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        String id="1",status="1",users_id="200";


        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected JSONObject doInBackground(String... params) {

            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();

            UserFunctions userFunctions = new UserFunctions();

            JSONObject json = userFunctions.ctrl_led(id, status, users_id);
            return json;
        }
        protected  void onPostExecute(JSONObject json){

            try {
                if(json.getString(KEY_SUCCESS) != null){
                     String res = json.getString(KEY_SUCCESS);
                    String error = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                        Toast.makeText(getApplicationContext(), "บันทึก", Toast.LENGTH_SHORT).show();
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");


                    }else if(Integer.parseInt(res) == 0){
                        Toast.makeText(getApplicationContext(), "ไม่สามารถบันทึกสถานะได้", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(error) ==1){
//                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.control_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if(id == R.id.action_logout){
            new ProcessLogout().execute();
            Intent newActivity = new Intent(HomeControl.this,MainActivity.class);
            newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newActivity);
        }else if (id == android.R.id.home){
            if (getParentActivityIntent() == null) {
                Log.i(line,"You have forgotten to specify the parentActivityName in the AndroidManifest!");
                onBackPressed();
            } else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void NetAsync(View view){
        new NetCheck().execute();
    }

    public class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(HomeControl.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("ตรวจสอบสถานะ");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        public Boolean doInBackground(String... args){

            final String ip_control_device;
            InputStream is = null;
            String result = "";


            ///////////////////////////////////////
            ////     สวนของดึงIPมาแสดง         /////
            ///////////////////////////////////////

            //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
            //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
            //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", "0"));

            //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(KEY_SERVER_IP);
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

                ip_adress = json_data.getString("ip_server").toString();

            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://"+ip_adress+"/");
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
        public void onPostExecute(Boolean th){

            if(th == true){

                nDialog.dismiss();

               connect.setTextColor(Color.GREEN);
               connect.setText("Online");
                SendDataOnDevice();
                ToggleButton1.setEnabled(true);
                ToggleButton2.setEnabled(true);
                ToggleButton3.setEnabled(true);
                ToggleButton4.setEnabled(true);

            }
            else{
                nDialog.dismiss();
                connect.setTextColor(Color.RED);
                connect.setText("Offline");

                ToggleButton1.setEnabled(false);
                ToggleButton2.setEnabled(false);
                ToggleButton3.setEnabled(false);
                ToggleButton4.setEnabled(false);
            }
        }


    }

    private void SendDataOnDevice() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
            String KEY_STATUS1 = "status";
            String Status1 = json_data.getString(KEY_STATUS1);





            if(Integer.parseInt(Status1) == 1){

                commandArduino("http://"+ip_adress+":"+port+"/?led1=on");
//
            }else {
                commandArduino("http://"+ip_adress+":"+port+"/?led1=off");
            }
            SWname1.setText(json_data.getString("device_name"));


            // status1.setText(json_data.getString("status"));


            JSONObject json_data1 = jArray.getJSONObject(1);
            String KEY_STATUS2 = "status";
            String Status2 = json_data1.getString(KEY_STATUS2);

            //เช็คค่าของเครื่องใช้ไฟฟ้า ToggleButton2

            if(Integer.parseInt(Status2) == 1 ){

                commandArduino("http://"+ip_adress+":"+port+"/?led2=on");

            }else {

                commandArduino("http://"+ip_adress+":"+port+"/?led2=off");

            }

            SWname2.setText(json_data1.getString("device_name"));

            JSONObject json_data3 = jArray.getJSONObject(2);
            String KEY_STATUS3 = "status";
            String Status3 = json_data3.getString(KEY_STATUS3);

            if(Integer.parseInt(Status3) == 1){

                commandArduino("http://"+ip_adress+":"+port+"/?led3=on");

            }else {
                commandArduino("http://"+ip_adress+":"+port+"/?led3=off");
            }

            SWname3.setText(json_data3.getString("device_name"));

            JSONObject json_data4 = jArray.getJSONObject(3);
            String KEY_STATUS4 = "status";
            String Status4 = json_data4.getString(KEY_STATUS4);

            if(Integer.parseInt(Status4) == 1){

                commandArduino("http://"+ip_adress+":"+port+"/?led4=on");
            }else {

                commandArduino("http://"+ip_adress+":"+port+"/?led4=off");

            }

            SWname4.setText(json_data4.getString("device_name"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog(){
        AlertDialog.Builder quitDialog
                = new AlertDialog.Builder(HomeControl.this);
        quitDialog.setTitle("คุณแน่ใจหรือไม่ที่จะออกจากระบบ?");

        quitDialog.setPositiveButton("ตกลง, " +
                "ออกจากระบบ!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                new ProcessLogout().execute();
                //finish();
            }
        }).setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        quitDialog.show();
    }
}
