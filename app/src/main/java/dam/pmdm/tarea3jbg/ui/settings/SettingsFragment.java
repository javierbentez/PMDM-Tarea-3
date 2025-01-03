package dam.pmdm.tarea3jbg.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

import dam.pmdm.tarea3jbg.Login;
import dam.pmdm.tarea3jbg.R;

/**
 * Fragmento para la configuración de la aplicación.
 * Proporciona opciones para cambiar el idioma, eliminar Pokémon capturados y
 * cerrar sesión.
 * 
 * @autor Javier Benítez García
 */
public class SettingsFragment extends Fragment {

    private MaterialTextView textDeleteCaptured;
    private MaterialTextView textLanguage;
    private SharedPreferences sharedPreferences;
    private boolean isLanguageEnglish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflamos el layout de la pestaña de ajustes
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inicializamos los SharedPreferences para guardar las preferencias
        sharedPreferences = getActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);

        // Obtenemos las preferencias ya guardadas
        isLanguageEnglish = sharedPreferences.getBoolean("language_english", false);

        // Configuramos los ids de los elementos de la vista
        textDeleteCaptured = view.findViewById(R.id.textDeleteCaptured);
        textLanguage = view.findViewById(R.id.textLanguage);

        // Configuramos el cambio de idioma
        textLanguage.setOnClickListener(v -> {
            isLanguageEnglish = !isLanguageEnglish;
            sharedPreferences.edit().putBoolean("language_english", isLanguageEnglish).apply();
            changeLanguage(isLanguageEnglish);
        });

        // Listener para el switch de eliminación de pokemons capturados
        textDeleteCaptured.setOnClickListener(v -> {
            // Mensaje de confirmación
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.confirm_delete_pokemon_title)
                    .setMessage(R.string.confirm_delete_pokemon_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        // Lógica para eliminar los pokemons capturados de firestore
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String userEmail = auth.getCurrentUser().getEmail();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("users").document(userEmail).collection("captured_pokemon")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete();
                                        }
                                        Toast.makeText(getContext(), R.string.pokemon_deleted,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), R.string.pokemon_delete_error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });

        // Opción para cerrar sesión
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        // Opción para mostrar "Acerca de la App"
        view.findViewById(R.id.btnAbout).setOnClickListener(v -> {
            showAboutDialog();
        });

        return view;
    }

    private void changeLanguage(boolean isEnglish) {
        String languageCode = isEnglish ? "en" : "es";
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        requireActivity().recreate();
    }

    // Método para mostrar un dialogo de confirmación para cerrar sesión
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Lógica para cerrar sesión con Firebase
                    FirebaseAuth.getInstance().signOut();
                    // Redirigir al LoginActivity
                    Intent intent = new Intent(getContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // Método para mostrar un dialogo con información "Acerca de la App"
    private void showAboutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.about_app_title)
                .setMessage(R.string.about_app_message)
                .setPositiveButton(R.string.accept, null)
                .show();
    }
}