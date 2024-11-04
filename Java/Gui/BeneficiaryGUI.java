import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.ArrayList;

public class BeneficiaryGUI extends JFrame {
    private final Organization organization;
    private final Beneficiary beneficiary;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private String currentPage = "Dashboard";

    // Enhanced Design Constants
    private final Color PRIMARY_COLOR = new Color(63, 81, 181);    // Material Indigo
    private final Color ACCENT_COLOR = new Color(255, 64, 129);    // Material Pink
    private final Color SIDEBAR_COLOR = new Color(25, 32, 48);     // Dark Blue-Grey
    private final Color BG_COLOR = new Color(245, 247, 250);       // Light Gray-Blue
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private final Color TEXT_SECONDARY = new Color(117, 117, 117);

    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public BeneficiaryGUI(Organization organization, Beneficiary beneficiary) {
        this.organization = organization;
        this.beneficiary = beneficiary;
        setupLookAndFeel();
        setupFrame();
        createMainLayout();
        showBeneficiaryDashboard();
        setVisible(true);
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFrame() {
        setTitle("Beneficiary Dashboard - " + organization.getName());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
    }

    private void createMainLayout() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        JPanel sidebar = createBeneficiarySidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 20;
                for (int i = 0; i < shadowSize; i++) {
                    float ratio = (float) i / shadowSize;
                    int alpha = (int)((1 - ratio) * 30);
                    if (alpha > 0) {
                        g2d.setColor(new Color(0, 0, 0, alpha));
                        g2d.drawRoundRect(i, i, getWidth() - (i * 2),
                                getHeight() - (i * 2), 20, 20);
                    }
                }
                g2d.dispose();
            }
        };
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createBeneficiarySidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, SIDEBAR_COLOR,
                        0, getHeight(), new Color(18, 23, 35)
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(),
                        getHeight(), 0, 0));
                g2d.dispose();
            }
        };

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        JPanel logoPanel = createBeneficiaryLogoPanel();
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

        addBeneficiarySidebarButton(sidebar, "Dashboard", "📊", this::showBeneficiaryDashboard);
        addBeneficiarySidebarButton(sidebar, "Add Requests", "➕", this::showAddRequests);
        addBeneficiarySidebarButton(sidebar, "View Requests", "📋", this::showViewRequests);
        addBeneficiarySidebarButton(sidebar, "View Received", "📦", this::showReceivedItems);
        addBeneficiarySidebarButton(sidebar, "Commit", "✔️", this::handleBeneficiaryCommit);

        sidebar.add(Box.createVerticalGlue());
        addBeneficiarySidebarButton(sidebar, "Logout", "🚪", this::handleBeneficiaryLogout);

        return sidebar;
    }

    private JPanel createBeneficiaryLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout(15, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(250, 45));

        // Use the default icon
        Icon logoIcon = UIManager.getIcon("OptionPane.informationIcon");
        JLabel logoLabel = new JLabel(logoIcon);

        // Update the text to "Beneficiary"
        JLabel titleLabel = new JLabel("Beneficiary");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        logoPanel.add(logoLabel, BorderLayout.WEST);
        logoPanel.add(titleLabel, BorderLayout.CENTER);
        return logoPanel;
    }

    private void addBeneficiarySidebarButton(JPanel sidebar, String text, String icon, Runnable action) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setMaximumSize(new Dimension(250, 45));
        buttonPanel.setOpaque(false);

        JButton button = createBeneficiaryStyledButton(icon + "  " + text);

        if (text.equals("Dashboard")) {
            button.setBackground(PRIMARY_COLOR);
        }

        button.addActionListener(e -> {
            currentPage = text;
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JPanel) {
                    Component[] comps = ((JPanel) c).getComponents();
                    if (comps.length > 0 && comps[0] instanceof JButton) {
                        comps[0].setBackground(SIDEBAR_COLOR);
                    }
                }
            }
            button.setBackground(PRIMARY_COLOR);
            action.run();
        });

        buttonPanel.add(button);
        sidebar.add(buttonPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JButton createBeneficiaryStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (text.contains(currentPage)) {
                    g2d.setColor(PRIMARY_COLOR);
                } else if (getModel().isRollover()) {
                    g2d.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2d.setColor(SIDEBAR_COLOR);
                }

                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel createBeneficiaryCard(String title, String value, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(HEADER_FONT);
        valueLabel.setForeground(PRIMARY_COLOR);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(NORMAL_FONT);
        descLabel.setForeground(TEXT_SECONDARY);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);

        return card;
    }

    private void showBeneficiaryDashboard() {
        currentPage = "Dashboard";
        contentPanel.removeAll();

        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + beneficiary.getName());
        welcomeLabel.setFont(HEADER_FONT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Statistics Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        int totalRequests = beneficiary.getRequestsList().getRdEntities().size();
        int totalReceived = beneficiary.getReceivedList().getRdEntities().size();

        statsPanel.add(createBeneficiaryCard("Family Members",
                String.valueOf(beneficiary.getNoPersons()), "Registered members"));
        statsPanel.add(createBeneficiaryCard("Pending Requests",
                String.valueOf(totalRequests), "Awaiting processing"));
        statsPanel.add(createBeneficiaryCard("Received Items",
                String.valueOf(totalReceived), "Total items received"));

        // Main Content
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        mainContent.add(statsPanel, BorderLayout.NORTH);

        dashboard.add(headerPanel, BorderLayout.NORTH);
        dashboard.add(mainContent, BorderLayout.CENTER);

        contentPanel.add(dashboard);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAddRequests() {
        currentPage = "Add Requests";
        contentPanel.removeAll();

        JPanel requestsPanel = new JPanel(new BorderLayout(0, 20));
        requestsPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Add New Requests");
        titleLabel.setFont(HEADER_FONT);
        requestsPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);

        // Materials Tab
        JPanel materialsPanel = createBeneficiaryCategoryPanel(true);
        tabbedPane.addTab("Materials", materialsPanel);

        // Services Tab
        JPanel servicesPanel = createBeneficiaryCategoryPanel(false);
        tabbedPane.addTab("Services", servicesPanel);

        requestsPanel.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(requestsPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createBeneficiaryCategoryPanel(boolean isMaterial) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns;
        if (isMaterial) {
            columns = new String[]{"ID", "Name", "Available", "Level Limit", "Description", "Actions"};
        } else {
            columns = new String[]{"ID", "Name", "Available Hours", "Description", "Actions"};
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1;
            }
        };

        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) || (!isMaterial && entity instanceof Service)) {
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                if (isMaterial) {
                    Material material = (Material) entity;
                    double limit = beneficiary.getNoPersons() == 1 ? material.getLevel1() :
                            beneficiary.getNoPersons() <= 4 ? material.getLevel2() : material.getLevel3();

                    model.addRow(new Object[]{
                            entity.getId(),
                            entity.getName(),
                            quantity,
                            limit,
                            entity.getDescription(),
                            "Add Request"
                    });
                } else {
                    model.addRow(new Object[]{
                            entity.getId(),
                            entity.getName(),
                            quantity,
                            entity.getDescription(),
                            "Add Request"
                    });
                }
            }
        }

        JTable table = new JTable(model);
        styleBeneficiaryTable(table);

        table.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new BeneficiaryActionButtonRenderer());
        table.getColumnModel().getColumn(columns.length - 1).setCellEditor(
                new BeneficiaryActionButtonEditor(new JCheckBox(), "Add Request", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        handleAddRequest(
                                organization.getEntity((int) table.getValueAt(row, 0))
                        );
                    }
                })
        );

        // Add hover effect
        BeneficiaryActionButtonRenderer renderer = (BeneficiaryActionButtonRenderer) table.getColumnModel()
                .getColumn(columns.length - 1).getCellRenderer();

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == columns.length - 1) {
                    renderer.setHoveredRow(row);
                } else {
                    renderer.setHoveredRow(-1);
                }
                table.repaint();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.setHoveredRow(-1);
                table.repaint();
            }
        });

        panel.add(new JScrollPane(table));
        return panel;
    }

    private void handleAddRequest(Entity entity) {
        JDialog dialog = new JDialog(this, "Add Request", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        // Entity Info
        JLabel nameLabel = new JLabel("Item: " + entity.getName());
        nameLabel.setFont(SUBTITLE_FONT);
        JLabel descLabel = new JLabel("Description: " + entity.getDescription());
        descLabel.setFont(NORMAL_FONT);

        // Available Quantity
        RequestDonation current = organization.getCurrentDonation(entity);
        double currentQuantity = (current != null) ? current.getQuantity() : 0;
        JLabel availableLabel = new JLabel(String.format("Currently Available: %.0f", currentQuantity));
        availableLabel.setFont(NORMAL_FONT);

        // Level Information for Materials
        JLabel levelLabel = null;
        double allowedQuantity = Double.MAX_VALUE;
        if (entity instanceof Material) {
            Material material = (Material) entity;
            int noPersons = beneficiary.getNoPersons();
            String levelInfo;

            if (noPersons == 1) {
                allowedQuantity = material.getLevel1();
                levelInfo = "Level 1 (1 person)";
            } else if (noPersons <= 4) {
                allowedQuantity = material.getLevel2();
                levelInfo = "Level 2 (2-4 people)";
            } else {
                allowedQuantity = material.getLevel3();
                levelInfo = "Level 3 (5+ people)";
            }

            levelLabel = new JLabel("Your Level Limit: " + levelInfo + " - Max: " + allowedQuantity);
            levelLabel.setFont(NORMAL_FONT);

            // Already received amount
            RequestDonation received = beneficiary.getReceivedList().get(entity.getId());
            double currentReceived = (received != null) ? received.getQuantity() : 0;
            if (currentReceived > 0) {
                JLabel receivedLabel = new JLabel("Already received: " + currentReceived);
                receivedLabel.setFont(NORMAL_FONT);
                panel.add(receivedLabel);
                allowedQuantity -= currentReceived;
            }
        }

        // Quantity Input
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setOpaque(false);
        JLabel quantityLabel = new JLabel("Quantity to request: ");
        quantityLabel.setFont(NORMAL_FONT);
        JTextField quantityField = new JTextField(10);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        final double finalAllowedQuantity = allowedQuantity;
        JButton confirmButton = createBeneficiaryActionButton("Confirm", PRIMARY_COLOR);
        confirmButton.addActionListener(e -> {
            try {
                double quantity = Double.parseDouble(quantityField.getText().trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a positive quantity",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity > currentQuantity) {
                    JOptionPane.showMessageDialog(dialog,
                            "Requested quantity exceeds available amount",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (entity instanceof Material && quantity > finalAllowedQuantity) {
                    JOptionPane.showMessageDialog(dialog,
                            "Requested quantity exceeds your level limit",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                RequestDonation request = new RequestDonation(entity, quantity);
                beneficiary.add(request);
                dialog.dispose();
                showViewRequests();
                JOptionPane.showMessageDialog(this,
                        "Request added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = createBeneficiaryActionButton("Cancel", ACCENT_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Add components to panel
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(availableLabel);
        if (levelLabel != null) {
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(levelLabel);
        }
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showViewRequests() {
        currentPage = "View Requests";
        contentPanel.removeAll();

        JPanel viewPanel = new JPanel(new BorderLayout(0, 20));
        viewPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Current Requests");
        titleLabel.setFont(HEADER_FONT);
        viewPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Item", "Quantity", "Type", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        for (RequestDonation request : beneficiary.getRequestsList().getRdEntities()) {
            Entity entity = request.getEntity();
            model.addRow(new Object[]{
                    entity.getName(),
                    request.getQuantity(),
                    entity instanceof Material ? "Material" : "Service",
                    "Manage"
            });
        }

        JTable table = new JTable(model);
        styleBeneficiaryTable(table);

        table.getColumnModel().getColumn(3).setCellRenderer(new BeneficiaryActionButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(
                new BeneficiaryActionButtonEditor(new JCheckBox(), "Manage", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        handleManageRequest(beneficiary.getRequestsList().getRdEntities().get(row));
                    }
                })
        );

        // Add hover effect
        BeneficiaryActionButtonRenderer renderer = (BeneficiaryActionButtonRenderer) table.getColumnModel()
                .getColumn(3).getCellRenderer();

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == 3) {
                    renderer.setHoveredRow(row);
                } else {
                    renderer.setHoveredRow(-1);
                }
                table.repaint();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.setHoveredRow(-1);
                table.repaint();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        viewPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton clearAllButton = createBeneficiaryActionButton("Clear All Requests", ACCENT_COLOR);
        clearAllButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear all requests?",
                    "Confirm Clear All",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                beneficiary.getRequestsList().reset();
                showViewRequests();  // Refresh the view
            }
        });

        JButton commitButton = createBeneficiaryActionButton("Commit Requests", PRIMARY_COLOR);
        commitButton.addActionListener(e -> handleBeneficiaryCommit());

        actionPanel.add(clearAllButton);
        actionPanel.add(commitButton);

        viewPanel.add(actionPanel, BorderLayout.SOUTH);

        contentPanel.add(viewPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handleManageRequest(RequestDonation request) {
        JDialog dialog = new JDialog(this, "Manage Request", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        // Request Info
        JLabel nameLabel = new JLabel("Item: " + request.getEntity().getName());
        nameLabel.setFont(SUBTITLE_FONT);
        JLabel currentQuantityLabel = new JLabel(
                String.format("Current Quantity: %.0f", request.getQuantity()));
        currentQuantityLabel.setFont(NORMAL_FONT);

        // Level Information for Materials
        double allowedQuantity = Double.MAX_VALUE;
        if (request.getEntity() instanceof Material) {
            Material material = (Material) request.getEntity();
            int noPersons = beneficiary.getNoPersons();

            if (noPersons == 1) {
                allowedQuantity = material.getLevel1();
            } else if (noPersons <= 4) {
                allowedQuantity = material.getLevel2();
            } else {
                allowedQuantity = material.getLevel3();
            }

            // Subtract already received amount
            RequestDonation received = beneficiary.getReceivedList().get(material.getId());
            if (received != null) {
                allowedQuantity -= received.getQuantity();
            }

            JLabel levelLabel = new JLabel("Remaining Level Limit: " + allowedQuantity);
            levelLabel.setFont(NORMAL_FONT);
            panel.add(levelLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // New Quantity Input
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setOpaque(false);
        JLabel quantityLabel = new JLabel("New Quantity: ");
        quantityLabel.setFont(NORMAL_FONT);
        JTextField quantityField = new JTextField(10);
        quantityField.setText(String.format("%.0f", request.getQuantity()));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        final double finalAllowedQuantity = allowedQuantity;
        JButton updateButton = createBeneficiaryActionButton("Update", PRIMARY_COLOR);
        updateButton.addActionListener(e -> {
            try {
                double quantity = Double.parseDouble(quantityField.getText().trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a positive quantity",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check available quantity
                RequestDonation available = organization.getCurrentDonation(request.getEntity());
                if (available == null || quantity > available.getQuantity()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Requested quantity exceeds available amount",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check level limit for materials
                if (request.getEntity() instanceof Material && quantity > finalAllowedQuantity) {
                    JOptionPane.showMessageDialog(dialog,
                            "Requested quantity exceeds your level limit",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                request.setQuantity(quantity);
                dialog.dispose();
                showViewRequests();  // Refresh the view
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteButton = createBeneficiaryActionButton("Delete", ACCENT_COLOR);
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this request?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                beneficiary.getRequestsList().remove(request);
                dialog.dispose();
                showViewRequests();  // Refresh the view
            }
        });

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add components to panel
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(currentQuantityLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showReceivedItems() {
        currentPage = "View Received";
        contentPanel.removeAll();

        JPanel viewPanel = new JPanel(new BorderLayout(0, 20));
        viewPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Received Items");
        titleLabel.setFont(HEADER_FONT);
        viewPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Item", "Quantity", "Type", "Date Received"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (RequestDonation received : beneficiary.getReceivedList().getRdEntities()) {
            Entity entity = received.getEntity();
            model.addRow(new Object[]{
                    entity.getName(),
                    received.getQuantity(),
                    entity instanceof Material ? "Material" : "Service",
                    "Today" // You could add a timestamp field to RequestDonation if needed
            });
        }

        JTable table = new JTable(model);
        styleBeneficiaryTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        viewPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(viewPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handleBeneficiaryCommit() {
        if (beneficiary.getRequestsList().getRdEntities().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No requests to commit",
                    "Commit Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to commit all requests?",
                "Confirm Commit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                ArrayList<RequestDonation> beforeCommit =
                        new ArrayList<>(beneficiary.getRequestsList().getRdEntities());
                beneficiary.commit();

                // Show results dialog
                JDialog resultDialog = new JDialog(this, "Commit Results", true);
                resultDialog.setSize(400, 300);
                resultDialog.setLocationRelativeTo(this);

                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
                resultPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                resultPanel.setBackground(CARD_COLOR);

                JLabel titleLabel = new JLabel("Commit Results");
                titleLabel.setFont(SUBTITLE_FONT);
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultPanel.add(titleLabel);
                resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));

                for (RequestDonation request : beforeCommit) {
                    boolean wasCommitted = beneficiary.getReceivedList().getRdEntities().stream()
                            .anyMatch(rd -> rd.getEntity().getId() == request.getEntity().getId());

                    JLabel resultLabel = new JLabel(
                            request.getEntity().getName() + " (" + request.getQuantity() +
                                    (request.getEntity() instanceof Material ? " items" : " hours") + "): " +
                                    (wasCommitted ? "✓ Successfully received" : "✗ Could not be fulfilled")
                    );
                    resultLabel.setFont(NORMAL_FONT);
                    resultLabel.setForeground(wasCommitted ? new Color(46, 125, 50) : new Color(198, 40, 40));
                    resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultPanel.add(resultLabel);
                    resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                JButton closeButton = createBeneficiaryActionButton("Close", PRIMARY_COLOR);
                closeButton.addActionListener(e -> resultDialog.dispose());
                closeButton.setAlignmentX(Component.LEFT_ALIGNMENT);

                resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                resultPanel.add(closeButton);

                resultDialog.add(resultPanel);
                resultDialog.setVisible(true);

                // Refresh views
                showViewRequests();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error committing requests: " + e.getMessage(),
                        "Commit Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleBeneficiaryLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Synchronized to ensure changes are visible to all threads
            synchronized (Menu.class) {
                Menu.triggerLogout(); // Set the forceLogout to true and reset currentUser
            }
            dispose();  // Close the GUI
        }
    }

    private void styleBeneficiaryTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setBackground(CARD_COLOR);
        table.getTableHeader().setFont(SUBTITLE_FONT);
        table.getTableHeader().setBackground(BG_COLOR);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
    }

    private JButton createBeneficiaryActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}

class BeneficiaryActionButtonRenderer extends JButton implements TableCellRenderer {
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);
    private int hoveredRow = -1;
    private JTable table;

    public BeneficiaryActionButtonRenderer() {
        setOpaque(true);
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        this.table = table;
        setText(value.toString());
        setBackground(row == hoveredRow ? HOVER_COLOR : NORMAL_COLOR);
        return this;
    }

    public void setHoveredRow(int row) {
        if (hoveredRow != row) {
            hoveredRow = row;
            if (table != null) {
                table.repaint();
            }
        }
    }
}

class BeneficiaryActionButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private ActionListener actionListener;
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);

    public BeneficiaryActionButtonEditor(JCheckBox checkBox, String label, ActionListener actionListener) {
        super(checkBox);
        this.label = label;
        this.actionListener = actionListener;

        button = new JButton();
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NORMAL_COLOR);
            }
        });

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        button.setText(label);
        button.setBackground(NORMAL_COLOR);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        actionListener.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, ""));
        return label;
    }
}