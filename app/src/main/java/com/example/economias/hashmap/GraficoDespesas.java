package com.example.economias;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class GraficoDespesas {
    public final ArrayList<String> resumoLista;
    public final ArrayList<PieEntry> pieEntries;

    public GraficoDespesas(ArrayList<String> resumoLista, ArrayList<PieEntry> pieEntries) {
        this.resumoLista = resumoLista;
        this.pieEntries = pieEntries;
    }
}
