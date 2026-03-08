/* questo file mi serve per gestire il ciclo principale del menu fino all uscita richiesta dall utente */
package com.meditactive.ui;

import com.meditactive.repository.DataStore;

public class ApplicationLoop {

    private final ConsoleUi consoleUi;
    private final DataStore dataStore;
    private final InputReader inputReader;

    public ApplicationLoop(ConsoleUi consoleUi, DataStore dataStore, InputReader inputReader) {
        // mi serve per usare la stessa ui in tutto il ciclo applicazione.
        this.consoleUi = consoleUi;

        // mi serve per tenere i dati in memoria disponibili ai comandi del menu.
        this.dataStore = dataStore;

        // mi serve per leggere input utente validato senza duplicare logica.
        this.inputReader = inputReader;
    }

    public void run() {
        // mi serve per mantenere il programma attivo fino a quando l utente sceglie uscita.
        boolean isRunning = true;

        // mi serve per ripetere il menu e l esecuzione comandi ad ogni interazione utente.
        while (isRunning) {
            consoleUi.printMenu();

            // mi serve per leggere il comando da tastiera con controlli validazione.
            MenuCommand selectedCommand = inputReader.readMenuCommand();

            // mi serve per eseguire l azione corretta in base al comando selezionato.
            isRunning = executeCommand(selectedCommand);

            System.out.println();
        }
    }

    private boolean executeCommand(MenuCommand command) {
        // mi serve per instradare ogni comando e mantenere un punto unico di controllo flusso.
        switch (command) {
            case SHOW_GOALS -> {
                // mi serve per indicare che il comando verra implementato nello step dedicato.
                consoleUi.printCommandPending(command, dataStore);
                return true;
            }
            case BOOK_GOAL -> {
                // mi serve per indicare che il comando verra implementato nello step dedicato.
                consoleUi.printCommandPending(command, dataStore);
                return true;
            }
            case CANCEL_BOOKING -> {
                // mi serve per indicare che il comando verra implementato nello step dedicato.
                consoleUi.printCommandPending(command, dataStore);
                return true;
            }
            case ADD_USER -> {
                // mi serve per indicare che il comando verra implementato nello step dedicato.
                consoleUi.printCommandPending(command, dataStore);
                return true;
            }
            case EXPORT_AVAILABLE_GOALS -> {
                // mi serve per indicare che il comando verra implementato nello step dedicato.
                consoleUi.printCommandPending(command, dataStore);
                return true;
            }
            case EXIT -> {
                // mi serve per chiudere il ciclo menu quando l utente vuole uscire.
                consoleUi.printExitMessage();
                return false;
            }
            default -> {
                // mi serve per gestire eventuali stati inattesi e tenere il loop stabile.
                consoleUi.printUnexpectedCommand();
                return true;
            }
        }
    }
}
