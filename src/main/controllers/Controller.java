package main.controllers;

import javafx.beans.value.ObservableValue;
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
import modeling.drawing.Diffraction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    private GraphicsContext gc;
    private Image curImage;

    private ImageGetter imageGetter;
    private Diffraction modulate;

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
        imageGetter = (len, radius) -> SwingFXUtils.toFXImage(modulate.getCircularImage(len, radius/3), null);
        updateImage();
    }

    @FXML
    void slit() {
        imageGetter = (len, radius) -> SwingFXUtils.toFXImage(modulate.getSlitImage(len, radius/8), null);
        updateImage();
    }

    @FXML
    void doubleSlit() {
        imageGetter = (len, radius) -> SwingFXUtils.toFXImage(modulate.getDoubleSlitImage(len, radius/8), null);
        updateImage();
    }

    @FXML
    void square() {
        imageGetter = (len, radius) -> SwingFXUtils.toFXImage(modulate.getSquareImage(len, radius/3), null);
        updateImage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modulate = new Diffraction(new Dimension(500,500));

        rSlider.valueProperty().addListener((observable, oldValue, newValue) -> rTextField.setText(Double.toString(((double) ((int) (newValue.doubleValue() * 100))) / 100)));
        rSlider.valueChangingProperty().addListener(this::changed);

        lSlider.valueProperty().addListener((observable, oldValue, newValue) -> lTextField.setText(Integer.toString(newValue.intValue())));
        lSlider.valueChangingProperty().addListener(this::changed);

        gc = image.getGraphicsContext2D();
        circle();
    }

    private void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean changing) {
        if (!changing) {
            updateImage();
        }
    }
}
