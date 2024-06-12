package it.mirea.kursovayaflowers.ViewModel;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.mirea.kursovayaflowers.room.AppDatabase;
import it.mirea.kursovayaflowers.room.CartItem;
import it.mirea.kursovayaflowers.room.CartItemDao;

public class CartViewModel extends AndroidViewModel {
    private CartItemDao cartItemDao;
    private LiveData<List<CartItem>> cartItems;

    public CartViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDbInstance(application);
        cartItemDao = db.cartItemDao();
        cartItems = cartItemDao.getAllCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void insertCartItem(CartItem cartItem) {
        Log.d("CartViewModel", "Inserting item: " + cartItem.name); // Добавьте эту строку
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.insertCartItem(cartItem));
    }
    public void clearCart() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartItemDao.deleteAll();
        });
    }
}
