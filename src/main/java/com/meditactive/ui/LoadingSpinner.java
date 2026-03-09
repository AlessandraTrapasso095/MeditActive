/* serve per mostrare un piccolo loading animato in console durante operazioni di avvio */
package com.meditactive.ui;

public class LoadingSpinner {

    private static final String[] FRAMES = {"◐", "◓", "◑", "◒"};

    public void runWithSpinner(String message, Runnable operation) {
        // mi serve per gestire lo stato di esecuzione condiviso tra thread principale e thread spinner
        SpinnerState state = new SpinnerState();

        // mi serve per creare un thread dedicato all'animazione senza bloccare il caricamento reale
        Thread spinnerThread = new Thread(() -> {
            int index = 0;
            while (state.running) {
                System.out.print("\r" + FRAMES[index % FRAMES.length] + " " + message);
                index++;
                sleepQuietly(120);
            }
        });

        // mi serve per avviare l'animazione prima dell'operazione principale
        spinnerThread.start();

        try {
            // mi serve per eseguire l'operazione reale mentre lo spinner è attivo
            operation.run();
        } finally {
            // mi serve per fermare l'animazione anche in caso di eccezioni
            state.running = false;
            joinQuietly(spinnerThread);
            System.out.print("\r✓ " + message + " completato\n");
        }
    }

    private void sleepQuietly(long millis) {
        // mi serve per gestire la pausa dellìanimazione senza propagare eccezioni al chiamante
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void joinQuietly(Thread thread) {
        // mi serve per attendere la chiusura del thread spinner senza interrompere il flusso principale
        try {
            thread.join();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static class SpinnerState {
        private volatile boolean running = true;
    }
}
