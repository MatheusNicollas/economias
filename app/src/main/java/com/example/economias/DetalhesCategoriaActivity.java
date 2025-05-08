package com.example.economias;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DetalhesCategoriaActivity extends AppCompatActivity {

    private TextView textCategoria;
    private ListView listViewDetalhes;
    private TextView textTotal;
    private DatabaseHelper dbHelper;
    private String categoriaAtual;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
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

        categoriaAtual = getIntent().getStringExtra("categoria");
        textCategoria.setText(categoriaAtual);

        carregarDespesas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            carregarDespesas();
        }
    }

    private void carregarDespesas() {
        List<Despesa> despesas = dbHelper.obterDespesasPorCategoria(categoriaAtual);

        DespesaAdapter adapter = new DespesaAdapter(
                this,
                despesas,
                dbHelper,
                this::carregarDespesas // callback para atualizar lista após exclusão
        );

        listViewDetalhes.setAdapter(adapter);
        calcularTotal(despesas);
    }

    private void calcularTotal(List<Despesa> despesas) {
        double total = 0.0;
        for (Despesa d : despesas) {
            total += d.getValor();
        }

        String totalFormatado = String.format("%.2f", total).replace(".", ",");
        textTotal.setText("💰 Total gasto: R$ " + totalFormatado);
    }
}
