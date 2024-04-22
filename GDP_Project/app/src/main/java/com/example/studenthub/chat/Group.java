package com.example.studenthub.chat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class Group implements Parcelable {
    private List<String> members;
    private Date createdAt;
    private String id;
    private String itemId;
    private String regarding;
    private Date updatedAt;
    private String recentMsg;

    public Group() {
    }
    // Constructor
    public Group(List<String> members, String id, String itemId, String regarding) {
        this.members = members;
        this.id = id;
        this.itemId = itemId;
        this.regarding = regarding;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.recentMsg = "";
    }
    protected Group(Parcel in) {
        members = in.createStringArrayList();
        id = in.readString();
        itemId = in.readString();
        regarding = in.readString();
        recentMsg = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // Getters and setters
    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRegarding() {
        return regarding;
    }

    public void setRegarding(String regarding) {
        this.regarding = regarding;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRecentMsg() {
        return recentMsg;
    }

    public void setRecentMsg(String recentMsg) {
        this.recentMsg = recentMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringList(members);
        dest.writeString(id);
        dest.writeString(itemId);
        dest.writeString(regarding);
        dest.writeString(recentMsg);
    }
}
