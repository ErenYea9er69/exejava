import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Welcomeform extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("tp2");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        Text scenetitle = new Text("welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("NAME");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("pswd:");
        grid.add(pw, 0, 2);

        PasswordField pswd = new PasswordField();
        grid.add(pswd, 1, 2);

        Button btn = new Button("sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Button exitBtn = new Button("exit");
        HBox hbExitBtn = new HBox(10);
        hbExitBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbExitBtn.getChildren().add(exitBtn);
        grid.add(hbExitBtn, 0, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String username = userTextField.getText();
                String password = pswd.getText();

                if (username.equals("rayen") && password.equals("12345")) {
                    actiontarget.setFill(Color.GREEN);
                    actiontarget.setText("Login Done");
                } else {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Invalid");
                }
            }
        });

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Platform.exit();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
