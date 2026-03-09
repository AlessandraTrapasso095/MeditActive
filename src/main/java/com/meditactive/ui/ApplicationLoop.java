/* serve per gestire il ciclo principale del menu fino all uscita richiesta dall utente */
package com.meditactive.ui;

import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;
import com.meditactive.repository.DataStore;
import com.meditactive.service.AvailableGoalsExporter;
import com.meditactive.service.CsvDataPersistenceService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ApplicationLoop {

    private final ConsoleUi consoleUi;
    private final DataStore dataStore;
    private final InputReader inputReader;
    private final AvailableGoalsExporter availableGoalsExporter;
    private final CsvDataPersistenceService csvDataPersistenceService;

    public ApplicationLoop(ConsoleUi consoleUi, DataStore dataStore, InputReader inputReader) {
        // usare la stessa ui in tutto il ciclo applicazione
        this.consoleUi = consoleUi;

        // per tenere i dati in memoria disponibili ai comandi del menu
        this.dataStore = dataStore;

        // per leggere input utente validato 
        this.inputReader = inputReader;

        // per gestire l'export csv degli obiettivi disponibili in un servizio dedicato 
        this.availableGoalsExporter = new AvailableGoalsExporter();

        // per salvare i csv dopo ogni modifica dati
        this.csvDataPersistenceService = new CsvDataPersistenceService();
    }

    public void run() {
        // per mantenere il programma attivo fino a quando l'utente sceglie uscita
        boolean isRunning = true;

        // serve per ripetere il menu e l'esecuzione comandi ad ogni interazione utente
        while (isRunning) {
            consoleUi.printMenu();

            // per leggere il comando da tastiera con controlli validazione
            MenuCommand selectedCommand = inputReader.readMenuCommand();

            // per eseguire l'azione corretta in base al comando selezionato
            isRunning = executeCommand(selectedCommand);

            System.out.println();
        }
    }

    private boolean executeCommand(MenuCommand command) {
        switch (command) {
            case SHOW_GOALS -> {
                // serve per mostrare la tabella completa degli obiettivi caricati in memoria
                consoleUi.printGoalsTable(dataStore.goals());
                return true;
            }
            case BOOK_GOAL -> {
                // serve per gestire il flusso completo di prenotazione obiettivo con controlli input
                handleBookGoalCommand();
                return true;
            }
            case CANCEL_BOOKING -> {
                // serve per gestire il flusso completo di cancellazione prenotazione e ripristino disponibilita
                handleCancelBookingCommand();
                return true;
            }
            case ADD_USER -> {
                // serve per gestire il flusso completo di inserimento nuovo utente con validazioni
                handleAddUserCommand();
                return true;
            }
            case EXPORT_AVAILABLE_GOALS -> {
                // serve per gestire il flusso completo di export csv degli obiettivi disponibili
                handleExportAvailableGoalsCommand();
                return true;
            }
            case EXIT -> {
                // serve per chiudere il ciclo menu quando l'utente vuole uscire
                consoleUi.printExitMessage();
                return false;
            }
            default -> {
                // serve per gestire eventuali stati inattesi e tenere il loop stabile
                consoleUi.printUnexpectedCommand();
                return true;
            }
        }
    }

    private void handleBookGoalCommand() {
        // serve per mostrare il titolo della procedura di prenotazione
        consoleUi.printBookGoalHeader();

        // serve per chiedere l'id obiettivo in input con validazione numerica positiva
        int goalId = inputReader.readPositiveInt("Inserisci ID obiettivo: ");

        // serve per recuperare l'obiettivo selezionato e verificare che esista
        Goal selectedGoal = dataStore.findGoalById(goalId);
        if (selectedGoal == null) {
            consoleUi.printGoalNotFound(goalId);
            return;
        }

        //per bloccare la prenotazione quando l'obiettivo non è piu disponibile
        if (!selectedGoal.disponibile()) {
            consoleUi.printGoalNotAvailable(selectedGoal);
            return;
        }

        // serve per chiedere l'id utente in input con validazione numerica positiva
        int userId = inputReader.readPositiveInt("Inserisci ID utente: ");

        // serve per recuperare l'utente selezionato e verificare che esista
        User selectedUser = dataStore.findUserById(userId);
        if (selectedUser == null) {
            consoleUi.printUserNotFound(userId);
            return;
        }

        // serve per assegnare automaticamente un nuovo id progressivo alla prenotazione
        int bookingId = dataStore.nextBookingId();

        // serve per valorizzare le date in automatico per la nuova prenotazione senza chiedere input extra
        LocalDate today = LocalDate.now();

        // per creare l'oggetto prenotazione con i dati validati raccolti dal terminale
        Booking newBooking = new Booking(bookingId, selectedGoal.id(), selectedUser.id(), today, today);

        // per salvare la prenotazione nel datastore in memoria
        dataStore.addBooking(newBooking);

        // per aggiornare la disponibilita dell'obiettivo da SI a NO dopo la prenotazione
        dataStore.setGoalAvailability(selectedGoal.id(), false);

        // serve per ristampare l'obiettivo aggiornato dopo il cambio disponibilita nel datastore
        Goal updatedGoal = dataStore.findGoalById(selectedGoal.id());

        // per confermare a video i dati principali della prenotazione appena creata
        consoleUi.printBookingCreated(newBooking, updatedGoal, selectedUser);

        // per persistire su csv la prenotazione creata e la disponibilita obiettivo aggiornata
        notifyPersistenceOutcome(persistCurrentData());
    }

    private void handleCancelBookingCommand() {
        // mi serve per mostrare il titolo della procedura di disdetta prenotazione
        consoleUi.printCancelBookingHeader();

        // er chiedere l'id prenotazione in input con validazione numerica positiva
        int bookingId = inputReader.readPositiveInt("Inserisci ID prenotazione: ");

        // per recuperare la prenotazione selezionata e verificare che esista
        Booking selectedBooking = dataStore.findBookingById(bookingId);
        if (selectedBooking == null) {
            consoleUi.printBookingNotFound(bookingId);
            return;
        }

        // per recuperare l'obiettivo associato alla prenotazione prima della cancellazione
        Goal linkedGoal = dataStore.findGoalById(selectedBooking.idObiettivo());
        if (linkedGoal == null) {
            consoleUi.printGoalNotFound(selectedBooking.idObiettivo());
            return;
        }

        // serve per rimuovere la prenotazione dal datastore in memoria
        dataStore.removeBookingById(bookingId);

        // serve per ripristinare la disponibilita dell obiettivo da NO a SI dopo la disdetta
        dataStore.setGoalAvailability(linkedGoal.id(), true);

        // serve per leggere l'obiettivo aggiornato e mostrarne lo stato finale in output
        Goal updatedGoal = dataStore.findGoalById(linkedGoal.id());

        // per confermare a video la cancellazione effettuata e il ripristino disponibilita
        consoleUi.printBookingCancelled(selectedBooking, updatedGoal);

        // serve per persistire su csv la cancellazione prenotazione e il ripristino disponibilita
        notifyPersistenceOutcome(persistCurrentData());
    }

    private void handleAddUserCommand() {
        // per mostrare il titolo della procedura di inserimento nuovo utente
        consoleUi.printAddUserHeader();

        // serve per leggere il nome obbligatorio inserito dall utente
        String nome = inputReader.readRequiredText("Inserisci nome: ");

        // per leggere il cognome obbligatorio inserito dall utente
        String cognome = inputReader.readRequiredText("Inserisci cognome: ");

        // per leggere e validare la data di nascita con i formati supportati
        LocalDate dataDiNascita = inputReader.readDate("Inserisci data di nascita (yyyy-MM-dd o dd/MM/yyyy): ");

        // serve per leggere l'indirizzo obbligatorio dell utente
        String indirizzo = inputReader.readRequiredText("Inserisci indirizzo: ");

        // mi serve per leggere il documento id obbligatorio dell utente
        String documentoId = inputReader.readRequiredText("Inserisci documento ID: ");

        // mi serve per bloccare la creazione se il documento e gia presente nel sistema
        if (dataStore.hasUserWithDocumentId(documentoId)) {
            consoleUi.printUserDocumentAlreadyUsed(documentoId);
            return;
        }

        // assegna automaticamente un nuovo id progressivo al nuovo utente
        int newUserId = dataStore.nextUserId();

        // costruisce il nuovo utente con tutti i dati validati raccolti dal terminale
        User newUser = new User(newUserId, nome, cognome, dataDiNascita, indirizzo, documentoId);

        // salva il nuovo utente nel datastore in memoria
        dataStore.addUser(newUser);

        // conferma a video i dati principali del nuovo utente appena creato
        consoleUi.printUserCreated(newUser);

        // persiste su csv il nuovo utente aggiunto dall utente via terminale
        notifyPersistenceOutcome(persistCurrentData());
    }

    private void handleExportAvailableGoalsCommand() {
        // mostra il titolo della procedura di export obiettivi disponibil
        consoleUi.printExportAvailableGoalsHeader();

        // serve per prendere dallo stato in memoria solo gli obiettivi con disponibilita SI
        List<Goal> availableGoals = dataStore.availableGoals();

        // per esportare il file csv con nome basato sulla data corrente
        Path exportFilePath = availableGoalsExporter.export(availableGoals, LocalDate.now());

        // per avvisare l'utente quando non erano presenti obiettivi disponibili
        if (availableGoals.isEmpty()) {
            consoleUi.printExportNoAvailableGoalsNotice();
        }

        // per confermare il risultato finale indicando file creato e quantita record esportati
        consoleUi.printExportCompleted(exportFilePath, availableGoals.size());
    }

    private boolean persistCurrentData() {
        // per tentare il salvataggio completo dei dati attuali su tutti i csv dell'applicazione
        try {
            csvDataPersistenceService.persistAll(dataStore);
            return true;
        } catch (RuntimeException exception) {
            // mostra il dettaglio errore salvataggio senza interrompere il loop applicativo
            consoleUi.printDataPersistenceError(exception.getMessage());
            return false;
        }
    }

    private void notifyPersistenceOutcome(boolean persisted) {
        // messaggi di esito persistenza dopo ogni comando che modifica dati
        if (persisted) {
            consoleUi.printDataPersistenceSuccess();
            return;
        }

        // avvisa l'utente che la modifica resta solo in memoria in caso di errore salvataggio
        consoleUi.printDataPersistenceWarning();
    }
}
