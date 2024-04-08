package com.example.studenthub.rides;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.Date;

public class Ride implements Parcelable {

    private String id;
    private String ownerId;
    private String pickupLocation;
    private String dropLocation;
    private Date dropTime;
    private Date pickupTime;
    private int noOfPassengers;
    private Date lastModified;
    private String type;

    // Constructor
    public Ride(String id, String ownerId, String pickupLocation, String dropLocation, Date dropTime, Date pickupTime, int noOfPassengers, Date lastModified, String type) {
        this.id = id;
        this.ownerId = ownerId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.dropTime = dropTime;
        this.pickupTime = pickupTime;
        this.noOfPassengers = noOfPassengers;
        this.lastModified = lastModified;
        this.type = type;
    }

    public Ride(){

    }

    protected Ride(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        pickupLocation = in.readString();
        dropLocation = in.readString();
        dropTime = new Date(in.readLong());
        pickupTime = new Date(in.readLong());
        noOfPassengers = in.readInt();
        lastModified = new Date(in.readLong());
        type = in.readString();
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Date getDropTime() {
        return dropTime;
    }

    public void setDropTime(Date dropTime) {
        this.dropTime = dropTime;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getNoOfPassengers() {
        return noOfPassengers;
    }

    public void setNoOfPassengers(int noOfPassengers) {
        this.noOfPassengers = noOfPassengers;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
// Getters and Setters (omitted for brevity)

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(pickupLocation);
        dest.writeString(dropLocation);
        dest.writeLong(dropTime != null ? dropTime.getTime() : -1);
        dest.writeLong(pickupTime != null ? pickupTime.getTime() : -1);
        dest.writeInt(noOfPassengers);
        dest.writeLong(lastModified != null ? lastModified.getTime() : -1);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
