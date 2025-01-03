package dam.pmdm.tarea3jbg.ui.pokedex;

/**
 * Clase que representa un Pokémon en la Pokédex.
 * Proporciona los atributos y métodos para acceder a los detalles de un
 * Pokémon.
 * 
 * @author Javier Benítez García
 */
public class Pokemon {

    private String name;
    private String url;
    private boolean captured;

    /**
     * Constructor de la clase.
     * 
     * @param name     El nombre del Pokémon.
     * @param url      La URL del Pokémon.
     * @param captured Indica si el Pokémon ha sido capturado.
     */
    public Pokemon(String name, String url, boolean captured) {
        this.name = name;
        this.url = url;
        this.captured = captured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}