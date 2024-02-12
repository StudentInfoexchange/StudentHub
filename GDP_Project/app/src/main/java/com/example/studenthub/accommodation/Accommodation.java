package com.example.studenthub.accommodation;

import android.os.Parcel;
import android.os.Parcelable;

public class Accommodation implements Parcelable {
    private String id;
    private String location;
    private double price;
    private String name;
    private String imageUrl;
    private String roomType;
    private boolean available;
    private String description;

    public Accommodation() {
    }

    public Accommodation(String id, String location, double price, String name, String imageUrl,
                         String roomType, boolean available, String description) {
        this.id = id;
        this.location = location;
        this.price = price;
        this.name = name;
        this.imageUrl = imageUrl;
        this.roomType = roomType;
        this.available = available;
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected Accommodation(Parcel in) {
        id = in.readString();
        location = in.readString();
        price = in.readDouble();
        name = in.readString();
        imageUrl = in.readString();
        roomType = in.readString();
        available = in.readByte() != 0;
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(location);
        dest.writeDouble(price);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(roomType);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Accommodation> CREATOR = new Creator<Accommodation>() {
        @Override
        public Accommodation createFromParcel(Parcel in) {
            return new Accommodation(in);
        }

        @Override
        public Accommodation[] newArray(int size) {
            return new Accommodation[size];
        }
    };
}
