/* questo file mi serve per rappresentare in memoria un obiettivo letto dal file obiettivi.csv */
package com.meditactive.model;

public record Goal(
        int id,
        String nome,
        String descrizione,
        String tipologia,
        int durata,
        boolean disponibile
) {

    public static Goal fromCsv(String[] columns) {
        // mi serve per validare il numero minimo di colonne richieste dal csv obiettivi.
        if (columns.length < 6) {
            throw new IllegalArgumentException("Riga obiettivi non valida: colonne insufficienti");
        }

        // mi serve per convertire il campo Disponibile in boolean in modo uniforme in tutta l app.
        boolean isAvailable = "SI".equalsIgnoreCase(columns[5].trim());

        // mi serve per creare l oggetto Goal con i dati convertiti dal csv.
        return new Goal(
                Integer.parseInt(columns[0].trim()),
                columns[1].trim(),
                columns[2].trim(),
                columns[3].trim(),
                Integer.parseInt(columns[4].trim()),
                isAvailable
        );
    }

    public Goal withDisponibile(boolean nuovoValore) {
        // mi serve per aggiornare il valore disponibile senza mutare direttamente l oggetto record.
        return new Goal(id, nome, descrizione, tipologia, durata, nuovoValore);
    }
}
