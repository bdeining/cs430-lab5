import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

    private static final String DB_URL = "jdbc:mysql://localhost:20002/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static final String INSERT_MEMBER = "insert into member (member_id, first_name, last_name, gender, dob) values (%s, '%s', '%s', '%s', '%s');";

    private static final String GET_MEMBER = "select * from member where member_id=%s;";

    private static final String GET_BOOK = "select * from stored_on where isbn='%s';";

    private static final String GET_BOOK_TITLE = "select * from book where isbn='%s';";

    private static final String ISBN = "isbn";

    private static final String TITLE = "title";

    private static final String LIBRARY = "name";

    private static final String SHELF_NUMBER = "shelf_number";

    private static final String TOTAL_COPIES = "total_copies";

    private String username;

    private String password;

    private String url;

    public DatabaseConnector(String username, String password) {
        this.username = username;
        this.password = password;

        try {
        Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.url = String.format(DB_URL, username);
    }

    public boolean memberExists(String memberId) {
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();
            boolean result = stmt.executeQuery(String.format(GET_MEMBER, memberId)).next();
            con.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addMember(String memberId, String firstName, String lastName, String dob, String gender) {
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();
            stmt.execute(String.format(INSERT_MEMBER, memberId, firstName, lastName, gender, dob));
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BookDetails> getBookInformation(String isbn) {
        List<BookDetails> bookDetails = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(String.format(GET_BOOK, isbn));

            ResultSet bookTitleResultSet = stmt.executeQuery(GET_BOOK_TITLE);
            String title = "Unknown";
            while(resultSet.next()) {
                title = bookTitleResultSet.getString(TITLE);
            }

            while(resultSet.next()) {
                bookDetails.add(new BookDetails(resultSet.getString(ISBN), resultSet.getString(LIBRARY), resultSet.getInt(SHELF_NUMBER), title, resultSet.getInt(TOTAL_COPIES)));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookDetails;
    }
}
