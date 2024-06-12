package it.mirea.kursovayaflowers.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public String place;
    public int totalPrice;
    public String items;
    public Order() {
    }
    public Order(String date, String place, int totalPrice, String items) {
        this.date = date;
        this.place = place;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return place;
    }

    public void setAddress(String address) {
        this.place = address;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

}
