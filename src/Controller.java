import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    String url = "jdbc:mysql://localhost:3307/Library";
    String user = "root";
    String password = "root";
    Connection myCon;
    Statement myStmt;

    @FXML
    private TableView<BookModel> tbData;
    @FXML
    private TableColumn<BookModel, String> title;

    @FXML
    private TableColumn<BookModel, String> author;

    @FXML
    private TableColumn<BookModel, String> ISBN;

    @FXML
    private TableColumn<BookModel, Boolean> available;

    @FXML private TextField add_title, add_author, add_ISBN, add_publisher, filter_text;

    @FXML
    private Text Error_text;

    @FXML
    private Button add_book_scene,cancel_add_book,add_book_btn_scene, show_book_btn, rented_books_scene;

    @FXML
    private ComboBox<String> combobox_filter;

    @FXML
    private void change_scene (ActionEvent event) throws Exception {
        Stage stage;
        Parent root;

        if(event.getSource()==add_book_scene){
            stage = (Stage) add_book_scene.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Add_book.fxml"));
        }
        else if(event.getSource()==cancel_add_book){
            stage = (Stage) cancel_add_book.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Primary.fxml"));
        }else if(event.getSource()==add_book_btn_scene){
            stage = (Stage) add_book_btn_scene.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Primary.fxml"));
        }else if(event.getSource()== show_book_btn){
            BookModel book = tbData.getSelectionModel().getSelectedItem();
            if(book==null)return;
            parse_data.parse_title = book.getTitle();
            stage = (Stage) show_book_btn.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Show_book.fxml"));
        }else {
            stage = (Stage) rented_books_scene.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Rented_Books.fxml"));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void add_book(ActionEvent e){
        String title = add_title.getText();
        String author = add_author.getText();
        String ISBN = add_ISBN.getText();
        String publisher = add_publisher.getText();
        if(title.equals("")||author.equals("")||ISBN.equals("")||publisher.equals("")){
            Error_text.setText("Completati toate campurile");
            return;
        }
        String sql = "insert into Books(title,author,ISBN,publisher)values('";
        sql+=title+"','";
        sql+=author+"','";
        sql+=ISBN+"','";
        sql+=publisher+"')";

        try{
            System.out.println("Executing: " + sql);
            myStmt.executeUpdate(sql);
        }
        catch (SQLIntegrityConstraintViolationException exception) {
            Error_text.setText("O carte cu acest titlu deja exista!");
            System.out.println("O carte cu acest titlu deja exista!");
            return;
        }
        catch (SQLException exception){
            Error_text.setText("A aparut o eroare in baza de date!");
            exception.printStackTrace();
            return;
        }
        System.out.println("Book added!");
        try {
            change_scene(e);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void delete_selected_book(ActionEvent e){
        BookModel book = tbData.getSelectionModel().getSelectedItem();
        if(book==null)return;
        String sql="delete from books where title = '" + book.getTitle() + "'";
        try{
            myStmt.executeUpdate(sql);
        }
        catch (SQLException exception){
            exception.printStackTrace();
            return;
        }
        System.out.println(book.getTitle());
        filter_search(e);
    }

    @FXML
    private void filter_search(ActionEvent e){
        String text = filter_text.getText();
        String column = combobox_filter.getValue();
        final ObservableList<BookModel> BooksModel = FXCollections.observableArrayList();

        String sql="";
        if(text.equals(""))sql = "select * from Books";
        else sql = "select * from Books where "+column+" = '"+text+"'";
        try {
            ResultSet rs = myStmt.executeQuery(sql);

            while(rs.next()){
                String title = rs.getString("title");
                String author = rs.getString("author");
                String ISBN = rs.getString("ISBN");
                String publisher = rs.getString("publisher");
                Boolean available = true;
                available = (Boolean) rs.getObject("available");

                BooksModel.add(new BookModel(title, author, ISBN, publisher, available));
                System.out.println(rs.getInt("id")+title+" "+author+" "+ISBN+" "+publisher);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        author.setCellValueFactory(new PropertyValueFactory<>("Author"));
        ISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        available.setCellValueFactory(new PropertyValueFactory<>("available"));
        tbData.setItems(BooksModel);
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
        if(title!=null) {
            String sql = "select* from Books";
            try {
                ResultSet rs = myStmt.executeQuery(sql);

                while(rs.next()){
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String ISBN = rs.getString("ISBN");
                    String publisher = rs.getString("publisher");
                    Boolean available = true;
                    available = (Boolean) rs.getObject("available");

                    BooksModel.add(new BookModel(title, author, ISBN, publisher, available));
                    System.out.println(rs.getInt("id")+title+" "+author+" "+ISBN+" "+publisher);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            title.setCellValueFactory(new PropertyValueFactory<>("Title"));
            author.setCellValueFactory(new PropertyValueFactory<>("Author"));
            ISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
            available.setCellValueFactory(new PropertyValueFactory<>("available"));
            tbData.setItems(BooksModel);
            tbData.setPlaceholder(new Label("Nu s-au gasit carti."));

            //Adaugam iteme in lista pt filtrare
            ObservableList<String> list = combobox_filter.getItems();
            list.add("title");
            list.add("author");
            list.add("ISBN");
            list.add("publisher");
            combobox_filter.setItems(list);
        }


    }

}
