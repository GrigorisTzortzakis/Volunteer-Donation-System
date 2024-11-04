import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.ArrayList;

public class DonatorGUI extends JFrame {
    private final Organization organization;
    private final Donator donator;
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

    public DonatorGUI(Organization organization, Donator donator) {
        this.organization = organization;
        this.donator = donator;
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
        setTitle("Donator Dashboard - " + organization.getName());
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
        addSidebarButton(sidebar, "Add Offers", "➕", this::showAddOffers);
        addSidebarButton(sidebar, "View Offers", "📋", this::showViewOffers);
        addSidebarButton(sidebar, "Commit", "✔️", this::handleCommit);

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

        // Update the text to "Donator"
        JLabel titleLabel = new JLabel("Donator");
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
        currentPage = "Dashboard";
        contentPanel.removeAll();

        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + donator.getName());
        welcomeLabel.setFont(HEADER_FONT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Statistics Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        int totalOffers = donator.getOffersList().getRdEntities().size();
        double totalQuantity = donator.getOffersList().getRdEntities().stream()
                .mapToDouble(RequestDonation::getQuantity)
                .sum();

        statsPanel.add(createCard("Total Offers", String.valueOf(totalOffers), "Current pending offers"));
        statsPanel.add(createCard("Total Quantity", String.format("%.0f", totalQuantity), "Items/hours offered"));
        statsPanel.add(createCard("Status", "Active", "Account status"));

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

    private void showAddOffers() {
        currentPage = "Add Offers";
        contentPanel.removeAll();

        JPanel offersPanel = new JPanel(new BorderLayout(0, 20));
        offersPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Add New Offers");
        titleLabel.setFont(HEADER_FONT);
        offersPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);

        // Materials Tab
        JPanel materialsPanel = createCategoryPanel(true);
        tabbedPane.addTab("Materials", materialsPanel);

        // Services Tab
        JPanel servicesPanel = createCategoryPanel(false);
        tabbedPane.addTab("Services", servicesPanel);

        offersPanel.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(offersPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCategoryPanel(boolean isMaterial) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Name", "Current Quantity", "Description", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) || (!isMaterial && entity instanceof Service)) {
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                model.addRow(new Object[]{
                        entity.getId(),
                        entity.getName(),
                        quantity,
                        entity.getDescription(),
                        "Add Offer"
                });
            }
        }

        JTable table = new JTable(model);
        styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(
                new ActionButtonEditor(new JCheckBox(), "Add Offer", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        handleAddOffer(
                                organization.getEntity((int) table.getValueAt(row, 0))
                        );
                    }
                })
        );

        // Add hover effect
        ActionButtonRenderer renderer = (ActionButtonRenderer) table.getColumnModel()
                .getColumn(4).getCellRenderer();

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == 4) {
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



    private void handleAddOffer(Entity entity) {
        JDialog dialog = new JDialog(this, "Add Offer", true);
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

        // Current Available
        RequestDonation current = organization.getCurrentDonation(entity);
        double currentQuantity = (current != null) ? current.getQuantity() : 0;
        JLabel availableLabel = new JLabel(String.format("Currently Available: %.0f", currentQuantity));
        availableLabel.setFont(NORMAL_FONT);

        // Quantity Input
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setOpaque(false);
        JLabel quantityLabel = new JLabel("Quantity to offer: ");
        quantityLabel.setFont(NORMAL_FONT);
        JTextField quantityField = new JTextField(10);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createActionButton("Confirm", PRIMARY_COLOR);
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

                RequestDonation donation = new RequestDonation(entity, quantity);
                donator.add(donation);
                dialog.dispose();
                showViewOffers();  // Refresh the offers view
                JOptionPane.showMessageDialog(this,
                        "Offer added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = createActionButton("Cancel", ACCENT_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Add components to panel
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(availableLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showViewOffers() {
        currentPage = "View Offers";
        contentPanel.removeAll();

        JPanel viewPanel = new JPanel(new BorderLayout(0, 20));
        viewPanel.setBackground(BG_COLOR);

        // Header
        JLabel titleLabel = new JLabel("Current Offers");
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

        for (RequestDonation offer : donator.getOffersList().getRdEntities()) {
            Entity entity = offer.getEntity();
            model.addRow(new Object[]{
                    entity.getName(),
                    offer.getQuantity(),
                    entity instanceof Material ? "Material" : "Service",
                    "Manage"
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        table.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(
                new ActionButtonEditor(new JCheckBox(), "Manage", e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        handleManageOffer(donator.getOffersList().getRdEntities().get(row));
                    }
                })
        );

        // Add hover effect
        ActionButtonRenderer renderer = (ActionButtonRenderer) table.getColumnModel()
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

        JButton clearAllButton = createActionButton("Clear All Offers", ACCENT_COLOR);
        clearAllButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear all offers?",
                    "Confirm Clear All",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                donator.getOffersList().reset();
                showViewOffers();  // Refresh the view
            }
        });

        JButton commitButton = createActionButton("Commit Offers", PRIMARY_COLOR);
        commitButton.addActionListener(e -> handleCommit());

        actionPanel.add(clearAllButton);
        actionPanel.add(commitButton);

        viewPanel.add(actionPanel, BorderLayout.SOUTH);

        contentPanel.add(viewPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }



    private void handleManageOffer(RequestDonation offer) {
        JDialog dialog = new JDialog(this, "Manage Offer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        // Offer Info
        JLabel nameLabel = new JLabel("Item: " + offer.getEntity().getName());
        nameLabel.setFont(SUBTITLE_FONT);
        JLabel currentQuantityLabel = new JLabel(
                String.format("Current Quantity: %.0f", offer.getQuantity()));
        currentQuantityLabel.setFont(NORMAL_FONT);

        // New Quantity Input
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setOpaque(false);
        JLabel quantityLabel = new JLabel("New Quantity: ");
        quantityLabel.setFont(NORMAL_FONT);
        JTextField quantityField = new JTextField(10);
        quantityField.setText(String.format("%.0f", offer.getQuantity()));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton updateButton = createActionButton("Update", PRIMARY_COLOR);
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

                offer.setQuantity(quantity);
                dialog.dispose();
                showViewOffers();  // Refresh the view
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteButton = createActionButton("Delete", ACCENT_COLOR);
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this offer?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                donator.getOffersList().remove(offer);
                dialog.dispose();
                showViewOffers();  // Refresh the view
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

    private void handleCommit() {
        if (donator.getOffersList().getRdEntities().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No offers to commit",
                    "Commit Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to commit all offers?",
                "Confirm Commit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                donator.commit();
                showViewOffers();  // Refresh the view
                JOptionPane.showMessageDialog(this,
                        "All offers have been committed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error committing offers: " + e.getMessage(),
                        "Commit Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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

class ActionButtonRenderer extends JButton implements TableCellRenderer {
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);
    private int hoveredRow = -1;
    private JTable table;

    public ActionButtonRenderer() {
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
class ActionButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private ActionListener actionListener;
    private static final Color NORMAL_COLOR = new Color(63, 81, 181);
    private static final Color HOVER_COLOR = new Color(100, 120, 220);

    public ActionButtonEditor(JCheckBox checkBox, String label, ActionListener actionListener) {
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

