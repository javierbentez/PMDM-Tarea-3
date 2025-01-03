package dam.pmdm.tarea3jbg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad para el registro de usuarios.
 * Proporciona la interfaz y funcionalidad para registrar un nuevo usuario utilizando Firebase Authentication.
 * 
 * @author Javier Benítez García
 */
public class Registro extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializamos Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los campos de entrada
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.btnRegister);

        registerButton.setOnClickListener(v -> registerUser());
    }

    /**
     * Registra un nuevo usuario con el correo electrónico y la contraseña proporcionados.
     * Muestra mensajes de éxito o error según el resultado del registro.
     */
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_both_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registro.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                        finish();  // Volvemos a LoginActivity
                    } else {
                        Toast.makeText(Registro.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}