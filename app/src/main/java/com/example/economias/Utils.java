package com.example.economias;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String formatarDataParaExibicao(String dataOriginal) {
        SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoExibicao = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date data = formatoBanco.parse(dataOriginal);
            return formatoExibicao.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return dataOriginal;
        }
    }

    public static String formatarDataParaBanco(String dataExibicao) {
        SimpleDateFormat formatoExibicao = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date data = formatoExibicao.parse(dataExibicao);
            return formatoBanco.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return dataExibicao;
        }
    }

}
