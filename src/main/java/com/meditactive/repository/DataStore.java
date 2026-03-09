/* serve per mantenere in memoria i dati caricati dai csv */
package com.meditactive.repository;

import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public class DataStore {

    private final Map<Integer, User> usersById;
    private final Map<Integer, Goal> goalsById;
    private final Map<Integer, Booking> bookingsById;

    public DataStore(Collection<User> users, Collection<Goal> goals, Collection<Booking> bookings) {
        // serve per indicizzare gli utenti per id
        this.usersById = indexById(users, User::id);

        // qui gli obiettivi per id
        this.goalsById = indexById(goals, Goal::id);

        // qui le prenotazioni per id e semplificare gli aggiornamenti futuri
        this.bookingsById = indexById(bookings, Booking::id);
    }

    public Collection<User> users() {
        // restituisce una copia dei dati utenti evitando modifiche esterne dirette alla mappa interna
        return new ArrayList<>(usersById.values());
    }

    public Collection<Goal> goals() {
        // restituisce una copia dei dati obiettivi 
        return new ArrayList<>(goalsById.values());
    }

    public List<Goal> availableGoals() {
        // filtra e restituisce solo gli obiettivi con disponibilita attiva
        return goalsById.values().stream()
                .filter(Goal::disponibile)
                .toList();
    }

    public Collection<Booking> bookings() {
        // qui una copia dei dati prenotazioni 
        return new ArrayList<>(bookingsById.values());
    }

    public int usersCount() {
        // conteggio utenti 
        return usersById.size();
    }

    public int goalsCount() {
        // conteggio obiettivi 
        return goalsById.size();
    }

    public int bookingsCount() {
        // conteggio prenotazioni
        return bookingsById.size();
    }

    public User findUserById(int userId) {
        // recupera un utente specifico dal suo id
        return usersById.get(userId);
    }

    public Goal findGoalById(int goalId) {
        // recupera un obiettivo specifico dal suo id
        return goalsById.get(goalId);
    }

    public Booking findBookingById(int bookingId) {
        // recupera una prenotazione specifica dal suo id
        return bookingsById.get(bookingId);
    }

    public int nextUserId() {
        // calcola il prossimo id utente
        return usersById.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    public int nextBookingId() {
        // qui il prossimo id prenotazione 
        return bookingsById.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    public void addBooking(Booking booking) {
        // impedisce inserimenti null e mantiene coerente lo stato in memoria
        if (booking == null) {
            throw new IllegalArgumentException("Prenotazione non valida: valore nullo");
        }

        // evita duplicazione di id prenotazione durante nuove assegnazioni
        if (bookingsById.containsKey(booking.id())) {
            throw new IllegalArgumentException("Prenotazione non valida: id gia presente " + booking.id());
        }

        // mi serve per salvare la nuova prenotazione indicizzata per id
        bookingsById.put(booking.id(), booking);
    }

    public Booking removeBookingById(int bookingId) {
        // cancella la prenotazione selezionata e ottiene i suoi dati per i passaggi successivi
        return bookingsById.remove(bookingId);
    }

    public void addUser(User user) {
        // serve per impedire inserimenti null e mantenere coerente lo stato utenti in memoria
        if (user == null) {
            throw new IllegalArgumentException("Utente non valido: valore nullo");
        }

        // evita duplicazione di id utente durante nuove registrazioni
        if (usersById.containsKey(user.id())) {
            throw new IllegalArgumentException("Utente non valido: id gia presente " + user.id());
        }

        // salva il nuovo utente indicizzato per id
        usersById.put(user.id(), user);
    }

    public boolean hasUserWithDocumentId(String documentId) {
        // serve per normalizzare il documento e confrontarlo con i dati esistenti
        String normalizedDocumentId = documentId == null ? "" : documentId.trim();

        // evita controlli quando il valore documento è vuoto
        if (normalizedDocumentId.isEmpty()) {
            return false;
        }

        // verifica se esiste gia un utente con lo stesso documento id
        return usersById.values().stream()
                .map(User::documentoId)
                .anyMatch(existingDocument -> existingDocument.equalsIgnoreCase(normalizedDocumentId));
    }

    public void setGoalAvailability(int goalId, boolean newAvailability) {
        // serve per recuperare l'obiettivo da aggiornare e validare 
        Goal currentGoal = goalsById.get(goalId);
        if (currentGoal == null) {
            throw new IllegalArgumentException("Obiettivo non trovato con id " + goalId);
        }

        // serve per aggiornare lo stato disponibile dell'obiettivo 
        goalsById.put(goalId, currentGoal.withDisponibile(newAvailability));
    }

    private static <T> Map<Integer, T> indexById(Collection<T> source, ToIntFunction<T> idExtractor) {
        // metodo unico di indicizzazione per tutti i tipi dati
        Map<Integer, T> indexedValues = new LinkedHashMap<>();

        // mi serve per popolare la mappa e bloccare eventuali id duplicati in modo esplicito.
        for (T element : source) {
            int id = idExtractor.applyAsInt(element);
            if (indexedValues.containsKey(id)) {
                throw new IllegalArgumentException("ID duplicato trovato nel caricamento: " + id);
            }
            indexedValues.put(id, element);
        }

        // mi serve per restituire la mappa 
        return indexedValues;
    }
}
