package com.example.economias;

import static android.text.TextUtils.isEmpty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "economias.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DESPESAS = "despesas";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String COLUMN_VALOR = "valor";
    private static final String COLUMN_NOME_ITEM = "nome_item";
    private static final String COLUMN_EMOJI = "emoji";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_DESPESAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME_ITEM + " TEXT,"
                + COLUMN_CATEGORIA + " TEXT,"
                + COLUMN_VALOR + " REAL,"
                + COLUMN_EMOJI + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESPESAS);
        onCreate(db);
    }

    // Método para inserir despesa
    public void inserirDespesa(String nomeItem, String categoria, double valor, String emoji) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME_ITEM, nomeItem);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_VALOR, valor);
        values.put(COLUMN_EMOJI, emoji);
        db.insert(TABLE_DESPESAS, null, values);
        db.close();
    }

    // Método para obter todas as despesas
    public List<Despesa> obterTodasDespesas() {
        List<Despesa> despesas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DESPESAS + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Despesa despesa = new Despesa();
                despesa.setNome(cursor.getString(1));
                despesa.setCategoria(cursor.getString(2));
                despesa.setValor(cursor.getDouble(3));
                despesa.setEmoji(cursor.getString(4));
                despesas.add(despesa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return despesas;
    }

    // Obter despesas por categoria
    public ArrayList<String> obterDespesasPorCategoria(String categoriaFiltro) {
        ArrayList<String> despesas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DESPESAS + " WHERE " + COLUMN_CATEGORIA + " = ?", new String[]{categoriaFiltro});

        if (cursor.moveToFirst()) {
            do {
                String nomeItem = cursor.getString(1);
                String categoria = cursor.getString(2);
                double valor = cursor.getDouble(3);

                if (isEmpty(nomeItem)) {
                    despesas.add("■ " + categoria + ": R$ " + String.format("%.2f", valor));
                } else {
                    despesas.add("■ " + nomeItem + ": R$ " + String.format("%.2f", valor));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return despesas;
    }
}