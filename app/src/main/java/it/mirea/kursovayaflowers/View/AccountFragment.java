package it.mirea.kursovayaflowers.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.mirea.kursovayaflowers.R;
import it.mirea.kursovayaflowers.models.User;

public class AccountFragment extends Fragment {
    DatabaseReference users;//Колонка пользователей в бд
    FirebaseAuth auth;//авторизация
    FirebaseDatabase db;//база данных
    User user;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Button btnHomePage, btnOrders, btnContacts, btnSpecial,
                btnExit, btnFlorist, btnAdmin;

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        btnHomePage = view.findViewById(R.id.btnHomePage);
        btnOrders = view.findViewById(R.id.btnOrders);
        btnFlorist = view.findViewById(R.id.btnFlorist);
        btnContacts = view.findViewById(R.id.btnContacts);
        btnSpecial = view.findViewById(R.id.btnSpecial);
        btnAdmin = view.findViewById(R.id.btnAdmin);
        btnExit = view.findViewById(R.id.btnExit);


        //настройка входа в google для получения данных пользователя с помощью параметра requestEmail
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            //пользователь вошёл через google
            btnFlorist.setVisibility(View.GONE);
            btnAdmin.setVisibility(View.GONE);
        } else {
            //получение зашедшего пользователя из бд
            auth = FirebaseAuth.getInstance();
            db = FirebaseDatabase.getInstance();
            users = db.getReference("Users");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                // считывание данных из бд (поиск аккаунта)
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = new User();
                    user = snapshot.child(auth.getCurrentUser().getUid()).getValue(User.class);
                    if (user.isUser()) {
                        btnFlorist.setVisibility(View.GONE);
                        btnAdmin.setVisibility(View.GONE);
                    } else if (user.isFlorist()) {
                        btnFlorist.setVisibility(View.VISIBLE);
                        btnAdmin.setVisibility(View.GONE);
                    } else {
                        btnFlorist.setVisibility(View.VISIBLE);
                        btnAdmin.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //Переход в другие фрагменты
        btnHomePage.setOnClickListener(v -> replaceFragment(new HomePageFragment()));
        btnOrders.setOnClickListener(v -> replaceFragment(new OrdersFragment()));
        btnContacts.setOnClickListener(v -> replaceFragment(new ContactsFragment()));
        btnFlorist.setOnClickListener(v -> {
            HomeDialogFragment dialogFragment = HomeDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "HomeDialogFragment");
        });
        //Выход
        btnExit.setOnClickListener(v -> {

            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }
            });
        });
        //Переход в окошки
        //btnFlorist.setOnClickListener(v -> showFloristWindow());
        btnSpecial.setOnClickListener(v -> showSpecialWindow());
        btnAdmin.setOnClickListener(v -> showAdminWindow());

        return view;
    }

    void showSpecialWindow() {
        //Окошко
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View window_special = inflater.inflate(R.layout.window_special, null);
        View view = getActivity().findViewById(R.id.fragment_container);
        dialog.setView(window_special);

        //База данных
        DatabaseReference adminCodes;
        DatabaseReference floristCodes;
        db = FirebaseDatabase.getInstance();
        adminCodes = db.getReference("Codes/AdminCodes");
        floristCodes = db.getReference("Codes/FloristCodes");

        final EditText id = window_special.findViewById(R.id.special_field);
        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Отправить", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(id.getText().toString())) {
                Snackbar snackbar = Snackbar.make(view, "Введите код", Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP;
                snackbarView.setLayoutParams(params);
                snackbar.show();
                return;
            }
            //Ищем коды админа
            adminCodes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(id.getText().toString()).getValue() != null) {
                        user.setAdmin();
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(unused -> {
                                    Snackbar snackbar = Snackbar.make(view, "Права админа получены!", Snackbar.LENGTH_SHORT);
                                    View snackbarView = snackbar.getView();
                                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    snackbarView.setLayoutParams(params);
                                    snackbar.show();
                                });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            //Ищем коды флориста
            floristCodes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(id.getText().toString()).getValue() != null) {
                        if (user.isAdmin()) {
                            Snackbar snackbar = Snackbar.make(view, "Пользователь уже админ!", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            snackbarView.setLayoutParams(params);
                            snackbar.show();
                            return;
                        }
                        user.setFlorist();
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(unused -> {
                                    Snackbar snackbar = Snackbar.make(view, "Права флориста получены!", Snackbar.LENGTH_SHORT);
                                    View snackbarView = snackbar.getView();
                                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    snackbarView.setLayoutParams(params);
                                    snackbar.show();
                                    });
                    } else {
                        Snackbar snackbar = Snackbar.make(view, "Код неверный!", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        snackbarView.setLayoutParams(params);
                        snackbar.show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });
        dialog.show();
    }

    void showAdminWindow() {
        //Окошко
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View window_admin = inflater.inflate(R.layout.window_admin, null);
        View view = getActivity().findViewById(R.id.fragment_container);
        dialog.setView(window_admin);

        //База данных
        DatabaseReference adminCodes;
        DatabaseReference floristCodes;
        db = FirebaseDatabase.getInstance();
        adminCodes = db.getReference("Codes/AdminCodes");
        floristCodes = db.getReference("Codes/FloristCodes");

        final EditText admin = window_admin.findViewById(R.id.admin_field);
        final EditText florist = window_admin.findViewById(R.id.florist_field);
        dialog.setNegativeButton("Отменить", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Добавить", (dialogInterface, i) -> {
            String adminCode = admin.getText().toString();
            String floristCode = florist.getText().toString();
            if (TextUtils.isEmpty(adminCode) && TextUtils.isEmpty(florist.getText().toString())) {
                Snackbar.make(view, "Введите код", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(adminCode))
                adminCodes.child(adminCode)
                        .setValue(adminCode)
                        .addOnSuccessListener(unused -> {
                            Snackbar snackbar = Snackbar.make(view, "Код админа добавлен!", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            snackbarView.setLayoutParams(params);
                            snackbar.show();
                        }
                        );
            if (!TextUtils.isEmpty(floristCode))
                floristCodes.child(floristCode)
                        .setValue(floristCode)
                        .addOnSuccessListener(unused -> {
                            Snackbar snackbar = Snackbar.make(view, "Код флориста добавлен!", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            snackbarView.setLayoutParams(params);
                            snackbar.show();
                        });
        });
        dialog.show();
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}