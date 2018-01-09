package com.example.number7.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.number7.smarthome.Library.DatabaseHandler;
import com.example.number7.smarthome.Library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    Button btnLogin;
    Button Btnregister;
    Button passreset;
    EditText inputUname;
    EditText inputPassword;
    private TextView loginErrorMsg;
    SharedPreferences sharedPreferences;
    /**
     * Called when the activity is first created.
     */

    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_USERNAME = "uname";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_STATUS = "permission";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          getActionBar().hide();
//        getActionBar().hide();
        //getActionBar().setIcon(R.drawable.icon_hctl);
        setContentView(R.layout.activity_main);

        inputUname = (EditText) findViewById(R.id.Username);
        inputPassword = (EditText) findViewById(R.id.pword);

        sharedPreferences = getSharedPreferences("MY_PREFERRENCE",Context.MODE_PRIVATE);
        inputUname.setText(sharedPreferences.getString("USERNAME",""));
        //inputPassword.setText(sharedPreferences.getString("PASSWORD","1234"));



        btnLogin = (Button) findViewById(R.id.login);
       // passreset = (Button)findViewById(R.id.passres);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);





/**
 * Login button click event
 * A Toast is set to alert when the Email and Password field is empty
 **/
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !inputUname.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                     NetAsync(view);
                }
                else if ( ( !inputUname.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "โปรดป้อนรหัสผ่านของคุณ", Toast.LENGTH_SHORT).show();


                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "โปรดป้อนชื่อผู้ใช้งานของคุณ", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "โปรดป้อนชื่อผู้ใช้งานของคุณ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
protected void onDestroy(){
    super.onDestroy();
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("USERNAME",inputUname.getText().toString());
    editor.putString("PASSWORD",inputPassword.getText().toString());
    editor.commit();
}

    /**
     * Async Task to check whether internet connection is working.
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setTitle("ตรวจสอบอินเตอร์เน็ต ...");
            nDialog.setMessage("กำลังโหลด ...");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        @Override
        protected Boolean doInBackground(String... args){



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
                new ProcessLogin().execute();
            }
            else{
                nDialog.dismiss();
                loginErrorMsg.setText("Error in Network Connection");
            }
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        final AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);



        private ProgressDialog pDialog;

        String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            inputUname = (EditText) findViewById(R.id.Username);
            inputPassword = (EditText) findViewById(R.id.pword);
            email = inputUname.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("ติดต่อข้อมูล ...");
            pDialog.setMessage("กำลังเข้าสู่ระบบ ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
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
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        if(json.getString(KEY_STATUS) != null){
                            String per = json.getString(KEY_STATUS);

                            if(Integer.parseInt(per)==1){
                                Intent intent = new Intent(MainActivity.this, MyTabHost.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("uname",email);
                                startActivity(intent);


//                              pDialog.dismiss();
                                /**
                                 * Close Login Screen
                                 **/
                                finish();
                            }else if (Integer.parseInt(per)==2){
                                Intent upanel = new Intent(getApplicationContext(), HomeControl.class);
                                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                pDialog.dismiss();
                                upanel.putExtra("uname",email);
                                startActivity(upanel);
                                /**
                                 * Close Login Screen
                                 **/
                                finish();
                            }


                        }

                    }else if(Integer.parseInt(error)==0){
                        pDialog.dismiss();
                        ad.setTitle("เข้าสู่ระบบ");
                        //ad.setIcon(R.drawable.ic_warning_amber_36dp);
                        ad.setPositiveButton("ตกลง", null);
                        ad.setMessage("คุณเข้าสู่ระบบซ้ำ");
                        ad.show();
                        inputUname.requestFocus();
                    }

                    else{
                        pDialog.dismiss();
                        ad.setTitle("เข้าสู่ระบบ ");
                        //ad.setIcon(R.drawable.ic_warning_amber_36dp);
                        ad.setPositiveButton("ตกลง", null);
                        ad.setMessage("ชื่อผู้ใช้งาน และรหัสผ่านของคุณไม่ถูกต้อง โปรดลองใหม่อีกครั้ง");
                        ad.show();
                       inputUname.requestFocus();


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}