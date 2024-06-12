package it.mirea.kursovayaflowers.View;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.ViewModel.OrderViewModel;
import it.mirea.kursovayaflowers.room.OrderAdapter;

public class OrdersFragment extends Fragment {

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.devider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        final OrderAdapter orderAdapter = new OrderAdapter();
        recyclerView.setAdapter(orderAdapter);


        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> orderAdapter.setOrderList(orders));

        return view;
    }
}