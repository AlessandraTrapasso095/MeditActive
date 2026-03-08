/* questo file mi serve per rappresentare in memoria una prenotazione letta dal file prenotazioni.csv */
package com.meditactive.model;

import com.meditactive.service.DateParser;

import java.time.LocalDate;

public record Booking(
        int id,
        int idObiettivo,
        int idUtente,
        LocalDate dataInizio,
        LocalDate dataFine
) {

    public static Booking fromCsv(String[] columns) {
        // mi serve per validare il numero minimo di colonne richieste dal csv prenotazioni.
        if (columns.length < 5) {
            throw new IllegalArgumentException("Riga prenotazioni non valida: colonne insufficienti");
        }

        // mi serve per creare l oggetto Booking da una riga csv gia divisa per colonne.
        return new Booking(
                Integer.parseInt(columns[0].trim()),
                Integer.parseInt(columns[1].trim()),
                Integer.parseInt(columns[2].trim()),
                parseNullableDate(columns[3]),
                parseNullableDate(columns[4])
        );
    }

    private static LocalDate parseNullableDate(String rawDate) {
        // mi serve per gestire i campi data vuoti senza generare errore durante il caricamento iniziale.
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return null;
        }

        // mi serve per riusare la logica comune di parsing date.
        return DateParser.parseDate(rawDate);
    }
}
