package com.keepcalm.blomo;


import java.util.Date;
import com.google.firebase.firestore.ServerTimestamp;

/**
 * Created by Keep Calm on 3/26/2018.
 */
//Modal class to BlogPostID to receive post unique id
public class BlogPost extends BlogPostId {

    public String user_id, image_url, title_txt, message,image_thumb, like_text, comment_text;

    public Date timestamp;



    public BlogPost(){}

    public BlogPost(String user_id, String image_url, String title_txt, String message, String image_thumb,Date timestamp, String like_text, String comment_text) {

        this.user_id = user_id;
        this.image_url = image_url;
        this.title_txt = title_txt;
        this.message = message;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.like_text=like_text;
        this.comment_text = comment_text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle_txt() {
        return title_txt;
    }

    public void setTitle_txt(String title_txt) {
        this.title_txt = title_txt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }

    public String getLike_text() {
        return like_text;
    }

    public void setLike_text(String like_text) {
        this.like_text = like_text;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

}
