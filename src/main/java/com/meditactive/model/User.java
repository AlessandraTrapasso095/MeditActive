/* serve per rappresentare in memoria un utente letto dal file utenti.csv */
package com.meditactive.model;

import com.meditactive.service.DateParser;

import java.time.LocalDate;

public record User(
        int id,
        String nome,
        String cognome,
        LocalDate dataDiNascita,
        String indirizzo,
        String documentoId
) {

    public static User fromCsv(String[] columns) {
        // valida il numero minimo di colonne richieste dal csv utenti
        if (columns.length < 6) {
            throw new IllegalArgumentException("Riga utenti non valida: colonne insufficienti");
        }

        //trasforma la riga csv in un oggetto User 
        return new User(
                Integer.parseInt(columns[0].trim()),
                columns[1].trim(),
                columns[2].trim(),
                DateParser.parseDate(columns[3]),
                columns[4].trim(),
                columns[5].trim()
        );
    }
}
