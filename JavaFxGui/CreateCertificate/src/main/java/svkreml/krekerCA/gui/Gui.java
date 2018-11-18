package svkreml.krekerCA.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import svkreml.krekerCA.CreateCertGUI;
import svkreml.krekerCA.CrlGeneratorHandler;

public class Gui {
    @FXML
    TabPane tabPane;

    public void initialize() {
        CreateCertGUI createCertGUI = new CreateCertGUI();
        tabPane.getTabs().add(createCertGUI.initCreator());

        CrlGeneratorHandler crlGeneratorGUI = new CrlGeneratorHandler();
        tabPane.getTabs().add(crlGeneratorGUI.initCrl());
    }
}
