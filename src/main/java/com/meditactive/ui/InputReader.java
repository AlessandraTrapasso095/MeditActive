/* questo file mi serve per leggere e validare l input utente dal terminale in modo centralizzato */
package com.meditactive.ui;

import com.meditactive.service.DateParser;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class InputReader {

    private final Scanner scanner;

    public InputReader() {
        // mi serve per aprire lo scanner standard input e riusarlo in tutto il ciclo applicativo.
        this.scanner = new Scanner(System.in);
    }

    public MenuCommand readMenuCommand() {
        // mi serve per validare che il numero digitato corrisponda ad un comando del menu previsto.
        IntPredicate menuValidator = commandCode -> MenuCommand.fromCode(commandCode) != null;

        // mi serve per leggere un numero comando valido riusando la stessa logica di input numerico.
        int commandCode = readValidatedInt("Seleziona un comando (0-5): ", menuValidator, "Comando non valido: usa solo 0, 1, 2, 3, 4, 5.");

        // mi serve per convertire il numero validato nel comando enum da usare nel flusso applicazione.
        return MenuCommand.fromCode(commandCode);
    }

    public int readPositiveInt(String promptLabel) {
        // mi serve per leggere un id numerico positivo evitando ripetizioni nei vari comandi applicativi.
        return readValidatedInt(promptLabel, value -> value > 0, "Input non valido: inserisci un numero intero positivo.");
    }

    public String readRequiredText(String promptLabel) {
        // mi serve per leggere un testo obbligatorio evitando campi vuoti nell input utente.
        while (true) {
            System.out.print(promptLabel);

            // mi serve per leggere la riga digitata e rimuovere spazi superflui ai bordi.
            String inputValue = scanner.nextLine().trim();

            // mi serve per restituire il testo quando e presente almeno un carattere valido.
            if (!inputValue.isEmpty()) {
                return inputValue;
            }

            // mi serve per guidare l utente quando lascia il campo vuoto.
            System.out.println("Input non valido: il campo non puo essere vuoto.");
            System.out.println();
        }
    }

    public LocalDate readDate(String promptLabel) {
        // mi serve per leggere una data valida con i formati supportati in tutta l applicazione.
        while (true) {
            System.out.print(promptLabel);

            // mi serve per leggere il testo data digitato da utente.
            String inputValue = scanner.nextLine().trim();

            try {
                // mi serve per convertire la stringa in LocalDate usando la stessa utility DRY del progetto.
                return DateParser.parseDate(inputValue);
            } catch (IllegalArgumentException exception) {
                // mi serve per mostrare un feedback chiaro e permettere un nuovo tentativo.
                System.out.println("Data non valida: usa formato yyyy-MM-dd oppure dd/MM/yyyy.");
                System.out.println();
            }
        }
    }

    private int readValidatedInt(String promptLabel, IntPredicate validator, String invalidValueMessage) {
        // mi serve per leggere il comando finche l utente non inserisce un valore valido.
        while (true) {
            System.out.print(promptLabel);

            // mi serve per leggere la riga digitata dall utente dal terminale.
            String inputValue = scanner.nextLine().trim();

            try {
                // mi serve per convertire il testo digitato in numero intero.
                int parsedValue = Integer.parseInt(inputValue);

                // mi serve per restituire il numero quando supera il controllo di validazione ricevuto.
                if (validator.test(parsedValue)) {
                    return parsedValue;
                }

                // mi serve per guidare l utente quando il numero e nel formato giusto ma non rispetta la regola richiesta.
                System.out.println(invalidValueMessage);
            } catch (NumberFormatException exception) {
                // mi serve per gestire input non numerici senza interrompere il programma.
                System.out.println("Input non valido: inserisci un numero.");
            }

            System.out.println();
        }
    }
}
