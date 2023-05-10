package sg.edu.nus.iss.workshop37_server.models;

import java.util.Arrays;

public class Post {
    
    private String id;
    private String comment;
    private byte[] picture;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public byte[] getPicture() {
        return picture;
    }
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
    @Override
    public String toString() {
        return "Post [id=" + id + ", comment=" + comment + ", picture=" + Arrays.toString(picture) + "]";
    }

    

    
}
