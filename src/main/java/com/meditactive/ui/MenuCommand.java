/* serve per rappresentare i comandi del menu */
package com.meditactive.ui;

import java.util.Arrays;

public enum MenuCommand {
    SHOW_GOALS(1, "Visualizzare tutti gli obiettivi all interno del sistema"),
    BOOK_GOAL(2, "Impostare un obiettivo esistente"),
    CANCEL_BOOKING(3, "Disdire la prenotazione di un obiettivo"),
    ADD_USER(4, "Aggiungere nuovo utente"),
    EXPORT_AVAILABLE_GOALS(5, "Esportare un file con gli obiettivi disponibili"),
    EXIT(0, "Uscire dal programma");

    private final int code;
    private final String description;

    MenuCommand(int code, String description) {
        // salva il numero comando associato all'enum
        this.code = code;

        // salva la descrizione testuale del comando
        this.description = description;
    }

    public int code() {
        // legge il codice numerico del comando dove mi serve fare confronti
        return code;
    }

    public String description() {
        // legge la descrizione del comando nelle schermate console
        return description;
    }

    public static MenuCommand fromCode(int inputCode) {
        // converte un numero digitato dall'utente in un comando valido del sistema
        return Arrays.stream(values())
                .filter(command -> command.code == inputCode)
                .findFirst()
                .orElse(null);
    }
}
