package com.example.economias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "economias.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_DESPESAS = "despesas";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String COLUMN_VALOR = "valor";
    private static final String COLUMN_NOME_ITEM = "nome_item";
    private static final String COLUMN_EMOJI = "emoji";
    private static final String COLUMN_DATA_DESPESA = "data_despesa";

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
                + COLUMN_EMOJI + " TEXT,"
                + COLUMN_DATA_DESPESA + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESPESAS);
        onCreate(db);
    }

    public void inserirDespesa(String nomeItem, String categoria, double valor, String emoji, String dataDespesa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME_ITEM, nomeItem);
        values.put(COLUMN_CATEGORIA, categoria);
        values.put(COLUMN_VALOR, valor);
        values.put(COLUMN_EMOJI, emoji);
        values.put(COLUMN_DATA_DESPESA, dataDespesa);
        db.insert(TABLE_DESPESAS, null, values);
        db.close();
    }

    public List<Despesa> obterDespesasAdicionadasRecentemente() {
        List<Despesa> despesas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DESPESAS + " ORDER BY id DESC LIMIT 15", null);

        if (cursor.moveToFirst()) {
            do {
                Despesa despesa = new Despesa();
                despesa.setId(cursor.getInt(0));
                despesa.setNome(cursor.getString(1));
                despesa.setCategoria(cursor.getString(2));
                despesa.setValor(cursor.getDouble(3));
                despesa.setEmoji(cursor.getString(4));
                despesa.setDataDespesa(cursor.getString(5));
                despesas.add(despesa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return despesas;
    }

    public List<Despesa> obterDespesasPorCategoriaEData(String categoriaFiltro, String dataInicio, String dataFim) {
        List<Despesa> despesas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DESPESAS + " WHERE " + COLUMN_CATEGORIA + " = ?" +
                        " AND date(" + COLUMN_DATA_DESPESA + ") BETWEEN date(?) AND date(?)",
                new String[]{categoriaFiltro, dataInicio, dataFim});

        if (cursor.moveToFirst()) {
            do {
                Despesa despesa = new Despesa();
                despesa.setId(cursor.getInt(0));
                despesa.setNome(cursor.getString(1));
                despesa.setCategoria(cursor.getString(2));
                despesa.setValor(cursor.getDouble(3));
                despesa.setEmoji(cursor.getString(4));
                despesa.setDataDespesa(cursor.getString(5));
                despesas.add(despesa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return despesas;
    }

    public void apagarDespesa(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DESPESAS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Despesa> obterDespesasPorData(String dataInicio, String dataFim) {
        List<Despesa> despesas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_DESPESAS +
                        " WHERE date(" + COLUMN_DATA_DESPESA + ") BETWEEN date(?) AND date(?)",
                new String[]{dataInicio, dataFim});

        if (cursor.moveToFirst()) {
            do {
                Despesa despesa = new Despesa();
                despesa.setId(cursor.getInt(0));
                despesa.setNome(cursor.getString(1));
                despesa.setCategoria(cursor.getString(2));
                despesa.setValor(cursor.getDouble(3));
                despesa.setEmoji(cursor.getString(4));
                despesa.setDataDespesa(cursor.getString(5));
                despesas.add(despesa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return despesas;
    }

}