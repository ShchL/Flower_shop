package it.mirea.kursovayaflowers.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Event {
    public Event() {}
    public Event(String name, String price, byte[] img) {
        this.date = name;
        this.description = price;
        this.imgBlob = img;
    }
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "img")
    public byte[] imgBlob;
}
