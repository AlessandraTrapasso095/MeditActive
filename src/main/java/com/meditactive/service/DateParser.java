/* questo file mi serve per gestire il parsing delle date csv in un unico punto, rispettando DRY */
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
        // mi serve per evitare di creare oggetti DateParser, perche questa classe espone solo metodi statici.
        throw new IllegalStateException("Classe di utility non istanziabile");
    }

    public static LocalDate parseDate(String rawDate) {
        // mi serve per pulire il valore ricevuto dal csv prima del parsing.
        String normalizedValue = rawDate == null ? "" : rawDate.trim();

        // mi serve per controllare subito i casi vuoti e dare un errore chiaro all utente.
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Data mancante nel file csv");
        }

        // mi serve per provare piu formati senza duplicare codice in ogni classe modello.
        for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
            try {
                // mi serve per convertire la stringa data in LocalDate usando il formato corrente.
                return LocalDate.parse(normalizedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // mi serve per continuare il tentativo col formato successivo.
            }
        }

        // mi serve per segnalare quando la data non rispetta i formati supportati.
        throw new IllegalArgumentException("Formato data non supportato: " + rawDate);
    }
}
