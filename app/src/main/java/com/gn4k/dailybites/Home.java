package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.gn4k.dailybites.Animatin.LoadingDialog;
import com.gn4k.dailybites.RoomForCalender.Calender;
import com.gn4k.dailybites.RoomForCalender.CalenderDao;
import com.gn4k.dailybites.RoomForCalender.CalenderDatabase;
import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;
import com.gn4k.dailybites.RoomForRecent.MessDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Home extends AppCompatActivity {

    private static final int YOUR_REQUEST_CODE = 123;

    private String date, menu, day;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;

    Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE);

        MenuItem homebtn = bottomNavigationView.getMenu().findItem(R.id.nav_home);
        MenuItem calenderbtn = bottomNavigationView.getMenu().findItem(R.id.nav_calender);
        MenuItem recentbtn = bottomNavigationView.getMenu().findItem(R.id.nav_recent);
        MenuItem userbtn = bottomNavigationView.getMenu().findItem(R.id.nav_user);

        homebtn.setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        homebtn.setEnabled(false);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_calender:
                        fragment = new CalenderFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(false);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_recent:
                        fragment = new RecentFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(false);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_user:
                        fragment = new UserFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(false);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });

        getTodaysMenuInCalenderRoom();

    }

    private void getTodaysMenuInCalenderRoom() {


        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess").child(sharedPreferences.getString("MessNo",""));
        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    menu = String.valueOf(data.get("menu"));

                    GetDateTime getDateTime = new GetDateTime(activity);
                    getDateTime.getDateTime(new GetDateTime.VolleyCallBack() {
                        @Override
                        public void onGetDateTime(String date2, String time) {

                            date = date2;
                            ValuesLocal values = new ValuesLocal();
                            day = values.DateToWeekDate(date);
                            new CalenderBgthread().start();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    class CalenderBgthread extends Thread { // to add a mess in recent list in room database
        public void run() {
            super.run();

            CalenderDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    CalenderDatabase.class, "CalenderView_DB").build();

            CalenderDao messDao = messdb.userDao();

            long lastUid = messDao.getLastCalenderUid();





            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                messDao.insert(new Calender(initialUid, date, menu, day));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByCalenderNo(date)) {
                    Calender existingDate = messDao.getCalenderByUid(date);
                    messDao.delete(existingDate);
                    messDao.insert(new Calender(nextUid, date, menu, day));
                } else {
                    messDao.insert(new Calender(nextUid, date, menu, day));
                }

            }
        }
    }



}

class ValuesLocal {
    static boolean show = true;

    public String DateToWeekDate(String dateString) {
        dateString = "06/11/2023";
        String dateFormat = "MM/dd/yyyy";
        String dayName = "null";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            // Parse the date string to a Date object
            Date date = simpleDateFormat.parse(dateString);

            // Create a Calendar instance and set it to the parsed date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Get the day of the week (Sunday = 1, Monday = 2, ..., Saturday = 7)
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Convert to the corresponding day name

            switch (dayOfWeek) {
                case Calendar.SUNDAY:
                    dayName = "Sunday";
                    break;
                case Calendar.MONDAY:
                    dayName = "Monday";
                    break;
                case Calendar.TUESDAY:
                    dayName = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    dayName = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    dayName = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    dayName = "Friday";
                    break;
                case Calendar.SATURDAY:
                    dayName = "Saturday";
                    break;
                default:
                    dayName = "Invalid day";
                    break;
            }

            return dayName;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayName;
    }
}
