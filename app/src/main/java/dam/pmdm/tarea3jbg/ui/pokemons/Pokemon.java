package dam.pmdm.tarea3jbg.ui.pokemons;

import java.util.List;

/**
 * Clase que representa un Pokémon.
 * Proporciona los atributos y métodos para acceder a los detalles de un
 * Pokémon.
 * 
 * @author Javier Benítez García
 */
public class Pokemon {
    private String name;
    private int index;
    private String image;
    private List<String> types;
    private float weight;
    private float height;

    /**
     * Constructor de la clase.
     * 
     * @param name   El nombre del Pokémon.
     * @param index  El índice del Pokémon.
     * @param image  La URL o recurso de imagen del Pokémon.
     * @param types  La lista de tipos del Pokémon.
     * @param weight El peso del Pokémon.
     * @param height La altura del Pokémon.
     */
    public Pokemon(String name, int index, String image, List<String> types, float weight, float height) {
        this.name = name;
        this.index = index;
        this.image = image;
        this.types = types;
        this.weight = weight;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getImage() {
        return image;
    }

    public List<String> getTypes() {
        return types;
    }

    public float getWeight() {
        return weight;
    }

    public float getHeight() {
        return height;
    }
}