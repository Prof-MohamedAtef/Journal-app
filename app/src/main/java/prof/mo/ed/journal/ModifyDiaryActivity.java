package prof.mo.ed.journal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
/*
Created by Prof-Mohamed on 6/27/2018.
This class Enables users to modify their entries updating both of sqlite and firebase
 */

public class ModifyDiaryActivity extends AppCompatActivity {

    private final String LOG_TAG = AddToDiary.class.getSimpleName();
    SessionManagement sessionManagement;
    private DatabaseReference mDatabase;
    DBHelper DB;
    CircleImageView ProfilePic;
    TextView UserName, ThoughtsDate, txt_status;
    EditText Thoughts_Edit;
    ImageView Feeling_happy, Feeling_sad, Feeling_optimistic, Feeling_Angry, Feeling_crying,img_status;
    HashMap<String, String> user;
    String LoggedProfilePic, LoggedUserName, LoggedEmail;
    OptionsEntity optionsEntity;
    String Key_Happy="Key_Happy", Key_Optimistic="Key_Optimistic", Key_Angry="Key_Angry", Key_Sad="Key_Sad", Key_Crying="Key_Crying";
    String Thoughts_Str, Status_ImageUrl, Status_Str;
    private String currentDateTimeString;
    private Object HourOfay;
    private Object AM_PM;
    private Object day;
    private Date Now;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postedit_approve, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Post_Btn_update:
                // post  - database saving ... insert
                if (!Thoughts_Edit.equals("")){
                    Thoughts_Str=Thoughts_Edit.getText().toString();
                    if (optionsEntity.imgID!=null){
                        if (optionsEntity.imgID==Key_Happy){
                            Status_ImageUrl=getString(R.string.happy_face);
                            Status_Str="Happy";
                        }else if (optionsEntity.imgID==Key_Optimistic){
                            Status_ImageUrl=getString(R.string.optimistic_face);
                            Status_Str="Optimistic";
                        }else if (optionsEntity.imgID==Key_Sad){
                            Status_ImageUrl=getString(R.string.sad_face);
                            Status_Str="Sad";
                        }else if (optionsEntity.imgID==Key_Angry){
                            Status_ImageUrl=getString(R.string.angry_face);
                            Status_Str="Angry";
                        }else if (optionsEntity.imgID==Key_Crying){
                            Status_ImageUrl=getString(R.string.crying_face);
                            Status_Str="Crying";
                        }
                        Now= Calendar.getInstance().getTime();
                        user =sessionManagement.getUserDetails();
                        if (user!=null){
                            LoggedUserName=user.get(SessionManagement.KEY_NAME);
                            LoggedProfilePic=user.get(SessionManagement.KEY_Profile_Pic);
                            LoggedEmail=user.get(SessionManagement.KEY_EMAIL);
                        }
                        String FoundKey=DB.FindFirebaseKey(optionsEntity.Thoughts);
                        if (FoundKey!=null){
                            if (isConnected()){
                                UpdateFirebase(FoundKey,Now.toString(),Thoughts_Str,Status_ImageUrl,Status_Str, LoggedEmail);
                                boolean Updated=DB.UpdateToDiary( optionsEntity.Thoughts,FoundKey, Now.toString(),Thoughts_Str,Status_ImageUrl,Status_Str, LoggedEmail);
                                if (Updated){
                                    Intent intent_create=new Intent(this,DiaryActivity.class);
                                    startActivity(intent_create);
                                }
                            }else if (!isConnected()){
                                boolean Updated=DB.UpdateToDiary( optionsEntity.Thoughts,FoundKey, Now.toString(),Thoughts_Str,Status_ImageUrl,Status_Str, LoggedEmail);
                                if (Updated){
                                    Intent intent_create=new Intent(this,DiaryActivity.class);
                                    startActivity(intent_create);
                                }
                            }

                        }
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isInternetConnected;
    private boolean checkConnection() {
        return isInternetConnected=isConnected();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); //NetworkApplication.getInstance().getApplicationContext()/
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null){
            return isInternetConnected= activeNetwork.isConnected();
        }else
            return isInternetConnected=false;
    }

    private void UpdateFirebase(String foundKey, String date, String thoughts_Str, String status_ImageUrl, String status_Str, String loggedEmail) {
        mDatabase.child(foundKey).child("email").setValue(loggedEmail);
        mDatabase.child(foundKey).child("status_ImgUrl").setValue(status_ImageUrl);
        mDatabase.child(foundKey).child("status_Str").setValue(status_Str);
        mDatabase.child(foundKey).child("thoughtDate").setValue(date);
        mDatabase.child(foundKey).child("thought_Str").setValue(thoughts_Str);
        UserChangeListener(foundKey);
    }

    private void UserChangeListener(String foundKey) {
        mDatabase.child(foundKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OptionsEntity optionsEntity=dataSnapshot.getValue(OptionsEntity.class);
                if (optionsEntity==null){
                    Log.e(LOG_TAG, "User data is null!");
                    return;
                }
                Log.e(LOG_TAG, "options data is changed!" + optionsEntity.Email + ", " + optionsEntity.Thought_Str + ", "+optionsEntity.Status_ImgUrl+", "+optionsEntity.Status_Str);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(LOG_TAG, "Failed to read options", databaseError.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_diary);
        this.setTitle(getString(R.string.modifyPoststitle));
        setTheme(R.style.AppTheme);
        if (mDatabase==null){
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            mDatabase=database.getReference("data");
        }
        checkConnection();
        Intent intent=getIntent();
        optionsEntity=new OptionsEntity();
        ProfilePic=(CircleImageView)findViewById(R.id.ProfileImage_header_Post);
        UserName=(TextView)findViewById(R.id.txtProfileOwner_Posts);
        Thoughts_Edit=(EditText)findViewById(R.id.txt_shareideas);
        ThoughtsDate=(TextView)findViewById(R.id.txtDiaryDate);
        txt_status=(TextView)findViewById(R.id.txt_status);
        img_status=(ImageView)findViewById(R.id.img_status);
        if (intent!=null){
            Bundle bundle= intent.getExtras();
            if (bundle!=null){
                optionsEntity= (OptionsEntity) bundle.getSerializable("DiaryExtras");
                Thoughts_Edit.setText(optionsEntity.getThought_Str());
                optionsEntity.Thoughts=optionsEntity.getThought_Str();
                ThoughtsDate.setText(optionsEntity.getThoughtDate());
                txt_status.setText(optionsEntity.getStatus_Str());
                String Status=optionsEntity.getStatus_Str();

                if (Status.equals("Happy")){
                    Picasso.with(getApplicationContext()).load(getString(R.string.happy_face)).into(img_status);
                }else if (Status.equals("Sad")){
                    Picasso.with(getApplicationContext()).load(getString(R.string.sad_face)).into(img_status);
                }else if (Status.equals("Angry")){
                    Picasso.with(getApplicationContext()).load(getString(R.string.angry_face)).into(img_status);
                }else if (Status.equals("Optimistic")){
                    Picasso.with(getApplicationContext()).load(getString(R.string.optimistic_face)).into(img_status);
                }else if (Status.equals("Crying")){
                    Picasso.with(getApplicationContext()).load(getString(R.string.crying_face)).into(img_status);
                }
            }
        }
        sessionManagement= new SessionManagement(getApplicationContext());
        try {
            DB = new DBHelper(getApplicationContext());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Didn't Create Database", e);
        }
        user =sessionManagement.getUserDetails();
        if (user!=null){
            LoggedUserName=user.get(SessionManagement.KEY_NAME);
            LoggedProfilePic=user.get(SessionManagement.KEY_Profile_Pic);
            LoggedEmail=user.get(SessionManagement.KEY_EMAIL);
            if (LoggedUserName!=null){
                Picasso.with(getApplicationContext()).load(LoggedProfilePic).error(R.drawable.news).into(ProfilePic);
                UserName.setText(LoggedUserName);
            }else if (LoggedUserName==null&&LoggedEmail!=null){
                UserName.setText(LoggedEmail);
                Picasso.with(getApplicationContext()).load(LoggedProfilePic).error(R.drawable.news).into(ProfilePic);
            }else {
                Picasso.with(getApplicationContext()).load(LoggedProfilePic)
                        .error(R.drawable.news)
                        .into(ProfilePic);
            }
        }
        Feeling_happy=(ImageView)findViewById(R.id.img_happy);
        Feeling_sad=(ImageView)findViewById(R.id.img_sad);
        Feeling_optimistic=(ImageView)findViewById(R.id.optimistic);
        Feeling_Angry=(ImageView)findViewById(R.id.img_angry);
        Feeling_crying=(ImageView)findViewById(R.id.img_crying);
        Picasso.with(getApplicationContext()).load(getString(R.string.happy_face)).into(Feeling_happy);
        Picasso.with(getApplicationContext()).load(getString(R.string.optimistic_face)).into(Feeling_optimistic);
        Picasso.with(getApplicationContext()).load(getString(R.string.sad_face)).into(Feeling_sad);
        Picasso.with(getApplicationContext()).load(getString(R.string.crying_face)).into(Feeling_crying);
        Picasso.with(getApplicationContext()).load(getString(R.string.angry_face)).into(Feeling_Angry);
        Feeling_Angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsEntity.imgID=Key_Angry;
                Picasso.with(getApplicationContext()).load(getString(R.string.angry_face)).into(img_status);
                txt_status.setText("Angry");
            }
        });
        Feeling_sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsEntity.imgID=Key_Sad;
                Picasso.with(getApplicationContext()).load(getString(R.string.sad_face)).into(img_status);
                txt_status.setText("Sad");
            }
        });
        Feeling_optimistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsEntity.imgID=Key_Optimistic;
                Picasso.with(getApplicationContext()).load(getString(R.string.optimistic_face)).into(img_status);
                txt_status.setText("Optimistic");
            }
        });
        Feeling_happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsEntity.imgID=Key_Happy;
                Picasso.with(getApplicationContext()).load(getString(R.string.happy_face)).into(img_status);
                txt_status.setText("Happy");
            }
        });
        Feeling_crying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsEntity.imgID=Key_Crying;
                Picasso.with(getApplicationContext()).load(getString(R.string.crying_face)).into(img_status);
                txt_status.setText("Crying");
            }
        });
        calendar=new Calendar() {
            @Override
            protected void computeTime() {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            public void add(int field, int amount) {

            }

            @Override
            public void roll(int field, boolean up) {

            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
        };
    }
    Calendar calendar;
}
