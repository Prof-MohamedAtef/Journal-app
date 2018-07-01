package prof.mo.ed.journal.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import prof.mo.ed.journal.AddToDiary;
import prof.mo.ed.journal.FirebaseHelper;
import prof.mo.ed.journal.OptionsEntity;
import prof.mo.ed.journal.DBHelper;
import prof.mo.ed.journal.LazyAdapter;
import prof.mo.ed.journal.ModifyDiaryActivity;
import prof.mo.ed.journal.R;
import prof.mo.ed.journal.SessionManagement;

/**
 * Created by Prof-Mohamed Atef on 6/26/2018.
 * This class is needed for displaying diary entries with details
 * either from sqlite in offline mode
 * or from Firebase Realtime Database in online mode
 */

public class NewsFeedFragment extends Fragment {

    private final String LOG_TAG = NewsFeedFragment.class.getSimpleName();
    LazyAdapter lazyAdapter;
    ArrayList<OptionsEntity> Diarylist = new ArrayList<OptionsEntity>();
    DBHelper DB;
    OptionsEntity optionsEntity;
    private ListView DiaryListView;
    ViewGroup header;
    String getDiaryText, getStatusText;
    private String getDiaryDate;

    FirebaseHelper firebaseHelper;

    @Override
    public void onStart() {
        super.onStart();
        firebaseHelper=new FirebaseHelper();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.article_list, container, false);
        header= (ViewGroup) inflater.inflate(R.layout.post_settings_header,DiaryListView,false);
        sessionManagement=new SessionManagement(getActivity());
        optionsEntity = new OptionsEntity();
        DiaryListView= (ListView)mainView.findViewById(R.id.listview);
        lazyAdapter = new LazyAdapter(getActivity(), R.layout.article_list_item,Diarylist);
        DiaryListView.addHeaderView(header,null,false);
        lazyAdapter.notifyDataSetChanged();
        DiaryListView.setAdapter(lazyAdapter);
        DiaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDiaryText= ((TextView)(view.findViewById(R.id.post_text))).getText().toString();
                getStatusText= ((TextView)(view.findViewById(R.id.txt_status))).getText().toString();
                getDiaryDate= ((TextView)(view.findViewById(R.id.post_Date))).getText().toString();
                optionsEntity.setThought_Str(getDiaryText);
                optionsEntity.setStatus_Str(getStatusText);
                optionsEntity.setThoughtDate(getDiaryDate);
                Intent intent = new Intent(getActivity(), ModifyDiaryActivity.class);
                intent.putExtra("DiaryExtras",optionsEntity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });
        DiaryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });
        try {
            DB = new DBHelper(getActivity());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Didn't Create Database", e);
        }
        return mainView;
    }

    String LoggedUserName, LoggedProfilePic, LoggedEmail;
    boolean isInternetConnected;
    TextView txtProfileOwner_Posts;
    TextView txt_AddPost_posts;
    CircleImageView ProfileImage_header_Post;
    SessionManagement sessionManagement;
    LinearLayout Linear_PhotoLauncherpostsFragment_header;
    HashMap<String, String> user,loginType;
    private DatabaseReference mDatabase;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDatabase==null){
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            mDatabase=database.getInstance().getReference();

        }
        user =sessionManagement.getUserDetails();
        loginType=sessionManagement.getLoginType();
        txtProfileOwner_Posts = (TextView) header.findViewById(R.id.txtProfileOwner_Posts);
        ProfileImage_header_Post = (CircleImageView) header.findViewById(R.id.ProfileImage_header_Post);
        if (user!=null) {
            LoggedUserName = user.get(SessionManagement.KEY_NAME);
            LoggedProfilePic = user.get(SessionManagement.KEY_Profile_Pic);
            LoggedEmail=user.get(SessionManagement.KEY_EMAIL);
            if (LoggedUserName != null) {
                txtProfileOwner_Posts.setText(LoggedUserName.toString());
                Picasso.with(getActivity()).load(LoggedProfilePic)
                        .error(R.drawable.news)
                        .into(ProfileImage_header_Post);
            }else if (LoggedUserName==null&&LoggedEmail!=null){
                txtProfileOwner_Posts.setText(LoggedEmail);
            }else {
                txtProfileOwner_Posts.setText(getString(R.string.welcome));
            }
        }
        txt_AddPost_posts = (TextView) header.findViewById(R.id.txt_AddPost_posts);
        txt_AddPost_posts.setFocusable(false);
        txt_AddPost_posts.setFocusableInTouchMode(false);
        txt_AddPost_posts.setSelected(false);
        Linear_PhotoLauncherpostsFragment_header = (LinearLayout) header.findViewById(R.id.Linear_PhotoLauncherpostsFragment_header);
        Linear_PhotoLauncherpostsFragment_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddToDiary.class);
                getActivity().startActivity(intent);
            }
        });
    }

    private boolean checkConnection() {
        return isInternetConnected=isConnected();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //NetworkApplication.getInstance().getApplicationContext()/
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null){
            return isInternetConnected= activeNetwork.isConnected();
        }else
            return isInternetConnected=false;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkConnection();
        try {
            if (user!=null){
                LoggedUserName=user.get(SessionManagement.KEY_NAME);
                LoggedProfilePic=user.get(SessionManagement.KEY_Profile_Pic);
                LoggedEmail=user.get(SessionManagement.KEY_EMAIL);
            }
            Diarylist.clear();
            if (isConnected()){
                FetchDataFromFirebase();
            }else if (!isConnected()){
                Diarylist = DB.selectAllDiaryData(LoggedEmail);
                if (Diarylist.size() > 0) {
                    lazyAdapter = new LazyAdapter(getActivity(), R.layout.article_list_item, Diarylist);
                    lazyAdapter.notifyDataSetChanged();
                    DiaryListView.setAdapter(lazyAdapter);
                }else {
                    Intent intent = new Intent(getActivity(), AddToDiary.class);
                    getActivity().startActivity(intent);
                }
            }
        } catch (Exception e) {
        }
    }

    private void FetchDataFromFirebase() {
        DatabaseReference ThoughtsRef=mDatabase.child("data");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Diarylist.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    key2=ds.getKey();
                    email = ds.child("email").getValue(String.class);
                    status_imgUrl = ds.child("status_ImgUrl").getValue(String.class);
                    status_str= ds.child("status_Str").getValue(String.class);
                    thoughtDate= ds.child("thoughtDate").getValue(String.class);
                    thought_str= ds.child("thought_Str").getValue(String.class);
                    Log.d("TAG", email+ " / " + status_imgUrl+ " / " + status_str+ " / " + thoughtDate+ " / " + thought_str);

                    optionsEntity=new OptionsEntity(key2, email, status_imgUrl,status_str,thoughtDate,thought_str);
                    Diarylist.add(optionsEntity);

                }
                if (Diarylist.size()>0) {
                    int maxID = DB.getInitialMaxValue();
                    if (maxID > 0) {
                        boolean Deleted = DB.deleteAll();
                        if (Deleted == true) {
                            for (final OptionsEntity optionsEntity : Diarylist) {
                                // Delete then Insert
                                boolean inserted = DB.InsertToDiary(optionsEntity.getKey(),optionsEntity.getThoughtDate(),optionsEntity.getThought_Str(),optionsEntity.getStatus_ImgUrl(),optionsEntity.getStatus_Str(),optionsEntity.getEmail());
                                if (inserted == true) {
                                }
                            }
                        }
                    } else {
                            // Delete then Insert
                            for (OptionsEntity optionsEntity : Diarylist) {
                                boolean inserted = DB.InsertToDiary(optionsEntity.getKey(),optionsEntity.getThoughtDate(),optionsEntity.getThought_Str(),optionsEntity.getStatus_ImgUrl(),optionsEntity.getStatus_Str(),optionsEntity.getEmail());
                                if (inserted == true) {
                                }
                            }

                    }
                    LaunchDiaryContent(Diarylist);
                }else {
                    Intent intent = new Intent(getActivity(), AddToDiary.class);
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ThoughtsRef.addListenerForSingleValueEvent(valueEventListener);


    }

    public  void LaunchDiaryContent(ArrayList<OptionsEntity> diarylist){
        if (diarylist.size()>0) {
            lazyAdapter = new LazyAdapter(getActivity(), R.layout.article_list_item,diarylist);
            lazyAdapter.notifyDataSetChanged();
            DiaryListView.setAdapter(lazyAdapter);
        }
    }
    String key2, email, status_imgUrl,status_str, thoughtDate,thought_str;

}