import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.List;
import java.util.stream.Collectors;

public class Lab5 extends JPanel {

  public static void main(String[] args) {

    if (args.length != 2) {
      System.out.println("Must supply username and password as arguments");
      System.out.println("Usage :  java Lab4 username password");
    }

    String username = args[0];
    String password = args[1];

    DatabaseConnector databaseConnector = new DatabaseConnector(username, password);

    SwingUtilities.invokeLater(() -> startUi(databaseConnector));
  }

  Lab5(DatabaseConnector databaseConnector) {

    String memberId = JOptionPane.showInputDialog("Enter Member ID:");

    boolean memberExists = databaseConnector.memberExists(memberId);

    System.out.println(memberExists);

    if (!memberExists) {

      int dialogButton = JOptionPane.YES_NO_OPTION;
      int dialogResult =
          JOptionPane.showConfirmDialog(
              null,
              "This Member Does Not Exist.  Would you like to create one?",
              "Add Member",
              dialogButton);
      if (dialogResult == JOptionPane.YES_OPTION) {
        // Saving code here

        JTextField firstName = new JTextField(5);
        JTextField lastName = new JTextField(5);
        JTextField dateOfBirth = new JTextField(5);
        JTextField gender = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("First Name:"));
        myPanel.add(firstName);
        myPanel.add(new JLabel("Last Name:"));
        myPanel.add(lastName);
        myPanel.add(new JLabel("Gender:"));
        myPanel.add(gender);
        myPanel.add(new JLabel("DOB:"));
        myPanel.add(dateOfBirth);
        int result =
            JOptionPane.showConfirmDialog(
                null, myPanel, "Please Enter Member Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == 0) {
          System.out.println(firstName.getText());
          System.out.println(lastName.getText());
          System.out.println(dateOfBirth.getText());
          databaseConnector.addMember(
              memberId,
              firstName.getText(),
              lastName.getText(),
              gender.getText(),
              dateOfBirth.getText());
        } else {
          return;
        }
      }
    }

    String isbn = JOptionPane.showInputDialog("Enter ISBN of book to check out:");
    List<BookDetails> bookDetailsList = databaseConnector.getBookInformation(isbn);
    List<BookDetails> bookDetailListWithCopies =
        bookDetailsList
            .stream()
            .filter(bookDetails -> bookDetails.getTotalCopies() > 0)
            .collect(Collectors.toList());

    if (bookDetailsList.size() == 0) {
      JOptionPane.showInputDialog("This book is not in stock.");
    } else if (bookDetailListWithCopies.size() == 0) {
      JOptionPane.showInputDialog("All copies of this book are checked out.");
    } else {

      Object[][] objects =
          bookDetailsList
              .stream()
              .map(BookDetails::getTable)
              .collect(Collectors.toList())
              .toArray(new Object[0][0]);
      JTable jTable = new JTable(objects, BookDetails.COLUMN_NAMES);
      JScrollPane scrollPane = new JScrollPane(jTable);
      add(scrollPane, BorderLayout.CENTER);
    }
  }

  private static void startUi(DatabaseConnector databaseConnector) {
    JFrame frame = new JFrame("Library System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JComponent newContentPane = new Lab5(databaseConnector);
    newContentPane.setOpaque(true);
    frame.setContentPane(newContentPane);
    frame.pack();
    frame.setVisible(true);
  }
}
