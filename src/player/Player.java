/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 *
 * @author Laercio Lima
 */
public abstract class Player {

    protected MediaPlayer player;

    
    protected Duration duration;

    public void play(Media arq) {
        player = new MediaPlayer(arq);
        player.setAutoPlay(true);

       
        player.setOnReady(new Runnable() {
            public void run() {
                duration = player.getMedia().getDuration();
            }
        });

    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void pausar() {

        Status status = player.getStatus();
        if (status == Status.UNKNOWN || status == Status.HALTED) {
            return;
        }

        if (status == Status.PAUSED || status == Status.READY
                || status == Status.STOPPED) {
            player.play();
        } else {
            player.pause();
        }

    }

    public MediaPlayer getPlayer() {
        return player;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
