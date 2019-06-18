package main.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import modeling.Modulating;

import javax.imageio.ImageIO;
import javax.swing.text.Element;
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
    Slider aSlider;
    @FXML
    TextField aTextField;
    @FXML
    Slider bSlider;
    @FXML
    TextField bTextField;
    @FXML
    Slider mSlider;
    @FXML
    TextField mTextField;

    @FXML
    Canvas image;
    GraphicsContext gc;
    Image curImage;
    @FXML
    void updateImage() {
        double a = aSlider.getValue() / 100;
        double b = bSlider.getValue() / 100;
        double m = mSlider.getValue();
        double lambda = 7E-7d;

        curImage = SwingFXUtils.toFXImage(Modulating.getImage(a, b, m, lambda), null);
        gc.clearRect(0, 0, 300, 300);
        gc.drawImage(curImage, 0, 0, 300, 300);
    }


    @FXML
    void setFromAField(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            aSlider.setValue(Double.parseDouble(aTextField.getText()));
        }
    }

    @FXML
    void setFromBField(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            bSlider.setValue(Double.parseDouble(bTextField.getText()));
        }
    }

    @FXML
    void setFromRField1(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            mSlider.setValue(Double.parseDouble(mTextField.getText()));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aTextField.textProperty().bindBidirectional(aSlider.valueProperty(), NumberFormat.getNumberInstance());
        bTextField.textProperty().bindBidirectional(bSlider.valueProperty(), NumberFormat.getNumberInstance());
        mTextField.textProperty().bindBidirectional(mSlider.valueProperty(), NumberFormat.getNumberInstance());

        gc = image.getGraphicsContext2D();

        updateImage();
    }
}
