package com.portfolio.bank;
import com.portfolio.bank.data.Database;
import com.portfolio.bank.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage stage;
    @Override public void start(Stage primaryStage) {
        stage = primaryStage;
        Database.initialize();
        showLogin();
        stage.setTitle("NOVA Bank | Banking System");
        stage.setMinWidth(900); stage.setMinHeight(620); stage.show();
    }
    public void showLogin() { stage.setScene(new Scene(new LoginView(this).getView(), 1100, 720)); }
    public void showDashboard(long userId) {
        stage.setScene(new Scene(new com.portfolio.bank.ui.DashboardView(this, userId).getView(), 1200, 780));
    }
    public static void main(String[] args) { launch(args); }
}
