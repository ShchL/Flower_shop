package it.mirea.kursovayaflowers.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.ViewModel.CartViewModel;
import it.mirea.kursovayaflowers.ViewModel.CatalogViewModel;
import it.mirea.kursovayaflowers.models.User;
import it.mirea.kursovayaflowers.room.CartItem;
import it.mirea.kursovayaflowers.room.EventListAdapter;


public class CatalogFragment extends Fragment {

    private EventListAdapter eventListAdapter;
    private CatalogViewModel catalogViewModel;
    private CartViewModel cartViewModel;

    private FirebaseAuth auth;
    private DatabaseReference users;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        initRecyclerView(view);
        catalogViewModel = new ViewModelProvider(this).get(CatalogViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        catalogViewModel.getEventList().observe(getViewLifecycleOwner(), events ->
                eventListAdapter.setEventList(events));
        eventListAdapter.setOnItemClickListener(event -> {
            CartItem cartItem = new CartItem(event.date, event.description, event.imgBlob);
            cartViewModel.insertCartItem(cartItem);
            Snackbar snackbar = Snackbar.make(view, "Товар добавлен в корзину", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbarView.setLayoutParams(params);
            snackbar.show();
        });

        // Инициализация Firebase Auth и Database
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        getUserRoleAndSetAdapter();

        return view;
    }
    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        eventListAdapter = new EventListAdapter(this.getActivity());
        recyclerView.setAdapter(eventListAdapter);
    }

    private void getUserRoleAndSetAdapter() {
        String userId = auth.getCurrentUser().getUid();
        users.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    boolean isUser = user.isUser();
                    eventListAdapter.setUserRole(isUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}


