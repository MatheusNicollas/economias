package com.example.economias.hashmap;

import java.util.HashMap;

public class DespesasPorCategoria {
    public final HashMap<String, Double> totaisPorCategoria;
    public final HashMap<String, String> categoriasEmoji;

    public DespesasPorCategoria(HashMap<String, Double> totaisPorCategoria, HashMap<String, String> categoriasEmoji) {
        this.totaisPorCategoria = totaisPorCategoria;
        this.categoriasEmoji = categoriasEmoji;
    }
}
