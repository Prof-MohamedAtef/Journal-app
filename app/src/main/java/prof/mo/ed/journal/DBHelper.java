package prof.mo.ed.journal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by Prof-Mohamed on 6/27/2018.
 * this class is to create sqlite database, work with tables, insert and update Thoughts_TBL
 * which is concerned with thoughts and feelings to be entered and displayed
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Journal.db";
    public static final String Thoughts_TBL="Thoughts_TBL";

    //// Columns of Posts Table
    private static final String Thoughts_Key= "Thoughts_Key";
    private static final String Thoughts_Date= "Thoughts_Date";
    private static final String Thoughts_Str= "Thoughts_Str";
    private static final String Status_ImgUrl= "Status_ImgUrl";
    private static final String Status_Str= "Status_Str";
    private static final String Email_Str= "Email_Str";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null , 2);
        SQLiteDatabase Db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table "+Thoughts_TBL+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, Thoughts_Key TEXT, Thoughts_Date TEXT, Thoughts_Str TEXT, Status_ImgUrl TEXT, Status_Str TEXT, Email_Str TEXT) ");
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Thoughts_TBL);
        onCreate(db);
    }

    public boolean InsertToDiary(String Key,String date, String Thoughts, String Status_ImgUrl, String Status_Str, String Email) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues content_values=new ContentValues();
        content_values.put(this.Thoughts_Key,Key);
        content_values.put(this.Thoughts_Date,date);
        content_values.put(this.Thoughts_Str,Thoughts);
        content_values.put(this.Status_ImgUrl,Status_ImgUrl);
        content_values.put(this.Status_Str, Status_Str);
        content_values.put(this.Email_Str, Email);
        long result= db.insert(Thoughts_TBL,null,content_values);
        if (result==-1){
            return false;
        }else
            return true;
    }

    public ArrayList<OptionsEntity> selectAllDiaryData(String loggedEmail) {
        SQLiteDatabase db=this.getWritableDatabase();
        ArrayList PostsArray=new ArrayList<OptionsEntity>();
        String[] projection = {
                Email_Str,
                Thoughts_Key,
                Thoughts_Str,
                Thoughts_Date,
                Status_ImgUrl,
                Status_Str
        };
        String selection =  Email_Str + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = loggedEmail;
        Cursor res = db.query(
                Thoughts_TBL,                             // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                "ID DESC"                                 // The sort order
        );

        try{
            if (res.moveToNext()){
                do {
                    OptionsEntity optionsEntity=new OptionsEntity();
                    optionsEntity.setKey(res.getString(res.getColumnIndex(Thoughts_Key)));
                    optionsEntity.setThoughtDate(res.getString(res.getColumnIndex(Thoughts_Date)));
                    optionsEntity.setThought_Str(res.getString(res.getColumnIndex(Thoughts_Str)));
                    optionsEntity.setStatus_ImgUrl(res.getString(res.getColumnIndex(Status_ImgUrl)));
                    optionsEntity.setStatus_Str(res.getString(res.getColumnIndex(Status_Str)));
                    optionsEntity.setEmail(res.getString(res.getColumnIndex(Email_Str)));
                    PostsArray.add(optionsEntity);
                } while (res.moveToNext());
            }
            res.close();
            return  PostsArray;
        } catch (Exception e) {
            return PostsArray;
        }
    }

    public boolean UpdateToDiary(String ThoughtsQuery, String Key,String currentDateTimeString, String thoughts_str, String status_imageUrl, String status_str, String Email) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues content_value=new ContentValues();
        content_value.put(this.Thoughts_Key,Key);
        content_value.put(this.Thoughts_Date,currentDateTimeString);
        content_value.put(this.Thoughts_Str,thoughts_str);
        content_value.put(this.Status_ImgUrl, status_imageUrl);
        content_value.put(this.Status_Str, status_str);
        content_value.put(this.Email_Str, Email);

        String whereClause =  Email_Str + "=? and "+Thoughts_Str+" =?";
        String[] whereArgs = new String[2];
        whereArgs[0] = Email;
        whereArgs[1] = ThoughtsQuery;
        db.update(
                Thoughts_TBL,  // The table to query
                content_value,                               // The columns to return
                whereClause,                                // The columns for the WHERE clause
                whereArgs                                            // The sort order
        );

        return true;
    }

    public int getInitialMaxValue() {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * from "+Thoughts_TBL,null);
        int maxID=0;
        try{
            if (res.moveToNext()){
                do {
                    OptionsEntity optionsEntity=new OptionsEntity();
                    optionsEntity.setID(res.getString(res.getColumnIndex("ID")));
                    maxID=Integer.parseInt(optionsEntity.getID());
                } while (res.moveToNext());
            }
            return maxID;
        }catch (Exception e){
            return 0;
        }
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows= db.delete(Thoughts_TBL,null, null);
        if (deletedRows==0){
            return false;
        }else {
            return true;
        }
    }

    public String FindFirebaseKey(String thoughts) {
        SQLiteDatabase db=this.getWritableDatabase();
        ArrayList PostsArray=new ArrayList<OptionsEntity>();
        String[] projection = {
                Thoughts_Key
        };
        String selection =  Thoughts_Str + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = thoughts;
        Cursor res = db.query(
                Thoughts_TBL,                             // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                               // The sort order
        );

        String Key=null;
        try{
            if (res.moveToNext()){
                do {
                    OptionsEntity optionsEntity=new OptionsEntity();
                    Key=res.getString(res.getColumnIndex(Thoughts_Key));
                } while (res.moveToNext());
            }
            res.close();
            return Key;
        } catch (Exception e) {
            return Key;
        }
    }
}