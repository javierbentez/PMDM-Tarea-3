package dam.pmdm.tarea3jbg.ui.pokemons;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;

import dam.pmdm.tarea3jbg.R;

/**
 * Actividad para mostrar los detalles de un Pokémon.
 * Proporciona la interfaz y funcionalidad para visualizar los detalles de un
 * Pokémon específico.
 * 
 * @author Javier Benítez García
 */
public class PokemonDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        // Obtenemos datos pasados desde el intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String imageUrl = intent.getStringExtra("image");
        String types = intent.getStringExtra("types");
        int index = intent.getIntExtra("index", -1);
        float weight = intent.getFloatExtra("weight", 0);
        float height = intent.getFloatExtra("height", 0);

        // Configuramos vistas
        TextView nameTextView = findViewById(R.id.details_name);
        TextView indexTextView = findViewById(R.id.details_index);
        TextView typesTextView = findViewById(R.id.details_types);
        TextView weightTextView = findViewById(R.id.details_weight);
        TextView heightTextView = findViewById(R.id.details_height);
        ImageView imageView = findViewById(R.id.details_image);

        nameTextView.setText(name);
        indexTextView.setText(getString(R.string.details_index, index));
        typesTextView.setText(getString(R.string.details_types, types));
        weightTextView.setText(getString(R.string.details_weight, weight));
        heightTextView.setText(getString(R.string.details_height, height));

        // Descargamos la imagen
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                imageView.post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                imageView.post(() -> imageView.setImageResource(R.drawable.pokeball));
            }
        }).start();
    }
}