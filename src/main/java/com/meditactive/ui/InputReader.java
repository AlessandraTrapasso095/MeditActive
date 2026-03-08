/* questo file mi serve per leggere e validare l input utente dal terminale in modo centralizzato */
package com.meditactive.ui;

import java.util.Scanner;

public class InputReader {

    private final Scanner scanner;

    public InputReader() {
        // mi serve per aprire lo scanner standard input e riusarlo in tutto il ciclo applicativo.
        this.scanner = new Scanner(System.in);
    }

    public MenuCommand readMenuCommand() {
        // mi serve per leggere il comando finche l utente non inserisce un valore valido.
        while (true) {
            System.out.print("Seleziona un comando (0-5): ");

            // mi serve per leggere la riga digitata dall utente dal terminale.
            String inputValue = scanner.nextLine().trim();

            try {
                // mi serve per convertire il testo digitato in numero intero.
                int commandCode = Integer.parseInt(inputValue);

                // mi serve per ottenere il comando tipizzato partendo dal codice numerico.
                MenuCommand command = MenuCommand.fromCode(commandCode);

                // mi serve per restituire il comando quando e valido.
                if (command != null) {
                    return command;
                }

                // mi serve per guidare l utente quando inserisce un numero non previsto dal menu.
                System.out.println("Comando non valido: usa solo 0, 1, 2, 3, 4, 5.");
            } catch (NumberFormatException exception) {
                // mi serve per gestire input non numerici senza interrompere il programma.
                System.out.println("Input non valido: inserisci un numero.");
            }

            System.out.println();
        }
    }
}
