package com.example.economias.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String[] getPrimeiroEUltimoDiaDoMes(String dataDespesa) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date data = sdf.parse(dataDespesa);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String primeiroDia = sdf.format(calendar.getTime());

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String ultimoDia = sdf.format(calendar.getTime());

            return new String[]{primeiroDia, ultimoDia};

        } catch (ParseException e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }
}
