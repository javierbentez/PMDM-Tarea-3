package dam.pmdm.tarea3jbg.ui.pokedex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.tarea3jbg.R;

/**
 * Adaptador para la lista de Pokémon en la Pokédex.
 * Proporciona la interfaz y funcionalidad para visualizar y gestionar los
 * elementos de la lista de Pokémon en la Pokédex.
 * 
 * @author Javier Benítez García
 */
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<Pokemon> pokemons;
    private OnItemClickListener listener;

    /**
     * Constructor de la clase.
     * 
     * @param pokemons La lista de Pokémon a mostrar.
     * @param listener El listener para gestionar los clics en los elementos.
     */
    public PokemonAdapter(List<Pokemon> pokemons, OnItemClickListener listener) {
        this.pokemons = pokemons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokedex, parent, false);
        return new PokemonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemons.get(position);
        holder.bind(pokemon, listener);
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Pokemon pokemon);
    }

    /**
     * Clase para gestionar la vista de un Pokémon.
     */
    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pokemon_name);
        }

        public void bind(final Pokemon pokemon, final OnItemClickListener listener) {
            nameTextView.setText(pokemon.getName());
            if (pokemon.isCaptured()) {
                nameTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.pokemon_red)); // rojo
            } else {
                nameTextView.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                itemView.setOnClickListener(v -> listener.onItemClick(pokemon));
            }
        }
    }
}