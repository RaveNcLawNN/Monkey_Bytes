package monkeybytes.quiz.game;

import java.util.Timer;
import java.util.TimerTask;

//Anmerkung: Die Datentypen passen noch nicht ganz.
public class QuestionTimer {

    private Timer timer;
    private int timeLimitInSeconds;
    private long startTime;
    private boolean isTimerUp;

    //Konstruktor für den QuestionTimer.
    public QuestionTimer(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
        this.isTimerUp = false;
    }

    //Startet den Timer.
    public void startTimer() {
        //Stoppt etwaige alte Timer.
        stopTimer();
        //Setzt timeUp auf false, weil ein neuer Timer gestartet wird.
        isTimerUp = false;
        //Prüft Startzeit. Wichtig für die berechnung der verbleibenden Zeit, um später die gesamten Punkte zu berechnen.
        startTime = System.currentTimeMillis();
        //Startet den Timer.
        timer = new Timer();

        //timer.schedule plant, was nach Ablauf des timeLimitInSeconds passieren soll.
        timer.schedule(new TimerTask() {
            @Override
            //Die Methode "run" der Klasse "TimerTask" wird überschrieben, um den Timer nach Ablauf der Zeit zu stoppen.
            public void run() {
                isTimerUp = true;
                stopTimer();
            }
            //timeLimitInSeconds wird mit 1000 (für Millisekunden) als Long multipliziert, weil das als Input von TimerTask erwartet wird.
        }, timeLimitInSeconds * 1000L);
    }

    //Stoppt und entfernt den Timer.
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //Gibt aus, ob die Zeit abgelaufen ist.
    public boolean getTimerUp() {
        return isTimerUp;
    }

    //Findet die verbleibende Zeit heraus, um sie z.B. für die Berechnung der Punkte zu verwenden.
    public int getRemainingTime() {
        if (isTimerUp) {
            return 0;
        }
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        //Falls das Ergebnis negativ sein sollte (unwahrscheinlich), wird 0 ausgegeben.
        return Math.max(0, timeLimitInSeconds - (int) elapsedTime);
    }
}
