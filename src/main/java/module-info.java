module org.example.deadknight {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires static lombok;

    opens org.example.deadknight to javafx.fxml;
    exports org.example.deadknight;
}