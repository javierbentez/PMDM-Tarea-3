package dam.pmdm.tarea3jbg.pokeapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Retrofit para la API de Pokémon.
 * Proporciona una instancia de Retrofit configurada para interactuar con la API
 * de Pokémon.
 * 
 * @author Javier Benítez García
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private static Retrofit retrofit;

    /**
     * Obtiene la instancia de Retrofit.
     * 
     * @return La instancia de Retrofit.
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}