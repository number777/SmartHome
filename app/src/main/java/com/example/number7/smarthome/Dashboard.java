package com.example.number7.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Dashboard extends Activity {
    String[] Cmd = {"Command1","Command2","Command3","Command4"};
    String[] CmdName = {"ความปลอดภัย","หมายเลขIP","รหัสผ่าน","ชื่ออุปกรณ์"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Button btn1 = (Button)findViewById(R.id.setting_btn);

        registerForContextMenu(btn1);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(v);
                v.showContextMenu();
            }
        });
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderIcon(android.R.drawable.btn_star_big_on);
        menu.setHeaderTitle("Menu 1");
        String[] menuItems = CmdName;

        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        String[] menuItems = Cmd;
        String CmdName = menuItems[menuItemIndex];

        // Check Event Command
        if ("Command1".equals(CmdName)) {
            Toast.makeText(Dashboard.this, "Your Selected Command1", Toast.LENGTH_LONG).show();
            /**
             * Call the mthod
             * Eg: Command1();
             */
        } else if ("Command2".equals(CmdName)) {
            Toast.makeText(Dashboard.this, "Your Selected Command2", Toast.LENGTH_LONG).show();
            /**
             * Call the mthod
             * Eg: Command2();
             */
        } else if ("Command3".equals(CmdName)) {
            Toast.makeText(Dashboard.this, "Your Selected Command3", Toast.LENGTH_LONG).show();
            /**
             * Call the mthod
             * Eg: Command3();
             */
        } else if ("Command4".equals(CmdName)) {
            Toast.makeText(Dashboard.this, "Your Selected Command4", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(),Setting.class);
            startActivity(intent);
            /**
             * Call the mthod
             * Eg: Command4();
             */
        }

        return true;
    }



    public void onButtonClicker(View v)
    {
        Intent intent;

        switch (v.getId()) {
            case R.id.main_btn:
                intent = new Intent(this, HomeControl.class);
                startActivity(intent);
                break;

            case R.id.users_btn:

                intent = new Intent(Dashboard.this, UserList.class);
                startActivity(intent);
                break;

            case R.id.name_device_btn:
                intent = new Intent(this, Setting.class);
                startActivity(intent);
                break;

            case R.id.profile_btn:
                intent = new Intent(this, Profile.class);
                startActivity(intent);
                break;

            case R.id.logout_btn:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent newActivity = new Intent(Dashboard.this,MainActivity.class);
            startActivity(newActivity);
            return true;
        }else if(id == R.id.action_profile){
            Intent newActivity = new Intent(Dashboard.this,Profile.class);
            startActivity(newActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
