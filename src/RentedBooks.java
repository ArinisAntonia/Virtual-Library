import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class RentedBooks implements Initializable {
    String url = "jdbc:mysql://localhost:3307/Library";
    String user = "root";
    String password = "root";
    Connection myCon;
    Statement myStmt;

    @FXML
    private Button back_to_primary;

    @FXML
    private TableView<BookModel> tbData;
    @FXML
    private TableColumn<BookModel, String> title;

    @FXML
    private TableColumn<BookModel, String> author;

    @FXML
    private TableColumn<BookModel, String> ISBN;

    @FXML
    private TableColumn<BookModel, String> publisher;

    @FXML
    private TableColumn<BookModel, Date> return_date;
    @FXML
    private void change_scene (ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) back_to_primary.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("Primary.fxml"));

        parse_data.parse_title="";

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Initializam baza de date si citim toate cartile si le punem in tabel
        try {
            myCon = DriverManager.getConnection(url, user, password);
            myStmt = myCon.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }

        final ObservableList<BookModel> BooksModel = FXCollections.observableArrayList();
        String sql = "select* from Books where available = false";
        try {
            ResultSet rs = myStmt.executeQuery(sql);

            while(rs.next()){
                String title = rs.getString("title");
                String author = rs.getString("author");
                String ISBN = rs.getString("ISBN");
                String publisher = rs.getString("publisher");
                Boolean available = true;
                available = (Boolean) rs.getObject("available");
                Date return_date = rs.getDate("return_date");

                BooksModel.add(new BookModel(title, author, ISBN, publisher, available, return_date.toString()));

                System.out.println(rs.getInt("id")+title+" "+author+" "+ISBN+" "+publisher+" "+return_date.toString());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        author.setCellValueFactory(new PropertyValueFactory<>("Author"));
        ISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        publisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        return_date.setCellValueFactory(new PropertyValueFactory<>("return_date"));
        tbData.setItems(BooksModel);
        tbData.setPlaceholder(new Label("Nu s-au gasit carti."));
    }
}
