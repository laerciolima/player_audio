/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.swing.JOptionPane;

/**
 *
 * @author laercio
 */
public class Controller implements Initializable {

    @FXML
    ListView<String> playlist;
    @FXML
    Slider volume;
    @FXML
    Slider linha_do_tempo;
    @FXML
    Label tempo;
    @FXML
    Label label;

    @FXML
    ToggleButton botao_aleatorio;

    @FXML
    ImageView img_btn_play;

    @FXML
    ToggleButton botao_repeat;

    Player player;

    ObservableList<String> lista_de_musicas = null;

    PlayList currentPlayList;
    int faixa_atual = 0;
    boolean aleatorio = false;
    boolean repeat = false;
    final FileChooser fileChooser = new FileChooser();

    public void play() {

        if (player == null) {

            player = new PlayerAudio();

        } else {
            player.stop();
        }

        Media media = null;
        if (currentPlayList != null) {
            if (currentPlayList.getMusicas().isEmpty()) {
                return;
            }

            try {
                media = new Media(currentPlayList.getMusicas().get(faixa_atual).toURI().toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Arquivo não encontrado ou não suportado!", "Erro ao abrir o arquivo!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!playlist.getFocusModel().isFocused(faixa_atual)) {
                playlist.scrollTo(faixa_atual);
            }
            playlist.getSelectionModel().select(faixa_atual);

            playlist.getFocusModel().focus(faixa_atual);

            player.play(media);
            label.setText(currentPlayList.getMusicas().get(faixa_atual).getName());
            player.getPlayer().setVolume(volume.getValue() / 100);
            player.getPlayer().setOnEndOfMedia(new Runnable() {

                public void run() {
                    if (repeat && (faixa_atual == (currentPlayList.getMusicas().size() - 1))) {
                        faixa_atual = 0;
                        play();
                    } else {
                        next();
                    }
                }
            }
            );

            player.getPlayer().currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    updateValues();
                }
            });
        }

        img_btn_play.setImage(new Image(Controller.class.getResource("Imagens/pause.png").toString()));

    }

    public void abrir() {

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3", "*.pls")
        );
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {

            currentPlayList = new PlayList(new ArrayList<File>());

            faixa_atual = 0;
            currentPlayList.getMusicas().clear();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isDirectory()) {
                    continue;
                }

                if (list.get(i).getAbsolutePath().contains(".pls")) {
                    currentPlayList.abrir(list.get(i).getAbsolutePath());
                    continue;
                }
                currentPlayList.getMusicas().add(list.get(i));
            }
            String musicas[] = new String[currentPlayList.getMusicas().size()];
            for (int i = 0; i < musicas.length; i++) {
                musicas[i] = currentPlayList.getMusicas().get(i).getName();
            }

            lista_de_musicas = FXCollections.observableArrayList(musicas);
            playlist.setItems(lista_de_musicas);
            play();

        }

    }

    public void addMusica() {

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3")
        );

        List<File> list
                = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                lista_de_musicas.add(list.get(i).getName());
                currentPlayList.getMusicas().add(list.get(i));
            }

            playlist.setItems(lista_de_musicas);
        }

    }

    public void removerMusica() {

        int item = playlist.getSelectionModel().getSelectedIndex();
        if (item == -1) {
            return;
        }
        lista_de_musicas.remove(item);

        if (item < faixa_atual) {
            faixa_atual--;
        }
        currentPlayList.getMusicas().remove(item);
        playlist.setItems(lista_de_musicas);

        if (item == faixa_atual) {
            faixa_atual = 0;
            if (currentPlayList.getMusicas().size() > 0) {
                play();
            } else {
                stop();
            }
        }
    }

    public void stop() {

        player.stop();
        img_btn_play.setImage(new Image(Controller.class.getResource("Imagens/play.png").toString()));
        tempo.setText("00:00/00:00");
        label.setText("");
    }

    public void btn_play() {

        img_btn_play.setImage(new Image(Controller.class.getResource("Imagens/pause.png").toString()));

        if (player == null) {
            return;
        }

        player.pausar();
        if (MediaPlayer.Status.PAUSED == player.getPlayer().getStatus()) {

            img_btn_play.setImage(new Image(Controller.class.getResource("Imagens/pause.png").toString()));

        } else if (MediaPlayer.Status.STOPPED == player.getPlayer().getStatus()) {
            player.stop();
            play();

        } else {
            img_btn_play.setImage(new Image(Controller.class.getResource("Imagens/play.png").toString()));
        }

    }

    public void changeVolume() {
        if (player != null) {
            player.getPlayer().setVolume(volume.getValue() / 100.0);
        }

    }

    public void mudarTempo() {
        if (player != null) {
            player.getPlayer().seek(player.getDuration().multiply(linha_do_tempo.getValue() / 100.0));
        }
    }

    public void next() {
        if (currentPlayList == null) {
            return;
        }
        if (currentPlayList.getMusicas().isEmpty()) {
            return;
        }
        if (aleatorio) {
            Random r = new Random();
            int nova_faixa = r.nextInt(currentPlayList.getMusicas().size());

            while (nova_faixa == faixa_atual) {
                nova_faixa = r.nextInt(currentPlayList.getMusicas().size());
            }

            faixa_atual = nova_faixa;
        } else {
            faixa_atual++;

        }
        if (faixa_atual == currentPlayList.getMusicas().size()) {
            faixa_atual = 0;
            stop();
        } else {
            play();
        }
    }

    public void prev() {
        faixa_atual--;
        if (faixa_atual < 0) {
            faixa_atual = 0;
        }
        play();
    }

    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        linha_do_tempo.setMinWidth(50);
        linha_do_tempo.setMaxWidth(Double.MAX_VALUE);

        playlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && playlist.getSelectionModel().getSelectedIndex() >= 0) {

                    faixa_atual = playlist.getSelectionModel().getSelectedIndex();
                    play();
                }

            }
        });

        volume.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                changeVolume();
            }
        });

        botao_aleatorio.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                aleatorio = !aleatorio;
            }
        });

        botao_repeat.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                repeat = !repeat;
            }
        });
    }

    public void salvar() {

        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("PlayList", "*.pls")
        );

        File f = fileChooser.showSaveDialog(null);
        if (f != null) {
            currentPlayList.setNome(f.getName());

            if (currentPlayList.salvar(f.getAbsolutePath())) {
                JOptionPane.showMessageDialog(null, "Playslist " + f.getName() + " foi salva!");
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao salvar a playslist, tente novamente!");
            }
        }

    }

    public void updateValues() {
        if (tempo != null && linha_do_tempo != null && volume != null) {
            Platform.runLater(new Runnable() {
                public void run() {

                    Duration currentTime = player.getPlayer().getCurrentTime();
                    tempo.setText(formatTime(currentTime, player.getDuration()));
                    linha_do_tempo.setDisable(player.getDuration().isUnknown());
                    if (!linha_do_tempo.isDisabled()
                            && player.getDuration().greaterThan(Duration.ZERO)
                            && !linha_do_tempo.isValueChanging()) {
                        linha_do_tempo.setValue(currentTime.divide(player.getDuration()).toMillis()
                                * 100.0);
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else if (elapsedHours > 0) {
            return String.format("%d:%02d:%02d", elapsedHours,
                    elapsedMinutes, elapsedSeconds);
        } else {
            return String.format("%02d:%02d", elapsedMinutes,
                    elapsedSeconds);
        }
    }

}
