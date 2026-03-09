/* questo file mi serve per centralizzare la stampa della UI console in modo pulito e riusabile */
package com.meditactive.ui;

import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;
import com.meditactive.repository.DataStore;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ConsoleUi {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final int[] GOAL_COLUMN_WIDTHS = {6, 22, 36, 14, 10, 12};
    private static final String[] GOAL_HEADERS = {"ID", "Nome", "Descrizione", "Tipologia", "Durata", "Disponibile"};

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

    public void printGoalsTable(Collection<Goal> goals) {
        // mi serve per ordinare gli obiettivi per id e avere sempre una vista coerente.
        List<Goal> sortedGoals = goals.stream().sorted(Comparator.comparingInt(Goal::id)).toList();

        // mi serve per informare l utente quando non ci sono obiettivi disponibili in memoria.
        if (sortedGoals.isEmpty()) {
            System.out.println("Nessun obiettivo presente nel sistema.");
            return;
        }

        // mi serve per mostrare il titolo della sezione obiettivi con stile coerente col resto della ui.
        System.out.println(BOLD + CYAN + "Elenco obiettivi nel sistema" + RESET);

        // mi serve per calcolare una sola volta il separatore tabellare e riusarlo in tutti i punti necessari.
        String separator = buildSeparator(GOAL_COLUMN_WIDTHS);
        System.out.println(separator);
        System.out.println(buildRow(GOAL_HEADERS, GOAL_COLUMN_WIDTHS));
        System.out.println(separator);

        // mi serve per stampare ogni obiettivo su una riga tabellare con i campi richiesti dalla traccia.
        for (Goal goal : sortedGoals) {
            String[] rowValues = {
                    String.valueOf(goal.id()),
                    goal.nome(),
                    goal.descrizione(),
                    goal.tipologia(),
                    String.valueOf(goal.durata()),
                    goal.disponibile() ? "SI" : "NO"
            };
            System.out.println(buildRow(rowValues, GOAL_COLUMN_WIDTHS));
        }

        // mi serve per chiudere la tabella con la stessa linea separatrice.
        System.out.println(separator);
    }

    public void printBookGoalHeader() {
        // mi serve per aprire la sezione del comando 2 con contesto chiaro per l utente.
        System.out.println(BOLD + CYAN + "Impostare un obiettivo esistente" + RESET);
        System.out.println("Inserisci i dati richiesti per creare la prenotazione.");
    }

    public void printGoalNotFound(int goalId) {
        // mi serve per segnalare in modo esplicito quando l id obiettivo non esiste nel sistema.
        System.out.println("Operazione annullata: obiettivo con ID " + goalId + " non trovato.");
    }

    public void printGoalNotAvailable(Goal goal) {
        // mi serve per bloccare prenotazioni duplicate su obiettivi gia non disponibili.
        System.out.println("Operazione annullata: obiettivo con ID " + goal.id() + " gia non disponibile.");
    }

    public void printUserNotFound(int userId) {
        // mi serve per segnalare in modo esplicito quando l id utente non esiste nel sistema.
        System.out.println("Operazione annullata: utente con ID " + userId + " non trovato.");
    }

    public void printBookingCreated(Booking booking, Goal goal, User user) {
        // mi serve per confermare all utente che la prenotazione e stata creata correttamente.
        System.out.println(BOLD + GREEN + "Prenotazione creata con successo." + RESET);
        System.out.println("- ID Prenotazione: " + booking.id());
        System.out.println("- ID Obiettivo: " + goal.id() + " (" + goal.nome() + ")");
        System.out.println("- ID Utente: " + user.id() + " (" + user.nome() + " " + user.cognome() + ")");
        System.out.println("- Data inizio: " + booking.dataInizio());
        System.out.println("- Data fine: " + booking.dataFine());
        System.out.println("- Disponibilita obiettivo aggiornata a: NO");
    }

    public void printCancelBookingHeader() {
        // mi serve per aprire la sezione del comando 3 con contesto chiaro per l utente.
        System.out.println(BOLD + CYAN + "Disdire la prenotazione di un obiettivo" + RESET);
        System.out.println("Inserisci l ID prenotazione da cancellare.");
    }

    public void printBookingNotFound(int bookingId) {
        // mi serve per segnalare quando l id prenotazione inserito non esiste nel sistema.
        System.out.println("Operazione annullata: prenotazione con ID " + bookingId + " non trovata.");
    }

    public void printBookingCancelled(Booking booking, Goal goal) {
        // mi serve per confermare la cancellazione e l aggiornamento disponibilita obiettivo.
        System.out.println(BOLD + GREEN + "Prenotazione cancellata con successo." + RESET);
        System.out.println("- ID Prenotazione rimossa: " + booking.id());
        System.out.println("- ID Obiettivo: " + goal.id() + " (" + goal.nome() + ")");
        System.out.println("- Disponibilita obiettivo aggiornata a: SI");
    }

    public void printAddUserHeader() {
        // mi serve per aprire la sezione del comando 4 con istruzioni sintetiche per l inserimento utente.
        System.out.println(BOLD + CYAN + "Aggiungere nuovo utente" + RESET);
        System.out.println("Inserisci nome, cognome, data di nascita, indirizzo e documento ID.");
    }

    public void printUserDocumentAlreadyUsed(String documentId) {
        // mi serve per bloccare la creazione di utenti duplicati sul campo documento id.
        System.out.println("Operazione annullata: documento ID " + documentId + " gia presente nel sistema.");
    }

    public void printUserCreated(User user) {
        // mi serve per confermare la creazione utente con i principali dati salvati.
        System.out.println(BOLD + GREEN + "Utente creato con successo." + RESET);
        System.out.println("- ID Utente: " + user.id());
        System.out.println("- Nome: " + user.nome());
        System.out.println("- Cognome: " + user.cognome());
        System.out.println("- Data di nascita: " + user.dataDiNascita());
        System.out.println("- Indirizzo: " + user.indirizzo());
        System.out.println("- Documento ID: " + user.documentoId());
    }

    public void printExportAvailableGoalsHeader() {
        // mi serve per aprire la sezione del comando 5 con contesto chiaro per l utente.
        System.out.println(BOLD + CYAN + "Esportare un file con gli obiettivi disponibili" + RESET);
    }

    public void printExportCompleted(Path exportPath, int exportedGoalsCount) {
        // mi serve per confermare il completamento export e mostrare percorso file creato.
        System.out.println(BOLD + GREEN + "Export completato con successo." + RESET);
        System.out.println("- Obiettivi esportati: " + exportedGoalsCount);
        System.out.println("- File creato: " + exportPath);
    }

    public void printExportNoAvailableGoalsNotice() {
        // mi serve per avvisare che non c erano obiettivi disponibili, ma il file viene creato con solo header.
        System.out.println("Nessun obiettivo disponibile trovato: il file export contiene solo l intestazione.");
    }

    public void printDataPersistenceSuccess() {
        // mi serve per confermare che le modifiche in memoria sono state salvate correttamente sui file csv.
        System.out.println("Dati salvati su file csv.");
    }

    public void printDataPersistenceError(String errorMessage) {
        // mi serve per segnalare un errore di salvataggio con un messaggio comprensibile.
        System.out.println("Errore salvataggio csv: " + errorMessage);
    }

    public void printDataPersistenceWarning() {
        // mi serve per avvisare che la modifica e stata applicata in memoria ma non scritta su file.
        System.out.println("Attenzione: modifica applicata in memoria ma non persistita su file.");
    }

    public void printExitMessage() {
        // mi serve per mostrare un messaggio di chiusura quando l utente sceglie il comando 0.
        System.out.println(BOLD + "Uscita in corso. A presto." + RESET);
    }

    public void printUnexpectedCommand() {
        // mi serve per gestire eventuali casi non previsti senza interrompere il flusso del programma.
        System.out.println("Comando inatteso: il menu continua in sicurezza.");
    }

    private String buildSeparator(int[] columnWidths) {
        // mi serve per creare la linea orizzontale della tabella in base alle larghezze colonne.
        StringBuilder separatorBuilder = new StringBuilder("+");

        // mi serve per aggiungere un blocco tratteggiato per ogni colonna mantenendo lo stesso formato.
        for (int width : columnWidths) {
            separatorBuilder.append("-".repeat(width + 2)).append("+");
        }

        // mi serve per restituire la stringa separatrice pronta per la stampa.
        return separatorBuilder.toString();
    }

    private String buildRow(String[] values, int[] columnWidths) {
        // mi serve per costruire una riga tabellare allineata con celle a larghezza fissa.
        StringBuilder rowBuilder = new StringBuilder("|");

        // mi serve per inserire ogni valore nel proprio spazio evitando fuoriuscite visive.
        for (int index = 0; index < values.length; index++) {
            rowBuilder.append(" ").append(fitCell(values[index], columnWidths[index])).append(" |");
        }

        // mi serve per restituire la riga completa pronta da stampare in console.
        return rowBuilder.toString();
    }

    private String fitCell(String value, int width) {
        // mi serve per normalizzare i valori null e garantire sempre una stringa valida.
        String safeValue = value == null ? "" : value.trim();

        // mi serve per troncare i testi troppo lunghi e mantenere la tabella leggibile.
        if (safeValue.length() > width) {
            return safeValue.substring(0, Math.max(0, width - 3)) + "...";
        }

        // mi serve per allineare a sinistra i valori corti usando padding a spazi.
        return String.format("%-" + width + "s", safeValue);
    }
}
