/* serve per avviare l applicazione, caricare i csv allo startup e mostrare la base della console */
package com.meditactive;

import com.meditactive.repository.DataStore;
import com.meditactive.service.CsvTableReader;
import com.meditactive.service.StartupDataLoader;
import com.meditactive.ui.ApplicationLoop;
import com.meditactive.ui.ConsoleUi;
import com.meditactive.ui.InputReader;
import com.meditactive.ui.LoadingSpinner;

public class Main {

    public static void main(String[] args) {
        // mi serve per inizializzare la UI console usata durante l'avvio
        ConsoleUi consoleUi = new ConsoleUi();

        // mi serve per mostrare subito il banner principale dell'applicazione
        consoleUi.printWelcome();

        // mi serve per preparare il servizio di caricamento dati dai csv
        StartupDataLoader startupDataLoader = new StartupDataLoader(new CsvTableReader());

        // mi serve per mostrare l'animazione di caricamento mentre leggo i file csv
        LoadingSpinner loadingSpinner = new LoadingSpinner();

        // mi serve per conservare il risultato del caricamento ed usarlo nelle schermate successive
        DataStore[] dataStoreHolder = new DataStore[1];

        try {
            // mi serve per eseguire il caricamento dati dentro lo spinner di avvio
            loadingSpinner.runWithSpinner("Caricamento dati da file csv", () -> dataStoreHolder[0] = startupDataLoader.loadAllData());

            // mi serve per mostrare il riepilogo dei dati caricati una volta completato lo startup
            consoleUi.printStartupSummary(dataStoreHolder[0]);

            // mi serve per avviare il loop principale del menu fino alla scelta di uscita utente
            ApplicationLoop applicationLoop = new ApplicationLoop(consoleUi, dataStoreHolder[0], new InputReader());
            applicationLoop.run();
        } catch (RuntimeException exception) {
            // mi serve per stampare un errore leggibile se qualcosa va storto durante lo startup
            System.err.println("Errore durante lo startup: " + exception.getMessage());
        }
    }
}
