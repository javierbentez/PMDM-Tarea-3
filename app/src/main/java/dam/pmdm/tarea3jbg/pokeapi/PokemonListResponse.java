package dam.pmdm.tarea3jbg.pokeapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dam.pmdm.tarea3jbg.ui.pokedex.Pokemon;

/**
 * Clase para la respuesta de la lista de Pokémon.
 * Proporciona los atributos y métodos para acceder a la lista de Pokémon.
 * 
 * @author Javier Benítez García
 */
public class PokemonListResponse {
    @SerializedName("results")
    private List<Pokemon> results;

    /**
     * Obtiene la lista de Pokémon.
     * 
     * @return La lista de Pokémon.
     */
    public List<Pokemon> getResults() {
        for (Pokemon pokemon : results) {
            pokemon.setUrl("https://pokeapi.co/api/v2/pokemon/" + pokemon.getName() + "/");
        }
        return results;
    }
}