module project1_1_test {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens project1_1 to javafx.fxml;
    exports project1_1;
}
