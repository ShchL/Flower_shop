package it.mirea.kursovayaflowers.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int orderId;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "price")
    public String price;
    @ColumnInfo(name = "img")
    public byte[] image;

    public CartItem(String name, String price, byte[] image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
}
