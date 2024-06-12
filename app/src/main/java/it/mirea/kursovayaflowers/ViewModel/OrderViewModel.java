package it.mirea.kursovayaflowers.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.mirea.kursovayaflowers.room.AppDatabase;
import it.mirea.kursovayaflowers.room.CartItem;
import it.mirea.kursovayaflowers.room.CartItemDao;
import it.mirea.kursovayaflowers.room.Order;
import it.mirea.kursovayaflowers.room.OrderDao;

public class OrderViewModel extends AndroidViewModel {
    private CartItemDao cartItemDao;
    private LiveData<List<CartItem>> cartItems;
    private OrderDao orderDao;
    private LiveData<List<Order>> orders;

    public OrderViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDbInstance(application);
        cartItemDao = db.cartItemDao();
        cartItems = cartItemDao.getAllCartItems();
        orderDao = db.orderDao();
        orders = orderDao.getAllOrders();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }
    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public void insertOrder(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.insertOrder(order);
        });
    }

    public void clearCart() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartItemDao.deleteAll();
        });
    }
    public void insertCartItems(List<CartItem> cartItems) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.insertCartItems(cartItems);
        });
    }

}
