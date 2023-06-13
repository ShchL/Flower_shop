package it.mirea.kursovayaflowers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister; //кнопка входа и регистрации
    FirebaseAuth auth;//авторизация
    FirebaseDatabase db;//база данных
    DatabaseReference users;//Колонка пользователей в бд

    RelativeLayout root;//главная активность

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btnSignIn1);
        btnRegister = findViewById(R.id.btnRegister1);

        root = findViewById(R.id.root_element1);
        //работа с FireBase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //кнопка регистрации
        btnRegister.setOnClickListener(view -> showRegistrationActivity());
        //Кнопка входа
        btnSignIn.setOnClickListener(view -> signIn());

        //вход с помощью google
        googleBtn = findViewById(R.id.google);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        googleBtn.setOnClickListener(view -> signInGoogle());
    }

    //Окно входа
    private void signIn() {
        final EditText email = root.findViewById(R.id.email_field);
        final EditText pass = root.findViewById(R.id.pass_field);
        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(root, "Введите почту", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (pass.getText().toString().length() < 6) {
            Snackbar.make(root, "Пароль слишком короткий", Snackbar.LENGTH_SHORT).show();
            return;
        }
        //Авторизация пользователя
        auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnSuccessListener(authResult -> {   //успешная авторизация
                    finish();
                    startActivity(new Intent(this, MainActivity.class));
                })
                .addOnFailureListener(e -> //не успешная авторизация
                        Snackbar.make(root, "Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show());
    }

    //окно авторизации google
    void signInGoogle() {
        Intent signInGoogleIntent = gsc.getSignInIntent();
        startActivityForResult(signInGoogleIntent, 1000);
    }

    //попытка войти в аккаунт
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //переход в окно пользователя после успешного входа
    void navigateToSecondActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    //Окно регистрации
    private void showRegistrationActivity() {
        finish();
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}