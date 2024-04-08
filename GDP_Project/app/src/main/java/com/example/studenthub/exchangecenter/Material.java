package com.example.studenthub.material_exchange;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Material implements Parcelable {
    private String id;
    private double price;
    private String type;
    private String subCategory;
    private String studentId;
    private String title;

    protected Material(Parcel in) {
        id = in.readString();
        price = in.readDouble();
        type = in.readString();
        subCategory = in.readString();
        studentId = in.readString();
        title = in.readString();
        contact = in.readString();
        imageUrl = in.readString();
    }

    public Material() {
    }

    public static final Creator<Material> CREATOR = new Creator<Material>() {
        @Override
        public Material createFromParcel(Parcel in) {
            return new Material(in);
        }

        @Override
        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String contact;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(price);
        dest.writeString(type);
        dest.writeString(subCategory);
        dest.writeString(studentId);
        dest.writeString(title);
        dest.writeString(contact);
        dest.writeString(imageUrl);
    }
}
