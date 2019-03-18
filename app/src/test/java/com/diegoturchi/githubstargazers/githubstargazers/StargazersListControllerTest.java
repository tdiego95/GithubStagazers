package com.diegoturchi.githubstargazers.githubstargazers;

import org.junit.Test;

import static org.junit.Assert.*;

public class StargazersListControllerTest{

    //In questo metodo faccio due test :
    //1) il primo con un vero repository che dovrebbe restituire una lista di risultati
    //2) il secondo con un repository che non verrà trovato
    @Test
    public void lookForStargazersTest() throws InterruptedException {

        StargazersListController controller = new StargazersListController();

        MockIRequestCallback listener = new MockIRequestCallback();

        controller.InitializeStargazers("googlevr", "gvr-android-sdk", listener);

        //Metto un timer di attesa per la richiesta di 10 secondi (classico tempo di timeout)
        //Grazie al listener appena si riceve un risultato dalla ricerca il synchronized sbloccherà il processo dall'attesa e andrà avanti
        synchronized (listener) {
            listener.wait(10000);
        }

        //Se la richiesta si è conclusa con successo avremo una lista di risultati con un count != da 0 e un risultato success
        assertNotEquals(listener.getStargazersList().size(), 0);
        assertEquals(listener.getResponse(), StargazersListController.Result.success);



        String randomWord1 = "lorem";
        String randomWord2 = "ipsum";
        controller.InitializeStargazers(randomWord1, randomWord2, listener);

        synchronized (listener) {
            listener.wait(10000);
        }

        //Se la richiesta non si è conclusa con successo avremo una lista di risultati con un count == 0 e un risultato notFound
        assertEquals(listener.getStargazersList().size(), 0);
        assertEquals(listener.getResponse(), StargazersListController.Result.notFound);
    }
}