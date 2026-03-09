/* serve per gestire il parsing delle date csv */
package com.meditactive.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DateParser {

    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    private DateParser() {
        // evitadi creare oggetti DateParser
        throw new IllegalStateException("Classe di utility non istanziabile");
    }

    public static LocalDate parseDate(String rawDate) {
        // serve per pulire il valore ricevuto dal csv prima del parsing
        String normalizedValue = rawDate == null ? "" : rawDate.trim();

        // per controllare subito i casi vuoti e dare un errore chiaro all'utente
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Data mancante nel file csv");
        }

        // serve per provare piu formati senza duplicare codice in ogni classe modello
        for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
            try {
                // converte la stringa data in LocalDate usando il formato corrente
                return LocalDate.parse(normalizedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // serve per continuare il tentativo col formato successivo
            }
        }

        // per segnalare quando la data non rispetta i formati supportati
        throw new IllegalArgumentException("Formato data non supportato: " + rawDate);
    }
}
