
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

public class Video_Player extends JFrame {
    private final JFXPanel jfxPanel;
    private MediaPlayer mediaPlayer;
    private JTextField urlTextField;
    private JButton loadButton,pauseButton;
    private JComboBox<String> speedComboBox;

    public Video_Player() {
        setTitle("Video Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Disable default close operation
        setPreferredSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        JPanel controlsPanel = createControlsPanel();
        add(controlsPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopAndDisposeMediaPlayer();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel();
        urlTextField = new JTextField(45);
        urlTextField.setText("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        loadButton = new JButton("Load Video");
        pauseButton = new JButton("||");
        speedComboBox = new JComboBox<>(new String[]{"0.5x", "1.0x", "1.5x", "2.0x"});
        speedComboBox.setSelectedItem("1.0x");
        loadButton.addActionListener(e -> {
            String videoUrl = urlTextField.getText().trim();
            speedComboBox.setEnabled(true);
            loadVideo(videoUrl);
        });

        pauseButton.addActionListener(e -> pauseVideo());

        speedComboBox.addActionListener(e -> {
            String selectedSpeed = (String) speedComboBox.getSelectedItem();
            setPlaybackSpeed(selectedSpeed);
        });

     
        panel.add(urlTextField);
        panel.add(loadButton);
        panel.add(pauseButton);
        panel.add(new JLabel("Speed:"));
        panel.add(speedComboBox);

        return panel;
    }

    private void loadVideo(String videoUrl) {
        if (mediaPlayer != null) {
            stopAndDisposeMediaPlayer();
        }

        urlTextField.setEnabled(false);
        loadButton.setEnabled(false);
        pauseButton.setEnabled(true);

        Platform.runLater(() -> {
            try {
                URL url = new URL(videoUrl);
                Media media = new Media(url.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                BorderPane pane = new BorderPane();
                pane.setCenter(mediaView);

                Scene scene = new Scene(pane);
                jfxPanel.setScene(scene);

                mediaPlayer.setOnEndOfMedia(() -> {
                    stopAndDisposeMediaPlayer();
                    urlTextField.setText("");
                    urlTextField.setEnabled(true);
                    loadButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    speedComboBox.setEnabled(false);
                });

                mediaPlayer.play();
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(this, "Not Valid!!");
                urlTextField.setEnabled(true);
                loadButton.setEnabled(true);
                pauseButton.setEnabled(false);
                speedComboBox.setEnabled(false);
            }
        });
    }

    private void stopAndDisposeMediaPlayer() {
        try {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            urlTextField.setEnabled(true);
            loadButton.setEnabled(true);
            pauseButton.setEnabled(false);

        } catch (NullPointerException ex) {
        }
    }

    private void pauseVideo() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            pauseButton.setText("|>");
        } else if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
            pauseButton.setText("||");
        }
    }

    private void setPlaybackSpeed(String speed) {
        if (mediaPlayer != null) {
            double rate = Double.parseDouble(speed.replace("x", ""));
            mediaPlayer.setRate(rate);
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Video_Player player = new Video_Player();
            player.setVisible(true);
        });
    }
}
/*
for example 
http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
*/