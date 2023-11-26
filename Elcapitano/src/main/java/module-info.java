module com.example.elcapitano {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires org.apache.poi.ooxml;

    opens com.example.elcapitano to javafx.fxml;
    exports com.example.elcapitano;
}