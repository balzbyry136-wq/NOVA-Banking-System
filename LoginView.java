package com.portfolio.bank.ui;
import com.portfolio.bank.MainApp;
import com.portfolio.bank.service.AuthService;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private final MainApp app;
    private final AuthService auth=new AuthService();
    public LoginView(MainApp app){this.app=app;}
    public Parent getView(){
        HBox root=new HBox(); root.setStyle(Styles.CSS);
        VBox brand=new VBox(22); brand.setAlignment(Pos.CENTER_LEFT); brand.setPadding(new Insets(70)); brand.setPrefWidth(440);
        brand.setStyle("-fx-background-color: linear-gradient(to bottom right, #102b54, #24558e);");
        Label logo=new Label("NOVA"); logo.setTextFill(Color.WHITE); logo.setFont(Font.font("Segoe UI",FontWeight.EXTRA_BOLD,38));
        Label tagline=new Label("BANKING, MADE SIMPLE."); tagline.setTextFill(Color.web("#c8d8ed"));
        Label note=new Label("A modern desktop banking experience\\nfor everyday financial operations."); note.setTextFill(Color.WHITE); note.setFont(Font.font(18));
        brand.getChildren().addAll(logo,tagline,new Region(),note);
        VBox form=new VBox(15); form.setAlignment(Pos.CENTER_LEFT); form.setPadding(new Insets(55,75,55,75)); HBox.setHgrow(form,Priority.ALWAYS);
        Label title=new Label("Welcome back"); title.setFont(Font.font("Segoe UI",FontWeight.BOLD,30));
        Label subtitle=new Label("Sign in to access your account."); subtitle.setStyle("-fx-text-fill:#718096;");
        TextField email=new TextField(); email.setPromptText("Email address"); email.setStyle(Styles.FIELD);
        PasswordField password=new PasswordField(); password.setPromptText("Password"); password.setStyle(Styles.FIELD);
        Button signIn=new Button("Sign in"); signIn.setMaxWidth(Double.MAX_VALUE); signIn.setStyle(Styles.PRIMARY);
        Hyperlink register=new Hyperlink("Create a new account"); Label message=new Label(); message.setStyle("-fx-text-fill:#c0392b;");
        signIn.setOnAction(e->{
            try {
                if(email.getText().isBlank()||password.getText().isBlank()){message.setText("Enter your email and password.");return;}
                var user=auth.login(email.getText(),password.getText());
                if(user==null) message.setText("Incorrect email or password."); else app.showDashboard(user.id());
            } catch(Exception ex){message.setText("Sign in failed: "+ex.getMessage());}
        });
        register.setOnAction(e->{
            Dialog<String[]> d=new Dialog<>(); d.setTitle("Create account");
            ButtonType create=new ButtonType("Create account",ButtonBar.ButtonData.OK_DONE);
            d.getDialogPane().getButtonTypes().addAll(create,ButtonType.CANCEL);
            TextField name=new TextField(); name.setPromptText("Full name");
            TextField newEmail=new TextField(); newEmail.setPromptText("Email address");
            PasswordField newPassword=new PasswordField(); newPassword.setPromptText("Password (8+ characters)");
            VBox fields=new VBox(10,name,newEmail,newPassword); fields.setPadding(new Insets(15)); d.getDialogPane().setContent(fields);
            d.setResultConverter(bt->bt==create?new String[]{name.getText(),newEmail.getText(),newPassword.getText()}:null);
            d.showAndWait().ifPresent(v->{
                try {auth.register(v[0],v[1],v[2]); email.setText(v[1]); message.setStyle("-fx-text-fill:#16804a;"); message.setText("Account created. Sign in now.");}
                catch(Exception ex){message.setStyle("-fx-text-fill:#c0392b;");message.setText("Registration failed: "+ex.getMessage());}
            });
        });
        form.getChildren().addAll(title,subtitle,new Region(),new Label("EMAIL"),email,new Label("PASSWORD"),password,signIn,message,register);
        VBox.setVgrow(form.getChildren().get(2),Priority.ALWAYS);
        root.getChildren().addAll(brand,form); return root;
    }
}
