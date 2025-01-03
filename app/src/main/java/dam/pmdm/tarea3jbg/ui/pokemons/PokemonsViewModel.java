package dam.pmdm.tarea3jbg.ui.pokemons;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel para la gestión de Pokémon.
 * Proporciona datos y métodos para obtener y gestionar la lista de Pokémon
 * capturados.
 * 
 * @author Javier Benítez García
 */
public class PokemonsViewModel extends ViewModel {

    private final MutableLiveData<List<Pokemon>> pokemonsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> capturedPokemonNamesLiveData = new MutableLiveData<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    /**
     * Obtiene la lista de Pokémon capturados.
     * 
     * @return La lista de Pokémon capturados.
     */
    public LiveData<List<Pokemon>> getPokemons() {
        return pokemonsLiveData;
    }

    /**
     * Obtiene la lista de nombres de Pokémon capturados.
     * 
     * @return La lista de nombres de Pokémon capturados.
     */
    public LiveData<List<String>> getCapturedPokemonNames() {
        return capturedPokemonNamesLiveData;
    }

    /**
     * Recupera la lista de Pokémon capturados desde Firestore.
     */
    public void fetchPokemons() {
        List<Pokemon> pokemons = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("users").document(userEmail).collection("captured_pokemon").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Pokemon pokemon;
                    List<String> types = new ArrayList<>();
                    List<Map<String, Object>> typesList = (List<Map<String, Object>>) document.get("types");
                    if (typesList != null) {
                        for (Map<String, Object> typeEntry : typesList) {
                            Map<String, Object> typeMap = (Map<String, Object>) typeEntry.get("type");
                            if (typeMap != null) {
                                String typeName = (String) typeMap.get("name");
                                if (typeName != null) {
                                    types.add(StringUtils.capitalize(typeName));
                                }
                            }
                        }
                    }
                    String nameMayuscula = StringUtils.capitalize(document.getString("name"));
                    pokemon = new Pokemon(
                            nameMayuscula,
                            document.getLong("id").intValue(),
                            document.getString("image"),
                            types,
                            document.getDouble("weight").floatValue(),
                            document.getDouble("height").floatValue());
                    pokemons.add(pokemon);
                }
                pokemonsLiveData.setValue(pokemons);
            } else {
                Log.e("PokemonsViewModel", "Error obteniendo documentos: ", task.getException());
            }
        });
    }

    /**
     * Recupera la lista de nombres de Pokémon capturados desde Firestore.
     */
    public void fetchPokemonNames() {
        List<String> pokemonNames = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("users").document(userEmail).collection("captured_pokemon").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    pokemonNames.add(document.getId());
                }
                capturedPokemonNamesLiveData.setValue(pokemonNames);
            } else {
                Log.e("PokemonsViewModel", "Error obteniendo colecciones: ", task.getException());
            }
        });
    }
}