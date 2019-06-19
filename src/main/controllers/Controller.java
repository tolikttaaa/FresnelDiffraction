package main.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import main.ImageGetter;
//import modeling.Modulating;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    HBox mainPane;

    @FXML
    AnchorPane authorsPane;

    @FXML
    VBox parametersPane;

    @FXML
    Slider lSlider;
    @FXML
    TextField lTextField;
    @FXML
    Slider rSlider;
    @FXML
    TextField rTextField;

    @FXML
    Canvas image;

    GraphicsContext gc;
    Image curImage;

    private ImageGetter imageGetter;

    @FXML
    void updateImage() {
        int len = (int) lSlider.getValue();
        double radius = rSlider.getValue();

        curImage = imageGetter.getImage(len, radius);
        gc.clearRect(0, 0, 300, 300);
        gc.drawImage(curImage, 0, 0, 300, 300);
    }


    @FXML
    void setFromLField(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            int d = Integer.parseInt(lTextField.getText());
            if (d < lSlider.getMin()) {
                d = (int) lSlider.getMin();
                lTextField.setText(Integer.toString(d));
            }

            if (d > lSlider.getMax()) {
                d = (int) lSlider.getMax();
                lTextField.setText(Integer.toString(d));
            }

            lSlider.setValue(Integer.parseInt(lTextField.getText()));
            updateImage();
        }
    }

    @FXML
    void setFromRField(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            double d = Double.parseDouble(rTextField.getText());
            if (d < rSlider.getMin()) {
                d = rSlider.getMin();
                rTextField.setText(Double.toString(d));
            }

            if (d > rSlider.getMax()) {
                d = rSlider.getMax();
                rTextField.setText(Double.toString(d));
            }

            rSlider.setValue(Double.parseDouble(rTextField.getText()));
            updateImage();
        }
    }

    @FXML
    void getAuthors() {
        authorsPane.setVisible(true);
        mainPane.setVisible(false);
    }

    @FXML
    void authorsBack() {
        authorsPane.setVisible(false);
        mainPane.setVisible(true);
    }

    @FXML
    void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        File file = fileChooser.showSaveDialog(image.getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(curImage,
                        null), "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void exit() {
        System.exit(0);
    }

    @FXML
    void circle() {
        imageGetter = new ImageGetter() {
            @Override
            public Image getImage(int len, double radius) {
//                return SwingFXUtils.toFXImage(Modulating.getCircleImage(len, radius), null);
            return null;
            }
        };
    }

    @FXML
    void slit() {
        imageGetter = new ImageGetter() {
            @Override
            public Image getImage(int len, double radius) {
                //TODO
                return null;
            }
        };
    }

    @FXML
    void doubleSlit() {
        imageGetter = new ImageGetter() {
            @Override
            public Image getImage(int len, double radius) {
                //TODO
                return null;
            }
        };
    }

    @FXML
    void square() {
        imageGetter = new ImageGetter() {
            @Override
            public Image getImage(int len, double radius) {
                //TODO
                return null;
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        circle();

        rSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            rTextField.setText(Double.toString(((double) ((int) (newValue.doubleValue() * 100))) / 100));
        });

        lSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            lTextField.setText(Integer.toString(newValue.intValue()));
        });

        gc = image.getGraphicsContext2D();

        updateImage();
    }
}
