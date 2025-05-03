package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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

        List<Despesa> despesasCategoria = dbHelper.obterDespesasPorCategoria(categoria);
        ArrayList<String> despesasFormatadas = new ArrayList<>();

        double totalGeral = 0.0;

        for (Despesa despesa : despesasCategoria) {
            String nomeOuCategoria = isEmpty(despesa.getNome()) ? despesa.getCategoria() : despesa.getNome();
            String valorFormatado = String.format("%.2f", despesa.getValor()).replace(".", ",");
            String linha = "■ " + nomeOuCategoria + ": R$ " + valorFormatado + "\n📆 " + despesa.getDataDespesa();
            despesasFormatadas.add(linha);

            totalGeral += despesa.getValor();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, despesasFormatadas);
        listViewDetalhes.setAdapter(adapter);

        textTotal.setText("💰 Total gasto: R$ " + String.format("%.2f", totalGeral).replace(".", ","));
    }

}