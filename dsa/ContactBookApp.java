import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ContactBookApp extends JFrame {
    private JTable table; //created table 
    private DefaultTableModel tableModel;

    public ContactBookApp() {
        setTitle("Contact Book");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // window will be centered on the user's screen
        initUI();
        loadContacts();
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new String[]{"Name", "Phone", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(this::handleAdd);
        editButton.addActionListener(this::handleEdit);
        deleteButton.addActionListener(this::handleDelete);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<String[]> contacts = DatabaseHelper.getAllContacts();
        for (String[] contact : contacts) {
            tableModel.addRow(contact);
        }
    }

    private void handleAdd(ActionEvent e) {
        ContactDialog dialog = new ContactDialog(this, "Add Contact", null);
        dialog.setVisible(true);
        loadContacts();
    }

    private void handleEdit(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact to edit.");
            return;
        }

        String name = tableModel.getValueAt(selectedRow, 0).toString();
        String phone = tableModel.getValueAt(selectedRow, 1).toString();
        String email = tableModel.getValueAt(selectedRow, 2).toString();

        ContactDialog dialog = new ContactDialog(this, "Edit Contact", new String[]{name, phone, email});
        dialog.setVisible(true);
        loadContacts();
    }

    private void handleDelete(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact to delete.");
            return;
        }

        String name = tableModel.getValueAt(selectedRow, 0).toString();
        if (DatabaseHelper.deleteContact(name)) {
            JOptionPane.showMessageDialog(this, "Contact deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting contact.");
        }
        loadContacts();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactBookApp().setVisible(true)); //provides a safe way to handle such interactions
    }
}

class ContactDialog extends JDialog {
    private JTextField nameField, phoneField, emailField;
    private boolean isEdit;
    private String originalName;

    public ContactDialog(JFrame parent, String title, String[] contactData) {
        super(parent, title, true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        initUI();

        if (contactData != null) {
            isEdit = true;
            originalName = contactData[0];
            nameField.setText(contactData[0]);
            phoneField.setText(contactData[1]);
            emailField.setText(contactData[2]);
            nameField.setEditable(false); // Name is primary key; it cannot be changed
        }
    }

    private void initUI() {
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> handleSave());

        setLayout(new GridLayout(4, 2));
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel());
        add(saveButton);
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Phone are required.");
            return;
        }

        boolean success;
        if (isEdit) {
            success = DatabaseHelper.updateContact(name, phone, email);
        } else {
            success = DatabaseHelper.addContact(name, phone, email);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Contact saved.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving contact.");
        }
    }
}
