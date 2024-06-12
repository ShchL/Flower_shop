package it.mirea.kursovayaflowers.room;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.mirea.kursovayaflowers.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList = new ArrayList<>();

    public void setOrderList(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderPlace, orderTotalPrice, orderItemsTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.order_date);
            orderPlace = itemView.findViewById(R.id.order_place);
            orderTotalPrice = itemView.findViewById(R.id.order_total_price);
            orderItemsTextView = itemView.findViewById(R.id.orderItemsTextView);
        }

        public void bind(Order order) {
            SpannableString spannableString = new SpannableString("Дата доставки: ".concat(order.date));
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Дата доставки:".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            orderDate.setText(spannableString);
            spannableString = new SpannableString("Адрес доставки: ".concat(order.place));
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Адрес доставки:".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            orderPlace.setText(spannableString);
            spannableString = new SpannableString("Итоговая стоимость: ".concat(String.valueOf(order.totalPrice)).concat(" руб."));
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Итоговая стоимость".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            orderTotalPrice.setText(spannableString);
            spannableString = new SpannableString(order.items);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Состав заказа:".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            orderItemsTextView.setText(spannableString);
        }
    }
}
