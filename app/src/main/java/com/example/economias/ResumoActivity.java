package com.example.economias;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumoActivity extends AppCompatActivity {

    private ListView listViewResumo;
    private PieChart pieChart;
    private TextView textTotalGeral;
    private DatabaseHelper dbHelper;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Resumo das Despesas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listViewResumo = findViewById(R.id.listViewResumo);
        pieChart = findViewById(R.id.pieChart);
        textTotalGeral = findViewById(R.id.textTotalGeral);

        dbHelper = new DatabaseHelper(this);
        List<Despesa> listaDespesas = dbHelper.obterTodasDespesas();

        HashMap<String, Double> totaisPorCategoria = new HashMap<>();
        HashMap<String, String> categoriasEmoji = new HashMap<>();
        double totalGeral = 0.0;

        listViewResumo.setOnItemClickListener((parent, view, position, id) -> {
            String itemClicado = (String) parent.getItemAtPosition(position);
            String semEmoji = itemClicado.substring(2); // Remove o primeiro caractere (emoji)
            String categoria = semEmoji.split(":")[0].trim(); // Pega tudo até o primeiro ":" e remove espaços

            Intent intent = new Intent(ResumoActivity.this, DetalhesCategoriaActivity.class);
            intent.putExtra("categoria", categoria);
            startActivity(intent);
        });

        if (listaDespesas != null) {
            for (Despesa despesa : listaDespesas) {
                totalGeral += despesa.getValor();
                categoriasEmoji.put(despesa.getCategoria(), despesa.getEmoji());
                if (totaisPorCategoria.containsKey(despesa.getCategoria())) {
                    totaisPorCategoria.put(despesa.getCategoria(), totaisPorCategoria.get(despesa.getCategoria()) + despesa.getValor());
                } else {
                    totaisPorCategoria.put(despesa.getCategoria(), despesa.getValor());
                }
            }
        }

        // Exibir total geral
        textTotalGeral.setText("💰 Total: R$ " + String.format("%.2f", totalGeral));

        // Exibir na lista
        ArrayList<String> resumoLista = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double valor = entry.getValue();
            resumoLista.add(categoriasEmoji.get(categoria) + " " + categoria + ": R$ " + String.format("%.2f", valor));
            pieEntries.add(new PieEntry((float) valor, categoria));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_categoria_resumo, R.id.textCategoriaValor, resumoLista);
        listViewResumo.setAdapter(adapter);

        // Exibir gráfico pizza
        PieDataSet dataSet = new PieDataSet(pieEntries, "Despesas por Categoria");
        dataSet.setColors(getColors());
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.animateY(1000);
        pieChart.invalidate();
        // Configura labels (categorias) com sombra
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);

        // Sombra nos valores (percentuais)
        pieChart.getRenderer().getPaintValues().setShadowLayer(1f, 1f, 1f, Color.BLACK);

    }

    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(244, 67, 54));
        colors.add(Color.rgb(33, 150, 243));
        colors.add(Color.rgb(76, 175, 80));
        colors.add(Color.rgb(255, 193, 7));
        colors.add(Color.rgb(156, 39, 176));
        colors.add(Color.rgb(255, 87, 34));
        colors.add(Color.rgb(63, 81, 181));
        colors.add(Color.rgb(0, 150, 136));
        colors.add(Color.rgb(205, 220, 57));
        colors.add(Color.rgb(121, 85, 72));
        colors.add(Color.rgb(96, 125, 139));
        colors.add(Color.rgb(233, 30, 99));
        colors.add(Color.rgb(0, 188, 212));
        return colors;
    }
}