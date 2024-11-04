import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.util.ArrayList;

public class AdminGUI extends JFrame {
    private final Organization organization;
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

    public AdminGUI(Organization organization) {
        this.organization = organization;
        setupLookAndFeel();
        setupFrame();
        createMainLayout();
        showDashboard();
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
        setTitle("Admin Dashboard - " + organization.getName());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
    }

    private void createMainLayout() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        JPanel sidebar = createAnimatedSidebar();
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
                    int alpha = (int)((1 - ratio) * 30);  // Fixed alpha calculation
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

    private JPanel createAnimatedSidebar() {
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

        JPanel logoPanel = createLogoPanel();
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

        addSidebarButton(sidebar, "Dashboard", "📊", this::showDashboard);
        addSidebarButton(sidebar, "View Entities", "📦", this::showEntitiesView);
        addSidebarButton(sidebar, "Monitor Organization", "👥", this::showMonitorOrganization);

        sidebar.add(Box.createVerticalGlue());
        addSidebarButton(sidebar, "Logout", "🚪", this::handleLogout);

        return sidebar;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout(15, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(250, 45));

        // Use the default icon
        Icon logoIcon = UIManager.getIcon("OptionPane.informationIcon");
        JLabel logoLabel = new JLabel(logoIcon);

        // Update the text to "Admin" instead of the organization name
        JLabel titleLabel = new JLabel("Admin");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        logoPanel.add(logoLabel, BorderLayout.WEST);
        logoPanel.add(titleLabel, BorderLayout.CENTER);
        return logoPanel;
    }


    private void addSidebarButton(JPanel sidebar, String text, String icon, Runnable action) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setMaximumSize(new Dimension(250, 45));
        buttonPanel.setOpaque(false);

        JButton button = createStyledButton(icon + "  " + text);

        // Set initial state if this is the dashboard button
        if (text.equals("Dashboard")) {
            button.setBackground(PRIMARY_COLOR);
        }

        button.addActionListener(e -> {
            currentPage = text;  // Update current page
            // Reset all button backgrounds
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JPanel) {
                    Component[] comps = ((JPanel) c).getComponents();
                    if (comps.length > 0 && comps[0] instanceof JButton) {
                        comps[0].setBackground(SIDEBAR_COLOR);
                    }
                }
            }
            button.setBackground(PRIMARY_COLOR);  // Highlight selected button
            action.run();
        });

        buttonPanel.add(button);
        sidebar.add(buttonPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Always paint a background
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

    private JPanel createCard(String title, String value, String description) {
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

    private void showDashboard() {
        currentPage = "Dashboard";  // Set current page
        contentPanel.removeAll();

        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + organization.getAdmin().getName());
        welcomeLabel.setFont(HEADER_FONT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Statistics Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        int materials = countEntities(true);
        int services = countEntities(false);
        int users = organization.getBeneficiaryList().size() + organization.getDonatorList().size();

        statsPanel.add(createCard("Materials", String.valueOf(materials), "Available items"));
        statsPanel.add(createCard("Services", String.valueOf(services), "Available services"));
        statsPanel.add(createCard("Total Users", String.valueOf(users), "Active system users"));

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

    private int countEntities(boolean isMaterial) {
        int count = 0;
        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) ||
                    (!isMaterial && entity instanceof Service)) {
                count++;
            }
        }
        return count;
    }

    private void showEntitiesView() {
        currentPage = "View Entities";  // Set current page
        contentPanel.removeAll();

        JPanel entitiesPanel = new JPanel(new BorderLayout(0, 20));
        entitiesPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Entities Overview");
        titleLabel.setFont(HEADER_FONT);
        entitiesPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);
        tabbedPane.addTab("Materials", createMaterialsPanel());
        tabbedPane.addTab("Services", createServicesPanel());

        entitiesPanel.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(entitiesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMaterialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Name", "Available Quantity", "Level 1", "Level 2", "Level 3"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Entity entity : organization.getEntityList()) {
            if (entity instanceof Material) {
                Material material = (Material) entity;
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                model.addRow(new Object[]{
                        entity.getId(),
                        entity.getName(),
                        quantity,
                        material.getLevel1(),
                        material.getLevel2(),
                        material.getLevel3()
                });
            }
        }

        JTable table = new JTable(model);
        styleTable(table);

        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Name", "Available Hours", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Entity entity : organization.getEntityList()) {
            if (entity instanceof Service) {
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                model.addRow(new Object[]{
                        entity.getId(),
                        entity.getName(),
                        quantity,
                        entity.getDescription()
                });
            }
        }

        JTable table = new JTable(model);
        styleTable(table);

        panel.add(new JScrollPane(table));
        return panel;
    }

    private void showMonitorOrganization() {
        currentPage = "Monitor Organization";  // Set current page
        contentPanel.removeAll();

        JPanel monitorPanel = new JPanel(new BorderLayout(0, 20));
        monitorPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Monitor Organization");
        titleLabel.setFont(HEADER_FONT);
        monitorPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);

        tabbedPane.addTab("Beneficiaries", createBeneficiariesPanel());
        tabbedPane.addTab("Donators", createDonatorsPanel());

        monitorPanel.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(monitorPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createBeneficiariesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(CARD_COLOR);

        JButton resetAllButton = createActionButton("Reset All Lists", PRIMARY_COLOR);
        resetAllButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reset all beneficiary lists?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                organization.resetBeneficiaryLists();
                showMonitorOrganization();
                JOptionPane.showMessageDialog(this,
                        "All beneficiary lists have been reset successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        actionsPanel.add(resetAllButton);
        panel.add(actionsPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Name", "Phone", "Family Members", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        for (Beneficiary b : organization.getBeneficiaryList()) {
            model.addRow(new Object[]{
                    b.getName(),
                    b.getPhone(),
                    b.getNoPersons(),
                    "Manage"
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        // Custom renderer for the actions column
        table.getColumnModel().getColumn(3).setCellRenderer(new AdminActionButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(
                new AdminActionButtonEditor(new JCheckBox(), "Manage", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        showBeneficiaryActions(organization.getBeneficiaryList().get(row));
                    }
                })
        );

        // Add hover effect
        AdminActionButtonRenderer renderer = (AdminActionButtonRenderer) table.getColumnModel()
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
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.setHoveredRow(-1);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDonatorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Name", "Phone", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        for (Donator d : organization.getDonatorList()) {
            model.addRow(new Object[]{
                    d.getName(),
                    d.getPhone(),
                    "Manage"
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        table.getColumnModel().getColumn(2).setCellRenderer(new AdminActionButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(
                new AdminActionButtonEditor(new JCheckBox(), "Manage", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        showDonatorActions(organization.getDonatorList().get(row));
                    }
                })
        );

        // Add hover effect
        AdminActionButtonRenderer renderer = (AdminActionButtonRenderer) table.getColumnModel()
                .getColumn(2).getCellRenderer();

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == 2) {
                    renderer.setHoveredRow(row);
                } else {
                    renderer.setHoveredRow(-1);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.setHoveredRow(-1);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showBeneficiaryActions(Beneficiary beneficiary) {
        JDialog dialog = new JDialog(this, "Beneficiary Actions", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        // Beneficiary Info
        JLabel nameLabel = new JLabel("Name: " + beneficiary.getName());
        nameLabel.setFont(SUBTITLE_FONT);
        JLabel phoneLabel = new JLabel("Phone: " + beneficiary.getPhone());
        phoneLabel.setFont(NORMAL_FONT);
        JLabel familyLabel = new JLabel("Family Members: " + beneficiary.getNoPersons());
        familyLabel.setFont(NORMAL_FONT);

        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(phoneLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(familyLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Received Items Section
        JLabel receivedLabel = new JLabel("Received Items");
        receivedLabel.setFont(SUBTITLE_FONT);
        panel.add(receivedLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] columns = {"Item", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (RequestDonation rd : beneficiary.getReceivedList().getRdEntities()) {
            model.addRow(new Object[]{rd.getEntity().getName(), rd.getQuantity()});
        }

        JTable receivedTable = new JTable(model);
        styleTable(receivedTable);
        JScrollPane scrollPane = new JScrollPane(receivedTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        panel.add(scrollPane);

        // Action Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setOpaque(false);

        JButton clearButton = createActionButton("Clear Received Items", PRIMARY_COLOR);
        clearButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to clear received items for this beneficiary?",
                    "Confirm Clear",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                beneficiary.getReceivedList().reset();
                dialog.dispose();
                showMonitorOrganization();
            }
        });

        JButton deleteButton = createActionButton("Delete Beneficiary", ACCENT_COLOR);
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this beneficiary?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                organization.removeBeneficiary(beneficiary);
                dialog.dispose();
                showMonitorOrganization();
            }
        });

        buttonsPanel.add(clearButton);
        buttonsPanel.add(deleteButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonsPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDonatorActions(Donator donator) {
        JDialog dialog = new JDialog(this, "Donator Actions", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        // Donator Info
        JLabel nameLabel = new JLabel("Name: " + donator.getName());
        nameLabel.setFont(SUBTITLE_FONT);
        JLabel phoneLabel = new JLabel("Phone: " + donator.getPhone());
        phoneLabel.setFont(NORMAL_FONT);

        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(phoneLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Current Offers Section
        JLabel offersLabel = new JLabel("Current Offers");
        offersLabel.setFont(SUBTITLE_FONT);
        panel.add(offersLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] columns = {"Item", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (RequestDonation rd : donator.getOffersList().getRdEntities()) {
            model.addRow(new Object[]{rd.getEntity().getName(), rd.getQuantity()});
        }

        JTable offersTable = new JTable(model);
        styleTable(offersTable);
        JScrollPane scrollPane = new JScrollPane(offersTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        panel.add(scrollPane);

        // Delete Button
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setOpaque(false);

        JButton deleteButton = createActionButton("Delete Donator", ACCENT_COLOR);
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this donator?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                organization.removeDonator(donator);
                dialog.dispose();
                showMonitorOrganization();
            }
        });

        buttonsPanel.add(deleteButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonsPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void handleLogout() {
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

    private void styleTable(JTable table) {
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

    private JButton createActionButton(String text, Color color) {
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

// Custom renderer for action buttons in tables
class AdminActionButtonRenderer extends JButton implements TableCellRenderer {
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);
    private int hoveredRow = -1;
    private JTable table;

    public AdminActionButtonRenderer() {
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

// Custom editor for action buttons in tables
class AdminActionButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private ActionListener actionListener;
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);

    public AdminActionButtonEditor(JCheckBox checkBox, String label, ActionListener actionListener) {
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

