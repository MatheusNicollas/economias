package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ResumoActivity extends AppCompatActivity {

    private ListView listViewResumo;
    private PieChart pieChart;
    private TextView textTotalGeral;
    private DatabaseHelper dbHelper;
    private EditText editDataInicio;
    private EditText editDataFim;
    private Button btnFiltrar;
    private List<Despesa> listaDespesas;
    private TextView textResumo;

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
        textResumo = findViewById(R.id.textResumo);

        settarValoresPadraoParaDatas();
        setupDatePicker(editDataInicio);
        setupDatePicker(editDataFim);

        dbHelper = new DatabaseHelper(this);
        listaDespesas = dbHelper.obterDespesasPorData(editDataInicio.getText().toString(), editDataFim.getText().toString());

        btnFiltrar.setOnClickListener(v -> {
            String dataInicio = editDataInicio.getText().toString();
            String dataFim = editDataFim.getText().toString();

            if (isEmpty(editDataInicio.getText())) {
                Toasty.warning(this, "Por favor, selecione uma data de início", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEmpty(editDataFim.getText())){
                Toasty.warning(this, "Por favor, selecione uma data de Fim", Toast.LENGTH_SHORT).show();
                return;
            }

            listaDespesas = dbHelper.obterDespesasPorData(dataInicio, dataFim);

            exibirListaDespesaPorCategoria();
            DespesasPorCategoria result = mapearDespesasPorCategoria(listaDespesas);
            exibirTotalGeral(listaDespesas);
            GraficoDespesas despesasPorCategoria = mapearListaEGraficoDeDespesas(result);
            plotarGraficoDespesasPorCategoria(despesasPorCategoria);
        });

        exibirListaDespesaPorCategoria();
        DespesasPorCategoria result = mapearDespesasPorCategoria(listaDespesas);
        exibirTotalGeral(listaDespesas);
        GraficoDespesas despesasPorCategoria = mapearListaEGraficoDeDespesas(result);
        plotarGraficoDespesasPorCategoria(despesasPorCategoria);

    }

    private void settarValoresPadraoParaDatas() {
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH) + 1; // Janeiro = 0 então +1
        int ultimoDia = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);

        String primeiroDia = String.format("%04d-%02d-01", ano, mes);
        String ultimoDiaFormatado = String.format("%04d-%02d-%02d", ano, mes, ultimoDia);

        editDataInicio.setText(primeiroDia);
        editDataFim.setText(ultimoDiaFormatado);

        atualizarTituloResumo(mes, ano);
    }

    private void atualizarTituloResumo(int mes, int ano) {
        Calendar tempCalendario = Calendar.getInstance();
        tempCalendario.set(Calendar.MONTH, mes - 1); // Ajusta para janeiro = 0
        tempCalendario.set(Calendar.YEAR, ano);

        String nomeMes = new SimpleDateFormat("MMMM", Locale.getDefault()).format(tempCalendario.getTime());
        String tituloResumo = String.format("Despesas de %s de %04d", nomeMes, ano);

        textResumo.setText(tituloResumo);
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mostrarDatePicker(editText);
            }
        });

        editText.setOnClickListener(v -> mostrarDatePicker(editText));
    }

    private void mostrarDatePicker(EditText editText) {
        final Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String dataFormatada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    editText.setText(dataFormatada);
                },
                ano, mes, dia
        );
        atualizarTituloResumo(mes, ano);
        datePicker.show();
    }

    private void plotarGraficoDespesasPorCategoria(GraficoDespesas despesasPorCategoria) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_categoria_resumo, R.id.textCategoriaValor, despesasPorCategoria.resumoLista);
        listViewResumo.setAdapter(adapter);

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
    private static GraficoDespesas mapearListaEGraficoDeDespesas(DespesasPorCategoria result) {
        ArrayList<String> resumoLista = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : result.totaisPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double valor = entry.getValue();
            resumoLista.add(result.categoriasEmoji.get(categoria) + " " + categoria + ": R$ " + String.format("%.2f", valor));
            pieEntries.add(new PieEntry((float) valor, categoria));
        }
        GraficoDespesas despesasPorCategoria = new GraficoDespesas(resumoLista, pieEntries);
        return despesasPorCategoria;
    }

    @NonNull
    private static DespesasPorCategoria mapearDespesasPorCategoria(List<Despesa> listaDespesas) {
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