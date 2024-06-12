package it.mirea.kursovayaflowers.room;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartItemDao {
    @Query("SELECT * FROM CartItem")
    LiveData<List<CartItem>> getAllCartItems();

    @Insert
    void insertCartItem(CartItem cartItem);

    @Query("DELETE FROM cartitem")
    void deleteAll(); // Метод для удаления всех элементов из корзины
}
