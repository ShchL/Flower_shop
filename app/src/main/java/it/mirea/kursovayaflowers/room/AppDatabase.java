package it.mirea.kursovayaflowers.room;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import it.mirea.kursovayaflowers.R;

@Database(entities = {Event.class, CartItem.class, Order.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    public abstract CartItemDao cartItemDao();
    public abstract OrderDao orderDao();
    private static AppDatabase INSTANCE;

    public static ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);
    public static AppDatabase getDbInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Event")
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                fillInitialData(context);
                            });
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    private static void fillInitialData(Context context) {
        AppDatabase database = getDbInstance(context);
        EventDao eventDao = database.eventDao();

        // Добавление начальных данных
        eventDao.insertEvent(new Event("Фикус лирата", "2205", getImageAsByteArray(context, R.drawable.f_lyrata)));
        eventDao.insertEvent(new Event("Фикус эластика", "4200", getImageAsByteArray(context, R.drawable.f_elastica)));
        eventDao.insertEvent(new Event("Фикус Бенджамина", "1000", getImageAsByteArray(context, R.drawable.f_benjamin)));
        eventDao.insertEvent(new Event("Крассула", "800", getImageAsByteArray(context, R.drawable.f_krassula)));
        eventDao.insertEvent(new Event("Эхеверия", "200", getImageAsByteArray(context, R.drawable.f_eheveria)));
        eventDao.insertEvent(new Event("Кашпо - диаметр 13.5, высота 13 см", "1000", getImageAsByteArray(context, R.drawable.f_pot)));
    }

    private static byte[] getImageAsByteArray(Context context, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
