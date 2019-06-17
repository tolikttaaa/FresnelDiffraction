package main.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    VBox countPane;

    @FXML
    Slider aSlider;
    @FXML
    TextField aTextField;
    @FXML
    Slider bSlider;
    @FXML
    TextField bTextField;
    @FXML
    Slider rSlider1;
    @FXML
    TextField rTextField1;

    @FXML
    Slider rSlider2;
    @FXML
    TextField rTextField2;

    @FXML
    void updateImage1() {
        //TODO
    }

    @FXML
    void updateImage2() {
        //TODO
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
            rSlider1.setValue(Double.parseDouble(rTextField1.getText()));
        }
    }

    @FXML
    void byParameter() {
        authorsBack();
        parametersPane.setVisible(true);
        countPane.setVisible(false);
    }

    @FXML
    void byCount() {
        authorsBack();
        parametersPane.setVisible(false);
        countPane.setVisible(true);
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
        //TODO
    }

    @FXML
    void exit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aTextField.textProperty().bindBidirectional(aSlider.valueProperty(), NumberFormat.getNumberInstance());
        bTextField.textProperty().bindBidirectional(bSlider.valueProperty(), NumberFormat.getNumberInstance());
        rTextField1.textProperty().bindBidirectional(rSlider1.valueProperty(), NumberFormat.getNumberInstance());

        rTextField2.textProperty().bindBidirectional(rSlider2.valueProperty(), NumberFormat.getNumberInstance());
    }
}
