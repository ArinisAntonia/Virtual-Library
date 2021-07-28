import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ShowBook implements Initializable {
    String url = "jdbc:mysql://localhost:3307/Library";
    String user = "root";
    String password = "root";
    Connection myCon;
    Statement myStmt;

    private String title;

    @FXML
    private Button back_to_primary;

    @FXML
    private TextField show_title, show_author, show_ISBN, show_publisher;

    @FXML
    private DatePicker show_returndate;

    @FXML
    private Text returndate_text, Error_text;

    @FXML
    private CheckBox available_checkbox;

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

    @FXML
    private void hide_returndate(ActionEvent e){
        if(available_checkbox.isSelected()){
            show_returndate.setVisible(false);
            returndate_text.setVisible(false);
        }else{
            show_returndate.setVisible(true);
            returndate_text.setVisible(true);
        }
    }

    @FXML
    private void edit_book(ActionEvent e){
        String title = show_title.getText();
        String author = show_author.getText();
        String ISBN = show_ISBN.getText();
        String publisher = show_publisher.getText();
        Boolean available = available_checkbox.isSelected();
        Date return_date = null;
        if(available==false){
            try{
                return_date = Date.valueOf(show_returndate.getValue());
            }
            catch(NullPointerException exception){
                Error_text.setText("Completati toate campurile");
                return;
            }
        }
        if(title.equals("")||author.equals("")||ISBN.equals("")||publisher.equals("")){
            Error_text.setText("Completati toate campurile");
            return;
        }

        String sql = "update Books set title='";
        sql+=title+"',";
        sql+="author = '"+author+"',";
        sql+="ISBN = '"+ISBN+"',";
        sql+="publisher = '"+publisher+"',";
        sql+="available = "+available+",";
        if(available)sql+="return_date = null";
        else sql+="return_date = CONCAT('"+return_date+" ', TIME(0))";
        sql+=" where title = '"+parse_data.parse_title+"';";
        //return_date = CONCAT('2014-12-14 ', TIME(0))
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

        try {
            change_scene(e);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        System.out.println("Cartea a fost editata!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = parse_data.parse_title;
        System.out.println(title);
        try {
            myCon = DriverManager.getConnection(url, user, password);
            myStmt = myCon.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }

        String sql = "select* from Books where title = '"+ title+"'";
        try {
            ResultSet rs = myStmt.executeQuery(sql);

            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String ISBN = rs.getString("ISBN");
                String publisher = rs.getString("publisher");
                Boolean available = true;
                available = (Boolean) rs.getObject("available");
                Date date = null;
                if(available==false){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    date = rs.getDate("return_date");
                    show_returndate.setValue(date.toLocalDate());
                    show_returndate.setVisible(true);
                    returndate_text.setVisible(true);
                    available_checkbox.setSelected(false);
                }

                show_title.setText(title);
                show_author.setText(author);
                show_ISBN.setText(ISBN);
                show_publisher.setText(publisher);

                System.out.println(rs.getInt("id") + title + " " + author + " " + ISBN + " " + publisher);
            }
        }
        catch (SQLException throwables) {
                throwables.printStackTrace();
        }
    }
}
