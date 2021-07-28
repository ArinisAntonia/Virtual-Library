import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookModel {

    private SimpleStringProperty title;
    private SimpleStringProperty author;
    private SimpleStringProperty ISBN;
    private SimpleStringProperty publisher;
    private SimpleBooleanProperty available;
    private SimpleStringProperty return_date;

    public BookModel(String title, String author, String ISBN, String publisher, Boolean available) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.ISBN = new SimpleStringProperty(ISBN);
        this.publisher = new SimpleStringProperty(publisher);
        this.available = new SimpleBooleanProperty(available);
    }

    public BookModel(String title, String author, String ISBN, String publisher, Boolean available, String return_date) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.ISBN = new SimpleStringProperty(ISBN);
        this.publisher = new SimpleStringProperty(publisher);
        this.available = new SimpleBooleanProperty(available);
        this.return_date = new SimpleStringProperty(return_date);
    }

    public boolean isAvailable() {
        return available.get();
    }

    public SimpleBooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title= new SimpleStringProperty(title);
    }

    public String getAuthor() {
        return author.get();
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author= new SimpleStringProperty(author);
    }

    public String getISBN() {
        return ISBN.get();
    }

    public SimpleStringProperty ISBNProperty() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN= new SimpleStringProperty(ISBN);
    }

    public String getPublisher() {
        return publisher.get();
    }

    public SimpleStringProperty publisherProperty() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher= new SimpleStringProperty(publisher);
    }

    public String getReturn_date() {
        return return_date.get();
    }

    public SimpleStringProperty return_dateProperty() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date.set(return_date);
    }
}
