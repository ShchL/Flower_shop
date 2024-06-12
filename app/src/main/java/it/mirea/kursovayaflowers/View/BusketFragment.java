package it.mirea.kursovayaflowers.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.ViewModel.CartViewModel;

import it.mirea.kursovayaflowers.room.CartItemAdapter;

public class BusketFragment extends Fragment {

    private CartViewModel cartViewModel;
    private CartItemAdapter cartItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busket, container, false);

        initRecyclerView(view);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(),
                cartItems -> cartItemAdapter.setCartItemList(cartItems));

        Button btn = view.findViewById(R.id.btnBusket);
        btn.setOnClickListener(v -> {
            replaceFragment(new DeliveryFragment());
        });

        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBusket);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        cartItemAdapter = new CartItemAdapter(this.getActivity());
        recyclerView.setAdapter(cartItemAdapter);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}