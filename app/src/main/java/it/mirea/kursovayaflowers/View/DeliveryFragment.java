package it.mirea.kursovayaflowers.View;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.ViewModel.OrderViewModel;
import it.mirea.kursovayaflowers.addressLogic.AddressAnalysis;
import it.mirea.kursovayaflowers.room.CartItem;
import it.mirea.kursovayaflowers.room.Order;


public class DeliveryFragment extends Fragment {

    public DeliveryFragment() {}

    private AddressAnalysis mAnalysis;
    private OrderViewModel orderViewModel;
    private TextView fullPrice, orderItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        final EditText dateInput = view.findViewById(R.id.date);
        final AutoCompleteTextView placeInput = view.findViewById(R.id.place);
        fullPrice = view.findViewById(R.id.full_price);
        orderItems = view.findViewById(R.id.items);
        final Button payButton  = view.findViewById(R.id.pay);

        if (mAnalysis == null) {
            mAnalysis = new AddressAnalysis();
        }
        placeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAnalysis.getAddressesFromPattern(s.toString()).observe(getViewLifecycleOwner(),
                        (List<String> values) -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    getContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    values
                            );
                            adapter.getFilter().filter(null);
                            placeInput.setAdapter(adapter);
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            int total = 0;

            StringBuilder items = new StringBuilder();
            items.append("Состав заказа:\n");
            for (CartItem item : cartItems) {
                total += Integer.parseInt(item.price); // Предполагается, что price - это строка с числовым значением
                items.append(item.name).append(" 1шт - ").append(item.price).append(" руб.\n");
            }
            SpannableString spannableString = new SpannableString(items.toString());
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Состав заказа:".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            orderItems.setText(spannableString);
            String price = "Итоговая стоимость: ".concat(Integer.toString(total)).concat(" руб.");
            spannableString = new SpannableString(price);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, "Итоговая стоимость:".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fullPrice.setText(spannableString);

        });

        payButton.setOnClickListener(v -> {
            delivery(view);
            // Сохранение заказа (например, через метод в OrderViewModel)
            saveOrder(dateInput.getText().toString(),
                    placeInput.getText().toString(),
                    Integer.parseInt(fullPrice.getText().toString().replaceAll("[^0-9]", "")),
                    orderItems.getText().toString());
            orderViewModel.clearCart();
            Snackbar snackbar = Snackbar.make(view, "Заказ оформлен!", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbarView.setLayoutParams(params);
            snackbar.show();
            replaceFragment(new OrdersFragment());
        });

        return view;
    }

    private void delivery(View view) {
        Snackbar.make(view, "Оплата СБП здесь", Snackbar.LENGTH_SHORT).show();
    }

    private void saveOrder(String date, String place, int totalPrice, String items) {
        Order newOrder = new Order(date, place, totalPrice, items);

        // Получение экземпляра OrderViewModel
        OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Вставка заказа в базу данных через OrderViewModel
        orderViewModel.insertOrder(newOrder);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}