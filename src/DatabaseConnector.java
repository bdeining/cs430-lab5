import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

  private static final String DB_URL =
      "jdbc:mysql://localhost:20002/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

  private static final String INSERT_MEMBER =
      "insert into member (member_id, first_name, last_name, gender, dob) values (%s, '%s', '%s', '%s', '%s');";

  private static final String GET_MEMBER = "select * from member where member_id=%s;";

  private static final String GET_BOOK_BY_ISBN = "select * from stored_on where isbn='%s';";

  private static final String GET_BOOK_BY_TITLE = "select * from book where title like '%%%s%%';";
  private static final String GET_BOOK_TITLE_BY_ISBN = "select * from book where isbn='%s';";

  private static final String GET_AUTHOR_ID =
      "select * from author where first_name='%s' and last_name='%s';";

  private static final String GET_BOOKS_BY_AUTHOR_ID =
      "select * from author_book where author_id=%s;";

  private static final String GET_BOOK_TITLE = "select * from book where isbn='%s';";

  private static final String ISBN = "isbn";

  private static final String TITLE = "title";

  private static final String AUTHOR_ID = "author_id";

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

  public void addMember(
      String memberId, String firstName, String lastName, String dob, String gender) {
    try {
      Connection con = DriverManager.getConnection(url, username, password);
      Statement stmt = con.createStatement();
      stmt.execute(String.format(INSERT_MEMBER, memberId, firstName, lastName, gender, dob));
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<BookDetails> getBookInformationByIsbn(String isbn) {
    List<BookDetails> bookDetails = new ArrayList<>();
    try {
      Connection con = DriverManager.getConnection(url, username, password);
      Statement stmt = con.createStatement();
      ResultSet resultSet = stmt.executeQuery(String.format(GET_BOOK_BY_ISBN, isbn));

      Statement stmt2 = con.createStatement();
      ResultSet bookTitleResultSet = stmt2.executeQuery(String.format(GET_BOOK_TITLE, isbn));
      String title = "Unknown";
      while (bookTitleResultSet.next()) {
        title = bookTitleResultSet.getString(TITLE);
      }

      while (resultSet.next()) {
        bookDetails.add(
            new BookDetails(
                resultSet.getString(ISBN),
                resultSet.getString(LIBRARY),
                resultSet.getInt(SHELF_NUMBER),
                title,
                resultSet.getInt(TOTAL_COPIES)));
      }

      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return bookDetails;
  }

  public List<BookDetails> getBookInformationByTitle(String title) {
    List<BookDetails> bookDetails = new ArrayList<>();
    try {
      Connection con = DriverManager.getConnection(url, username, password);
      Statement stmt = con.createStatement();
      ResultSet resultSet = stmt.executeQuery(String.format(GET_BOOK_BY_TITLE, title));

      while (resultSet.next()) {
        bookDetails.addAll(getBookInformationByIsbn(resultSet.getString(ISBN)));
      }

      con.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return bookDetails;
  }

  public List<String> getBooksByTitle(String title) {
    List<String> bookTitles = new ArrayList<>();
    try {
      Connection con = DriverManager.getConnection(url, username, password);
      Statement stmt = con.createStatement();
      ResultSet resultSet = stmt.executeQuery(String.format(GET_BOOK_BY_TITLE, title));

      while (resultSet.next()) {
        bookTitles.add(resultSet.getString(TITLE));
      }

      con.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return bookTitles;
  }

  public List<String> getBooksByAuthor(String author) {
    String[] authorArray = author.split(",");
      List<String> bookDetails = new ArrayList<>();
    if (authorArray.length != 2) {
        return bookDetails;
    }

    try {
      Connection con = DriverManager.getConnection(url, username, password);
      Statement stmt = con.createStatement();
      ResultSet resultSet =
          stmt.executeQuery(String.format(GET_AUTHOR_ID, authorArray[0], authorArray[1]));

      String authorId = "";
      while (resultSet.next()) {
        authorId = resultSet.getString(AUTHOR_ID);
      }

      System.out.println(authorId);
      if (authorId.equals("")) {
          return bookDetails;
      }

      Statement stmt2 = con.createStatement();
      ResultSet resultSet2 = stmt2.executeQuery(String.format(GET_BOOKS_BY_AUTHOR_ID, authorId));

      List<String> isbns = new ArrayList<>();
      while (resultSet2.next()) {
          System.out.println(resultSet2.getString(ISBN));
        isbns.add(resultSet2.getString(ISBN));
      }

      for (String isbn : isbns) {

        Statement stmt3 = con.createStatement();
        ResultSet resultSet3 = stmt3.executeQuery(String.format(GET_BOOK_TITLE_BY_ISBN, isbn));
        while (resultSet3.next()) {
            System.out.println(resultSet3.getString(TITLE));
          bookDetails.add(resultSet3.getString(TITLE));
        }
      }
      con.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return bookDetails;
  }
}
