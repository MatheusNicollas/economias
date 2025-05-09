package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private EditText editDataInicio;
    private EditText editDataFim;
    private Button btnFiltrar;
    private List<Despesa> listaDespesas;

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

        editDataInicio = findViewById(R.id.editDataInicio);
        editDataFim = findViewById(R.id.editDataFim);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        listViewResumo = findViewById(R.id.listViewResumo);
        pieChart = findViewById(R.id.pieChart);
        textTotalGeral = findViewById(R.id.textTotalGeral);

        dbHelper = new DatabaseHelper(this);
        listaDespesas = dbHelper.obterTodasDespesas();

        btnFiltrar.setOnClickListener(v -> {
            String dataInicio = editDataInicio.getText().toString();
            String dataFim = editDataFim.getText().toString();

            if (isEmpty(editDataInicio.getText())) {
                Toast.makeText(this, "Por favor, selecione uma data de início", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEmpty(editDataFim.getText())){
                Toast.makeText(this, "Por favor, selecione uma data de Fim", Toast.LENGTH_SHORT).show();
                return;
            }

            listaDespesas = dbHelper.obterDespesasPorData(dataInicio, dataFim);
        });

        exibirListaDespesaPorCategoria();
        DespesasPorCategoria result = getMapearDespesasPorCategoria(listaDespesas);
        exibirTotalGeral(listaDespesas);
        ListaEGraficoDeDespesas despesasPorCategoria = getMapearListaEGraficoDeDespesas(result);

        plotarGraficoDespesasPorCategoria(despesasPorCategoria);

    }

    private void plotarGraficoDespesasPorCategoria(ListaEGraficoDeDespesas despesasPorCategoria) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_categoria_resumo, R.id.textCategoriaValor, despesasPorCategoria.resumoLista);
        listViewResumo.setAdapter(adapter);

        // Exibir gráfico pizza
        PieDataSet dataSet = new PieDataSet(despesasPorCategoria.pieEntries, "Despesas por Categoria");
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

    @NonNull
    private static ListaEGraficoDeDespesas getMapearListaEGraficoDeDespesas(DespesasPorCategoria result) {
        ArrayList<String> resumoLista = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : result.totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double valor = entry.getValue();
            resumoLista.add(result.categoriasEmoji.get(categoria) + " " + categoria + ": R$ " + String.format("%.2f", valor));
            pieEntries.add(new PieEntry((float) valor, categoria));
        }
        ListaEGraficoDeDespesas despesasPorCategoria = new ListaEGraficoDeDespesas(resumoLista, pieEntries);
        return despesasPorCategoria;
    }

    private static class ListaEGraficoDeDespesas {
        public final ArrayList<String> resumoLista;
        public final ArrayList<PieEntry> pieEntries;

        public ListaEGraficoDeDespesas(ArrayList<String> resumoLista, ArrayList<PieEntry> pieEntries) {
            this.resumoLista = resumoLista;
            this.pieEntries = pieEntries;
        }
    }

    @NonNull
    private static DespesasPorCategoria getMapearDespesasPorCategoria(List<Despesa> listaDespesas) {
        HashMap<String, Double> totaisPorCategoria = new HashMap<>();
        HashMap<String, String> categoriasEmoji = new HashMap<>();

        if (listaDespesas != null) {
            for (Despesa despesa : listaDespesas) {
                categoriasEmoji.put(despesa.getCategoria(), despesa.getEmoji());
                if (totaisPorCategoria.containsKey(despesa.getCategoria())) {
                    totaisPorCategoria.put(despesa.getCategoria(), totaisPorCategoria.get(despesa.getCategoria()) + despesa.getValor());
                } else {
                    totaisPorCategoria.put(despesa.getCategoria(), despesa.getValor());
                }
            }
        }
        return new DespesasPorCategoria(totaisPorCategoria, categoriasEmoji);
    }

    private static class DespesasPorCategoria {
        public final HashMap<String, Double> totaisPorCategoria;
        public final HashMap<String, String> categoriasEmoji;

        public DespesasPorCategoria(HashMap<String, Double> totaisPorCategoria, HashMap<String, String> categoriasEmoji) {
            this.totaisPorCategoria = totaisPorCategoria;
            this.categoriasEmoji = categoriasEmoji;
        }
    }

    private void exibirTotalGeral(List<Despesa> listaDespesas) {
        double totalGeral = 0.0;
        for (Despesa despesa : listaDespesas) {
            totalGeral += despesa.getValor();
        }
        textTotalGeral.setText("💰 Total: R$ " + String.format("%.2f", totalGeral));
    }

    private void exibirListaDespesaPorCategoria() {
        listViewResumo.setOnItemClickListener((parent, view, position, id) -> {
            String itemClicado = (String) parent.getItemAtPosition(position);
            String semEmoji = itemClicado.substring(2); // Remove o primeiro caractere (emoji)
            String categoria = semEmoji.split(":")[0].trim(); // Pega tudo até o primeiro ":" e remove espaços

            Intent intent = new Intent(ResumoActivity.this, DetalhesCategoriaActivity.class);
            intent.putExtra("categoria", categoria);
            startActivity(intent);
        });
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