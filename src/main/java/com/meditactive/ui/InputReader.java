/* serve per leggere e validare l'input utente dal terminale */
package com.meditactive.ui;

import com.meditactive.service.DateParser;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class InputReader {

    private final Scanner scanner;

    public InputReader() {
        // apre lo scanner standard input 
        this.scanner = new Scanner(System.in);
    }

    public MenuCommand readMenuCommand() {
        // valida che il numero digitato corrisponda ad un comando del menu previsto
        IntPredicate menuValidator = commandCode -> MenuCommand.fromCode(commandCode) != null;

        // legge un numero comando valido riusando la stessa logica di input numerico
        int commandCode = readValidatedInt("Seleziona un comando (0-5): ", menuValidator, "Comando non valido: usa solo 0, 1, 2, 3, 4, 5.");

        // converte il numero validato nel comando enum da usare nel flusso applicazione
        return MenuCommand.fromCode(commandCode);
    }

    public int readPositiveInt(String promptLabel) {
        // legge un id numerico positivo 
        return readValidatedInt(promptLabel, value -> value > 0, "Input non valido: inserisci un numero intero positivo.");
    }

    public String readRequiredText(String promptLabel) {
        // legge un testo obbligatorio evitando campi vuoti nell'input utente
        while (true) {
            System.out.print(promptLabel);

            // legge la riga digitata e rimuove spazi superflui ai bordi
            String inputValue = scanner.nextLine().trim();

            // restituisce il testo quando è presente almeno un carattere valido
            if (!inputValue.isEmpty()) {
                return inputValue;
            }

            // guida l'utente quando lascia il campo vuoto
            System.out.println("Input non valido: il campo non puo essere vuoto.");
            System.out.println();
        }
    }

    public LocalDate readDate(String promptLabel) {
        // legge una data valida con i formati supportati in tutta l'applicazione
        while (true) {
            System.out.print(promptLabel);

            // leggere il testo data digitato da utente
            String inputValue = scanner.nextLine().trim();

            try {
                // converte la stringa in LocalDate 
                return DateParser.parseDate(inputValue);
            } catch (IllegalArgumentException exception) {
                // mostra un feedback e permettere un nuovo tentativo
                System.out.println("Data non valida: usa formato yyyy-MM-dd oppure dd/MM/yyyy.");
                System.out.println();
            }
        }
    }

    private int readValidatedInt(String promptLabel, IntPredicate validator, String invalidValueMessage) {
        // legge il comando finche l'utente non inserisce un valore valido
        while (true) {
            System.out.print(promptLabel);

            // legge la riga digitata dall'utente dal terminale
            String inputValue = scanner.nextLine().trim();

            try {
                // converte il testo digitato in numero intero
                int parsedValue = Integer.parseInt(inputValue);

                // restituisce il numero quando supera il controllo di validazione ricevuto
                if (validator.test(parsedValue)) {
                    return parsedValue;
                }

                // guida l'utente quando il numero è nel formato giusto ma non rispetta la regola richiesta
                System.out.println(invalidValueMessage);
            } catch (NumberFormatException exception) {
                // gestisce input non numerici senza interrompere il programma
                System.out.println("Input non valido: inserisci un numero.");
            }

            System.out.println();
        }
    }
}
