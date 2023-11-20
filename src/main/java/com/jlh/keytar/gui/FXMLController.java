package com.jlh.keytar.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class FXMLController {
    @FXML 
    private TextArea logView;
    
    @FXML 
    private Pane keysetView;

    @FXML 
    private Label currentChar;

    @FXML
    private Circle bgCircle;

    @FXML 
    private ListView<String> keysetList;

    private FadeTransition blink;
    private Label[][] text = new Label[5][5];
    private int row;
    private Logger logger = LogManager.getLogger(FXMLController.class);

    @FXML 
    private void initialize() {
        blink = new FadeTransition(Duration.seconds(0.25), currentChar);
        blink.setFromValue(1);
        blink.setToValue(0.1);
        blink.setCycleCount(2);
        blink.setAutoReverse(true);
        logger.trace("GUI init complete");
    }

    @FXML
    public void setLogView(String log) {
        logView.setText(log);
        logView.positionCaret(log.length());
    }

    @FXML
    public void setKeyset(char[][] keyset) {
        if(text[0][0] == null) {
            initText();
            logger.trace("Keyset nodes initalized");
        }
        for (int i = 0; i < text.length; i++) {
            for (int ii = 0; ii < text[i].length; ii++) {
                text[i][ii].setText(keyset[i][ii] + "");
            }
        }
        logger.trace("Keyset set");
    }

    @FXML
    public void clearHighlight() {
        for (int i = 0; i < text.length; i++) {
            for (int ii = 0; ii < text[0].length; ii++) {
                text[i][ii].setTextFill(Color.BLACK);
            }
        }
    }

    @FXML
    public void setHighlightedRow(int row) {
        clearHighlight();
        for (int i = 0; i < text[row].length; i++) {
            text[row][i].setTextFill(Color.RED);
        }

        this.row = row;
    }

    @FXML
    public void setCurrentChar(int col) {
        clearHighlight();
        text[row][col].setTextFill(Color.RED);
        currentChar.setText(text[row][col].getText());
        logger.trace("Current char set to " + text[row][col].getText() + " using coordinate system");
    }

    @FXML
    public void setCurrentChar(String txt) {
        currentChar.setText(txt);
        logger.trace("Current char set to " + txt + " directly");
    }

    @FXML
    public void blinkCurrentChar() {
        blink.play();
    }

    @FXML
    public void setCapsDisplay(boolean caps) {
        if(caps) {
            bgCircle.setFill(Paint.valueOf("green"));
        }else {
            bgCircle.setFill(Paint.valueOf("red"));
        }
    }

    @FXML
    public void setKeysetList(String[] sets) {
        keysetList.getItems().addAll(sets);
    }

    private void initText() {
        double height = keysetView.getHeight();
        double width = keysetView.getWidth();

        for (int i = 0; i < text.length; i++) {
            for (int ii = 0; ii < text[0].length; ii++) {
                text[i][ii] = new Label("");

                text[i][ii].setTranslateX(width * ((ii) / 5.0) + 30);
                text[i][ii].setTranslateY(height * ((i) / 5.0) + 20);
                text[i][ii].setFont(Font.font(20));

                keysetView.getChildren().add(text[i][ii]);
            }
        }
    }
}
