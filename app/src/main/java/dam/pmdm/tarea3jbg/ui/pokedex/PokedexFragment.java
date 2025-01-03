package dam.pmdm.tarea3jbg.ui.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.tarea3jbg.R;
import dam.pmdm.tarea3jbg.pokeapi.PokemonAPI;
import dam.pmdm.tarea3jbg.pokeapi.PokemonListResponse;
import dam.pmdm.tarea3jbg.pokeapi.RetrofitClient;
import dam.pmdm.tarea3jbg.ui.pokemons.PokemonsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento para mostrar la lista de Pokémon en la Pokédex.
 * Proporciona la interfaz y funcionalidad para visualizar y gestionar los
 * Pokémon en la Pokédex.
 * 
 * @author Javier Benítez García
 */
public class PokedexFragment extends Fragment {

    private RecyclerView recyclerView;
    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList;
    private List<Pokemon> capturedPokemonList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String jsonSelectedPokemon;
    private PokemonsViewModel pokemonsViewModel;

    /**
     * Método que se llama para crear y configurar la vista del fragmento.
     * 
     * @param inflater           El LayoutInflater que se utiliza para inflar la
     *                           vista del fragmento.
     * @param container          El ViewGroup al que se adjunta la vista del
     *                           fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está
     *                           reconstruyendo a partir de un estado guardado
     *                           anterior.
     * @return La vista del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(pokemonList, new PokemonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pokemon pokemon) {
                capturedPokemonList.add(pokemon);
                Toast.makeText(getContext(), getString(R.string.pokemon_captured_message, pokemon.getName()),
                        Toast.LENGTH_SHORT).show();
                // Hacemos fetch de la pokemon.getUrl() para obtener los detalles del Pokemon
                fetchPokemonDetails(pokemon.getName());
                pokemon.setCaptured(true); // Marcar como capturado
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(adapter);

        pokemonsViewModel = new ViewModelProvider(this).get(PokemonsViewModel.class);

        pokemonsViewModel.getCapturedPokemonNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> capturedPokemonNames) {
                loadPokemonList(capturedPokemonNames);
            }
        });

        pokemonsViewModel.fetchPokemonNames();

        return view;
    }

    /**
     * Carga la lista de Pokémon desde la API y marca los Pokémon capturados.
     * 
     * @param capturedPokemonNames La lista de nombres de Pokémon capturados.
     */
    private void loadPokemonList(List<String> capturedPokemonNames) {
        Log.d("PokedexFragment", "Iniciando carga de la lista de Pokémon");
        PokemonAPI api = RetrofitClient.getRetrofitInstance().create(PokemonAPI.class);
        api.getPokemonList(0, 150).enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PokedexFragment", "Lista de Pokémon cargada exitosamente");
                    pokemonList.clear();
                    for (Pokemon pokemon : response.body().getResults()) {
                        String nameMayuscula = StringUtils.capitalize(pokemon.getName());
                        pokemon.setName(nameMayuscula);
                        if (capturedPokemonNames.contains(pokemon.getName().toLowerCase())) {
                            pokemon.setCaptured(true);
                        }
                        pokemonList.add(pokemon);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("PokedexFragment", "Error al cargar la lista de Pokémon: " + response.message());
                    Toast.makeText(getContext(), R.string.error_loading_pokemons, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                Log.e("PokedexFragment", "Error al cargar la lista de Pokémon", t);
                Toast.makeText(getContext(), R.string.error_loading_pokemons, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Obtiene los detalles de un Pokémon desde la API y los almacena en Firestore.
     * 
     * @param name El nombre del Pokémon.
     */
    private void fetchPokemonDetails(String name) {
        PokemonAPI api = RetrofitClient.getRetrofitInstance().create(PokemonAPI.class);
        api.getPokemonDetails(name.toLowerCase()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = null;
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Convertimos el JSON a un Map<String, Object>
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> map = gson.fromJson(json, type);

                    Map<String, Object> info = new HashMap<>();
                    info.put("name", map.get("name"));
                    info.put("id", map.get("id"));
                    Map<String, Object> sprites = (Map<String, Object>) map.get("sprites");
                    info.put("image", sprites.get("front_default"));
                    info.put("types", map.get("types"));
                    info.put("weight", map.get("weight"));
                    info.put("height", map.get("height"));

                    // Almacenamos la información del Pokémon en Firestore
                    String userEmail = auth.getCurrentUser().getEmail();
                    db.collection("users").document(userEmail).collection("captured_pokemon")
                            .document(name.toLowerCase())
                            .set(info)
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), R.string.pokemon_details_store_error,
                                        Toast.LENGTH_SHORT).show();
                            });
                } else {
                    getActivity().runOnUiThread(() -> Toast
                            .makeText(getContext(), R.string.pokemon_details_error, Toast.LENGTH_SHORT)
                            .show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                getActivity().runOnUiThread(() -> System.out.print("ERRORRR: " + t.getMessage()));
            }
        });
    }
}