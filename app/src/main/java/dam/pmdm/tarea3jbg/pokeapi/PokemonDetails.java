package dam.pmdm.tarea3jbg.pokeapi;

import com.google.gson.annotations.SerializedName;

/**
 * Clase para los detalles del Pokémon.
 * Proporciona los atributos y métodos para acceder a los detalles de un
 * Pokémon.
 * 
 * @author Javier Benítez García
 */
public class PokemonDetails {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    private String json;

    /**
     * Obtiene el ID del Pokémon.
     * 
     * @return El ID del Pokémon.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre del Pokémon.
     * 
     * @return El nombre del Pokémon.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene el JSON con los detalles del Pokémon.
     * 
     * @return El JSON con los detalles del Pokémon.
     */
    public String getJson() {
        return json;
    }

    /**
     * Establece el JSON con los detalles del Pokémon.
     * 
     * @param json El JSON con los detalles del Pokémon.
     */
    public void setJson(String json) {
        this.json = json;
    }
}