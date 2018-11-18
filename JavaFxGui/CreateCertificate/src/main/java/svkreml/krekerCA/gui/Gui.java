package svkreml.krekerCA.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import svkreml.krekerCA.CreateCertGUI;
import svkreml.krekerCA.CrlGeneratorHandler;

public class Gui {
    @FXML
    TabPane tabPane;






    public void initialize() {



        CreateCertGUI createCertGUI = new CreateCertGUI();
        tabPane.getTabs().add( createCertGUI.initCreator());





        CrlGeneratorHandler crlGeneratorHandler = new CrlGeneratorHandler();
        tabPane.getTabs().add(crlGeneratorHandler.initCrl());






    }





}
