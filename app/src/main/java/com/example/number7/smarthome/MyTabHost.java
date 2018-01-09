package com.example.number7.smarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.example.number7.smarthome.Library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class MyTabHost extends Activity {
    private static String KEY_SUCCESS = "error";

    String userName;
    LocalActivityManager mLocalActivityManager;
    private Intent intent1,intent2,intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.onBackPressed();
        setContentView(R.layout.activity_mytab_host);
        ActionBar actionBar = getActionBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("uname");
            // and get whatever type user account id is
            Log.e("Username",userName);
        }else {

        }

       //actionBar.setDisplayHomeAsUpEnabled(true);
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(mLocalActivityManager);
        Resources  res = getResources();

        intent1 = new Intent(this, HomeControl.class);
        intent2 = new Intent(this, Setting.class);
        intent3 = new Intent(this, ControlList.class);

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1")
                .setIndicator("หน้าหลัก", res.getDrawable(android.R.drawable.ic_btn_speak_now))
                .setContent(intent1.putExtra("uname",userName));
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("tab2")
                .setIndicator("ตั้งค่า",getResources().getDrawable(android.R.drawable.ic_input_add))
                .setContent(intent2.putExtra("uname",userName));

        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("tab3")
                .setIndicator("ประวัติ",getResources().getDrawable(android.R.drawable.ic_menu_agenda))
                .setContent(intent3.putExtra("uname",userName));

        tabHost.addTab(tabSpec);
        tabHost.addTab(tabSpec2);
        tabHost.addTab(tabSpec3);

        //onBackPressed();

    }
    protected void onPause(){
        super.onPause();
       // onBackPressed();
        mLocalActivityManager.dispatchPause(!isFinishing());
    }
    protected void onResume(){
        super.onResume();
        //onBackPressed();
        mLocalActivityManager.dispatchResume();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit");
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setCancelable(true);
        dialog.setMessage("Do you want to exit?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_profile) {
            Intent newActivity = new Intent(MyTabHost.this,Profile.class);
            startActivity(newActivity);
            return true;
        }else if(id == R.id.action_logout){
            new ProcessLogout().execute();
            Intent newActivity = new Intent(MyTabHost.this,MainActivity.class);
            newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newActivity);
            finish();
            return true;

        }else if (id == android.R.id.home){
            if (getParentActivityIntent() == null) {

                onBackPressed();

            } else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }else if (id == R.id.action_settings){
            Intent newActivity = new Intent(MyTabHost.this,Setting.class);
            startActivity(newActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);


    }



    ////////////////
    public class ProcessLogout extends AsyncTask<String, String, JSONObject> {


        final AlertDialog.Builder ad = new AlertDialog.Builder(MyTabHost.this);



        private ProgressDialog pDialog;

        //String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog = new ProgressDialog(MyTabHost.this);
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


}
