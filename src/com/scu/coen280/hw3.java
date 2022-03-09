package com.scu.coen280;


import javax.swing.*;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.*;

import java.sql.*;

import java.util.*;
import java.util.List;

public class hw3 extends JFrame {

    static JFrame jf;
    private JPanel mainPanel;
    private JPanel topLeftPanel;
    private JPanel topRightPanel;
    private JPanel botLeftPanel;
    private JLabel memberSinceLabel;
    private JComboBox<String> memberSinceCombo;
    private JTextField memberSinceField;
    private JLabel reviewCountLabel;
    private JComboBox<String> reviewCountCombo;
    private JTextField reviewCountField;
    private JLabel numOfFriendsLabel;
    private JComboBox<String> numOfFriendsCombo;
    private JTextField numOfFriendsField;
    private JLabel avgStarsLabel;
    private JComboBox<String> avgStarsCombo;
    private JTextField avgStarsField;
    private JLabel voteFunnyLabel;
    private JComboBox<String> voteFunnyCombo;
    private JTextField voteFunnyField;
    private JLabel voteUsefulLabel;
    private JComboBox<String> voteUsefulCombo;
    private JTextField voteUsefulField;
    private JLabel voteCoolLabel;
    private JComboBox<String> voteCoolCombo;
    private JTextField voteCoolField;
    private JLabel logicLabel;
    private JComboBox<String> logicCombo;
    private JPanel botRightPanel;
    private JButton businessButton;
    private JButton userButton;
    private JScrollPane resultScrollPane;
    private JLabel reviewFromLabel;
    private JLabel reviewToLabel;
    private JTextField reviewFromField;
    private JComboBox<String> starValueCombo;
    private JComboBox<String> voteValueCombo;
    private JLabel starValueLabel;
    private JLabel voteValueLabel;
    private JTextField reciewToField;
    private JTextField voteValueField;
    private JTextField starValueField;
    private JScrollPane mainCategoryPane;
    private JScrollPane attributePane;
    private JScrollPane subcategory;
    private JButton getSubcategoryButton;
    private JButton getAttributeButton;
    private JComboBox<String> businessLogicCombo;
    private JLabel cityLabel;
    private JTextField cityField;
    private JScrollPane subcategoryPane;
    private JTable resultTable;

    private final int MAX_COL = 3;

    private final String DEFAULT_PLACEHOLDER = "1=1";
    private final String[] USER_HEADER = {
            "UID", "User Name", "Date Created", "Review Count", "Number of Friends", "Average Stars", "Total Votes"
    };
    private final String[] BUSINESS_HEADER = {
            "BID", "Full Address", "City", "Review Count", "Business Name", "Longitude", "Latitude", "State Name",
            "Stars", "Whether Open"
    };
    private final String[] POP_TABLE_HEADER = {
            "Review Date", "Stars", "Review Text", "Business Name", "Useful Votes", "Cool Votes", "Funny Votes"
    };
    private final String[] USER_EXHEADER = {
            "vote_funny", "vote_useful", "vote_cool"
    };
    private final String[] REVIEWS_EXHEADER = {
            "rid", "uid", "bid"
    };
    private static final String[] MAIN_CATEGORIES = {
            "Active Life", "Arts & Entertainment", "Automotive", "Car Rental", "Cafes", "Beauty & Spas",
            "Convenience Stores", "Dentists", "Doctors", "Drugstores", "Department Stores", "Education",
            "Event Planning & Services", "Flowers & Gifts", "Food", "Health & Medical", "Home Services",
            "Home & Garden", "Hospitals", "Hotels & Travel", "Hardware Stores", "Grocery", "Medical Centers",
            "Nurseries & Gardening", "Nightlife", "Restaurants", "Shopping", "Transportation"
    };
    private final String[] CHARSET_HEADER = {
            "good_for", "parking", "hair_types_specialized", "music", "payment_types", "ambience",
            "dietary_restrictions"
    };

    private final String subCatgeorySqlPattern =
            "SELECT DISTINCT subcategory FROM Business_Subcategory_Relation WHERE bid IN (%s)";
    private final String attributeSqlPattern =
            "SELECT * FROM Business_Attributes_Relation WHERE bid IN (%s)";
    private String finalSqlPattern = "SELECT * FROM Business WHERE bid IN (%s)";

    private String finalSqlConstraint = "";

    private HashSet<String> mainCategoryPool;
    
    // all subcategories in the database
    private HashSet<String> allSelectedSubcategories;
    
    // user selected subcategories
    private HashSet<String> subCategoryPool;
    
    private HashSet<String>  allSelectedAttributes;
    private HashSet<String> attributesPool;


    public hw3(String title, Connection root) {
        super(title);
        this.setContentPane(mainPanel);


        mainCategoryPool = new HashSet<>();

        allSelectedSubcategories = new HashSet<>();
        subCategoryPool = new HashSet<>();

        allSelectedAttributes = new HashSet<>();
        attributesPool = new HashSet<>();

        this.initMainCategory();
        this.addGetSubcategoryListener(root);
        this.addGetAttributeListener(root);

        // User Query Listener
        this.addUserButtonListener(root);
        this.addBusinessButtonListener(root);
    }

    private void initMainCategory () {
        JPanel mainCategoryShowPanel = new JPanel();
        mainCategoryShowPanel.setLayout(
                new GridLayout(getRowNumber(MAIN_CATEGORIES.length) , MAX_COL, 1, 1)
        );

        for (String name: MAIN_CATEGORIES) {
            JCheckBox currentCheckbox = createCheckBox(name, mainCategoryPool);
            mainCategoryShowPanel.add(currentCheckbox);
        }
        mainCategoryPane.getViewport().add(mainCategoryShowPanel);
    }

    private void addGetSubcategoryListener(Connection root) {
        getSubcategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryConstraint = buildGetSubcategorySql(
                        (String) Objects.requireNonNull(businessLogicCombo.getSelectedItem())
                );

                // update final sql constraint here only if it is empty
                if (finalSqlConstraint.equals("")){
                    finalSqlConstraint = categoryConstraint;
                }

                String getSubCategorySql = String.format(subCatgeorySqlPattern, categoryConstraint);
                try {
//                    System.out.println("Get sub " + getSubCategorySql);
                    ResultSet rs = root.createStatement().executeQuery(getSubCategorySql);

                    while (rs.next()) {
                        allSelectedSubcategories.add(rs.getString("subcategory"));
                    }

                    createShowPanel(allSelectedSubcategories, subCategoryPool, subcategoryPane);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void createShowPanel (HashSet<String> fullSet, HashSet<String> targetPool, JScrollPane targetPanel) {
        JPanel currentPanel = new JPanel();
        currentPanel.setLayout(
                new GridLayout((fullSet.size() / MAX_COL) + 1, MAX_COL, 1, 1)
        );

        for (String name : fullSet) {
            JCheckBox currentCheckbox = createCheckBox(name, targetPool);
            currentPanel.add(currentCheckbox);
        }

        targetPanel.getViewport().add(currentPanel);
    }

    private void addGetAttributeListener (Connection root) {
        getAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String getAttributeWithConstraintSql = buildGetAttributeSql(
                        (String) Objects.requireNonNull(businessLogicCombo.getSelectedItem())
                );
//                System.out.println(getAttributeWithConstraintSql);
                
                try {
                    ResultSet rs = root.createStatement().executeQuery(getAttributeWithConstraintSql);
                    ResultSetMetaData meta = rs.getMetaData();
                    int colSize = meta.getColumnCount();
//                    System.out.println(colSize);


                    while (rs.next()) {
//                        System.out.println(rs.getString("bid"));
                        for (int i = 1; i <= colSize; i++){
                            String colName = meta.getColumnName(i);
                            String output = rs.getString(i);

                            if (output == null || output.equals("") || colName.equals("bid" )) {
                                continue;
                            }

                            for(String item : output.split(",")){
                                StringBuilder keyWithValue = new StringBuilder();
                                keyWithValue.append(colName);
                                keyWithValue.append("@");
                                keyWithValue.append(item);
                                allSelectedAttributes.add(keyWithValue.toString());
                            }
                        }
                    }
                    createShowPanel(allSelectedAttributes, attributesPool, attributePane);
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addUserButtonListener(Connection root){
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    HashSet<String> userExHeader = new HashSet<>(
                            Arrays.asList(USER_EXHEADER)
                    );
                    String sql = prepareUserQuery();
                    System.out.println(sql);

                    Vector<String> userHeader = new Vector(Arrays.asList(USER_HEADER));
                    Statement stmt = root.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);


                    resultTable = getResultTable(rs, userHeader, userExHeader);
                    hiddenColumn(resultTable, "UID");
                    resultTable.setRowSelectionAllowed(true);
                    resultScrollPane.getViewport().add(resultTable);

                    rs.close();

                    resultTable.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            JTable resTable = null;

                            int rowNum = resultTable.rowAtPoint(e.getPoint());
                            String uid = (String) resultTable.getValueAt(rowNum, 0);
                            String reviewSql = prepareReviewQuery(true, uid);
                            System.out.println(reviewSql);

                            HashSet<String> exHeader = new HashSet<>(
                                    Arrays.asList(REVIEWS_EXHEADER)
                            );

                            try {
                                ResultSet rs = root.createStatement().executeQuery(reviewSql);
                                resTable = getResultTable(rs, new Vector<>(Arrays.asList(POP_TABLE_HEADER)), exHeader);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                            JFrame poppedWindow = new JFrame();
                            JScrollPane poppedWindowPane = new JScrollPane();

                            poppedWindow.setBounds(600, 360, 800, 600);
                            poppedWindow.setLocationRelativeTo(null);
                            poppedWindow.setContentPane(poppedWindowPane);
                            poppedWindowPane.getViewport().add(resTable);
                            poppedWindow.setVisible(true);
                        }
                    });
                } catch (SQLException exception) {
                    System.err.println("There were some errors in the sql");
                    exception.printStackTrace();
                }
            }
        });
    }

    private void addBusinessButtonListener (Connection root) {
        businessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!attributesPool.isEmpty()) {
                    finalSqlConstraint = buildFinalSql(
                            (String) Objects.requireNonNull(businessLogicCombo.getSelectedItem())
                    );
                }
                String finalSql = String.format(finalSqlPattern, finalSqlConstraint);
                if (!cityField.getText().equals("")){
                    finalSql = finalSql + " AND " + "city='" + cityField.getText() +"'";
                }
                System.out.println("[CONSTRAINT] " + finalSqlConstraint);
                System.out.println("[FINAL] " + finalSql);

                try {
                    ResultSet rs = root.createStatement().executeQuery(finalSql);
                    JTable resultTable = getResultTable(rs, new Vector<>(Arrays.asList(BUSINESS_HEADER)), new HashSet<>());

                    hiddenColumn(resultTable, "BID");
                    resultTable.setRowSelectionAllowed(true);
                    resultScrollPane.getViewport().add(resultTable);

                    resultTable.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            JTable resTable = null;

                            int rowNum = resultTable.rowAtPoint(e.getPoint());
                            String bid = (String) resultTable.getValueAt(rowNum, 0);
                            String reviewSql = prepareReviewQuery(false, bid);

                            System.out.println("[BUSINESS SQL] " + reviewSql);

                            HashSet<String> exHeader = new HashSet<>(
                                    Arrays.asList(REVIEWS_EXHEADER)
                            );

                            try {
                                ResultSet rs = root.createStatement().executeQuery(reviewSql);
                                resTable = getResultTable(rs, new Vector<>(Arrays.asList(POP_TABLE_HEADER)), exHeader);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                            JFrame poppedWindow = new JFrame();
                            JScrollPane poppedWindowPane = new JScrollPane();

                            poppedWindow.setBounds(600, 360, 800, 600);
                            poppedWindow.setLocationRelativeTo(null);
                            poppedWindow.setContentPane(poppedWindowPane);
                            poppedWindowPane.getViewport().add(resTable);
                            poppedWindow.setVisible(true);

                        }
                    });
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
            }
        });
    }

    private JCheckBox createCheckBox(String name, HashSet<String> targetPool) {
        JCheckBox cb = new JCheckBox(name);
        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String checkedItemName = cb.getText();
                if (cb.isSelected()) {
                    targetPool.add(checkedItemName);
                } else {
                    targetPool.remove(checkedItemName);
                }
            }
        });
        return cb;
    }

    private JTable getResultTable(ResultSet rs, Vector<String> header, HashSet<String> exHeader) throws SQLException{
        Vector<Vector<String>> res = new Vector();
        ResultSetMetaData meta = rs.getMetaData();
        int colSize = meta.getColumnCount();

        while (rs.next()) {
            Vector<String> rawData = new Vector<>();

            for (int i = 1; i <= colSize; i++){
                String colName = meta.getColumnName(i);
                if (!exHeader.contains(colName)) {
                    rawData.add(rs.getString(colName));
                }
            }
            res.add(rawData);
        }

        rs.close();
        return new JTable(res, header);
    }

    private void hiddenColumn(JTable table, String colName) {
        TableColumn hiddenCol = table.getColumn(colName);
        hiddenCol.setWidth(0);
        hiddenCol.setMaxWidth(0);
        hiddenCol.setMinWidth(0);

        int colIndex = table.getTableHeader().getColumnModel().getColumnIndex(colName);

        table.getTableHeader().getColumnModel().getColumn(colIndex).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(colIndex).setMinWidth(0);
    }

    private int getRowNumber (int total) {
        return (total / MAX_COL) + 1;
    }

    private String prepareUserQuery() {
        List<JLabel> labels = new ArrayList<>(
                Arrays.asList(
                        memberSinceLabel, reviewCountLabel, numOfFriendsLabel, avgStarsLabel, voteFunnyLabel,
                        voteUsefulLabel, voteCoolLabel
                )
        );
        List<JComboBox<String>> combos = new ArrayList<>(
                Arrays.asList(
                        memberSinceCombo, reviewCountCombo, numOfFriendsCombo, avgStarsCombo, voteFunnyCombo,
                        voteUsefulCombo, voteCoolCombo
                )
        );
        List<JTextField> fields = new ArrayList<>(
                Arrays.asList(
                        memberSinceField, reviewCountField, numOfFriendsField, avgStarsField, voteFunnyField,
                        voteUsefulField, voteCoolField
                )
        );

        String sql = "SELECT * FROM Users WHERE %s;";
        String sep = (Objects.equals(logicCombo.getSelectedItem(), "AND")) ? " AND " : " OR ";
        List<String> whereCondition = new ArrayList<>();

        for (int i = 0; i < labels.size(); i++) {
            String info = fields.get(i).getText();

            if (!info.isEmpty()) {
                String colName = labels.get(i).getName();
                String operator = (String) combos.get(i).getSelectedItem();

                String buffer = colName + operator;
                if (colName.equals("date_created")) {
                    buffer += "'%s'";
                    buffer = String.format(buffer, info);
                } else {
                    buffer += info;
                }
                whereCondition.add(buffer);
            }
        }

        return whereCondition.isEmpty() ? String.format(sql, DEFAULT_PLACEHOLDER)
                : String.format(sql, String.join(sep, whereCondition));
    }

    private String prepareReviewQuery(boolean isUID, String idValue) {
        String sql = "SELECT * FROM Review WHERE %s = '%s' AND %s";
        String idName = isUID ? "uid" : "bid";
        List<String> reviewWhereCondition = new ArrayList<>();

        if (!reviewFromField.getText().isEmpty()){
            reviewWhereCondition.add(reviewFromLabel.getName() + ">='" + reviewFromField.getText() + "'");
        }

        if (!reciewToField.getText().isEmpty()){
            reviewWhereCondition.add(reviewToLabel.getName() + "<='" + reciewToField.getText() + "'");
        }

        if (!starValueField.getText().isEmpty()){
            reviewWhereCondition.add(
                    starValueLabel.getName() + starValueCombo.getSelectedItem() + starValueField.getText()
            );
        }

        if (!voteValueField.getText().isEmpty()){
            reviewWhereCondition.add(
                    voteValueLabel.getName() + voteValueCombo.getSelectedItem() + voteValueField.getText()
            );
        }

        return reviewWhereCondition.isEmpty() ? String.format(sql, idName, idValue, DEFAULT_PLACEHOLDER)
                : String.format(sql, idName, idValue, String.join(" AND ", reviewWhereCondition));
    }

    private String buildGetSubcategorySql(String delimiter){
        String categoryConstraint =
                "SELECT bid FROM Business_Category_Relation WHERE category = '%s'";

        if (delimiter.equals("OR")){
            List<String> bufferSql = new ArrayList<>();
            for (String categoryName : mainCategoryPool) {
                bufferSql.add(String.format(categoryConstraint, categoryName));
            }
            return String.join(" UNION ALL ", bufferSql);
        } else {
            String followingPattern = categoryConstraint + " AND bid IN (%s)";
            String previousSql = "";

            for (String categoryName : mainCategoryPool) {
                if (previousSql.equals("")){
                    previousSql = String.format(categoryConstraint, categoryName);
                } else {
                    previousSql = String.format(followingPattern, categoryName, previousSql);
                }
            }
            System.out.println(previousSql);
            return previousSql;
        }
    }
    
    private String buildGetAttributeSql (String delimiter) {

        if (delimiter.equals("OR")){
            List<String> bufferSql = new ArrayList<>();
            for (String subcategoryName : subCategoryPool) {
                String getAttributePattern =
                        "SELECT bid FROM Business_Subcategory_Relation WHERE subcategory = '%s' AND bid IN (%s)";
                bufferSql.add(String.format(getAttributePattern, subcategoryName, finalSqlConstraint));
            }

            // BUG here
            String subcategoryConstraintSql = String.join(" UNION ALL ", bufferSql);
            finalSqlConstraint = subcategoryConstraintSql;

            System.out.println("{!!!!}: " + finalSqlConstraint);
            return String.format(attributeSqlPattern, subcategoryConstraintSql);
        } else {
            String getAttributePattern =
                    "SELECT bid FROM Business_Subcategory_Relation WHERE subcategory = '%s' AND bid IN (%s)";
            String previousSql = "";
            for (String subcategoryName : subCategoryPool){
                if (previousSql.equals("")){
                    previousSql = String.format(getAttributePattern, subcategoryName, finalSqlConstraint);
                } else {
                    previousSql = String.format(getAttributePattern, subcategoryName, previousSql);
                }
            }
            finalSqlConstraint = previousSql;
            return String.format(attributeSqlPattern, previousSql);
        }
    }

    private String buildFinalSql(String delimiter){
        HashSet<String> charSetHeader = new HashSet<>(Arrays.asList(CHARSET_HEADER));
        String normalPattern = "%s = '%s'";
        String specialPattern = "FIND_IN_SET('%s', %s) > 0";

        if (delimiter.equals("OR")) {
            List<String> bufferSql = new ArrayList<>();

            String finalPattern = "SELECT bid FROM Business_Attributes_Relation WHERE %s AND bid IN (%s)";

            for (String item: attributesPool){
                String[] keyAndValue = item.split("@");
                String key = keyAndValue[0];
                String value = keyAndValue[1];
                String attributeCondition;

                attributeCondition = charSetHeader.contains(key) ? String.format(specialPattern, value, key) :
                        String.format(normalPattern, key, value);
                String subFinalSql = String.format(finalPattern, attributeCondition, finalSqlConstraint);
                System.out.println(subFinalSql);
                bufferSql.add(subFinalSql);
            }

            return String.join(" UNION ALL ", bufferSql);
        } else {
            System.out.println("***_****" + finalSqlConstraint);
            String finalPattern = "SELECT bid FROM Business_Attributes_Relation WHERE %s AND bid IN (%s)";
            String previousSql = "";

            for (String item: attributesPool){
                String[] keyAndValue = item.split("@");
                String key = keyAndValue[0];
                String value = keyAndValue[1];
                String attributeCondition;

                attributeCondition = charSetHeader.contains(key) ? String.format(specialPattern, value, key) :
                        String.format(normalPattern, key, value);

                if (previousSql.equals("")){
                    previousSql = String.format(finalPattern, attributeCondition, finalSqlConstraint);
                } else {
                    previousSql = String.format(finalPattern, attributeCondition, previousSql);
                }
            }
            return previousSql;
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Loading MySql Driver Successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySql Driver");
            e.printStackTrace();
        }

        try {
            jf = new hw3("hw3", DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/yelp_data",
                    "root",
                    "x9zi4cr@Eyjan"
            ));
            System.out.println("Successfully! Connection is built");
        } catch (SQLException e) {
            System.err.println("Failed to connect with MySql");
            e.printStackTrace();
        }

        jf.setBounds(500, 300, 1440, 960);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

}
