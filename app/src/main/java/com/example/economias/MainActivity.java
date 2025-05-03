package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategoria;
    private EditText editValor;
    private EditText editNomeItem;
    private Button btnAdicionar, btnResumo;
    private ListView listViewDespesas;
    private ArrayList<String> listaDespesas;
    private ArrayAdapter<String> despesasAdapter;
    private DatabaseHelper dbHelper;
    private EditText editData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editValor = findViewById(R.id.editValor);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        btnResumo = findViewById(R.id.btnResumo);
        editNomeItem = findViewById(R.id.editNomeItem);
        listViewDespesas = findViewById(R.id.listViewDespesas);
        editData = findViewById(R.id.editData);

        editData.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        editData.setText(dataFormatada);
                    },
                    ano, mes, dia
            );
            datePicker.show();
        });

        // Categorias com emojis
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList(
                "▼ Selecione uma Categoria",
                "🛒 Mercado",
                "🍽️ Comer Fora",
                "👕 Roupas",
                "👟 Calçados",
                "🛍️ Compras Online",
                "💻 Tecnologia",
                "🎁 Presente",
                "🚗 Carro",
                "⛽ Combustível",
                "🚌 Transporte",
                "🎬 Entretenimento",
                "🛡️ Seguro",
                "🏖️ Lazer"
        ));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(spinnerAdapter);

        dbHelper = new DatabaseHelper(this);

        montarListaDespesas();

        listaDespesas = montarListaDespesas();

        despesasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDespesas);
        listViewDespesas.setAdapter(despesasAdapter);

        btnAdicionar.setOnClickListener(v -> adicionarDespesa());

        btnResumo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResumoActivity.class);
            intent.putStringArrayListExtra("listaDespesas", listaDespesas);
            startActivity(intent);
        });

        editValor.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editValor.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString) / 100.0;
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        String formatted = format.format(parsed);

                        current = formatted;
                        editValor.setText(formatted);
                        editValor.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        s.clear();
                    }

                    editValor.addTextChangedListener(this);
                }
            }
        });
    }

    private ArrayList<String> montarListaDespesas() {
        List<Despesa> despesas = dbHelper.obterTodasDespesas();
        ArrayList<String> despesasStr = new ArrayList<>();

        for (Despesa d : despesas) {
            String valor = ": R$ " + String.format("%.2f", d.getValor());
            if (isEmpty(d.getNome())) {
                despesasStr.add(d.getEmoji() + " "+ d.getCategoria() + valor);
            } else {
                despesasStr.add(d.getEmoji() + " " + d.getNome() + valor);
            }
        }
        return despesasStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void adicionarDespesa() {
        String nomeItem = editNomeItem.getText().toString().trim();
        String categoriaSelecionada = spinnerCategoria.getSelectedItem().toString();
        String valorTexto = editValor.getText().toString().trim();
        String dataDespesa = editData.getText().toString().trim();

        if (categoriaSelecionada.equals("▼ Selecione uma Categoria")) {
            Toast.makeText(this, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorTexto.isEmpty()) {
            Toast.makeText(this, "Digite o valor da despesa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataDespesa.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataDespesa = LocalDate.now().format(formatter);
        }

        // Remove "R$", ponto, vírgula e espaços
        String cleanString = valorTexto.replaceAll("[R$\\s]", "").replace(",", ".");

        double valor;
        try {
            valor = Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = categoriaSelecionada.substring(categoriaSelecionada.offsetByCodePoints(0, 1)).trim();
        String emoji = categoriaSelecionada.substring(0, categoriaSelecionada.offsetByCodePoints(0, 1));

        dbHelper.inserirDespesa(nomeItem, categoria, valor, emoji, dataDespesa);

        listaDespesas.clear();
        listaDespesas.addAll(montarListaDespesas());
        despesasAdapter.notifyDataSetChanged();

        editNomeItem.setText("");
        editValor.setText("");
        spinnerCategoria.setSelection(0);
        editData.setText("");
    }
}