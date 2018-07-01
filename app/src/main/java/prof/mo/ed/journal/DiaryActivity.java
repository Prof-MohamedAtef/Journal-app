package prof.mo.ed.journal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.test.ActivityTestCase;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;
import prof.mo.ed.journal.Fragments.NewsFeedFragment;

/*
Created by Prof-Mohamed on 6/27/2018.
This class hold the NewsFeedFragment and display data in Navigation mode (as follows) which displays diaries including all entries on the same time.
It enable users to add more and update last diaries with just a screen touch.
 */

public class DiaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    SessionManagement sessionManagement;
    CircleImageView ProfileImage_Navigation_header;
    DBHelper DB;
    private final String LOG_TAG = DiaryActivity.class.getSimpleName();
    TextView Email_Navigation_header,Address_Navigation_header;
    String LoggedEmail, LoggedProfilePic, LoggedUserID,LoggedType;
    String LoggedUserName;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    HashMap<String, String> user;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        setTheme(R.style.ArishTheme);
        sessionManagement= new SessionManagement(getApplicationContext());
        checkConnection();
        if (isConnected()){
            mAuth=FirebaseAuth.getInstance();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        try {
            DB = new DBHelper(getApplicationContext());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Didn't Create Database", e);
        }
        user =sessionManagement.getUserDetails();
        if (user!=null){
            LoggedEmail = user.get(SessionManagement.KEY_EMAIL);
            LoggedUserName=user.get(SessionManagement.KEY_NAME);
            LoggedProfilePic=user.get(SessionManagement.KEY_Profile_Pic);
            LoggedUserID=user.get(SessionManagement.KEY_userID);

        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        ProfileImage_Navigation_header=(CircleImageView)header.findViewById(R.id.profile_image);
        Email_Navigation_header=(TextView)header.findViewById(R.id.Email);
        Picasso.with(getApplicationContext()).load(LoggedProfilePic)
                .error(R.drawable.user_icon)
                .into(ProfileImage_Navigation_header);
        Email_Navigation_header.setText(LoggedEmail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.News:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_frame, new NewsFeedFragment(), "posts")
                                .commit();
                        return true;
                    case R.id.Logout:
                        SignOut();
                        return true;
                    default:
                        return true;
                }
            }
        });
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.openDrawer, R.string.closeDrawer){
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
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_frame, new NewsFeedFragment(), "posts")
                        .commit();
    }

    private void SignOut() {
        checkConnection();
        if (isConnected()){
            user =sessionManagement.getLoginType();
            if (user!=null){
                LoggedType = user.get(SessionManagement.KEY_LoginType);
                if (LoggedType!=null){
                    if (LoggedType.equals("G")){
                        mAuth.signOut();
                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()){
                                    sessionManagement.logoutUser();
                                }
                            }
                        });
                    }else if (LoggedType.equals("F")){
                        LoginManager.getInstance().logOut();
                        sessionManagement.logoutUser();
                    }else if (LoggedType.equals("EP")){
                        mAuth = FirebaseAuth.getInstance();
                        if (mAuth.getCurrentUser() != null) {
                            mAuth.signOut();
                            sessionManagement.logoutUser();
                        }
                    }
                }
            }
        }else {
            Toast.makeText(getApplicationContext(), "Internet disconnected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}