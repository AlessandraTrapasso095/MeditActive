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

    private static final char DELIMITER = ';';
    private static final char DOUBLE_QUOTE = '"';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char NEW_LINE = '\n';

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
            // per leggere il file riga per riga fino alla fine
            for (String[] rawColumns : parseCsvRecords(reader)) {
                String[] columns = normalizeColumns(rawColumns);

                // per ignorare righe vuote o righe commentate che iniziano con #
                if (isIgnorableRecord(columns)) {
                    continue;
                }

                // per saltare solo la prima riga valida di intestazione quando presente
                if (!headerAlreadySkipped) {
                    headerAlreadySkipped = true;
                    continue;
                }

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

    private List<String[]> parseCsvRecords(BufferedReader reader) throws IOException {
        List<String[]> records = new ArrayList<>();
        List<String> currentRecord = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;
        int nextCharCode;

        while ((nextCharCode = reader.read()) != -1) {
            char currentChar = (char) nextCharCode;

            if (currentChar == DOUBLE_QUOTE) {
                if (insideQuotes) {
                    reader.mark(1);
                    int lookAheadCode = reader.read();
                    if (lookAheadCode == DOUBLE_QUOTE) {
                        currentField.append(DOUBLE_QUOTE);
                    } else {
                        insideQuotes = false;
                        if (lookAheadCode != -1) {
                            reader.reset();
                        }
                    }
                } else {
                    insideQuotes = true;
                }
                continue;
            }

            if (currentChar == DELIMITER && !insideQuotes) {
                currentRecord.add(currentField.toString());
                currentField.setLength(0);
                continue;
            }

            if ((currentChar == NEW_LINE || currentChar == CARRIAGE_RETURN) && !insideQuotes) {
                if (currentChar == CARRIAGE_RETURN) {
                    reader.mark(1);
                    int lookAheadCode = reader.read();
                    if (lookAheadCode != NEW_LINE && lookAheadCode != -1) {
                        reader.reset();
                    }
                }

                currentRecord.add(currentField.toString());
                currentField.setLength(0);
                records.add(currentRecord.toArray(new String[0]));
                currentRecord = new ArrayList<>();
                continue;
            }

            currentField.append(currentChar);
        }

        if (insideQuotes) {
            throw new IllegalStateException("CSV non valido: virgolette non bilanciate");
        }

        if (currentField.length() > 0 || !currentRecord.isEmpty()) {
            currentRecord.add(currentField.toString());
            records.add(currentRecord.toArray(new String[0]));
        }

        return records;
    }

    private String[] normalizeColumns(String[] rawColumns) {
        if (rawColumns.length == 0) {
            return rawColumns;
        }

        String[] normalizedColumns = rawColumns.clone();
        normalizedColumns[0] = stripBom(normalizedColumns[0]);
        return normalizedColumns;
    }

    private boolean isIgnorableRecord(String[] columns) {
        if (columns.length == 0) {
            return true;
        }

        String firstColumn = columns[0] == null ? "" : columns[0].trim();
        if (firstColumn.startsWith("#")) {
            return true;
        }

        for (String column : columns) {
            if (column != null && !column.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String stripBom(String value) {
        if (value == null || value.isEmpty()) {
            return value == null ? "" : value;
        }

        if (value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }

        return value;
    }
}
