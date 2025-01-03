package dam.pmdm.tarea3jbg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Actividad para el inicio de sesión de usuarios.
 * Proporciona la interfaz y funcionalidad para iniciar sesión con correo
 * electrónico y Google Sign-In utilizando Firebase Authentication.
 * 
 * @author Javier Benítez García
 */
public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, googleSignInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializamos Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los campos de entrada
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);
        googleSignInButton = findViewById(R.id.btnGoogleSignIn);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> openRegisterActivity());
        googleSignInButton.setOnClickListener(v -> googleSignIn());

        if (mAuth.getCurrentUser() != null) {
            openMainActivity();
        }
    }

    /**
     * Inicia sesión con el correo electrónico y la contraseña proporcionados.
     * Muestra mensajes de éxito o error según el resultado del inicio de sesión.
     */
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_both_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        openMainActivity();
                    } else {
                        Toast.makeText(Login.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Abre la actividad de registro.
     */
    private void openRegisterActivity() {
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

    /**
     * Abre la actividad principal.
     */
    private void openMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Inicia el flujo de inicio de sesión de Google.
     */
    private void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 500) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("Login", "Google sign-in failed", e);
            }
        }
    }

    /**
     * Autentica con Firebase usando la cuenta de Google.
     * 
     * @param acct La cuenta de Google.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Login", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(Login.this, getString(R.string.welcome) + " " + user.getDisplayName(),
                                Toast.LENGTH_SHORT).show();
                        openMainActivity();
                    } else {
                        Log.w("Login", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}