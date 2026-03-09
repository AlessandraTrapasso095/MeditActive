/* questo file mi serve per salvare su csv i dati in memoria dopo le modifiche effettuate dai comandi utente */
package com.meditactive.service;

import com.meditactive.config.AppPaths;
import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;
import com.meditactive.repository.DataStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CsvDataPersistenceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String SEPARATOR = ";";

    public void persistAll(DataStore dataStore) {
        // mi serve per salvare tutti i csv in modo centralizzato evitando duplicazioni nei vari comandi.
        writeUsers(dataStore.users());

        // mi serve per salvare gli obiettivi con disponibilita aggiornata dopo prenotazioni o disdette.
        writeGoals(dataStore.goals());

        // mi serve per salvare le prenotazioni aggiornate dopo inserimenti e cancellazioni.
        writeBookings(dataStore.bookings());
    }

    private void writeUsers(Collection<User> users) {
        // mi serve per convertire gli utenti in lista ordinata per id prima della scrittura su file.
        List<User> sortedUsers = users.stream().sorted(Comparator.comparingInt(User::id)).toList();

        // mi serve per puntare al file utenti.csv configurato nei path applicativi.
        Path usersPath = Path.of(AppPaths.USERS_CSV);

        // mi serve per scrivere il file utenti completo con intestazione e righe dati aggiornate.
        try (BufferedWriter writer = Files.newBufferedWriter(usersPath)) {
            writer.write("# questo file mi serve per contenere i dati utenti in formato csv separato da ;");
            writer.newLine();
            writer.write("ID;Nome;Cognome;DataDiNascita;Indirizzo;DocumentoID");
            writer.newLine();

            // mi serve per scrivere una riga per ciascun utente presente in memoria.
            for (User user : sortedUsers) {
                writer.write(user.id() + SEPARATOR
                        + sanitize(user.nome()) + SEPARATOR
                        + sanitize(user.cognome()) + SEPARATOR
                        + formatDate(user.dataDiNascita()) + SEPARATOR
                        + sanitize(user.indirizzo()) + SEPARATOR
                        + sanitize(user.documentoId()));
                writer.newLine();
            }
        } catch (IOException exception) {
            // mi serve per rilanciare un errore descrittivo quando il salvataggio utenti fallisce.
            throw new IllegalStateException("Errore nel salvataggio file utenti: " + usersPath, exception);
        }
    }

    private void writeGoals(Collection<Goal> goals) {
        // mi serve per convertire gli obiettivi in lista ordinata per id prima della scrittura su file.
        List<Goal> sortedGoals = goals.stream().sorted(Comparator.comparingInt(Goal::id)).toList();

        // mi serve per puntare al file obiettivi.csv configurato nei path applicativi.
        Path goalsPath = Path.of(AppPaths.GOALS_CSV);

        // mi serve per scrivere il file obiettivi completo con disponibilita aggiornata.
        try (BufferedWriter writer = Files.newBufferedWriter(goalsPath)) {
            writer.write("# questo file mi serve per contenere i dati obiettivi in formato csv separato da ;");
            writer.newLine();
            writer.write("ID;Nome;Descrizione;Tipologia;Durata;Disponibile");
            writer.newLine();

            // mi serve per scrivere una riga per ciascun obiettivo presente in memoria.
            for (Goal goal : sortedGoals) {
                writer.write(goal.id() + SEPARATOR
                        + sanitize(goal.nome()) + SEPARATOR
                        + sanitize(goal.descrizione()) + SEPARATOR
                        + sanitize(goal.tipologia()) + SEPARATOR
                        + goal.durata() + SEPARATOR
                        + (goal.disponibile() ? "SI" : "NO"));
                writer.newLine();
            }
        } catch (IOException exception) {
            // mi serve per rilanciare un errore descrittivo quando il salvataggio obiettivi fallisce.
            throw new IllegalStateException("Errore nel salvataggio file obiettivi: " + goalsPath, exception);
        }
    }

    private void writeBookings(Collection<Booking> bookings) {
        // mi serve per convertire le prenotazioni in lista ordinata per id prima della scrittura su file.
        List<Booking> sortedBookings = bookings.stream().sorted(Comparator.comparingInt(Booking::id)).toList();

        // mi serve per puntare al file prenotazioni.csv configurato nei path applicativi.
        Path bookingsPath = Path.of(AppPaths.BOOKINGS_CSV);

        // mi serve per scrivere il file prenotazioni completo con le nuove prenotazioni salvate.
        try (BufferedWriter writer = Files.newBufferedWriter(bookingsPath)) {
            writer.write("# questo file mi serve per contenere le prenotazioni obiettivi in formato csv separato da ;");
            writer.newLine();
            writer.write("ID;IDObiettivo;IDUtente;DataInizio;DataFine");
            writer.newLine();

            // mi serve per scrivere una riga per ciascuna prenotazione presente in memoria.
            for (Booking booking : sortedBookings) {
                writer.write(booking.id() + SEPARATOR
                        + booking.idObiettivo() + SEPARATOR
                        + booking.idUtente() + SEPARATOR
                        + formatNullableDate(booking.dataInizio()) + SEPARATOR
                        + formatNullableDate(booking.dataFine()));
                writer.newLine();
            }
        } catch (IOException exception) {
            // mi serve per rilanciare un errore descrittivo quando il salvataggio prenotazioni fallisce.
            throw new IllegalStateException("Errore nel salvataggio file prenotazioni: " + bookingsPath, exception);
        }
    }

    private String sanitize(String value) {
        // mi serve per evitare rotture del formato csv sostituendo eventuali separatori nel testo.
        String safeValue = value == null ? "" : value.trim();
        return safeValue.replace(SEPARATOR, ",");
    }

    private String formatDate(LocalDate date) {
        // mi serve per convertire la data obbligatoria in formato iso stabile per il csv.
        return date.format(DATE_FORMATTER);
    }

    private String formatNullableDate(LocalDate date) {
        // mi serve per gestire date opzionali nelle prenotazioni senza generare errori di formattazione.
        if (date == null) {
            return "";
        }

        // mi serve per convertire la data opzionale valorizzata in formato iso stabile per il csv.
        return date.format(DATE_FORMATTER);
    }
}
