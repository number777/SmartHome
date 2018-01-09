package com.example.number7.smarthome.Library;

/**
 * Created by Number_7 on 15/7/2557.
 */

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserFunctions {

    private JSONParser jsonParser;

    //URL of the PHP API
    private static String loginURL = "http://smart-home-control.besaba.com/";
    private static String logoutURL = "http://smart-home-control.besaba.com/";
    private static String registerURL = "http://smart-home-control.besaba.com/";
    private static String forpassURL = "http://smart-home-control.besaba.com/";
    private static String chgpassURL = "http://smart-home-control.besaba.com/";
    private static String Ctrl_ledURL = "http://smart-home-control.besaba.com/";
    private static String Update_ProfileURL = "http://smart-home-control.besaba.com/";
    private static String Devices_nameURL = "http://smart-home-control.besaba.com/";
    private static String IP_Server_URL = "http://smart-home-control.besaba.com/";

    private static String login_tag = "login";
    private static String logout_tag = "Logout";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";
    private static String ctrl_led_tag = "ctrl_led";
    private static String Update_Profile_tag = "UpdateProfile";
    private static String DeviceName_tag = "device_name";
    private static String IP_Server_tag = "IP_Server";
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/

    public JSONObject Logout(String uname){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", logout_tag));
        params.add(new BasicNameValuePair("uname",uname));
        JSONObject json = jsonParser.getJSONFromUrl(logoutURL, params);
        return json;
    }

    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
    public JSONObject UpdateProfile(String id, String uname){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",Update_Profile_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("uname", uname));

        JSONObject json = jsonParser.getJSONFromUrl(Update_ProfileURL, params);
        return json;
    }
    public JSONObject device_name(String name1, String name2, String name3, String name4){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",DeviceName_tag));
        params.add(new BasicNameValuePair("name1", name1));
        params.add(new BasicNameValuePair("name2", name2));
        params.add(new BasicNameValuePair("name3", name3));
        params.add(new BasicNameValuePair("name4", name4));

        JSONObject json = jsonParser.getJSONFromUrl(Devices_nameURL, params);
        return json;
    }

    public JSONObject IP_Server(String id, String ip, String port,String TimeLogout){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",IP_Server_tag));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("ip", ip));
        params.add(new BasicNameValuePair("port", port));
        params.add(new BasicNameValuePair("TimedLogout", TimeLogout));


        JSONObject json = jsonParser.getJSONFromUrl(IP_Server_URL, params);
        return json;
    }

    /**
     * Function to change password
     **/

    public JSONObject chgPass(String newpas, String email){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", chgpass_tag));

        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
        return json;
    }





    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String forgotpassword){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }






    /**
     * Function to  Register
     **/
    public JSONObject registerUser(String fname, String lname, String email, String uname, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("uname", uname));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }


    /**
     * Function to  Ctrl LED
     **/
    public JSONObject ctrl_led(String status, String id,  String users_id){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",ctrl_led_tag));
        params.add(new BasicNameValuePair("id", id));
        //params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("status", status));
        params.add(new BasicNameValuePair("users_id", users_id));
        JSONObject json = jsonParser.getJSONFromUrl(Ctrl_ledURL,params);
        return json;
    }
    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }



}
