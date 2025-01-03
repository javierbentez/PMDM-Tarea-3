package dam.pmdm.tarea3jbg.ui.pokemons;

import android.app.AlertDialog;
import android.os.Bundle;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.tarea3jbg.R;
import dam.pmdm.tarea3jbg.databinding.FragmentPokemonsBinding;

/**
 * Fragmento para mostrar la lista de Pokémon capturados.
 * Proporciona la interfaz y funcionalidad para visualizar y gestionar los
 * Pokémon capturados.
 * 
 * @author Javier Benítez García
 */
public class PokemonsFragment extends Fragment {

    private FragmentPokemonsBinding binding;
    private PokemonAdapter adapter;
    private AlertDialog loadingDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PokemonsViewModel pokemonsViewModel = new ViewModelProvider(this).get(PokemonsViewModel.class);

        binding = FragmentPokemonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewPokemons;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PokemonAdapter(new ArrayList<>(), position -> {
            adapter.getPokemons().remove(position);
            adapter.notifyItemRemoved(position);
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pokemonsViewModel.fetchPokemons();
        });

        mostrarDialogoCargando();
        pokemonsViewModel.fetchPokemons();

        pokemonsViewModel.getPokemons().observe(getViewLifecycleOwner(), new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                ocultarDialogoCargando();
                swipeRefreshLayout.setRefreshing(false);
                if (pokemons != null) {
                    adapter.setPokemons(pokemons);
                } else {
                    Toast.makeText(getContext(), R.string.error_loading_pokemons, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    /**
     * Muestra un diálogo de carga.
     */
    private void mostrarDialogoCargando() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    /**
     * Oculta el diálogo de carga.
     */
    private void ocultarDialogoCargando() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}