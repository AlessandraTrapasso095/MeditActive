/* questo file mi serve per mantenere in memoria i dati caricati dai csv e offrire metodi riusabili alle feature successive */
package com.meditactive.repository;

import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
