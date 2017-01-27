package org.tndata.officehours.model;


import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * Model representing a user other than whoever is using the app
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Person extends Base{
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;

    @ColorInt
    private int color;

    private boolean isInstructor;


    /**
     * Constructor.
     *
     * @param id the id of the person.
     * @param name the name of the person.
     * @param avatar the photo url of the person.
     * @param isInstructor whether the person is an instructor.
     */
    public Person(long id, @NonNull String name, @NonNull String avatar, boolean isInstructor){
        super(id);
        this.name = name;
        this.avatar = avatar;
        this.isInstructor = isInstructor;
    }

    /**
     * Name getter.
     *
     * @return the name of the person.
     */
    public String getName(){
        return name;
    }

    /**
     * Avatar getter.
     *
     * @return the avatar of the person.
     */
    public String getAvatar(){
        return avatar;
    }

    @ColorInt
    public int getColor(){
        return color;
    }

    public void setColor(@ColorInt int color){
        this.color = color;
    }

    /**
     * Tells whether the person is an instructor.
     *
     * @return true if instructor, false if student.
     */
    public boolean isInstructor(){
        return isInstructor;
    }

    public void asInstructor(){
        isInstructor = true;
    }

    public void asStudent(){
        isInstructor = false;
    }

    @Override
    public String toString(){
        String result = "Person #" + getId() + ": " + getName();
        if (isInstructor()){
            result += " (instructor)";
        }
        return result;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Person && ((Person)o).getId() == getId();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags){
        super.writeToParcel(parcel, flags);
        parcel.writeString(name);
        parcel.writeString(avatar);
        parcel.writeInt(color);
        parcel.writeByte((byte)(isInstructor ? 1 : 0));
    }

    public static final Creator<Person> CREATOR = new Creator<Person>(){
        @Override
        public Person createFromParcel(Parcel in){
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size){
            return new Person[size];
        }
    };

    private Person(Parcel in){
        super(in);
        name = in.readString();
        avatar = in.readString();
        color = in.readInt();
        isInstructor = in.readByte() == 1;
    }
}