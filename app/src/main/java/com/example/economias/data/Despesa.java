package com.example.economias.data;

public class Despesa {

    private int id;
    private String nome;
    private String categoria;
    private Double valor;
    private String dataDespesa;

    public String getDataDespesa() {
        return dataDespesa;
    }

    public void setDataDespesa(String dataDespesa) {
        this.dataDespesa = dataDespesa;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    private String emoji;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
