/* questo file mi serve per leggere file csv in modo riusabile, evitando duplicazioni tra utenti, obiettivi e prenotazioni */
package com.meditactive.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CsvTableReader {

    private static final String DELIMITER = ";";

    public <T> List<T> readTable(String filePath, Function<String[], T> rowMapper) {
        // mi serve per delegare alla versione completa tenendo la chiamata semplice nei casi standard.
        return readTable(filePath, rowMapper, true);
    }

    public <T> List<T> readTable(String filePath, Function<String[], T> rowMapper, boolean hasHeader) {
        // mi serve per accumulare in memoria tutti gli elementi convertiti dal file csv.
        List<T> rows = new ArrayList<>();

        // mi serve per preparare il percorso filesystem del file csv da leggere.
        Path path = Path.of(filePath);

        // mi serve per saltare l intestazione sulla prima riga valida anche quando in testa esistono commenti.
        boolean headerAlreadySkipped = !hasHeader;

        // mi serve per aprire il file e garantire la chiusura automatica della risorsa.
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;

            // mi serve per leggere il file riga per riga fino alla fine.
            while ((line = reader.readLine()) != null) {
                // mi serve per ignorare righe vuote o righe commentate che iniziano con #.
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }

                // mi serve per saltare solo la prima riga valida di intestazione quando presente.
                if (!headerAlreadySkipped) {
                    headerAlreadySkipped = true;
                    continue;
                }

                // mi serve per separare le colonne con il delimitatore ; mantenendo anche eventuali campi vuoti.
                String[] columns = line.split(DELIMITER, -1);

                // mi serve per mappare la riga csv in un oggetto dominio e salvarlo in lista.
                rows.add(rowMapper.apply(columns));
            }
        } catch (IOException exception) {
            // mi serve per rilanciare un errore descrittivo senza obbligare i chiamanti a gestire IOException.
            throw new IllegalStateException("Errore nella lettura del file: " + filePath, exception);
        }

        // mi serve per restituire la lista finale con i dati caricati in memoria.
        return rows;
    }
}
