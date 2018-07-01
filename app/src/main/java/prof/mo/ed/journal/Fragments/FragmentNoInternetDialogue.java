package prof.mo.ed.journal.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import prof.mo.ed.journal.R;

/**
 * Created by prof on 6/26/2018.
 * This class is neede to display a layout once internet connection has been disabled.
 */
public class FragmentNoInternetDialogue extends android.support.v4.app.Fragment {
    LinearLayout Linear_reload;

    boolean isInternetConnected;

    private boolean checkConnection() {
        return isInternetConnected=isConnected();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //NetworkApplication.getInstance().getApplicationContext()
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null){
            return isInternetConnected= activeNetwork.isConnected();
        }else
            return isInternetConnected=false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_nointernet_connection, container, false);
        Linear_reload=(LinearLayout)mainView.findViewById(R.id.Linear_reload);
        Linear_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnection()) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container_frame, new NewsFeedFragment(), "posts")
                            .commit();
                }
            }
        });
        return mainView;
    }
}