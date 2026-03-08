/* questo file mi serve per centralizzare i percorsi dei file csv e riusarli senza duplicazioni */
package com.meditactive.config;

public final class AppPaths {

    public static final String USERS_CSV = "data/utenti.csv";
    public static final String GOALS_CSV = "data/obiettivi.csv";
    public static final String BOOKINGS_CSV = "data/prenotazioni.csv";

    private AppPaths() {
        // mi serve per impedire la creazione di oggetti AppPaths perche uso solo costanti statiche.
        throw new IllegalStateException("Classe di utility non istanziabile");
    }
}
