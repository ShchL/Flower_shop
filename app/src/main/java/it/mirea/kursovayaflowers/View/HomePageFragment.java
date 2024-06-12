package it.mirea.kursovayaflowers.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.models.User;

public class HomePageFragment extends Fragment {
    DatabaseReference users;//Колонка пользователей в бд
    FirebaseAuth auth;//авторизация
    FirebaseDatabase db;//база данных
    User user;
    TextView name, phone, email;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.address);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            //заполнение информации о пользователе, если вход через google
            String googleName = acct.getDisplayName();
            String googleEmail = acct.getEmail();
            email.setText(googleEmail);
            name.setText(googleName);
            phone.setVisibility(View.GONE);
        } else {
            //БД
            auth = FirebaseAuth.getInstance();
            db = FirebaseDatabase.getInstance();
            users = db.getReference("Users");
            //Инфа о юзере
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.child(auth.getCurrentUser().getUid()).getValue(User.class);
                    name.setText(user.getName());
                    phone.setText(user.getPhone());
                    email.setText(user.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        return view;
    }
}