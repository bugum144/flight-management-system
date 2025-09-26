import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChipperAirFrontend extends JFrame {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chipper_air";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234567890rs@@";
    private Connection conn;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ChipperAirFrontend() {
        super("Chipper Air Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center the window

        // Initialize card layout for login/main app switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create login panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "login");

        // Create main application panel (will be initialized after login)
        mainPanel.add(new JPanel(), "app");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel headerLabel = new JLabel("CHIPPER AIR MANAGEMENT SYSTEM", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);

        // Logo (placeholder)
        JLabel logoLabel = new JLabel("✈", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        logoLabel.setForeground(new Color(0, 102, 204));
        gbc.gridy = 1;
        panel.add(logoLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginButton, gbc);

        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                // Initialize database connection after successful login
                connectToDatabase();
                // Create main application panel
                JPanel appPanel = createMainApplicationPanel();
                mainPanel.add(appPanel, "app");
                cardLayout.show(mainPanel, "app");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Enter key to login
        panel.registerKeyboardAction(e -> loginButton.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return panel;
    }

    private boolean authenticateUser(String username, String password) {
        // Simple authentication - in a real application, this would validate against the database
        return "admin".equals(username) && "admin123".equals(password);
    }

    private JPanel createMainApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create header with logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Chipper Air Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(204, 0, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "login");
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed interface
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add tabs for each table with improved UI
        tabbedPane.addTab("Passenger", createStyledPanel("Passenger Management", createPassengerPanel()));
        tabbedPane.addTab("Next of Kin", createStyledPanel("Next of Kin Management", createNextOfKinPanel()));
        tabbedPane.addTab("Airport", createStyledPanel("Airport Management", createAirportPanel()));
        tabbedPane.addTab("Aircraft", createStyledPanel("Aircraft Management", createAircraftPanel()));
        tabbedPane.addTab("Flight", createStyledPanel("Flight Management", createFlightPanel()));
        tabbedPane.addTab("Booking", createStyledPanel("Booking Management", createBookingPanel()));
        tabbedPane.addTab("Payment", createStyledPanel("Payment Management", createPaymentPanel()));
        tabbedPane.addTab("Ticket", createStyledPanel("Ticket Management", createTicketPanel()));
        tabbedPane.addTab("Staff", createStyledPanel("Staff Management", createStaffPanel()));
        tabbedPane.addTab("Notification", createStyledPanel("Notification Management", createNotificationPanel()));

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStyledPanel(String title, JPanel contentPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 245, 249));

        // Add a title to the form
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Wrap the content panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(240, 245, 249));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Enhanced Passenger Panel with improved UI
    private JPanel createPassengerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Passenger ID:"), gbc);

        JTextField passengerID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(passengerID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("First Name:"), gbc);

        JTextField firstName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(firstName, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Last Name:"), gbc);

        JTextField lastName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(lastName, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);

        JTextField email = new JTextField(20);
        gbc.gridx = 1;
        panel.add(email, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Phone Number:"), gbc);

        JTextField phoneNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(phoneNumber, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Date of Birth:"), gbc);

        JTextField dob = new JTextField(20);
        dob.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
        gbc.gridx = 1;
        panel.add(dob, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        panel.add(new JLabel("Gender:"), gbc);

        JComboBox<String> gender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        gbc.gridx = 1;
        panel.add(gender, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        panel.add(new JLabel("Marital Status:"), gbc);

        JComboBox<String> maritalStatus = new JComboBox<>(new String[]{"Single", "Married", "Divorced", "Widowed"});
        gbc.gridx = 1;
        panel.add(maritalStatus, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        panel.add(new JLabel("Address:"), gbc);

        JTextField address = new JTextField(20);
        gbc.gridx = 1;
        panel.add(address, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        panel.add(new JLabel("Passport Number:"), gbc);

        JTextField passportNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(passportNumber, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Passenger");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        JButton viewButton = new JButton("View All");
        styleButton(viewButton, new Color(0, 153, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewButton);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Passenger VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, passengerID.getText());
                stmt.setString(2, firstName.getText());
                stmt.setString(3, lastName.getText());
                stmt.setString(4, email.getText());
                stmt.setString(5, phoneNumber.getText());
                stmt.setDate(6, Date.valueOf(dob.getText()));
                stmt.setString(7, (String) gender.getSelectedItem());
                stmt.setString(8, (String) maritalStatus.getSelectedItem());
                stmt.setString(9, address.getText());
                stmt.setString(10, passportNumber.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Passenger added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            passengerID.setText("");
            firstName.setText("");
            lastName.setText("");
            email.setText("");
            phoneNumber.setText("");
            dob.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
            gender.setSelectedIndex(0);
            maritalStatus.setSelectedIndex(0);
            address.setText("");
            passportNumber.setText("");
        });

        // View action
        viewButton.addActionListener(e -> {
            try {
                String sql = "SELECT * FROM Passenger";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                StringBuilder sb = new StringBuilder();
                sb.append("Passenger List:\n\n");

                while (rs.next()) {
                    sb.append("ID: ").append(rs.getString("passenger_id"))
                            .append(", Name: ").append(rs.getString("first_name"))
                            .append(" ").append(rs.getString("last_name"))
                            .append(", Email: ").append(rs.getString("email"))
                            .append("\n");
                }

                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "Passengers", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Query Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    // Next of Kin Panel (similar structure to Passenger panel)
    private JPanel createNextOfKinPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Next of Kin ID:"), gbc);

        JTextField nextOfKinID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nextOfKinID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Passenger ID:"), gbc);

        JTextField passengerID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(passengerID, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gbc);

        JTextField fullName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(fullName, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Relationship:"), gbc);

        JTextField relationship = new JTextField(20);
        gbc.gridx = 1;
        panel.add(relationship, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Address:"), gbc);

        JTextField address = new JTextField(20);
        gbc.gridx = 1;
        panel.add(address, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Phone Number:"), gbc);

        JTextField phoneNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(phoneNumber, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Next of Kin");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO NextOfKin VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nextOfKinID.getText());
                stmt.setString(2, passengerID.getText());
                stmt.setString(3, fullName.getText());
                stmt.setString(4, relationship.getText());
                stmt.setString(5, address.getText());
                stmt.setString(6, phoneNumber.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Next of Kin added successfully!");

                // Clear fields
                nextOfKinID.setText("");
                passengerID.setText("");
                fullName.setText("");
                relationship.setText("");
                address.setText("");
                phoneNumber.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            nextOfKinID.setText("");
            passengerID.setText("");
            fullName.setText("");
            relationship.setText("");
            address.setText("");
            phoneNumber.setText("");
        });

        return panel;
    }

    // Airport Panel
    private JPanel createAirportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Airport Code:"), gbc);

        JTextField airportCode = new JTextField(20);
        gbc.gridx = 1;
        panel.add(airportCode, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Airport Name:"), gbc);

        JTextField airportName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(airportName, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("City:"), gbc);

        JTextField city = new JTextField(20);
        gbc.gridx = 1;
        panel.add(city, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Country:"), gbc);

        JTextField country = new JTextField(20);
        gbc.gridx = 1;
        panel.add(country, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Airport");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Airport VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, airportCode.getText());
                stmt.setString(2, airportName.getText());
                stmt.setString(3, city.getText());
                stmt.setString(4, country.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Airport added successfully!");

                // Clear fields
                airportCode.setText("");
                airportName.setText("");
                city.setText("");
                country.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            airportCode.setText("");
            airportName.setText("");
            city.setText("");
            country.setText("");
        });

        return panel;
    }

    // Aircraft Panel
    private JPanel createAircraftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Aircraft ID:"), gbc);

        JTextField aircraftID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(aircraftID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Model:"), gbc);

        JTextField model = new JTextField(20);
        gbc.gridx = 1;
        panel.add(model, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Capacity:"), gbc);

        JTextField capacity = new JTextField(20);
        gbc.gridx = 1;
        panel.add(capacity, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Maintenance Status:"), gbc);

        JComboBox<String> maintenanceStatus = new JComboBox<>(new String[]{"Operational", "Maintenance", "Out of Service"});
        gbc.gridx = 1;
        panel.add(maintenanceStatus, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Aircraft");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Aircraft VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, aircraftID.getText());
                stmt.setString(2, model.getText());
                stmt.setInt(3, Integer.parseInt(capacity.getText()));
                stmt.setString(4, (String) maintenanceStatus.getSelectedItem());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Aircraft added successfully!");

                // Clear fields
                aircraftID.setText("");
                model.setText("");
                capacity.setText("");
                maintenanceStatus.setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            aircraftID.setText("");
            model.setText("");
            capacity.setText("");
            maintenanceStatus.setSelectedIndex(0);
        });

        return panel;
    }

    // Flight Panel
    private JPanel createFlightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Flight Number:"), gbc);

        JTextField flightNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(flightNumber, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Origin Airport Code:"), gbc);

        JTextField originAirportCode = new JTextField(20);
        gbc.gridx = 1;
        panel.add(originAirportCode, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Destination Airport Code:"), gbc);

        JTextField destinationAirportCode = new JTextField(20);
        gbc.gridx = 1;
        panel.add(destinationAirportCode, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Departure Time:"), gbc);

        JTextField depTime = new JTextField(20);
        depTime.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridx = 1;
        panel.add(depTime, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Arrival Time:"), gbc);

        JTextField arrTime = new JTextField(20);
        arrTime.setText(LocalDate.now().atTime(12, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridx = 1;
        panel.add(arrTime, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Total Seats:"), gbc);

        JTextField totalSeats = new JTextField(20);
        gbc.gridx = 1;
        panel.add(totalSeats, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        panel.add(new JLabel("Aircraft ID:"), gbc);

        JTextField aircraftID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(aircraftID, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Flight");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Flight VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, flightNumber.getText());
                stmt.setString(2, originAirportCode.getText());
                stmt.setString(3, destinationAirportCode.getText());
                stmt.setTimestamp(4, Timestamp.valueOf(depTime.getText().replace(" ", "T") + ":00"));
                stmt.setTimestamp(5, Timestamp.valueOf(arrTime.getText().replace(" ", "T") + ":00"));
                stmt.setInt(6, Integer.parseInt(totalSeats.getText()));
                stmt.setString(7, aircraftID.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Flight added successfully!");

                // Clear fields
                flightNumber.setText("");
                originAirportCode.setText("");
                destinationAirportCode.setText("");
                depTime.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                arrTime.setText(LocalDate.now().atTime(12, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                totalSeats.setText("");
                aircraftID.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            flightNumber.setText("");
            originAirportCode.setText("");
            destinationAirportCode.setText("");
            depTime.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            arrTime.setText(LocalDate.now().atTime(12, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            totalSeats.setText("");
            aircraftID.setText("");
        });

        return panel;
    }

    // Booking Panel
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Booking ID:"), gbc);

        JTextField bookingID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(bookingID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Passenger ID:"), gbc);

        JTextField passengerID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(passengerID, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Flight Number:"), gbc);

        JTextField flightNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(flightNumber, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Booking Date:"), gbc);

        JTextField bookingDate = new JTextField(20);
        bookingDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        gbc.gridx = 1;
        panel.add(bookingDate, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Seat Number:"), gbc);

        JTextField seatNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(seatNumber, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Booking Status:"), gbc);

        JComboBox<String> bookingStatus = new JComboBox<>(new String[]{"Confirmed", "Pending", "Cancelled"});
        gbc.gridx = 1;
        panel.add(bookingStatus, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Booking");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Booking VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, bookingID.getText());
                stmt.setString(2, passengerID.getText());
                stmt.setString(3, flightNumber.getText());
                stmt.setDate(4, Date.valueOf(bookingDate.getText()));
                stmt.setString(5, seatNumber.getText());
                stmt.setString(6, (String) bookingStatus.getSelectedItem());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking added successfully!");

                // Clear fields
                bookingID.setText("");
                passengerID.setText("");
                flightNumber.setText("");
                bookingDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
                seatNumber.setText("");
                bookingStatus.setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            bookingID.setText("");
            passengerID.setText("");
            flightNumber.setText("");
            bookingDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            seatNumber.setText("");
            bookingStatus.setSelectedIndex(0);
        });

        return panel;
    }

    // Payment Panel
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Payment ID:"), gbc);

        JTextField paymentID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(paymentID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Booking ID:"), gbc);

        JTextField bookingID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(bookingID, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Payment Method:"), gbc);

        JComboBox<String> method = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal", "Bank Transfer"});
        gbc.gridx = 1;
        panel.add(method, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Amount:"), gbc);

        JTextField amount = new JTextField(20);
        gbc.gridx = 1;
        panel.add(amount, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Payment Date:"), gbc);

        JTextField paymentDate = new JTextField(20);
        paymentDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        gbc.gridx = 1;
        panel.add(paymentDate, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Payment");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Payment VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, paymentID.getText());
                stmt.setString(2, bookingID.getText());
                stmt.setString(3, (String) method.getSelectedItem());
                stmt.setBigDecimal(4, new java.math.BigDecimal(amount.getText()));
                stmt.setDate(5, Date.valueOf(paymentDate.getText()));

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Payment added successfully!");

                // Clear fields
                paymentID.setText("");
                bookingID.setText("");
                method.setSelectedIndex(0);
                amount.setText("");
                paymentDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            paymentID.setText("");
            bookingID.setText("");
            method.setSelectedIndex(0);
            amount.setText("");
            paymentDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        });

        return panel;
    }

    // Ticket Panel
    private JPanel createTicketPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Ticket ID:"), gbc);

        JTextField ticketID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(ticketID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Booking ID:"), gbc);

        JTextField bookingID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(bookingID, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Issue Date:"), gbc);

        JTextField issueDate = new JTextField(20);
        issueDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        gbc.gridx = 1;
        panel.add(issueDate, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Travel Class:"), gbc);

        JComboBox<String> travelClass = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        gbc.gridx = 1;
        panel.add(travelClass, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Seat Position:"), gbc);

        JTextField seatPosition = new JTextField(20);
        gbc.gridx = 1;
        panel.add(seatPosition, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("QR Code:"), gbc);

        JTextField qrCode = new JTextField(20);
        gbc.gridx = 1;
        panel.add(qrCode, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Ticket");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Ticket VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, ticketID.getText());
                stmt.setString(2, bookingID.getText());
                stmt.setDate(3, Date.valueOf(issueDate.getText()));
                stmt.setString(4, (String) travelClass.getSelectedItem());
                stmt.setString(5, seatPosition.getText());
                stmt.setString(6, qrCode.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ticket added successfully!");

                // Clear fields
                ticketID.setText("");
                bookingID.setText("");
                issueDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
                travelClass.setSelectedIndex(0);
                seatPosition.setText("");
                qrCode.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            ticketID.setText("");
            bookingID.setText("");
            issueDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            travelClass.setSelectedIndex(0);
            seatPosition.setText("");
            qrCode.setText("");
        });

        return panel;
    }

    // Staff Panel
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Staff Number:"), gbc);

        JTextField staffNumber = new JTextField(20);
        gbc.gridx = 1;
        panel.add(staffNumber, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("First Name:"), gbc);

        JTextField firstName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(firstName, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Last Name:"), gbc);

        JTextField lastName = new JTextField(20);
        gbc.gridx = 1;
        panel.add(lastName, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);

        JTextField email = new JTextField(20);
        gbc.gridx = 1;
        panel.add(email, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Phone:"), gbc);

        JTextField phone = new JTextField(20);
        gbc.gridx = 1;
        panel.add(phone, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Address:"), gbc);

        JTextField address = new JTextField(20);
        gbc.gridx = 1;
        panel.add(address, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        panel.add(new JLabel("Date of Birth:"), gbc);

        JTextField dob = new JTextField(20);
        dob.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
        gbc.gridx = 1;
        panel.add(dob, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        panel.add(new JLabel("Gender:"), gbc);

        JComboBox<String> gender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        gbc.gridx = 1;
        panel.add(gender, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        panel.add(new JLabel("Job Role:"), gbc);

        JTextField jobRole = new JTextField(20);
        gbc.gridx = 1;
        panel.add(jobRole, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        panel.add(new JLabel("Salary:"), gbc);

        JTextField salary = new JTextField(20);
        gbc.gridx = 1;
        panel.add(salary, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Staff");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Staff VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, staffNumber.getText());
                stmt.setString(2, firstName.getText());
                stmt.setString(3, lastName.getText());
                stmt.setString(4, email.getText());
                stmt.setString(5, phone.getText());
                stmt.setString(6, address.getText());
                stmt.setDate(7, Date.valueOf(dob.getText()));
                stmt.setString(8, (String) gender.getSelectedItem());
                stmt.setString(9, jobRole.getText());
                stmt.setBigDecimal(10, new java.math.BigDecimal(salary.getText()));

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff added successfully!");

                // Clear fields
                staffNumber.setText("");
                firstName.setText("");
                lastName.setText("");
                email.setText("");
                phone.setText("");
                address.setText("");
                dob.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
                gender.setSelectedIndex(0);
                jobRole.setText("");
                salary.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            staffNumber.setText("");
            firstName.setText("");
            lastName.setText("");
            email.setText("");
            phone.setText("");
            address.setText("");
            dob.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
            gender.setSelectedIndex(0);
            jobRole.setText("");
            salary.setText("");
        });

        return panel;
    }

    // Notification Panel
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Notification ID:"), gbc);

        JTextField notificationID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(notificationID, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Passenger ID:"), gbc);

        JTextField passengerID = new JTextField(20);
        gbc.gridx = 1;
        panel.add(passengerID, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Notification Type:"), gbc);

        JComboBox<String> notificationType = new JComboBox<>(new String[]{"Booking Confirmation", "Flight Update", "Payment Receipt", "Promotional"});
        gbc.gridx = 1;
        panel.add(notificationType, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Content:"), gbc);

        JTextField content = new JTextField(20);
        gbc.gridx = 1;
        panel.add(content, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Date Sent:"), gbc);

        JTextField dateSent = new JTextField(20);
        dateSent.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridx = 1;
        panel.add(dateSent, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Status:"), gbc);

        JComboBox<String> status = new JComboBox<>(new String[]{"Sent", "Pending", "Failed"});
        gbc.gridx = 1;
        panel.add(status, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 245, 249));

        JButton submitButton = new JButton("Add Notification");
        styleButton(submitButton, new Color(0, 102, 204));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(102, 102, 102));

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO Notification VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, notificationID.getText());
                stmt.setString(2, passengerID.getText());
                stmt.setString(3, (String) notificationType.getSelectedItem());
                stmt.setString(4, content.getText());
                stmt.setTimestamp(5, Timestamp.valueOf(dateSent.getText().replace(" ", "T") + ":00"));
                stmt.setString(6, (String) status.getSelectedItem());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Notification added successfully!");

                // Clear fields
                notificationID.setText("");
                passengerID.setText("");
                notificationType.setSelectedIndex(0);
                content.setText("");
                dateSent.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                status.setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Insert Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear action
        clearButton.addActionListener(e -> {
            notificationID.setText("");
            passengerID.setText("");
            notificationType.setSelectedIndex(0);
            content.setText("");
            dateSent.setText(LocalDate.now().atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            status.setSelectedIndex(0);
        });

        return panel;
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> new ChipperAirFrontend());
    }
}