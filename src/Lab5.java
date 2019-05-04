import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class Lab5 extends JPanel implements ActionListener {

  private static final String ISBN = "ISBN";

  private static final String TITLE = "Book Title";

  private static final String AUTHOR = "Author (First Name,Last Name)";

  private static final String SUBMIT = "Submit";

  private static final String EXIT = "Exit";

  private static String querySelection = ISBN;

  private DatabaseConnector databaseConnector;

  private JPanel radioPanel;

  private JPanel bookListPanel;

  private JScrollPane scrollPane;

  private JButton continueButton;

  private JTextArea queryStringText;

  public static void main(String[] args) {

    if (args.length != 2) {
      System.out.println("Must supply username and password as arguments");
      System.out.println("Usage : java Lab4 username password");
    }

    String username = args[0];
    String password = args[1];

    DatabaseConnector databaseConnector = new DatabaseConnector(username, password);

    SwingUtilities.invokeLater(() -> startUi(databaseConnector));
  }

  Lab5(DatabaseConnector databaseConnector) {

    this.databaseConnector = databaseConnector;
    member();
  }

  private void member() {
    removeAll();
    validate();
    repaint();

    querySelection = ISBN;

    String memberId = JOptionPane.showInputDialog(this, "Enter Member ID:");
    if (memberId == null || "".equals(memberId)) {
      System.exit(1);
    }

    boolean memberExists = databaseConnector.memberExists(memberId);

    if (!memberExists) {

      int dialogButton = JOptionPane.YES_NO_OPTION;
      int dialogResult =
          JOptionPane.showConfirmDialog(
              this,
              "This Member Does Not Exist.  Would you like to create one?",
              "Add Member",
              dialogButton);
      if (dialogResult == JOptionPane.YES_OPTION) {

        JTextField firstName = new JTextField(5);
        JTextField lastName = new JTextField(5);
        JTextField dateOfBirth = new JTextField(5);
        JTextField gender = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(4, 2));
        myPanel.setPreferredSize(new Dimension(400, 200));
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
                this, myPanel, "Please Enter Member Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == 0) {
          databaseConnector.addMember(
              memberId,
              firstName.getText(),
              lastName.getText(),
              dateOfBirth.getText(),
              gender.getText());
        } else {
          System.exit(1);
        }
      } else {
        System.exit(1);
      }
    }

    radio();
  }

  private void radio() {

    JRadioButton isbnButton = new JRadioButton(ISBN);
    JRadioButton authorButton = new JRadioButton(AUTHOR);
    JRadioButton titleButton = new JRadioButton(TITLE);
    JButton queryButton = new JButton(SUBMIT);
    JButton exitButton = new JButton(EXIT);
    JLabel jLabel = new JLabel("Enter Query :");
    queryStringText = new JTextArea();

    ButtonGroup group = new ButtonGroup();
    group.add(isbnButton);
    group.add(authorButton);
    group.add(titleButton);

    isbnButton.setSelected(true);
    isbnButton.addActionListener(this);
    authorButton.addActionListener(this);
    titleButton.addActionListener(this);
    queryButton.addActionListener(this);
    exitButton.addActionListener(this);

    radioPanel = new JPanel(new GridLayout(0, 1));
    radioPanel.add(jLabel);
    radioPanel.add(queryStringText);
    radioPanel.add(isbnButton);
    radioPanel.add(authorButton);
    radioPanel.add(titleButton);
    radioPanel.add(queryButton);
    radioPanel.add(exitButton);

    add(radioPanel, BorderLayout.LINE_START);
    validate();
    repaint();
  }

  private static void startUi(DatabaseConnector databaseConnector) {
    JFrame frame = new JFrame("Library System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JComponent newContentPane = new Lab5(databaseConnector);
    newContentPane.setPreferredSize(new Dimension(800, 600));
    newContentPane.setOpaque(true);
    frame.setContentPane(newContentPane);
    frame.pack();
    frame.setVisible(true);
  }

  private void executeQuery() {

    JList<String> jList = new JList<>();
    bookListPanel = new JPanel(new GridLayout(0, 1));
    DefaultListModel<String> listModel = new DefaultListModel<>();
    jList.addListSelectionListener(new CustomListSelectionListener(jList));
    jList.setPreferredSize(new Dimension(800, 600));
    remove(radioPanel);

    switch (querySelection) {
      case ISBN:
        List<BookDetails> bookDetailsList =
            databaseConnector.getBookInformationByIsbn(queryStringText.getText());
        showBookDetails(bookDetailsList);
        break;

      case TITLE:
        List<String> booksByTitle = databaseConnector.getBooksByTitle(queryStringText.getText());
        if (booksByTitle.size() == 0) {
          JOptionPane.showMessageDialog(this, "There are no books with this title.");
          member();

        } else if (booksByTitle.size() == 1) {
          List<BookDetails> detailsList =
              databaseConnector.getBookInformationByTitle(queryStringText.getText());
          showBookDetails(detailsList);
        } else {

          for (String book : booksByTitle) {
            listModel.addElement(book);
          }
          jList.setModel(listModel);
          bookListPanel.add(jList);
          add(bookListPanel, BorderLayout.LINE_START);
          validate();
          repaint();
        }
        break;

      case AUTHOR:
        List<String> bookDetails = databaseConnector.getBooksByAuthor(queryStringText.getText());
        if (bookDetails.size() == 0) {
          JOptionPane.showMessageDialog(this, "There are no books by this author.");
          member();
        } else {

          for (String book : bookDetails) {
            listModel.addElement(book);
          }
          jList.setModel(listModel);
          bookListPanel.add(jList);
          add(bookListPanel, BorderLayout.LINE_START);
          validate();
          repaint();
        }

        break;
    }
  }

  private void showBookDetails(List<BookDetails> bookDetailsList) {

    remove(bookListPanel);

    List<BookDetails> bookDetailListWithCopies =
        bookDetailsList
            .stream()
            .filter(bookDetails -> bookDetails.getTotalCopies() > 0)
            .collect(Collectors.toList());

    if (bookDetailsList.size() == 0) {
      JOptionPane.showMessageDialog(this, "This book is not in stock.");
      member();
    } else if (bookDetailListWithCopies.size() == 0) {
      JOptionPane.showMessageDialog(this, "All copies of this book are checked out.");
      member();
    } else {

      Object[][] objects =
          bookDetailsList
              .stream()
              .map(BookDetails::getTable)
              .collect(Collectors.toList())
              .toArray(new Object[0][0]);
      JTable jTable = new JTable(objects, BookDetails.COLUMN_NAMES);

      jTable.setPreferredSize(new Dimension(800, 300));
      scrollPane = new JScrollPane(jTable);
      scrollPane.setPreferredSize(new Dimension(800, 300));
      add(scrollPane, BorderLayout.CENTER);
      continueButton = new JButton("Ok");
      continueButton.addActionListener(new CustomActionListener());
      add(continueButton);
      validate();
      repaint();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (TITLE.equals(command) || ISBN.equals(command) || AUTHOR.equals(command)) {
      querySelection = e.getActionCommand();
    } else if (EXIT.equals(command)) {
      member();
    } else if (SUBMIT.equals(command)) {
      executeQuery();
    }
  }

  private class CustomActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      member();
    }
  }

  private class CustomListSelectionListener implements ListSelectionListener {

    private JList defaultListModel;

    CustomListSelectionListener(JList defaultListModel) {
      this.defaultListModel = defaultListModel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      List<BookDetails> bookDetailsList =
          databaseConnector.getBookInformationByTitle((String) defaultListModel.getSelectedValue());
      showBookDetails(bookDetailsList);
    }
  }
}
