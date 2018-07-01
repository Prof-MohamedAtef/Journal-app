package prof.mo.ed.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Prof-Mohamed Atef on 6/26/2018.
 * This class is to convert data from ArrayList to a listView
 */
public class LazyAdapter extends ArrayAdapter implements Serializable{

    private ArrayList<OptionsEntity> feedOptionsList;
    public transient Context mContext;

    public LazyAdapter(Context context, int Resource,ArrayList<OptionsEntity>feedOptionsList) {
        super(context, Resource, feedOptionsList);
        this.feedOptionsList=feedOptionsList;
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return feedOptionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    SessionManagement sessionManagement;
    HashMap<String, String> user;
    String LoggedProfilePic, LoggedUserName, LoggedEmail;
    CircleImageView post_profile_picture;
    TextView post_username, post_Date,txt_status, post_text;
    ImageView img_status;
    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final OptionsEntity feedItem=feedOptionsList.get(i);
        View view=convertView;
        if(view==null){
            view=LayoutInflater.from(mContext).inflate(R.layout.article_list_item,parent,false);
        }
        sessionManagement=new SessionManagement(mContext);
        post_profile_picture=(CircleImageView)view.findViewById(R.id.post_profile_picture);
        post_username=(TextView)view.findViewById(R.id.post_username);
        post_Date=(TextView)view.findViewById(R.id.post_Date);
        txt_status=(TextView)view.findViewById(R.id.txt_status);
        post_text=(TextView)view.findViewById(R.id.post_text);
        img_status=(ImageView)view.findViewById(R.id.img_status);
        user =sessionManagement.getUserDetails();
        if (user!=null){
            LoggedUserName=user.get(SessionManagement.KEY_NAME);
            LoggedProfilePic=user.get(SessionManagement.KEY_Profile_Pic);
            LoggedEmail=user.get(SessionManagement.KEY_EMAIL);
            if (LoggedUserName!=null){
                post_username.setText(LoggedUserName);
                Picasso.with(mContext).load(LoggedProfilePic).error(R.drawable.user_icon).into(post_profile_picture);
            }else if (LoggedUserName==null&&LoggedEmail!=null){
                post_username.setText(LoggedEmail);
                Picasso.with(mContext).load(LoggedProfilePic).error(R.drawable.user_icon).into(post_profile_picture);
            }
        }
        post_Date.setText(feedItem.getThoughtDate());
        post_text.setText(feedItem.getThought_Str());
        txt_status.setText(feedItem.getStatus_Str());
        Picasso.with(mContext).load(feedItem.getStatus_ImgUrl()).error(R.drawable.news).into(img_status);
        return view;
    }
}


