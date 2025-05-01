package com.example.economias;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class DetalhesCategoriaActivity extends AppCompatActivity {

    private TextView textCategoria;
    private ListView listViewDetalhes;
    private DatabaseHelper dbHelper;
    private TextView textTotal;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Fecha a Activity atual e volta para a anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_categoria);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Categoria");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        textCategoria = findViewById(R.id.textCategoria);
        listViewDetalhes = findViewById(R.id.listViewDetalhes);
        textTotal = findViewById(R.id.textTotal);
        dbHelper = new DatabaseHelper(this);

        String categoria = getIntent().getStringExtra("categoria");
        textCategoria.setText(categoria);

        ArrayList<String> despesasCategoria = dbHelper.obterDespesasPorCategoria(categoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, despesasCategoria);
        listViewDetalhes.setAdapter(adapter);

        HashMap<String, Double> totaisPorCategoria = new HashMap<>();
        double totalGeral = 0.0;

        for (String despesa : despesasCategoria) {
            String[] partes = despesa.split(": R\\$ ");
            if (partes.length != 2) continue;
            String categoriaObj = partes[0];
            double valor = Double.parseDouble(partes[1].replace(",", "."));

            totalGeral += valor;

            if (totaisPorCategoria.containsKey(categoriaObj)) {
                totaisPorCategoria.put(categoriaObj, totaisPorCategoria.get(categoriaObj) + valor);
            } else {
                totaisPorCategoria.put(categoriaObj, valor);
            }
        }

        textTotal.setText("💰 Total gasto: R$ " + String.format("%.2f", totalGeral));
    }
}