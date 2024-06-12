package it.mirea.kursovayaflowers.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface OrderDao {
    @Transaction
    @Query("SELECT * FROM `Order`")
    LiveData<List<Order>> getAllOrders();

    @Insert
    void insertOrder(Order order);
    @Insert
    void insertCartItems(List<CartItem> cartItems);

}
