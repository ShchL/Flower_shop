package it.mirea.kursovayaflowers.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event")
    List<Event> getAllEvents();

    @Query("SELECT * FROM Event")
    LiveData<List<Event>> getAllEventsLiveData();
    @Insert
    void insertEvent(Event... event);

    @Delete
    void delete(Event event);

}
