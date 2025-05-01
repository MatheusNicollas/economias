package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategoria;
    private EditText editValor;
    private EditText editNomeItem;
    private Button btnAdicionar, btnResumo;
    private ListView listViewDespesas;
    private ArrayList<String> listaDespesas;
    private ArrayAdapter<String> despesasAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNomeItem.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editValor = findViewById(R.id.editValor);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        btnResumo = findViewById(R.id.btnResumo);
        editNomeItem = findViewById(R.id.editNomeItem);
        listViewDespesas = findViewById(R.id.listViewDespesas);

        // Categorias com emojis
        ArrayList<String> categorias = new ArrayList<>(Arrays.asList(
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

    private void adicionarDespesa() {
        String nomeItem = editNomeItem.getText().toString().trim();
        String categoriaSelecionada = spinnerCategoria.getSelectedItem().toString();
        String valorTexto = editValor.getText().toString().trim();

        if (valorTexto.isEmpty()) {
            Toast.makeText(this, "Digite o valor da despesa", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorTexto);

        String categoria = categoriaSelecionada.substring(categoriaSelecionada.offsetByCodePoints(0, 1));
        String emoji = categoriaSelecionada.substring(0, categoriaSelecionada.offsetByCodePoints(0, 1));

        dbHelper.inserirDespesa(nomeItem, categoria, valor, emoji);

        listaDespesas.clear();
        listaDespesas.addAll(montarListaDespesas());
        despesasAdapter.notifyDataSetChanged();

        editNomeItem.setText("");
        editValor.setText("");
    }
}