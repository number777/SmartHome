package com.example.number7.smarthome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;

public class ChangePassword extends Activity {

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";



    EditText newpass,comfirmPassword;
    TextView alert;
    Button changepass;
    Button cancel;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("ผิดพลาด! ");
        ad.setIcon(R.drawable.ic_warning_amber_36dp);
        ad.setPositiveButton("ปิด", null);





        newpass = (EditText) findViewById(R.id.uname);
        comfirmPassword = (EditText)findViewById(R.id.confpword);
        alert = (TextView) findViewById(R.id.alertpass);
        changepass = (Button) findViewById(R.id.btnSaveDname);

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newpass.getText().length() == 0)
                {
                    ad.setMessage("กรุณากรอกรหัสผ่าน! ");
                    ad.show();
                    newpass.requestFocus();

                }
                else if(newpass.getText().length() == 0 || comfirmPassword.getText().length() == 0 ){
                    ad.setMessage("กรุณายืนยันรหัสผ่าน!");
                    ad.show();
                    newpass.requestFocus();
                }
                else if(!newpass.getText().toString().equals(comfirmPassword.getText().toString())){
                    ad.setMessage("รหัสผ่านไม่ตรงกัน! ");
                    ad.show();
                    newpass.requestFocus();
                }else {
                    NetAsync(view);
                }

            }
        });}

    private class ChangPassword extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ChangePassword.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                alert.setText("Error in Network Connection");
            }
        }
    }

    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String newpas,email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();




            newpas = newpass.getText().toString();
            email = user.get("email");

            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
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
                    alert.setText("");
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);


                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
                        //alert.setText("Your Password is successfully changed.");
                        Toast.makeText(getApplicationContext(), "Your Password is successfully changed.", Toast.LENGTH_SHORT).show();

                        Intent Changepassword = new Intent(getApplicationContext(), Profile.class);

                        startActivity(Changepassword);
                        finish();

                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        alert.setText("Invalid old Password.");
                    } else {
                        pDialog.dismiss();
                        alert.setText("Error occured in changing Password.");
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();


            }

        }}
    public void NetAsync(View view){
        new ChangPassword().execute();
    }}



