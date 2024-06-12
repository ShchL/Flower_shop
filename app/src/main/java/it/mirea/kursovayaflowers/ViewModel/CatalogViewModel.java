package it.mirea.kursovayaflowers.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import it.mirea.kursovayaflowers.room.AppDatabase;
import it.mirea.kursovayaflowers.room.Event;

public class CatalogViewModel extends AndroidViewModel {

    private LiveData<List<Event>> eventList;

    public CatalogViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDbInstance(application);
        eventList = db.eventDao().getAllEventsLiveData();
    }

    public LiveData<List<Event>> getEventList() {
        return eventList;
    }
}
