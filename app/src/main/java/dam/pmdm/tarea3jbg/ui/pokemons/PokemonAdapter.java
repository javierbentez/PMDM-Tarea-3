package dam.pmdm.tarea3jbg.ui.pokemons;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.List;

import dam.pmdm.tarea3jbg.R;

/**
 * Adaptador para la lista de Pokémon.
 * Proporciona la interfaz y funcionalidad para visualizar y gestionar los
 * elementos de la lista de Pokémon.
 * 
 * @author Javier Benítez García
 */
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private final List<Pokemon> pokemons;
    private final OnPokemonDeleteListener deleteListener;
    private FirebaseFirestore db;

    /**
     * Interfaz para gestionar la eliminación de un Pokémon.
     */
    public interface OnPokemonDeleteListener {
        void onDelete(int position);
    }

    /**
     * Constructor de la clase.
     * 
     * @param pokemons La lista de Pokémon a mostrar.
     * @param deleteListener El listener para gestionar la eliminación de un Pokémon.
     */
    public PokemonAdapter(List<Pokemon> pokemons, OnPokemonDeleteListener deleteListener) {
        this.pokemons = pokemons;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemons.get(position);

        // Configuramos el texto
        holder.nameTextView.setText(pokemon.getName());
        holder.typeTextView.setText(
                holder.itemView.getContext().getString(R.string.details_types, String.join(", ", pokemon.getTypes())));

        // Cargamos la imagen
        new Thread(() -> {
            try {
                URL url = new URL(pokemon.getImage());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.imageView.post(() -> holder.imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                holder.imageView.post(() -> holder.imageView.setImageResource(R.drawable.pokeball));
            }
        }).start();

        // Configuramos el botón de eliminar para eliminar el elemento de Firestore
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            db = FirebaseFirestore.getInstance();
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            db.collection("users").document(userEmail).collection("captured_pokemon")
                    .document(pokemon.getName().toLowerCase()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(),
                                holder.itemView.getContext().getString(R.string.pokemon_deleted_message,
                                        pokemon.getName()),
                                Toast.LENGTH_SHORT).show();
                        pokemons.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, pokemons.size() - currentPosition);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(holder.itemView.getContext(), R.string.pokemon_delete_error_message,
                                Toast.LENGTH_SHORT)
                                .show();
                    });
        });

        // Configuramos el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PokemonDetailsActivity.class);
            intent.putExtra("name", pokemon.getName());
            intent.putExtra("image", pokemon.getImage());
            intent.putExtra("types", String.join(", ", pokemon.getTypes()));
            intent.putExtra("index", pokemon.getIndex());
            intent.putExtra("weight", pokemon.getWeight());
            intent.putExtra("height", pokemon.getHeight());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    /**
     * Obtiene la lista de Pokémon.
     * 
     * @return La lista de Pokémon.
     */
    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    /**
     * Establece la lista de Pokémon.
     * 
     * @param pokemons La lista de Pokémon.
     */
    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons.clear();
        this.pokemons.addAll(pokemons);
        notifyDataSetChanged();
    }

    /**
     * Clase interna para gestionar los elementos de la lista.
     */
    static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, typeTextView;
        Button deleteButton;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pokemon_image);
            nameTextView = itemView.findViewById(R.id.pokemon_name);
            typeTextView = itemView.findViewById(R.id.pokemon_type);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}