package com.portfolio.bank.ui;
import com.portfolio.bank.MainApp;
import com.portfolio.bank.model.BankTransaction;
import com.portfolio.bank.service.BankService;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardView {
    private final MainApp app; private final long userId; private final BankService bank=new BankService();
    private final NumberFormat money=NumberFormat.getCurrencyInstance(Locale.US);
    private VBox content;
    public DashboardView(MainApp app,long userId){this.app=app;this.userId=userId;}
    public Parent getView(){
        BorderPane root=new BorderPane(); root.setStyle(Styles.CSS);
        VBox side=new VBox(18); side.setPadding(new Insets(30,20,25,20)); side.setPrefWidth(220); side.setStyle("-fx-background-color:#102b54;");
        Label brand=new Label("NOVA BANK"); brand.setStyle("-fx-text-fill:white;-fx-font-size:20px;-fx-font-weight:bold;");
        Button overview=nav("Overview"),history=nav("Transactions"),logout=nav("Sign out");
        overview.setOnAction(e->refresh()); history.setOnAction(e->showHistory()); logout.setOnAction(e->app.showLogin());
        Region spacer=new Region(); VBox.setVgrow(spacer,Priority.ALWAYS); side.getChildren().addAll(brand,new Separator(),overview,history,spacer,logout);
        content=new VBox(22); content.setPadding(new Insets(34));
        ScrollPane scroll=new ScrollPane(content); scroll.setFitToWidth(true); scroll.setStyle("-fx-background:#f3f6fb;-fx-background-color:#f3f6fb;");
        root.setLeft(side); root.setCenter(scroll); refresh(); return root;
    }
    private Button nav(String text){Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.setAlignment(Pos.CENTER_LEFT);b.setStyle("-fx-background-color:transparent;-fx-text-fill:#e8eff8;-fx-padding:12;-fx-cursor:hand;");return b;}
    private void refresh(){
        try{
            var user=bank.getUser(userId); content.getChildren().clear();
            Label title=new Label("Good day, "+user.fullName());title.setFont(Font.font("Segoe UI",FontWeight.BOLD,28));
            Label sub=new Label("Here is your financial overview.");sub.setStyle("-fx-text-fill:#718096;");
            VBox card=new VBox(12);card.setPadding(new Insets(28));card.setStyle("-fx-background-color:linear-gradient(to right,#173b73,#2864a4);-fx-background-radius:18;");
            Label caption=new Label("TOTAL AVAILABLE BALANCE");caption.setStyle("-fx-text-fill:#d8e5f5;-fx-font-size:12px;-fx-font-weight:bold;");
            Label balance=new Label(money.format(user.balance()));balance.setStyle("-fx-text-fill:white;-fx-font-size:34px;-fx-font-weight:bold;");
            Label account=new Label("Account number  "+user.accountNumber());account.setStyle("-fx-text-fill:#d8e5f5;");
            card.getChildren().addAll(caption,balance,account);
            HBox actions=new HBox(12,action("Deposit",()->amountAction(true)),action("Withdraw",()->amountAction(false)),action("Transfer",this::transferAction));
            Label recent=new Label("Recent transactions");recent.setFont(Font.font("Segoe UI",FontWeight.BOLD,20));
            TableView<BankTransaction> table=table();table.setItems(FXCollections.observableArrayList(bank.history(userId).stream().limit(5).toList()));
            content.getChildren().addAll(title,sub,card,actions,recent,table);
        }catch(Exception e){error(e.getMessage());}
    }
    private Button action(String text,Runnable task){Button b=new Button(text);b.setStyle(Styles.PRIMARY);b.setOnAction(e->task.run());return b;}
    private void amountAction(boolean deposit){
        TextInputDialog d=new TextInputDialog();d.setTitle(deposit?"Deposit":"Withdraw");d.setHeaderText(deposit?"Add funds to your account":"Withdraw funds");d.setContentText("Amount (USD):");
        d.showAndWait().ifPresent(v->{try{double n=Double.parseDouble(v);if(deposit)bank.deposit(userId,n);else bank.withdraw(userId,n);refresh();info("Operation completed successfully.");}catch(Exception e){error(e.getMessage()==null?"Invalid amount.":e.getMessage());}});
    }
    private void transferAction(){
        Dialog<String[]> d=new Dialog<>();d.setTitle("Transfer funds");ButtonType ok=new ButtonType("Transfer",ButtonBar.ButtonData.OK_DONE);d.getDialogPane().getButtonTypes().addAll(ok,ButtonType.CANCEL);
        TextField account=new TextField();account.setPromptText("Destination account number");TextField amount=new TextField();amount.setPromptText("Amount (USD)");
        VBox fields=new VBox(12,new Label("Destination account"),account,new Label("Amount"),amount);fields.setPadding(new Insets(18));d.getDialogPane().setContent(fields);
        d.setResultConverter(bt->bt==ok?new String[]{account.getText(),amount.getText()}:null);
        d.showAndWait().ifPresent(v->{try{bank.transfer(userId,v[0],Double.parseDouble(v[1]));refresh();info("Transfer completed successfully.");}catch(Exception e){error(e.getMessage()==null?"Invalid transfer details.":e.getMessage());}});
    }
    private void showHistory(){
        try{content.getChildren().clear();Label title=new Label("Transaction history");title.setFont(Font.font("Segoe UI",FontWeight.BOLD,28));
            TableView<BankTransaction> t=table();t.setItems(FXCollections.observableArrayList(bank.history(userId)));
            content.getChildren().addAll(title,new Label("Your latest account activity"),t,action("Back to overview",this::refresh));
        }catch(Exception e){error(e.getMessage());}
    }
    private TableView<BankTransaction> table(){
        TableView<BankTransaction> t=new TableView<>();t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);t.setPrefHeight(300);
        TableColumn<BankTransaction,String> type=new TableColumn<>("TYPE");type.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().type()));
        TableColumn<BankTransaction,String> desc=new TableColumn<>("DESCRIPTION");desc.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().description()));
        TableColumn<BankTransaction,String> amount=new TableColumn<>("AMOUNT");amount.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(money.format(v.getValue().amount())));
        TableColumn<BankTransaction,String> date=new TableColumn<>("DATE");date.setCellValueFactory(v->new javafx.beans.property.SimpleStringProperty(v.getValue().createdAt()));
        t.getColumns().addAll(type,desc,amount,date);return t;
    }
    private void info(String s){new Alert(Alert.AlertType.INFORMATION,s,ButtonType.OK).showAndWait();}
    private void error(String s){new Alert(Alert.AlertType.ERROR,s==null?"Unexpected error":s,ButtonType.OK).showAndWait();}
}
