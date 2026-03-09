/* questo file mi serve per mantenere in memoria i dati caricati dai csv e offrire metodi riusabili alle feature successive */
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
        // mi serve per indicizzare gli utenti per id, cosi posso cercarli in modo veloce e senza cicli ripetuti.
        this.usersById = indexById(users, User::id);

        // mi serve per indicizzare gli obiettivi per id con la stessa logica DRY usata per gli altri dati.
        this.goalsById = indexById(goals, Goal::id);

        // mi serve per indicizzare le prenotazioni per id e semplificare gli aggiornamenti futuri.
        this.bookingsById = indexById(bookings, Booking::id);
    }

    public Collection<User> users() {
        // mi serve per restituire una copia dei dati utenti evitando modifiche esterne dirette alla mappa interna.
        return new ArrayList<>(usersById.values());
    }

    public Collection<Goal> goals() {
        // mi serve per restituire una copia dei dati obiettivi evitando modifiche esterne dirette alla mappa interna.
        return new ArrayList<>(goalsById.values());
    }

    public List<Goal> availableGoals() {
        // mi serve per filtrare e restituire solo gli obiettivi con disponibilita attiva.
        return goalsById.values().stream()
                .filter(Goal::disponibile)
                .toList();
    }

    public Collection<Booking> bookings() {
        // mi serve per restituire una copia dei dati prenotazioni evitando modifiche esterne dirette alla mappa interna.
        return new ArrayList<>(bookingsById.values());
    }

    public int usersCount() {
        // mi serve per avere un conteggio utenti riusabile in output e controlli.
        return usersById.size();
    }

    public int goalsCount() {
        // mi serve per avere un conteggio obiettivi riusabile in output e controlli.
        return goalsById.size();
    }

    public int bookingsCount() {
        // mi serve per avere un conteggio prenotazioni riusabile in output e controlli.
        return bookingsById.size();
    }

    public User findUserById(int userId) {
        // mi serve per recuperare un utente specifico dal suo id.
        return usersById.get(userId);
    }

    public Goal findGoalById(int goalId) {
        // mi serve per recuperare un obiettivo specifico dal suo id.
        return goalsById.get(goalId);
    }

    public Booking findBookingById(int bookingId) {
        // mi serve per recuperare una prenotazione specifica dal suo id.
        return bookingsById.get(bookingId);
    }

    public int nextUserId() {
        // mi serve per calcolare il prossimo id utente senza duplicare logica in altre classi.
        return usersById.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    public int nextBookingId() {
        // mi serve per calcolare il prossimo id prenotazione senza duplicare logica in altre classi.
        return bookingsById.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    public void addBooking(Booking booking) {
        // mi serve per impedire inserimenti null e mantenere coerente lo stato in memoria.
        if (booking == null) {
            throw new IllegalArgumentException("Prenotazione non valida: valore nullo");
        }

        // mi serve per evitare duplicazione di id prenotazione durante nuove assegnazioni.
        if (bookingsById.containsKey(booking.id())) {
            throw new IllegalArgumentException("Prenotazione non valida: id gia presente " + booking.id());
        }

        // mi serve per salvare la nuova prenotazione indicizzata per id.
        bookingsById.put(booking.id(), booking);
    }

    public Booking removeBookingById(int bookingId) {
        // mi serve per cancellare la prenotazione selezionata e ottenere i suoi dati per i passaggi successivi.
        return bookingsById.remove(bookingId);
    }

    public void addUser(User user) {
        // mi serve per impedire inserimenti null e mantenere coerente lo stato utenti in memoria.
        if (user == null) {
            throw new IllegalArgumentException("Utente non valido: valore nullo");
        }

        // mi serve per evitare duplicazione di id utente durante nuove registrazioni.
        if (usersById.containsKey(user.id())) {
            throw new IllegalArgumentException("Utente non valido: id gia presente " + user.id());
        }

        // mi serve per salvare il nuovo utente indicizzato per id.
        usersById.put(user.id(), user);
    }

    public boolean hasUserWithDocumentId(String documentId) {
        // mi serve per normalizzare il documento e confrontarlo in modo robusto con i dati esistenti.
        String normalizedDocumentId = documentId == null ? "" : documentId.trim();

        // mi serve per evitare controlli inutili quando il valore documento e vuoto.
        if (normalizedDocumentId.isEmpty()) {
            return false;
        }

        // mi serve per verificare se esiste gia un utente con lo stesso documento id.
        return usersById.values().stream()
                .map(User::documentoId)
                .anyMatch(existingDocument -> existingDocument.equalsIgnoreCase(normalizedDocumentId));
    }

    public void setGoalAvailability(int goalId, boolean newAvailability) {
        // mi serve per recuperare l obiettivo da aggiornare e validare che esista davvero in memoria.
        Goal currentGoal = goalsById.get(goalId);
        if (currentGoal == null) {
            throw new IllegalArgumentException("Obiettivo non trovato con id " + goalId);
        }

        // mi serve per aggiornare lo stato disponibile dell obiettivo mantenendo l immutabilita del record.
        goalsById.put(goalId, currentGoal.withDisponibile(newAvailability));
    }

    private static <T> Map<Integer, T> indexById(Collection<T> source, ToIntFunction<T> idExtractor) {
        // mi serve per avere un metodo unico di indicizzazione per tutti i tipi dati, cosi rispetto il principio DRY.
        Map<Integer, T> indexedValues = new LinkedHashMap<>();

        // mi serve per popolare la mappa e bloccare eventuali id duplicati in modo esplicito.
        for (T element : source) {
            int id = idExtractor.applyAsInt(element);
            if (indexedValues.containsKey(id)) {
                throw new IllegalArgumentException("ID duplicato trovato nel caricamento: " + id);
            }
            indexedValues.put(id, element);
        }

        // mi serve per restituire la mappa indicizzata pronta all uso.
        return indexedValues;
    }
}
