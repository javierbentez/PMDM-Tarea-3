package dam.pmdm.tarea3jbg.pokeapi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz para la API de Pokémon.
 * Proporciona métodos para obtener la lista de Pokémon y los detalles de un
 * Pokémon específico.
 * 
 * @author Javier Benítez García
 */
public interface PokemonAPI {

    /**
     * Obtiene una lista de Pokémon.
     * 
     * @param offset El desplazamiento para la lista de Pokémon.
     * @param limit  El número máximo de Pokémon a devolver.
     * @return Una llamada que devuelve la respuesta con la lista de Pokémon.
     */
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit);

    /**
     * Obtiene los detalles de un Pokémon específico.
     * 
     * @param name El nombre del Pokémon.
     * @return Una llamada que devuelve la respuesta con los detalles del Pokémon.
     */
    @GET("pokemon/{name}")
    Call<ResponseBody> getPokemonDetails(@Path("name") String name);
}