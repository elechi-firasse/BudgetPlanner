package app;

import core.model.Transaction;
import io.CsvImporter;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storage.TransactionRepository;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class MainApp extends Application {
    private final TransactionRepository repo = new TransactionRepository();
    private final ObservableList<Transaction> model = FXCollections.observableArrayList();
    private final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override public void start(Stage stage) {
        var table = new TableView<Transaction>(model);

        var cDate = new TableColumn<Transaction, String>("Date");
        cDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().postedDate().format(ISO)));

        var cPayee = new TableColumn<Transaction, String>("Payee");
        cPayee.setCellValueFactory(d -> new SimpleStringProperty(nullToEmpty(d.getValue().payee())));

        var cMemo = new TableColumn<Transaction, String>("Memo");
        cMemo.setCellValueFactory(d -> new SimpleStringProperty(nullToEmpty(d.getValue().memo())));

        var cAmount = new TableColumn<Transaction, String>("Amount");
        cAmount.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().amount().format()));

        table.getColumns().addAll(cDate, cPayee, cMemo, cAmount);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        var importBtn = new Button("Import CSV…");
        importBtn.setOnAction(e -> importCsv(stage));

        var toolbar = new ToolBar(importBtn);

        var root = new BorderPane(table, toolbar, null, null, null);
        var scene = new Scene(root, 900, 600);
        stage.setTitle("Budget Planner");
        stage.setScene(scene);
        stage.show();

        reload();
    }

    private void reload() {
        model.setAll(repo.listAll());
    }

    private void importCsv(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File f = fc.showOpenDialog(stage);
        if (f == null) return;
        var importer = new CsvImporter();
        var txs = importer.load(f.toPath(), 1L);
        txs.forEach(repo::insert);
        reload();
        new Alert(Alert.AlertType.INFORMATION, "Imported " + txs.size() + " transactions.").showAndWait();
    }

    private static String nullToEmpty(String s){ return s == null ? "" : s; }

    public static void main(String[] args) { launch(args); }
}
