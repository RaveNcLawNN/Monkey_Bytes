package monkeybytes.quiz.game;

import java.util.Timer;
import java.util.TimerTask;

public class QuestionTimer {

    private Timer timer;
    private int timeLimitInSeconds;
    private long startTime;
    private boolean isTimerUp;
    private boolean isTimerCanceled;

    /**
     * Konstruktor für QuestionTimer.
     * Setzt die Statuswerte `isTimerUp` und `isTimerCanceled` auf `false`.
     */
    public QuestionTimer(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
        this.isTimerUp = false;
        this.isTimerCanceled = false;
    }

    /**
     * Startet den Timer für die festgelegte Zeitbegrenzung.
     * - Stoppt einen eventuell laufenden Timer.
     * - Setzt die Statuswerte isTimerUp und isTimerCanceled zurück.
     * - Plant eine Aufgabe, die den Timer nach Ablauf der Zeitbegrenzung beendet,
     *   es sei denn, der Timer wurde zuvor abgebrochen.
     */
    public void startTimer() {
        stopTimer();

        isTimerUp = false;
        isTimerCanceled = false;
        startTime = System.currentTimeMillis();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isTimerCanceled) {
                    return;
                }
                isTimerUp = true;
                stopTimer();
            }
        }, timeLimitInSeconds * 1000L);
    }

    /**
     * Stoppt den aktuell laufenden Timer und setzt die Timer-Referenz auf null.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public boolean getTimerUp() {
        return isTimerUp;
    }

    /**
     * Gibt die verbleibende Zeit in Sekunden zurück.
     * - Berechnet die vergangene Zeit seit dem Start des Timers.
     * - Liefert die verbleibende Zeit oder 0, wenn die Zeitbegrenzung abgelaufen ist.
     */
    public int getRemainingTime() {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, timeLimitInSeconds - (int) elapsedTime);
    }

    /**
     * Berechnet den Punktestand basierend auf Basispunkten und verbleibender Zeit.
     * - Fügt einen Bonus hinzu, der proportional zum Quadrat der verbleibenden Zeit ist.
     * - Gibt den Gesamtscore als Ganzzahl zurück.
     */
    public int calculateScore(int basePoints, int remainingTime) {
        double bonus = remainingTime * remainingTime * 0.5;
        return basePoints + (int) bonus;
    }
}
