package com.example.economias.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economias.R;
import com.example.economias.adapter.DespesaAdapter;
import com.example.economias.data.DatabaseHelper;
import com.example.economias.data.Despesa;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetalhesCategoriaActivity extends AppCompatActivity {

    private TextView textCategoria;
    private ListView listViewDetalhes;
    private TextView textTotal;
    private DatabaseHelper dbHelper;
    private String categoriaAtual;
    private String dataInicio;
    private String dataFim;


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

        Intent intent = getIntent();
        categoriaAtual = intent.getStringExtra("categoria");
        dataInicio = intent.getStringExtra("data_inicio");
        dataFim = intent.getStringExtra("data_fim");

        String[] partes = dataFim.split("-");

        int ano = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);

        atualizarTituloResumo(mes, ano, categoriaAtual);
        carregarDespesas();
    }

    private void atualizarTituloResumo(int mes, int ano, String categoria) {
        Calendar tempCalendario = Calendar.getInstance();
        tempCalendario.set(Calendar.MONTH, mes - 1);
        tempCalendario.set(Calendar.YEAR, ano);

        String nomeMes = new SimpleDateFormat("MMMM", Locale.getDefault()).format(tempCalendario.getTime());
        String tituloResumo = String.format("Gastos com %s em %s de %04d", categoria, nomeMes, ano);

        textCategoria.setText(tituloResumo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            carregarDespesas();
        }
    }

    private void carregarDespesas() {
        List<Despesa> despesas = dbHelper.obterDespesasPorCategoriaEData(categoriaAtual, dataInicio, dataFim);

        DespesaAdapter adapter = new DespesaAdapter(
                this,
                despesas,
                dbHelper,
                this::carregarDespesas
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
