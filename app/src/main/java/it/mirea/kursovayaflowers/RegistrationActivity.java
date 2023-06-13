package it.mirea.kursovayaflowers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.mirea.kursovayaflowers.models.User;

public class RegistrationActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister; //кнопка входа и регистрации
    FirebaseAuth auth;//авторизация
    FirebaseDatabase db;//база данных
    DatabaseReference users;//Колонка пользователей в бд

    RelativeLayout root;//главная активность

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnSignIn = findViewById(R.id.btnSignIn2);
        btnRegister = findViewById(R.id.btnRegister2);

        root = findViewById(R.id.root_element2);
        //работа с FireBase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //кнопка регистрации
        btnRegister.setOnClickListener(view -> Registration());
        //Кнопка входа
        btnSignIn.setOnClickListener(view -> showSignInActivity());
    }

    private void showSignInActivity() {
        finish();
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void Registration() {
        final EditText email = root.findViewById(R.id.email_field);
        final EditText pass = root.findViewById(R.id.pass_field);
        final EditText name = root.findViewById(R.id.name_field);
        final EditText phone = root.findViewById(R.id.phone_field);

        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(root, "Введите почту", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (pass.getText().toString().length() < 5) {
            Snackbar.make(root, "Пароль слишком короткий", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name.getText().toString())) {
            Snackbar.make(root, "Введите имя", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Snackbar.make(root, "Введите телефон", Snackbar.LENGTH_SHORT).show();
            return;
        }

        //Регистрация пользователя
        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnSuccessListener(authResult -> { //успешная регистрация
                    User user = new User();
                    user.setEmail(email.getText().toString());
                    user.setPass(pass.getText().toString());
                    user.setName(name.getText().toString());
                    user.setPhone(phone.getText().toString());
                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user)
                            .addOnSuccessListener(unused -> {
                                        Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(this, MainActivity.class));
                                    }
                            );
                })
                .addOnFailureListener(e ->  //не успешная регистрация
                        Snackbar.make(root, "Ошибка регистрации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show());
    }
}
