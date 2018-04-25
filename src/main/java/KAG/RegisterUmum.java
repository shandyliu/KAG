package KAG;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class RegisterUmum {
    private JPanel mainPanel;
    private JPanel LeftPanel;
    private JPanel RightPanel;
    private JTextField Input_NoHp;
    private JTextField Input_Nama;
    private JButton registerButton;
    private JButton searchButton;
    private JButton daftarBaruButton;
    private JPanel panelButton;
    public JLabel Label_Tema;
    public JLabel Label_Tgl;
    public String eventID;
    private JTable table1;
    private JLabel countLabel;
    public JLabel lbl_pembicara;
    public JRadioButton umatRB;
    private JRadioButton timRB;
    public JFrame registerUmumFrame;
    private dbAccess db;
    private DefaultTableModel tableModel;
    public String jenisPD;
    private ButtonGroup radiobg;

    RegisterUmum(String jenisPD) {
        this.jenisPD = jenisPD;
        initComponents();
        setFrame();
        eListener();

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (umatRB.isSelected()) {
                    register("U");
                    loadTableUmat();
                } else if (timRB.isSelected()) {
                    register("T");
                    loadTableTIM();
                }

            }
        });

        umatRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadTableUmat();
            }
        });
        timRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadTableTIM();
            }
        });
    }

    private void initComponents() {
        registerUmumFrame = new JFrame();
        db = new dbAccess();
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        radiobg = new ButtonGroup();
        radiobg.add(umatRB);
        radiobg.add(timRB);
    }

    private void setFrame() {
        registerUmumFrame.setContentPane(mainPanel);
        registerUmumFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerUmumFrame.setSize(1200, 600);
        registerUmumFrame.setLocationRelativeTo(null);
        registerUmumFrame.setTitle("Register PD " + jenisPD);
        //chooseEventFrame.pack();
        registerUmumFrame.setVisible(true);
    }

    private void eListener() {
        Input_NoHp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (umatRB.isSelected()) {
                        register("U");
                        loadTableUmat();
                    } else if (timRB.isSelected()) {
                        register("T");
                        loadTableTIM();
                    }
                } else {
                    Input_Nama.setText("");
                    if (umatRB.isSelected()) {
                        checkNoHP("MD_UMAT");
                    } else if (timRB.isSelected()) {
                        checkNoHP("MD_TIM");
                    }
                }
            }
        });

        Input_Nama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (umatRB.isSelected()) {
                        register("U");
                        loadTableUmat();
                    } else if (timRB.isSelected()) {
                        register("T");
                        loadTableTIM();
                    }
                } else {
                    Input_NoHp.setText("");
                    if (umatRB.isSelected()) {
                        checkNames("MD_UMAT");
                    } else if (timRB.isSelected()) {
                        checkNames("MD_TIM");
                    }
                }
            }
        });
    }

    private void checkNoHP(String tab) {
        String regex = "\\d+";
        if (Input_NoHp.getText().matches(regex)) {
            try {
                ResultSet rs = db.st.executeQuery("select Nama, NoHp From " + tab + " where NoHp = " + Input_NoHp.getText());
                if (rs.next()) {
                    Input_Nama.setText(rs.getString(1));
                } else {
                    Input_Nama.setText("");
                }
            } catch (SQLException eDb) {

            }
        } else {
            Input_Nama.setText("");
        }
    }

    private void checkNames(String tab) {
        try {
            ResultSet rs = db.st.executeQuery("select Nama, NoHp from " + tab + " WHERE Nama = " + Input_Nama.getText());
            while (rs.next()) {
                if (rs.getString(1).equalsIgnoreCase(Input_Nama.getText())) {
                    Input_NoHp.setText(rs.getString(2));
                } else {
                    Input_NoHp.setText("");
                }
            }

        } catch (SQLException eDb) {

        }
    }

    private void register(String jns) {
        if (jns == "U") {
            checkNames("MD_UMAT");
            checkNoHP("MD_UMAT");
        } else if (jns == "T") {
            checkNames("MD_TIM");
            checkNoHP("MD_TIM");
        }
        if (Input_Nama.getText().isEmpty() && Input_NoHp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Silahkan masukkan data dengan benar terlebih dahulu");
        } else {
            try {
                int id = Integer.parseInt(eventID);
                if (jns == "U") {
                    db.st.executeUpdate("INSERT INTO DT_EVENT (ID_EVENT,NoHP) VALUES ('" + id + "','" + Input_NoHp.getText() + "')");
                } else if (jns == "T") {
                    db.st.executeUpdate("INSERT INTO ABSENSI_TIM (ID_EVENT,NoHP) VALUES ('" + id + "','" + Input_NoHp.getText() + "')");
                }
                JOptionPane.showMessageDialog(null, "Selamat Datang ! Tuhan Yesus Memberkati");
                cleartext();
            } catch (SQLException eDb) {
                eDb.printStackTrace();
            }
        }
    }

    private void cleartext() {
        Input_Nama.setText("");
        Input_NoHp.setText("");
    }

    public void loadTableUmat() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};
        try {
            ResultSet rs = db.st.executeQuery("select DT_EVENT.NoHP, MD_UMAT.Nama, MD_UMAT.Paroki, MD_UMAT.TglLahir, MD_UMAT.Kategori From DT_EVENT INNER JOIN MD_UMAT ON DT_EVENT.NoHP = MD_UMAT.NoHP WHERE DT_EVENT.ID_EVENT = " + Integer.parseInt(eventID));
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    if (i == 4) {
                        Locale locale = new Locale("in");
                        Timestamp timestamp = rs.getTimestamp(4);
                        SimpleDateFormat sf = new SimpleDateFormat("d MMMM yyyy", locale);
                        Date date = new Date(Long.valueOf(timestamp.getTime()));
                        String tgl = sf.format(date);
                        vector.add(tgl);
                    } else {
                        vector.add(rs.getObject(i));
                    }
                }
                data.add(vector);
            }
            countLabel.setText("Jumlah umat hari ini : " + data.size());
            tableModel.setDataVector(data, columnNames);

        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }
    }

    public void loadTableTIM() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};
        try {
            ResultSet rs = db.st.executeQuery("select ABSENSI_TIM.NoHP, MD_TIM.Nama, MD_TIM.TglLahir, MD_TIM.Kategori From ABSENSI_TIM INNER JOIN MD_TIM ON ABSENSI_TIM.NoHP = MD_TIM.NoHP WHERE ABSENSI_TIM.ID_EVENT = " + Integer.parseInt(eventID));
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    if (i == 3) {
                        Locale locale = new Locale("in");
                        Timestamp timestamp = rs.getTimestamp(3);
                        SimpleDateFormat sf = new SimpleDateFormat("d MMMM yyyy", locale);
                        Date date = new Date(Long.valueOf(timestamp.getTime()));
                        String tgl = sf.format(date);
                        vector.add(tgl);
                    } else {
                        vector.add(rs.getObject(i));
                    }
                }
                data.add(vector);
            }
            countLabel.setText("Jumlah TIM hari ini : " + data.size());
            tableModel.setDataVector(data, columnNames);

        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new RegisterUmum(null);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        LeftPanel = new JPanel();
        LeftPanel.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(LeftPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        LeftPanel.add(panel1, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("No HP");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nama");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText(":");
        panel1.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText(":");
        panel1.add(label4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Input_NoHp = new JTextField();
        panel1.add(Input_NoHp, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        Input_Nama = new JTextField();
        panel1.add(Input_Nama, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panelButton = new JPanel();
        panelButton.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchButton = new JButton();
        searchButton.setText("Search");
        searchButton.setVisible(false);
        panelButton.add(searchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registerButton = new JButton();
        registerButton.setText("Register");
        panelButton.add(registerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Label_Tema = new JLabel();
        Font Label_TemaFont = this.$$$getFont$$$("Open Sans", Font.BOLD, 16, Label_Tema.getFont());
        if (Label_TemaFont != null) Label_Tema.setFont(Label_TemaFont);
        Label_Tema.setText("Label");
        LeftPanel.add(Label_Tema, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        LeftPanel.add(spacer1, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 10), null, 0, false));
        final Spacer spacer2 = new Spacer();
        LeftPanel.add(spacer2, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 20), null, 0, false));
        lbl_pembicara = new JLabel();
        lbl_pembicara.setText("Label");
        LeftPanel.add(lbl_pembicara, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Label_Tgl = new JLabel();
        Label_Tgl.setText("Label");
        LeftPanel.add(Label_Tgl, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        umatRB = new JRadioButton();
        umatRB.setText("Umat");
        LeftPanel.add(umatRB, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timRB = new JRadioButton();
        timRB.setText("Tim");
        LeftPanel.add(timRB, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        LeftPanel.add(spacer3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer4 = new Spacer();
        LeftPanel.add(spacer4, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer5 = new Spacer();
        LeftPanel.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(1, -1), null, 0, false));
        RightPanel = new JPanel();
        RightPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(RightPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        RightPanel.add(spacer6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(50, 14), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        RightPanel.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        countLabel = new JLabel();
        countLabel.setText("Label");
        RightPanel.add(countLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
