/* serve per esportare in csv gli obiettivi disponibili */
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
        // lista ordinata per id
        List<Goal> sortedGoals = goals.stream().sorted(Comparator.comparingInt(Goal::id)).toList();

        // nome file con la data corrente.
        String exportFileName = "obiettivi_" + currentDate.format(FILE_DATE_FORMATTER) + ".csv";

        // percorso finale di export dentro la cartella dedicata
        Path outputPath = Path.of(OUTPUT_DIRECTORY, exportFileName);

        // cartella export se non esiste gia
        createOutputDirectory(outputPath.getParent());

        // header e righe csv 
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // riga header con le colonne 
            writer.write("ID;Nome;Descrizione;Tipologia;Durata");
            writer.newLine();

            // riga csv per ogni obiettivo disponibile incluso nell'export
            for (Goal goal : sortedGoals) {
                writer.write(buildGoalCsvRow(goal));
                writer.newLine();
            }
        } catch (IOException exception) {
            // errore quando la scrittura file fallisce
            throw new IllegalStateException("Errore durante la creazione del file export: " + outputPath, exception);
        }

        // restituisce percorso del file creato
        return outputPath;
    }

    private void createOutputDirectory(Path directoryPath) {
        // sicurezza i casi in cui il percorso directory è nullo
        if (directoryPath == null) {
            throw new IllegalArgumentException("Percorso directory export non valido");
        }

        // crea la directory export solo quando manca nel filesystem
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException exception) {
            // errore quando non è possibile creare la cartella export
            throw new IllegalStateException("Errore durante la creazione della cartella export: " + directoryPath, exception);
        }
    }

    private String buildGoalCsvRow(Goal goal) {
        // riga csv con il separatore 
        return goal.id() + CSV_SEPARATOR
                + goal.nome() + CSV_SEPARATOR
                + goal.descrizione() + CSV_SEPARATOR
                + goal.tipologia() + CSV_SEPARATOR
                + goal.durata();
    }
}
