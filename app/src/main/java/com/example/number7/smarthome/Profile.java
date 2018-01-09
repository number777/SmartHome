package com.example.number7.smarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Profile extends Activity {

   // Fragment myF;
    Button Changepass,btnCancel;

    public static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
    String uid;
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    TextView inputID;
    Button update,cancel;
    TextView registerErrorMsg;




    EditText newpass,comfirmPassword;
    TextView alert;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        inputFirstName = (EditText) findViewById(R.id.fname);
        inputLastName = (EditText) findViewById(R.id.lname);
        inputUsername = (EditText) findViewById(R.id.uname);
        inputEmail = (EditText) findViewById(R.id.email);
        alert = (TextView) findViewById(R.id.alertpass);
        update = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivityIntent() == null) {

                    onBackPressed();

                } else {
                    NavUtils.navigateUpFromSameTask(Profile.this);
                }
            }
        });
////////////////////////ดึงไอดี///////////////////////////////////////
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap<String, String> user =new HashMap<String, String>();
        user = db.getUserDetails();
/////////////////////////////////////////////////////////////////////

        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setTitle("Error! ");
        ad.setIcon(R.drawable.error);
        ad.setPositiveButton("Close", null);



        uid = user.get(KEY_UID).toString();
        /**
         * Displays the registration details in Text view
         **/
  

       // String id,fname,lname,uname,email;




        final EditText uname = (EditText)findViewById(R.id.uname);
        //final TextView id = (TextView)findViewById(R.id.txt_id);




        //String resultServer  = getHttpPost(url,params);

       // pword.setText(user.get(""));

//        id.setText(user.get("uid"));
        uname.setText(user.get("uname"));
        newpass = (EditText) findViewById(R.id.newpass);
        comfirmPassword = (EditText)findViewById(R.id.confpword);

        Changepass = (Button)findViewById(R.id.btnChangPass);

        Changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newpass.getText().length() == 0)
                {

                    Toast.makeText(getApplicationContext(), ("กรุณากรอกรหัสผ่าน! "), Toast.LENGTH_LONG).show();
                    newpass.requestFocus();

                }
                else if(newpass.getText().length() == 0 || comfirmPassword.getText().length() == 0 ){

                    /*ad.setMessage("กรุณายืนยันรหัสผ่าน!");
                    ad.show();*/

                    Toast.makeText(getApplicationContext(),("กรุณายืนยันรหัสผ่าน!"), Toast.LENGTH_LONG).show();
                    comfirmPassword.requestFocus();
                }
                else if(!newpass.getText().toString().equals(comfirmPassword.getText().toString())){
                    /*ad.setMessage("รหัสผ่านไม่ตรงกัน! ");
                    ad.show();*/
                    Toast.makeText(getApplicationContext(),("รหัสผ่านไม่ตรงกัน! "), Toast.LENGTH_LONG).show();
                    comfirmPassword.requestFocus();
                }else {
                     new ChangPassword().execute();
                }

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetAsync(view);

            }
        });
    }







    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Profile.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
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
                registerErrorMsg.setText("Error in Network Connection");
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
            inputFirstName = (EditText) findViewById(R.id.fname);
            inputLastName = (EditText) findViewById(R.id.lname);
            inputUsername = (EditText) findViewById(R.id.uname);
            //inputEmail = (EditText) findViewById(R.id.email);
            //inputID = (TextView) findViewById(R.id.txt_id);

//            id = inputID.getText().toString();
            uname = inputUsername.getText().toString();
           // password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Updateing ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.UpdateProfile(uid, uname);

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
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");

                        Toast.makeText(getApplicationContext(), ("บันทึกเรียบร้อย"), Toast.LENGTH_LONG).show();


                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        /**
                         * Removes all the previous data in the SQlite database
                         **/

                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                        /**
                         * Stores registered data in SQlite Database
                         * Launch Registered screen
                         **/

                        if (getParentActivityIntent() == null) {

                            onBackPressed();
                        } else {
                            NavUtils.navigateUpFromSameTask(Profile.this);
                        }

                        finish();


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

                    registerErrorMsg.setText("Error occured in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }

    private class ChangPassword extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Profile.this);
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
            final AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());

            if(th == true){
                nDialog.dismiss();
                new ProcessChangPass().execute();
            }
            else{

                ad.setTitle("เตือน! ");
                ad.setIcon(R.drawable.error);
                ad.setPositiveButton("Close", null);

                nDialog.dismiss();
                ad.setMessage("Error in Network Connection ");
                ad.show();
                newpass.requestFocus();
                //alert.setText("Error in Network Connection");
            }
        }
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




            newpas = newpass.getText().toString();
            email = user.get("email");

            pDialog = new ProgressDialog(Profile.this);
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

                        Intent Changepassword = new Intent(getApplicationContext(), Profile.class);

                        startActivity(Changepassword);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
