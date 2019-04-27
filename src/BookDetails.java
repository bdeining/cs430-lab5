public class BookDetails {

    public static final String[] COLUMN_NAMES = {"ISBN", "Title", "Library", "Shelf Number", "Total Copies" };

    private String library;

    private int shelfNumber;

    private int totalCopies;

    private String isbn;

    private String title;


    public BookDetails(String isbn, String library, int shelfNumber, String title, int totalCopies) {
        this.isbn = isbn;
        this.library = library;
        this.shelfNumber = shelfNumber;
        this.title = title;
        this.totalCopies = totalCopies;
    }

    public Object[] getTable() {
        Object[] objects = {isbn, title, library, shelfNumber, totalCopies};
        return objects;
    }

    public int getTotalCopies() {
        return totalCopies;
    }
}
