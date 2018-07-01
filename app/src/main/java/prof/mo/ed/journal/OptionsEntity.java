package prof.mo.ed.journal;

import java.io.Serializable;

/**
 * Created by Prof-Mohamed Atef on 6/26/2018.
 * This is A class model for data being handled
 */

public class OptionsEntity implements Serializable{

    public static Object FBAccessToken;

    public OptionsEntity(){

    }
    public OptionsEntity(String Thought, String Status){
        this.Thought_Str=Thought;
        this.Status_Str=Status;
    }

    String ThoughtDate,Thought_Str, Status_ImgUrl, Status_Str;

    public OptionsEntity(String s, String thoughts_str, String status_imageUrl, String status_str, String loggedEmail) {
        this.ThoughtDate=s;
        this.Thought_Str=thoughts_str;
        this.Status_ImgUrl=status_imageUrl;
        this.Status_Str=status_str;
        this.Email=loggedEmail;
    }

    String Key;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public OptionsEntity(String key2, String email, String status_imgUrl, String status_str, String thoughtDate, String thought_str) {
        this.Key=key2;
        this.Email=email;
        this.Status_ImgUrl=status_imgUrl;
        this.Status_Str=status_str;
        this.ThoughtDate=thoughtDate;
        this.Thought_Str=thought_str;
    }

    public String getThoughtDate() {
        return ThoughtDate;
    }

    public void setThoughtDate(String thoughtDate) {
        ThoughtDate = thoughtDate;
    }

    public String getThought_Str() {
        return Thought_Str;
    }

    public void setThought_Str(String thought_Str) {
        Thought_Str = thought_Str;
    }

    public String getStatus_ImgUrl() {
        return Status_ImgUrl;
    }

    public void setStatus_ImgUrl(String status_ImgUrl) {
        Status_ImgUrl = status_ImgUrl;
    }

    public String getStatus_Str() {
        return Status_Str;
    }

    public void setStatus_Str(String status_Str) {
        Status_Str = status_Str;
    }

    /*
        User Details
         */
    String UserName;
    String Email;
    String PhotoUrl;
    String TokenID;
    public static String imgID = null;
    public static String Thoughts = null;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    String ID;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getTokenID() {
        return TokenID;
    }

    public void setTokenID(String tokenID) {
        TokenID = tokenID;
    }
}
