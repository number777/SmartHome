package com.example.number7.smarthome;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.number7.smarthome.Library.UserFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ControlList extends Activity  implements  AdapterView.OnItemSelectedListener{
    //String KEY_TagShowData,KEY_DelectData;
    LinearLayout layoutSpinner;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ScrollView scrollView;
    TextView txtRefraesh;
    ArrayList<HashMap<String, String>> MyArrList;
    String[] Cmd = {"View","Update","Delete"};
    String[] CmdTH = {"รายละเอียด","แก้ไข","ลบ"};
    String[] arr = { "วันนี้", "สัปดาห์นี้", "เดือนนี้", "เดือนที่แล้ว"};
    Spinner spin ;
    String userName;
    public static String KEY_SUCCESS = "success";
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_list);
         mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
//Spinner
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("uname");
            // and get whatever type user account id is
            Log.e("Username", userName);
        }else {

        }

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ShowDataAll("all");
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(spin.getSelectedItem()=="วันนี้"){
                    ShowDataAll("date");
                    Log.e("tag","date");
                }else if(spin.getSelectedItem()=="สัปดาห์นี้"){
                    ShowDataAll("week");
                    Log.e("tag","week");
                }else if(spin.getSelectedItem()=="เดือนนี้"){
                    ShowDataAll("month");
                    Log.e("tag","month");
                }else if (spin.getSelectedItem()=="เดือนที่แล้ว"){
                    ShowDataAll("last_month");
                    Log.e("tag","last_month");
                }
            }
        });

        //txtView1 = (TextView) findViewById(R.id.textView1);

         spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrAd = new ArrayAdapter<String>(ControlList.this,
                android.R.layout.simple_spinner_item,
                arr);

        arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(arrAd);

        txtRefraesh = (TextView) findViewById(R.id.txtRefresh);

        txtRefraesh.setOnClickListener(new View.OnClickListener() {

           // String Text = spin.getSelectedItem().toString();
            public void onClick(View v) {
                if(spin.getSelectedItem()=="วันนี้"){
                    ShowDataAll("date");
                    Log.e("tag","date");
                }else if(spin.getSelectedItem()=="สัปดาห์นี้"){
                    ShowDataAll("week");
                    Log.e("tag","week");
                }else if(spin.getSelectedItem()=="เดือนนี้"){
                    ShowDataAll("month");
                    Log.e("tag","month");
                }else if (spin.getSelectedItem()=="เดือนที่แล้ว"){
                    ShowDataAll("last_month");
                    Log.e("tag","last_month");
                }
            }
        });


    }

    private void DeleteAll(final String TagDelectData) {

        final AlertDialog.Builder adb = new AlertDialog.Builder(ControlList.this);

        adb.setTitle("ลบข้อมูล?");
        adb.setMessage("คุณแน่ใจหรือไม่ที่จะลบข้อมูลทั้งหมดนี้ ");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Request to Delete data.
                String url = "http://smart-home-control.comxa.com/deleteData.php";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag",TagDelectData));

                String resultServer  = getJSONUrl(url,params);

                String strStatusID = "0";
                String strError = "Unknow Status";

                try {
                    JSONObject c = new JSONObject(resultServer);
                    strStatusID = c.getString("StatusID");
                    strError = c.getString("Error");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Prepare Delete
                if(strStatusID.equals("0"))
                {
                    // Dialog
                    adb.setTitle("Error! ");
                    adb.setIcon(android.R.drawable.btn_star_big_on);
                    adb.setPositiveButton("Close", null);
                    adb.setMessage(strError);
                    adb.show();
                }
                else if(strStatusID.equals("1"))
                {
                    Toast.makeText(ControlList.this, "ลบเรีบยร้อยแล้ว", Toast.LENGTH_SHORT).show();

                    ShowDataAll(TagDelectData); // reload data again
                }

            }});
        adb.show();
    }


    public void ShowDataAll( String TagShowData)
    {
        // listView1
        final ListView lisView1 = (ListView)findViewById(R.id.listView1);

        // keySearch
        EditText strKeySearch = (EditText)findViewById(R.id.txtKeySearch);



        String url = "http://smart-home-control.besaba.com/showAllData.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", TagShowData));

        try {
            JSONArray data = new JSONArray(getJSONUrl(url,params));


            MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("id", c.getString("id"));
                map.put("name_devices", c.getString("name_devices"));
                map.put("control_date", c.getString("control_date"));
                map.put("status", c.getString("status"));
                map.put("user_control", c.getString("user_control"));
                map.put("IMEI_Number", c.getString("IMEI_Number"));
                MyArrList.add(map);

            }

            lisView1.setAdapter(new ImageAdapter(this));
            mSwipeRefreshLayout.setRefreshing(true);
            registerForContextMenu(lisView1);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        menu.setHeaderIcon(android.R.drawable.btn_star_big_on);
        menu.setHeaderTitle("คำสั่ง");
        String[] menuItems = CmdTH;
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = Cmd;
        String CmdName = menuItems[menuItemIndex];

        // Check Event Command
        if ("View".equals(CmdName)) {
            Toast.makeText(ControlList.this, "Your Selected View", Toast.LENGTH_LONG).show();
            /**
             * Call the mthod
             */


            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
            // OnClick Item

            String sMemberID = MyArrList.get(info.position).get("id").toString();
            String sName = MyArrList.get(info.position).get("name_devices").toString();
            String sTel = MyArrList.get(info.position).get("status").toString();


            //String sMemberID = ((TextView) myView.findViewById(R.id.ColMemberID)).getText().toString();
            // String sName = ((TextView) myView.findViewById(R.id.ColName)).getText().toString();
            // String sTel = ((TextView) myView.findViewById(R.id.ColTel)).getText().toString();

            viewDetail.setIcon(android.R.drawable.btn_star_big_on);
            viewDetail.setTitle("Member Detail");
            viewDetail.setMessage("MemberID : " + sMemberID + "\n"
                    + "Name : " + sName + "\n" + "Tel : " + sTel);
            viewDetail.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
            viewDetail.show();


        } else if ("Update".equals(CmdName)) {
            Toast.makeText(ControlList.this, "Your Selected Update", Toast.LENGTH_LONG).show();

            String sMemberID = MyArrList.get(info.position).get("id").toString();
            String sName = MyArrList.get(info.position).get("name_devices").toString();
            String sTel = MyArrList.get(info.position).get("status").toString();

            Intent newActivity = new Intent(ControlList.this,UsersUpdate.class);
            newActivity.putExtra("id", sMemberID);
            newActivity.putExtra("name_devices",sName);
            newActivity.putExtra("status",sTel);
            startActivity(newActivity);

        } else if ("Delete".equals(CmdName)) {
            Toast.makeText(ControlList.this, "Your Selected Delete", Toast.LENGTH_LONG).show();
            /**
             * Call the mthod
             */


            final AlertDialog.Builder adb1 = new AlertDialog.Builder(ControlList.this);
            final AlertDialog.Builder adb2 = new AlertDialog.Builder(ControlList.this);


            final String sMemberID = MyArrList.get(info.position).get("id").toString();
            adb1.setTitle("Delete?");
            adb1.setMessage("Are you sure delete [" + MyArrList.get(info.position).get("firstname") +" "+MyArrList.get(info.position).get("lastname")+"]");

            adb1.setNegativeButton("Cancel", null);
            adb1.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Request to Delete data.
                    String url = "http://smart-home-control.besaba.com/deleteData.php";
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("sMemberID",sMemberID));

                    String resultServer  = getJSONUrl(url,params);

                    /** Get result delete data from Server (Return the JSON Code)
                     * StatusID = ? [0=Failed,1=Complete]
                     * Error	= ?	[On case error return custom error message]
                     *
                     * Eg Login Failed = {"StatusID":"0","Error":"Cannot delete data!"}
                     * Eg Login Complete = {"StatusID":"1","Error":""}
                     */
                    String strStatusID = "0";
                    String strError = "Unknow Status";

                    try {
                        JSONObject c = new JSONObject(resultServer);
                        strStatusID = c.getString("StatusID");
                        strError = c.getString("Error");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // Prepare Delete
                    if(strStatusID.equals("0"))
                    {
                        // Dialog
                        adb2.setTitle("Error! ");
                        adb2.setIcon(android.R.drawable.btn_star_big_on);
                        adb2.setPositiveButton("Close", null);
                        adb2.setMessage(strError);
                        adb2.show();
                    }
                    else if(strStatusID.equals("1"))
                    {
                        Toast.makeText(ControlList.this, "Delete data successfully.", Toast.LENGTH_SHORT).show();
                       // ShowData(); // reload data again
                    }

                }});
            adb1.show();
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

         switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                //KEY_TagShowData = "all";
                ShowDataAll("date");
                spin.setSelection(position);
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                //KEY_TagShowData = "date";
                ShowDataAll("week");
                spin.setSelection(position);
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                //KEY_TagShowData = "week";
                ShowDataAll("month");
                spin.setSelection(position);
                break;
            case 3:
                // Whatever you want to happen when the thrid item gets selected
               // KEY_TagShowData = "month";
                ShowDataAll("last_month");
                spin.setSelection(position);
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c)
        {
            // TODO Auto-generated method stub
            context = c;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArrList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, null);
            }

                String CheckStatus = MyArrList.get(position).get("status");
            ImageView txtMemberID = (ImageView) convertView.findViewById(R.id.ColMemberID);

            if(Integer.parseInt(CheckStatus) == 1){
                txtMemberID.setPadding(5, 0, 10, 0);
                txtMemberID.setImageResource(R.drawable.turn_on_off_power_24);

            }else {
               // TextView txtMemberID = (TextView) convertView.findViewById(R.id.ColMemberID);
                txtMemberID.setPadding(5, 0, 10, 0);
                txtMemberID.setImageResource(R.drawable.turn_off_on_power_24);

            }

            // ColMemberID
            /*ImageView txtMemberID = (ImageView) convertView.findViewById(R.id.ColMemberID);
            txtMemberID.setPadding(5, 0, 10, 0);
            txtMemberID.setImageResource(R.drawable.turn_off_on_power_24);*/

            // R.id.ColName
            TextView txtName = (TextView) convertView.findViewById(R.id.ColName);
            txtName.setPadding(5, 0, 0, 0);
            txtName.setText(MyArrList.get(position).get("name_devices"));

            // R.id.ColTel
            /*TextView txtTel = (TextView) convertView.findViewById(R.id.ColTel);
            txtTel.setPadding(5, 0, 0, 0);
            txtTel.setText(MyArrList.get(position).get("status"));*/

            TextView txtTel = (TextView) convertView.findViewById(R.id.ColTel);
            txtTel.setPadding(5, 0, 10, 0);
            txtTel.setText(MyArrList.get(position).get("control_date"));


            ImageButton cmdView = (ImageButton) convertView.findViewById(R.id.imgCmdView);
            final AlertDialog.Builder viewDetail = new AlertDialog.Builder(ControlList.this);
            //cmdView.setBackgroundColor(Color.TRANSPARENT);
            cmdView.setOnClickListener(new View.OnClickListener() {

                Date date;
                public void onClick(View v) {

                    /**
                     * Command for Shared (Intent to Another Activity)
                     * Intent newActivity = new Intent(ListDeleteActivity.this,ViewActivity.class);
                     * newActivity.putExtra("ImgID", MyArrList.get(position).get("ImageID"));
                     * startActivity(newActivity);
                     */
                    // OnClick Item

                    String sMemberID = MyArrList.get(position).get("id").toString();
                    String sName = MyArrList.get(position).get("name_devices").toString();
                    String sStatus = MyArrList.get(position).get("status").toString();
                    String sDate = MyArrList.get(position).get("control_date").toString();
                    String IMEI = MyArrList.get(position).get("IMEI_Number").toString();
                    String sUser_control = MyArrList.get(position).get("user_control").toString();

                    //แปลงDatetime ให้อยู่ในรูปแบบของวันที่

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                         date = df.parse(sDate);
                        System.out.println("Full Date : " + date);
                        Log.e("Fail 1", String.valueOf(date));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if(Integer.parseInt(sStatus) == 1){
                        sStatus = "เปิด";
                    }else {
                        sStatus = "ปิด";
                    }

                    //String sMemberID = ((TextView) myView.findViewById(R.id.ColMemberID)).getText().toString();
                    // String sName = ((TextView) myView.findViewById(R.id.ColName)).getText().toString();
                    // String sTel = ((TextView) myView.findViewById(R.id.ColTel)).getText().toString();


                    viewDetail.setIcon(android.R.drawable.btn_star_big_on);
                    viewDetail.setTitle("รายละเอียด");
                    viewDetail.setMessage("หมายเลขIMEI : " + IMEI + "\n"
                            + "ชื่อ : " + sName + "\n"+ "วันที่ : " + sDate + "\n"+ "ผู้ใช้งาน : " + sUser_control + "\n" + "สถานะ : " + sStatus);

                    viewDetail.setPositiveButton("ตกลง", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(UserList.this,"Your View (ID = " + MyArrList.get(position).get("id") + ")",Toast.LENGTH_LONG).show();
                            /**
                             * Command for Delete
                             * Eg : myDBClass.DeleteData(MyArrList.get(position).get("ImageID"));
                             */
                        }});

                    viewDetail.show();
                }
            });

            // imgCmdDelete

            //cmdDelete.setBackgroundColor(Color.TRANSPARENT);
            final String sMemberID = MyArrList.get(position).get("id").toString();
            final AlertDialog.Builder adb = new AlertDialog.Builder(ControlList.this);
           final String KEY_DelectData = "DeleteData";



            return convertView;

        }

    }


    public String getJSONUrl(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class ProcessLogout extends AsyncTask<String, String, JSONObject> {


        final AlertDialog.Builder ad = new AlertDialog.Builder(ControlList.this);



        private ProgressDialog pDialog;

        //String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog = new ProgressDialog(ControlList.this);
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

                        Intent newActivity = new Intent(ControlList.this,MainActivity.class);
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
                = new AlertDialog.Builder(ControlList.this);
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
    ////menu hide
    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutSpinner, View.TRANSLATION_Y);

      //  ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutActionBar, View.TRANSLATION_Y, 0);

      //  ObjectAnimator anim3 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutSpinner, View.TRANSLATION_Y);

       // ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutActionBar, View.TRANSLATION_Y, -layoutActionBar.getHeight());

       // ObjectAnimator anim3 = ObjectAnimator.ofFloat(layoutHeader, View.TRANSLATION_Y, -layoutHeader.getHeight() * 2);

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }
}
