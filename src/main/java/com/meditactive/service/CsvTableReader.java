/* serve per leggere file csv */
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
        // delega alla versione completa tenendo la chiamata semplice nei casi standard
        return readTable(filePath, rowMapper, true);
    }

    public <T> List<T> readTable(String filePath, Function<String[], T> rowMapper, boolean hasHeader) {
        // tiene in memoria tutti gli elementi convertiti dal file csv
        List<T> rows = new ArrayList<>();

        // percorso filesystem del file csv da leggere
        Path path = Path.of(filePath);

        // salta l'intestazione sulla prima riga valida anche quando in testa esistono commenti
        boolean headerAlreadySkipped = !hasHeader;

        // serve per aprire il file e garantire la chiusura automatica della risorsa
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;

            // per leggere il file riga per riga fino alla fine
            while ((line = reader.readLine()) != null) {
                // per ignorare righe vuote o righe commentate che iniziano con #
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }

                // per saltare solo la prima riga valida di intestazione quando presente
                if (!headerAlreadySkipped) {
                    headerAlreadySkipped = true;
                    continue;
                }

                // per separare le colonne con il delimitatore ; mantenendo anche eventuali campi vuoti
                String[] columns = line.split(DELIMITER, -1);

                // serve per mappare la riga csv in un oggetto dominio e salvarlo in lista
                rows.add(rowMapper.apply(columns));
            }
        } catch (IOException exception) {
            // per rilanciare un errore descrittivo senza obbligare i chiamanti a gestire IOException
            throw new IllegalStateException("Errore nella lettura del file: " + filePath, exception);
        }

        // serve per restituire la lista finale con i dati caricati in memoria
        return rows;
    }
}
