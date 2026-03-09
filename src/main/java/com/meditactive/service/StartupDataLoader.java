/* serve per caricare automaticamente tutti i csv allo startup e costruire il DataStore in memoria */
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
        // dipendenza esterna 
        this.csvTableReader = csvTableReader;
    }

    public DataStore loadAllData() {
        // tutti gli utenti dal csv dedicato
        List<User> users = csvTableReader.readTable(AppPaths.USERS_CSV, User::fromCsv);

        // tutti gli obiettivi dal csv dedicato
        List<Goal> goals = csvTableReader.readTable(AppPaths.GOALS_CSV, Goal::fromCsv);

        // tutte le prenotazioni dal csv dedicato
        List<Booking> bookings = csvTableReader.readTable(AppPaths.BOOKINGS_CSV, Booking::fromCsv);

        // serve per costruire lo stato iniziale in memoria usato dal resto dell'applicazione.
        return new DataStore(users, goals, bookings);
    }
}
