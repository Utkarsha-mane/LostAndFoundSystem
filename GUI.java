package lost_and_found_system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Item {
    int id;
    String category;
    String name;
    String combinedDescription;

    Item(int id, String category, String name, String combinedDescription) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.combinedDescription = combinedDescription.toLowerCase();
    }
}

class LostFoundDatabase {
    private HashMap<String, HashMap<String, Item>> registry;
    int nextId = 1;

    LostFoundDatabase() {
        registry = new HashMap<>();
    }

    void registerFound(String category, String name, String combined) {
        Item item = new Item(nextId++, category, name, combined.toLowerCase());
        registry.putIfAbsent(category, new HashMap<>());
        registry.get(category).put(combined.toLowerCase(), item);
    }

    List<Map.Entry<Item, Double>> searchItems(String category, String query) {
        if (!registry.containsKey(category) || registry.get(category).isEmpty()) {
            return new ArrayList<>();
        }

        HashMap<String, Item> items = registry.get(category);
        PriorityQueue<Map.Entry<Item, Double>> pq = new PriorityQueue<>(
            (a, b) -> Double.compare(b.getValue(), a.getValue())
        );

        for (Item obj : items.values()) {
            double score = similarityScore(query.toLowerCase(), obj.combinedDescription);
            if (score > 0.3) {
                pq.offer(Map.entry(obj, score));
            }
        }

        List<Map.Entry<Item, Double>> results = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < 3) {
            results.add(pq.poll());
            count++;
        }
        return results;
    }

    void claimItem(Item item) {
        registry.get(item.category).remove(item.combinedDescription);
    }

    List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        for (HashMap<String, Item> categoryItems : registry.values()) {
            allItems.addAll(categoryItems.values());
        }
        return allItems;
    }

    double similarityScore(String str1, String str2) {
        int distance = editDistance(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 1.0;
        return 1.0 - ((double) distance / maxLength);
    }

    int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[n][m];
    }
}

public class Gui extends JFrame {
    private LostFoundDatabase db;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public Gui() {
        db = new LostFoundDatabase();
        setupFrame();
        createMainMenu();
    }

    private void setupFrame() {
        setTitle("Lost & Found Management System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        add(mainPanel);
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(900, 100));
        JLabel titleLabel = new JLabel("Lost & Found System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JButton foundBtn = createStyledButton("Register Found Item", PRIMARY_COLOR);
        foundBtn.addActionListener(e -> showRegisterFoundPanel());
        
        JButton lostBtn = createStyledButton("Search for Lost Item", SECONDARY_COLOR);
        lostBtn.addActionListener(e -> showRegisterLostPanel());
        
        JButton showBtn = createStyledButton("View All Found Items", SUCCESS_COLOR);
        showBtn.addActionListener(e -> showAllItems());
        
        JButton exitBtn = createStyledButton("Exit", DANGER_COLOR);
        exitBtn.addActionListener(e -> System.exit(0));

        centerPanel.add(foundBtn, gbc);
        centerPanel.add(lostBtn, gbc);
        centerPanel.add(showBtn, gbc);
        centerPanel.add(exitBtn, gbc);

        menuPanel.add(headerPanel, BorderLayout.NORTH);
        menuPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(menuPanel, "MENU");
        cardLayout.show(mainPanel, "MENU");
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(400, 60));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private void showRegisterFoundPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel header = createHeader("Register Found Item");
        panel.add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
            new EmptyBorder(20, 40, 20, 40),
            new LineBorder(PRIMARY_COLOR, 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] categories = {"Electronics", "Daily Use", "Stationary", "Accessories", "Miscellaneous", "ID Card"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(NORMAL_FONT);

        JTextField nameField = new JTextField(20);
        JTextField brandField = new JTextField(20);
        JTextField colorField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        styleTextField(nameField);
        styleTextField(brandField);
        styleTextField(colorField);
        styleTextField(locationField);
        styleTextField(dateField);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Brand/Model:"), gbc);
        gbc.gridx = 1;
        formPanel.add(brandField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Color:"), gbc);
        gbc.gridx = 1;
        formPanel.add(colorField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createLabel("Location Found:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createLabel("Date Found:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(Color.WHITE);
        
        JButton submitBtn = createStyledButton("Register Item", SUCCESS_COLOR);
        submitBtn.setPreferredSize(new Dimension(180, 45));
        submitBtn.addActionListener(e -> {
            String category = (String) categoryBox.getSelectedItem();
            String name = nameField.getText().trim();
            String combined = (name + " " + brandField.getText() + " " + 
                             colorField.getText() + " " + locationField.getText() + 
                             " " + dateField.getText() + " " + descArea.getText()).trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter item name!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            db.registerFound(category, name, combined);
            JOptionPane.showMessageDialog(this, 
                "Item registered successfully!\nID: " + (db.nextId - 1), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "MENU");
        });

        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        btnPanel.add(submitBtn);
        btnPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.add(formPanel);

        panel.add(wrapper, BorderLayout.CENTER);
        
        mainPanel.add(panel, "REGISTER_FOUND");
        cardLayout.show(mainPanel, "REGISTER_FOUND");
    }

    private void showRegisterLostPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel header = createHeader("Search for Lost Item");
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
            new EmptyBorder(20, 40, 20, 40),
            new LineBorder(SECONDARY_COLOR, 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] categories = {"Electronics", "Daily Use", "Stationary", "Accessories", "Miscellaneous", "ID Card"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(NORMAL_FONT);

        JTextArea queryArea = new JTextArea(5, 30);
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        queryArea.setFont(NORMAL_FONT);
        JScrollPane queryScroll = new JScrollPane(queryArea);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        formPanel.add(queryScroll, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(Color.WHITE);

        JButton searchBtn = createStyledButton("🔍 Search", SUCCESS_COLOR);
        searchBtn.setPreferredSize(new Dimension(180, 45));
        searchBtn.addActionListener(e -> {
            String category = (String) categoryBox.getSelectedItem();
            String query = queryArea.getText().trim();
            
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            showSearchResults(category, query);
        });

        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        btnPanel.add(searchBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.add(formPanel);

        panel.add(wrapper, BorderLayout.CENTER);

        mainPanel.add(panel, "REGISTER_LOST");
        cardLayout.show(mainPanel, "REGISTER_LOST");
    }

    private void showSearchResults(String category, String query) {
        List<Map.Entry<Item, Double>> results = db.searchItems(category, query);
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No matching items found in this category.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel header = createHeader("Search Results");
        panel.add(header, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(BACKGROUND_COLOR);
        resultsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        ButtonGroup group = new ButtonGroup();
        List<JRadioButton> radioButtons = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Map.Entry<Item, Double> entry = results.get(i);
            Item item = entry.getKey();
            double score = entry.getValue();

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(new CompoundBorder(
                new EmptyBorder(5, 10, 5, 10),
                new LineBorder(PRIMARY_COLOR, 1, true)
            ));
            itemPanel.setMaximumSize(new Dimension(800, 100));

            JRadioButton radio = new JRadioButton();
            radio.setBackground(Color.WHITE);
            group.add(radio);
            radioButtons.add(radio);

            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            infoPanel.setBackground(Color.WHITE);
            
            JLabel matchLabel = new JLabel(String.format("Match: %.1f%%", score * 100));
            matchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            matchLabel.setForeground(SUCCESS_COLOR);

            JLabel idLabel = new JLabel("ID: " + item.id + " | Category: " + item.category);
            idLabel.setFont(NORMAL_FONT);

            JLabel nameLabel = new JLabel("Name: " + item.name);
            nameLabel.setFont(NORMAL_FONT);

            infoPanel.add(matchLabel);
            infoPanel.add(idLabel);
            infoPanel.add(nameLabel);

            itemPanel.add(radio, BorderLayout.WEST);
            itemPanel.add(infoPanel, BorderLayout.CENTER);

            resultsPanel.add(itemPanel);
            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(BACKGROUND_COLOR);

        JButton claimBtn = createStyledButton("✓ Claim Selected", SUCCESS_COLOR);
        claimBtn.setPreferredSize(new Dimension(180, 45));
        claimBtn.addActionListener(e -> {
            int selected = -1;
            for (int i = 0; i < radioButtons.size(); i++) {
                if (radioButtons.get(i).isSelected()) {
                    selected = i;
                    break;
                }
            }
            
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to claim!", 
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Item item = results.get(selected).getKey();
            db.claimItem(item);
            
            JOptionPane.showMessageDialog(this, 
                "Item claimed successfully!\n\nCollect from:\nLost and Found Department\nIT Building, 3rd Floor", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "MENU");
        });

        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER_LOST"));

        btnPanel.add(claimBtn);
        btnPanel.add(backBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "RESULTS");
        cardLayout.show(mainPanel, "RESULTS");
    }

    private void showAllItems() {
        List<Item> items = db.getAllItems();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JPanel header = createHeader("All Found Items (" + items.size() + ")");
        panel.add(header, BorderLayout.NORTH);

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items currently registered", SwingConstants.CENTER);
            emptyLabel.setFont(SUBTITLE_FONT);
            emptyLabel.setForeground(Color.GRAY);
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"ID", "Category", "Name"};
            Object[][] data = new Object[items.size()][3];
            
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                data[i][0] = item.id;
                data[i][1] = item.category;
                data[i][2] = item.name;
            }

            JTable table = new JTable(data, columns);
            table.setFont(NORMAL_FONT);
            table.setRowHeight(35);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(PRIMARY_COLOR);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionBackground(SECONDARY_COLOR);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BACKGROUND_COLOR);
        JButton backBtn = createStyledButton("← Back to Menu", DANGER_COLOR);
        backBtn.setPreferredSize(new Dimension(200, 45));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "SHOW_ALL");
        cardLayout.show(mainPanel, "SHOW_ALL");
    }

    private JPanel createHeader(String title) {
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(900, 80));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        header.add(label);
        return header;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(NORMAL_FONT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Gui().setVisible(true);
        });
    }
}
