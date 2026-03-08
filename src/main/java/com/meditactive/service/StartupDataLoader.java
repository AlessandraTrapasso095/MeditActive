/* questo file mi serve per caricare automaticamente tutti i csv allo startup e costruire il DataStore in memoria */
package com.meditactive.service;

import com.meditactive.config.AppPaths;
import com.meditactive.model.Booking;
import com.meditactive.model.Goal;
import com.meditactive.model.User;
import com.meditactive.repository.DataStore;

import java.util.List;

public class StartupDataLoader {

    private final CsvTableReader csvTableReader;

    public StartupDataLoader(CsvTableReader csvTableReader) {
        // mi serve per usare una dipendenza esterna riusabile e facilmente testabile.
        this.csvTableReader = csvTableReader;
    }

    public DataStore loadAllData() {
        // mi serve per leggere tutti gli utenti dal csv dedicato.
        List<User> users = csvTableReader.readTable(AppPaths.USERS_CSV, User::fromCsv);

        // mi serve per leggere tutti gli obiettivi dal csv dedicato.
        List<Goal> goals = csvTableReader.readTable(AppPaths.GOALS_CSV, Goal::fromCsv);

        // mi serve per leggere tutte le prenotazioni dal csv dedicato.
        List<Booking> bookings = csvTableReader.readTable(AppPaths.BOOKINGS_CSV, Booking::fromCsv);

        // mi serve per costruire lo stato iniziale in memoria usato dal resto dell applicazione.
        return new DataStore(users, goals, bookings);
    }
}
