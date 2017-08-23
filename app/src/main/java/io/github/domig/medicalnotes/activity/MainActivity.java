package io.github.domig.medicalnotes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Calendar;
import java.util.Date;

import io.github.domig.medicalnotes.BuildConfig;
import io.github.domig.medicalnotes.fragment.SettingsFragment;
import io.github.domig.medicalnotes.fragment.HomeFragment;
import io.github.domig.medicalnotes.fragment.EditFragment;

import io.github.domig.medicalnotes.R;
import io.github.domig.medicalnotes.other.CircleTransform;

/**
 * Created by Win on 07.08.2017.
 */

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtAge;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private int profileBackground;
    private int profilePic;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_GENERAL= "generalNotes";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    static SQLiteDatabase sql;
    static Cursor c;

    private EditText editTextName;
    private EditText editTextBirthday;
    private static EditText editTextGeneral;

    final String PREFS_NAME = "MyPrefsFile";
    static final String PREF_GENERAL_NOTES_KEY = "general_notes";
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextName = (EditText) findViewById(R.id.editTextName);


        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtAge = (TextView) navHeader.findViewById(R.id.age);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        profilePic = R.mipmap.profilepic;

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        try{
            sql = this.openOrCreateDatabase("Body", MODE_PRIVATE, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        checkFirstRun();
    }

    /***
     * Checks if the App is newly installed, upgraded or a normal run
     */
    private void checkFirstRun() {

        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode;
        try {
            currentVersionCode = BuildConfig.VERSION_CODE;
            //currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            Log.i("First Run Check: ", "This is just a normal run");
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            Log.i("First Run Check: ", "This is a new install (or the user cleared the shared preferences)");
            fillDatabaseDE();

        } else if (currentVersionCode > savedVersionCode) {

            Log.i("First Run Check: ", "This is an upgrade");
            return;

        }
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
    }

    /***
     * Initializes the Database for the first use
     */
    private void fillDatabaseDE() {
        //sql.execSQL("DROP TABLE IF EXISTS body");

        sql.execSQL("CREATE TABLE IF NOT EXISTS body (bodypart VARCHAR, lOrR VARCHAR, desc VARCHAR, drugs VARCHAR)");
        String[] headArray = getResources().getStringArray(R.array.head_array);
        String[] torsoArray = getResources().getStringArray(R.array.torso_array);
        String[] armArray = getResources().getStringArray(R.array.arm_array);
        String[] legArray = getResources().getStringArray(R.array.leg_array);

        for(String s : headArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'x', '', '')");
        }
        for(String s : torsoArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'x', '', '')");
        }
        for(String s : legArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'l', '', '')");
        }
        for(String s : legArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'r', '', '')");
        }
        for(String s : armArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'l', '', '')");
        }
        for(String s : armArray){
            sql.execSQL("INSERT INTO body (bodypart, lOrR, desc, drugs) VALUES ('" + s + "', 'r', '', '')");
        }
    }

    private void fillDatabaseEN() {
        sql.execSQL("CREATE TABLE IF NOT EXISTS body (bodypart VARCHAR, lOrR VARCHAR, desc VARCHAR, drugs VARCHAR)");
    }

    //Load navigation menu header information
    private void loadNavHeader() {
        txtName.setText("your name");
        txtAge.setText("your Age");

        // loading header background image
        //TODO: settings, change picture, add background
        Glide.with(this).load(profileBackground)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);


        Glide.with(this).load(profilePic)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns fragment that user selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        toggleFab();

        drawer.closeDrawers();

        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0: //TODO: doesn't work
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                EditFragment editFragment = new EditFragment();
                return editFragment;
            case 2:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_edit:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_GENERAL;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    public void headClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "head");
        startActivity(myIntent);
    }

    public void leftLegClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "rightLeg");
        startActivity(myIntent);
    }

    public void rightLegClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "leftLeg");
        startActivity(myIntent);

    }

    public void rightArmClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "rightArm");
        startActivity(myIntent);
    }

    public void leftArmClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "leftArm");
        startActivity(myIntent);
    }

    public void torsoClicked(View view) {
        Intent myIntent = new Intent(this, ListActivity.class);
        myIntent.putExtra("part", "torso");
        startActivity(myIntent);
    }
    public static void save(String part, String lOrR, String editedText, String editedDrugs){
        //sql.execSQL("INSERT INTO body (bodypart, desc, drugs) VALUES ('" + part + "', '" + editedText + "', '" + editedDrugs + "'");
        if(lOrR.equals("l") || lOrR.equals("r")){
            sql.execSQL("UPDATE body SET desc = '" + editedText + "' WHERE bodypart = '" + part + "' AND  lOrR = '" + lOrR +"'");
            sql.execSQL("UPDATE body SET drugs = '" + editedDrugs + "' WHERE bodypart = '" + part + "' AND  lOrR = '" + lOrR +"'");
        }
        else{
            sql.execSQL("UPDATE body SET desc = '" + editedText + "' WHERE bodypart = '" + part + "'");
            sql.execSQL("UPDATE body SET drugs = '" + editedDrugs + "' WHERE bodypart = '" + part + "'");
        }

        //showDatabase();
    }

    public static String loadDescFromDB(String p, String lr){
        String result = "";

        c = sql.rawQuery("SELECT * FROM body", null);

        int bodyIndex = c.getColumnIndex("bodypart");
        int lRIndex = c.getColumnIndex("lOrR");
        int descIndex = c.getColumnIndex("desc");

        c.moveToFirst();

        if(c.moveToFirst() && c.getCount() >= 1){
            do{
                String s = c.getString(bodyIndex);
                String leftRight = c.getString(lRIndex);
                if(leftRight.equals(lr) && s.equals(p)) {

                    result = c.getString(descIndex);
                    Log.i("load", c.getString(descIndex));
                }

            }while(c.moveToNext());

        }

        return result;
    }

    public static String loadDrugsFromDB(String p, String lr){
        String result = "";

        c = sql.rawQuery("SELECT * FROM body", null);

        int bodyIndex = c.getColumnIndex("bodypart");
        int lRIndex = c.getColumnIndex("lOrR");
        int drugsIndex = c.getColumnIndex("drugs");

        c.moveToFirst();

        if(c.moveToFirst() && c.getCount() >= 1){
            do{
                String s = c.getString(bodyIndex);
                String leftRight = c.getString(lRIndex);
                if(leftRight.equals(lr) && s.equals(p)) {

                    result = c.getString(drugsIndex);
                }

            }while(c.moveToNext());

        }

        return result;
    }

    public void btnSaveClicked(View view) {
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        String name = editTextName.toString();
        //convert datepicker to date
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();
        String age = getAge(year, month, day);
        updateNavProfile(name, age);
    }

    private void updateNavProfile(String name, String age) {
        txtName.setText(name);
        txtAge.setText(age);
    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public static void saveGeneralNotesTxt(String n){
        prefs.edit().putString(PREF_GENERAL_NOTES_KEY, n).commit();
    }

    public static String loadGeneralNotesTxt(){
       return prefs.getString(PREF_GENERAL_NOTES_KEY, "");
    }
}
