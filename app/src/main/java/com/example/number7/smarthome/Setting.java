package com.example.number7.smarthome;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

;

public class Setting extends Activity {
    LinearLayout layoutMenu;
    //TextView txtUsername,Devicename1,Devicename2,Devicename3,Devicename4,IP_Adress,Port;
    RelativeLayout layoutHeader;
    public static final int DIALOG_DOWNLOAD_IMAGE_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    ScrollView scrollView;
    public static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
    String userName;
    TextView USER,IP_Adress,Port,name1,name2,name3,name4,txtLastLogin,txtLastLogout,txtStatus,txtTimedLogout;
    public static final String KEY_USER = "http://smart-home-control.besaba.com/ShowAll_User.php";
   public static final String KEY_DEVICE = "http://smart-home-control.besaba.com/TestJSON.php";
    public static final String KEY_SERVER = "http://smart-home-control.besaba.com/ShowAll_IPServer.php";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userName = extras.getString("uname");
            // and get whatever type user account id is
            Log.e("Username", userName);
        }else {

        }
        TextView Ref = (TextView) findViewById(R.id.ref);

        String returnString = "";
        InputStream is = null;
        String result = "";
/////

        Ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref();
            }
        });
        USER = (TextView) findViewById(R.id.txtUsername);
        IP_Adress = (TextView) findViewById(R.id.txtIP);
        Port = (TextView) findViewById(R.id.txtPort);

        txtTimedLogout = (TextView) findViewById(R.id.txtTimedLogout);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtLastLogout = (TextView) findViewById(R.id.txtLogout);
        txtLastLogin = (TextView) findViewById(R.id.txtLastLogin);
        name1 = (TextView)findViewById(R.id.editTextDeviceName1);
        name2 = (TextView)findViewById(R.id.editTextDeviceName2);
        name3 = (TextView)findViewById(R.id.editTextDeviceName3);
        name4 = (TextView)findViewById(R.id.editTextDeviceName4);
       // USER.setText("ONGARD");
        layoutMenu = (LinearLayout)findViewById(R.id.layoutMenu);
        ///////////////////////////////
        //// Show detail devices //////
        ////////////////////////////////
        ArrayList<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>();
        nameValuePairs2.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_DEVICE);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
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
        /////// แสดงชื่อของUser//////////
        ///////////////////////////////

        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
        //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
        //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_USER);
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
            USER.setText(json_data1.getString("username"));
            txtLastLogin.setText(json_data1.getString("LastLogin"));
            txtLastLogout.setText(json_data1.getString("LastLogout"));
            //txtStatus.setText(json_data1.getString("LoginStatus"));
            String status = json_data1.getString("LoginStatus");
            if(Integer.parseInt(status)==1){
                txtStatus.setText("เข้าใช้งาน");
            }else {
                txtStatus.setText("ออกจากระบบ");
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        ////////////////////////////////////
        ///// Show Details Server  ////////
        ///////////////////////////////////

        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_SERVER);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
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
            txtTimedLogout.setText(json_data.getString("TimedLogout")+" นาที");



            //status2.setText(json_data1.getString("status"));


            //status4.setText(json_data4.getString("status"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

       //layoutActionBar = (RelativeLayout)findViewById(R.id.layoutActionBar);
        layoutHeader = (RelativeLayout)findViewById(R.id.layoutHeader);

        scrollView = (ScrollView)findViewById(R.id.scrollView);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 3;

            float startY = 0;
            float dist = 0;
            boolean isMenuHide = false;

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                } else if(action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;

                    if((pxToDp((int)dist) <= -DISTANCE) && !isMenuHide) {
                        isMenuHide = true;
                        hideMenuBar();
                    } else if((pxToDp((int)dist) > DISTANCE) && isMenuHide) {
                        isMenuHide = false;
                        showMenuBar();
                    }

                    if((isMenuHide && (pxToDp((int)dist) <= -DISTANCE))
                            || (!isMenuHide && (pxToDp((int)dist) > 0))) {
                        startY = event.getY();
                    }
                } else if(action == MotionEvent.ACTION_UP) {
                    startY = 0;
                }

                return false;
            }
        });
        //ปุ่มตั้งค่าServer Intent to SettingServer.class
        final Button Setting_server = (Button)findViewById(R.id.btn_Server);
        Setting_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Setting = new Intent(getApplicationContext(), SettingServer.class);
                Setting.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(Setting);
            }
        });
        //ปุ่มตั้งค่าผู้ใช้งาน Intent to SettingUser.class
        Button SettingUser =(Button)findViewById(R.id.btn_user);
        SettingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), com.example.number7.smarthome.SettingUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //ปุ่มตั้งค่าอุปกรณื Intent to SettingDevice.class
        Button Setting_Device = (Button)findViewById(R.id.btn_device);
        Setting_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingDevices.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }



    private void Ref() {






        String returnString = "";
        InputStream is = null;
        String result = "";
///////////////////////////////
        //// Show detail devices //////
        ////////////////////////////////
        ArrayList<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>();
        nameValuePairs2.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {



            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_DEVICE);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
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


           // pDialog.dismiss();
        } catch (JSONException e) {
           // pDialog.dismiss();
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        /////// แสดงชื่อของUser//////////
        ///////////////////////////////

        //ส่วนของการกำหนดตัวแปรเพื่อส่งให้กับ php
        //ส่วนนี้สามารถประยุกต์ไปใช้ในการเพิ่มข้อมูลให้กับ Server ได้
        //จากตัวอย่างส่งค่า moreYear ที่มีค่า 1990
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_USER);
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
            USER.setText(json_data1.getString("username"));
            txtLastLogin.setText(json_data1.getString("LastLogin"));
            txtLastLogout.setText(json_data1.getString("LastLogout"));


        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        ////////////////////////////////////
        ///// Show Details Server  ////////
        ///////////////////////////////////

        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("id", "0"));

        //ส่วนของการเชื่อมต่อกับ http เพื่อดึงข้อมูล
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(KEY_SERVER);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
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



            //status2.setText(json_data1.getString("status"));


            //status4.setText(json_data4.getString("status"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
      //  pDialog.dismiss();

    }


    public class ProcessLogout extends AsyncTask<String, String, JSONObject> {


        final AlertDialog.Builder ad = new AlertDialog.Builder(Setting.this);



        private ProgressDialog pDialog;

        //String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog = new ProgressDialog(Setting.this);
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

                        Intent newActivity = new Intent(Setting.this,MainActivity.class);
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
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog(){
        AlertDialog.Builder quitDialog
                = new AlertDialog.Builder(Setting.this);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Option1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Option2", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "Option3", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item4:
                Toast.makeText(this, "Option4", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item5:
                Toast.makeText(this, "Option5", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item6:
                Toast.makeText(this, "Option6", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////menu hide

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, 0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1,anim2);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, layoutMenu.getHeight());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, -layoutHeader.getHeight() * 2);

        animSet.playTogether(anim1,anim2);
        animSet.setDuration(300);
        animSet.start();
    }
}
