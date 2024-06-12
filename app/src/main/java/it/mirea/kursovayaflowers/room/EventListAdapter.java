package it.mirea.kursovayaflowers.room;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.mirea.kursovayaflowers.R;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnItemClickListener listener;
    private boolean isUser = true;
    public EventListAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public void setUserRole(boolean isUser) {
        this.isUser = isUser;
        notifyDataSetChanged(); // Обновляем список для отображения изменений в видимости кнопки
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, listener);

        // Устанавливаем видимость кнопки в зависимости от роли пользователя
        if (isUser) {
            holder.removeButton.setVisibility(View.GONE);
        } else {
            holder.removeButton.setVisibility(View.VISIBLE);
        }

        holder.removeButton.setOnClickListener(v -> {
            // Выполняем операцию в фоновом потоке
            AppDatabase.databaseWriteExecutor.execute(() -> {
                AppDatabase db = AppDatabase.getDbInstance(context);
                db.eventDao().delete(event);
            });
            eventList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, eventList.size());
        });
    }

    @Override
    public int getItemCount() {
        return eventList == null ? 0 : eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        ImageView imageView;
        ImageButton removeButton;

        public EventViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.event_name);
            priceTextView = itemView.findViewById(R.id.event_price);
            imageView = itemView.findViewById(R.id.event_image);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public void bind(Event event, OnItemClickListener listener) {
            nameTextView.setText(event.date);
            priceTextView.setText(event.description.concat(" руб."));
            if (event.imgBlob != null && event.imgBlob.length > 0) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(event.imgBlob, 0, event.imgBlob.length));
            }
            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }
}
