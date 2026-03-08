/* questo file mi serve per centralizzare la stampa della UI console in modo pulito e riusabile */
package com.meditactive.ui;

import com.meditactive.repository.DataStore;

public class ConsoleUi {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";

    public void printWelcome() {
        // mi serve per mostrare un header iniziale con look piu professionale in console.
        System.out.println(BOLD + CYAN + "============================================================" + RESET);
        System.out.println(BOLD + CYAN + "                     MEDITACTIVE CLI                        " + RESET);
        System.out.println(BOLD + CYAN + "============================================================" + RESET);
        System.out.println("App Java per gestire obiettivi, utenti e prenotazioni.");
        System.out.println();
    }

    public void printStartupSummary(DataStore dataStore) {
        // mi serve per confermare all utente che i csv sono stati caricati in memoria correttamente.
        System.out.println(BOLD + GREEN + "Caricamento completato:" + RESET);
        System.out.println("- Utenti caricati: " + dataStore.usersCount());
        System.out.println("- Obiettivi caricati: " + dataStore.goalsCount());
        System.out.println("- Prenotazioni caricate: " + dataStore.bookingsCount());
        System.out.println();
    }

    public void printMenu() {
        // mi serve per stampare il menu comandi da mostrare in ogni ciclo dell applicazione in modo DRY.
        System.out.println(BOLD + "Comandi disponibili" + RESET);
        for (MenuCommand command : MenuCommand.values()) {
            // mi serve per stampare in riga unica codice e descrizione di ogni comando disponibile.
            System.out.println(command.code() + " - " + command.description());
        }
        System.out.println();
    }

    public void printCommandPending(MenuCommand command, DataStore dataStore) {
        // mi serve per notificare all utente che il comando scelto verra implementato nello step dedicato successivo.
        System.out.println("Hai scelto il comando " + command.code() + ": " + command.description() + ".");
        System.out.println("Questo comando verra implementato nel prossimo step.");

        // mi serve per mostrare che i dati in memoria restano disponibili durante il loop menu.
        System.out.println("Stato corrente -> utenti: " + dataStore.usersCount() + ", obiettivi: " + dataStore.goalsCount() + ", prenotazioni: " + dataStore.bookingsCount());
    }

    public void printExitMessage() {
        // mi serve per mostrare un messaggio di chiusura quando l utente sceglie il comando 0.
        System.out.println(BOLD + "Uscita in corso. A presto." + RESET);
    }

    public void printUnexpectedCommand() {
        // mi serve per gestire eventuali casi non previsti senza interrompere il flusso del programma.
        System.out.println("Comando inatteso: il menu continua in sicurezza.");
    }
}
