module com.example.scrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;


    opens com.example.scrabble to javafx.fxml;
    exports com.example.scrabble;
    opens com.example.scrabble.Controllers;
    exports com.example.scrabble.Controllers to javafx.fxml;

}