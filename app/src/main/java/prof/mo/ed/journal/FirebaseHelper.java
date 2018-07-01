package prof.mo.ed.journal;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Prof-Mohamed Atef on 7/1/2018.
 * This class to initialize Firebase Database, and prevent exception which means " setPersistenceEnabled cannot be declared after any other thing"
 */

public class FirebaseHelper extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("data");
        databaseReference.keepSynced(true);
    }
}
