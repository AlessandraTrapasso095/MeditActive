/* questo file mi serve per esportare in csv gli obiettivi disponibili con un nome file basato sulla data corrente */
package com.meditactive.service;

import com.meditactive.model.Goal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class AvailableGoalsExporter {

    private static final String OUTPUT_DIRECTORY = "exports";
    private static final String CSV_SEPARATOR = ";";
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public Path export(Collection<Goal> goals, LocalDate currentDate) {
        // mi serve per ottenere una lista ordinata per id e mantenere l export sempre leggibile e coerente.
        List<Goal> sortedGoals = goals.stream().sorted(Comparator.comparingInt(Goal::id)).toList();

        // mi serve per costruire il nome file richiesto dalla traccia con la data corrente.
        String exportFileName = "obiettivi_" + currentDate.format(FILE_DATE_FORMATTER) + ".csv";

        // mi serve per puntare al percorso finale di export dentro la cartella dedicata.
        Path outputPath = Path.of(OUTPUT_DIRECTORY, exportFileName);

        // mi serve per creare automaticamente la cartella export se non esiste gia.
        createOutputDirectory(outputPath.getParent());

        // mi serve per scrivere header e righe csv in un unico flusso con chiusura automatica.
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // mi serve per scrivere la riga header con le colonne richieste dal file di export.
            writer.write("ID;Nome;Descrizione;Tipologia;Durata");
            writer.newLine();

            // mi serve per scrivere una riga csv per ogni obiettivo disponibile incluso nell export.
            for (Goal goal : sortedGoals) {
                writer.write(buildGoalCsvRow(goal));
                writer.newLine();
            }
        } catch (IOException exception) {
            // mi serve per rilanciare un errore semplificato quando la scrittura file fallisce.
            throw new IllegalStateException("Errore durante la creazione del file export: " + outputPath, exception);
        }

        // mi serve per restituire il percorso del file creato e mostrarlo in output utente.
        return outputPath;
    }

    private void createOutputDirectory(Path directoryPath) {
        // mi serve per gestire in sicurezza i casi in cui il percorso directory e nullo.
        if (directoryPath == null) {
            throw new IllegalArgumentException("Percorso directory export non valido");
        }

        // mi serve per creare la directory export solo quando manca nel filesystem.
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException exception) {
            // mi serve per rilanciare un errore chiaro quando non e possibile creare la cartella export.
            throw new IllegalStateException("Errore durante la creazione della cartella export: " + directoryPath, exception);
        }
    }

    private String buildGoalCsvRow(Goal goal) {
        // mi serve per costruire la riga csv con il separatore ; senza duplicare formattazioni in giro.
        return goal.id() + CSV_SEPARATOR
                + goal.nome() + CSV_SEPARATOR
                + goal.descrizione() + CSV_SEPARATOR
                + goal.tipologia() + CSV_SEPARATOR
                + goal.durata();
    }
}
