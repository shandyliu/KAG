package KAG;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.validator.routines.EmailValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Vector;

public class DaftarBaru {
    private JPanel MainPanel;
    private JTextField InputHP;
    private JTextField InputNama;
    private JTextField InputParoki;
    private JTextField InputEmail;
    private JButton simpanButton;
    private JButton clearButton;
    private JTable table1;
    private JPanel DataPanel;
    private JPanel ButtonPanel;
    private JTextField InputAlamat;
    private DatePicker InputTglLahir;
    private JComboBox InputKategori;
    public JFrame DaftarBaruFrame;
    private dbAccess db;
    private DefaultTableModel tableModel;
    private Vector<String> columnNames;
    private Vector<Vector<Object>> data;
    private int columnCount;
    private Locale locale;
    private DatePickerSettings dateSetting;
    private LocalDate date;
    private String category;

    DaftarBaru() {
        initComponents();
        setDateSetting();
        setSosmed();
        setKategori();
        setFrame();
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clear();
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                save();
            }
        });
    }

    private void initComponents() {
        DaftarBaruFrame = new JFrame();
        db = new dbAccess();
        date = LocalDate.now();
    }

    private void setFrame() {
        DaftarBaruFrame.setContentPane(MainPanel);
        DaftarBaruFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // DaftarBaruFrame.setSize(1200,600);
        DaftarBaruFrame.setTitle("Daftar Umat Baru");
        DaftarBaruFrame.pack();
        DaftarBaruFrame.setLocationRelativeTo(null);
        DaftarBaruFrame.setVisible(true);
    }

    private void setKategori() {
        InputKategori.addItem("UMUM");
        InputKategori.addItem("YOUTH");
        InputKategori.setSelectedIndex(0);
    }

    private void setDateSetting() {
        locale = new Locale("in");
        dateSetting = new DatePickerSettings(locale);
        dateSetting.setFormatForDatesCommonEra("d MMMM yyyy");
        InputTglLahir.setSettings(dateSetting);
    }

    private void setSosmed() {
        try {
            ResultSet rs = db.st.executeQuery("SELECT MD_SOSMED.IDSosmed, MD_SOSMED.JenisSosmed,DT_SOSMED_UMAT.Sosmed From MD_SOSMED INNER JOIN DT_SOSMED_UMAT " +
                    "ON MD_SOSMED.IDSosmed = DT_SOSMED_UMAT.IDSosmed");
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            columnNames = new Vector<String>();
            columnCount = metaData.getColumnCount();
            for (int i = 2; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 2; i < columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                data.add(vector);
            }

            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    if (column == 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            table1.setModel(tableModel);
            tableModel.setDataVector(data, columnNames);
        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }

    }

    private void clear() {
        InputNama.setText("");
        InputHP.setText("");
        InputAlamat.setText("");
        InputParoki.setText("");
        InputEmail.setText("");
        InputTglLahir.setText("");
        InputKategori.setSelectedIndex(0);
        for (int i = 0; i < columnCount - 1; i++) {
            table1.getCellEditor(i, 0).stopCellEditing();
            tableModel.setValueAt("", i, 1);
        }
    }

    private boolean checkNoHP() {
        int bool = 0;
        try {
            ResultSet rs = db.st.executeQuery("select NoHp from MD_UMAT WHERE NoHP = " + InputHP.getText());
            while (rs.next()) {
                bool = 1;
            }
        } catch (SQLException eDb) {

        }
        if (bool == 1) {
            return false;
        } else {
            return true;
        }
    }

    private void save() {
        for (int i = 0; i < columnCount - 1; i++) {
            table1.getCellEditor(i, 0).stopCellEditing();
        }

        String regex = "\\d+";
        EmailValidator validator = EmailValidator.getInstance();
        category = InputKategori.getSelectedItem().toString();

        if (InputHP.getText().length() < 11 || !InputHP.getText().startsWith("0")) {
            JOptionPane.showMessageDialog(null, "Masukkan no HP dengan benar");
        } else if (!InputHP.getText().matches(regex)) {
            JOptionPane.showMessageDialog(null, "No HP hanya boleh dimasukkan angka");
        } else if (checkNoHP() == false) {
            JOptionPane.showMessageDialog(null, "Maaf nomor HP Anda telah terdaftar");
        } else if (InputNama.getText().isEmpty() || InputHP.getText().isEmpty() || InputParoki.getText().isEmpty() || InputTglLahir.getText().isEmpty() || InputAlamat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Mohon masukkan data dengan lengkap");
        } else if (!InputEmail.getText().isEmpty()) {
            if (!validator.isValid(InputEmail.getText())) {
                JOptionPane.showMessageDialog(null, "Mohon masukkan email dengan benar");
            } else {
                insertDb();
            }
        } else {
            insertDb();
        }

    }

    private void insertDb() {
        String sql = "INSERT INTO MD_UMAT (NoHP,Nama,Alamat,Paroki,TglLahir,Email,Kategori,Created_On,Changed_On) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = db.st.getConnection().prepareStatement(sql);
            ps.setString(1, InputHP.getText());
            ps.setString(2, InputNama.getText());
            ps.setString(3, InputAlamat.getText());
            ps.setString(4, InputParoki.getText());
            ps.setDate(5, Date.valueOf(InputTglLahir.getDate()));
            ps.setString(6, InputEmail.getText());
            ps.setString(7, category);
            ps.setDate(8, Date.valueOf(date));
            ps.setDate(9, Date.valueOf(date));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Terima kasih ! Anda telah terdaftar dalam keluarga KAG");
            clear();

        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DaftarBaru();
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
        MainPanel = new JPanel();
        MainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        DataPanel = new JPanel();
        DataPanel.setLayout(new GridLayoutManager(9, 3, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(DataPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("No HP");
        DataPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nama");
        DataPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Paroki");
        DataPanel.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Tgl Lahir");
        DataPanel.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Email");
        DataPanel.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText(":");
        DataPanel.add(label6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText(":");
        DataPanel.add(label7, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText(":");
        DataPanel.add(label8, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText(":");
        DataPanel.add(label9, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText(":");
        DataPanel.add(label10, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InputHP = new JTextField();
        DataPanel.add(InputHP, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        InputNama = new JTextField();
        DataPanel.add(InputNama, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        InputParoki = new JTextField();
        DataPanel.add(InputParoki, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        InputEmail = new JTextField();
        DataPanel.add(InputEmail, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        table1 = new JTable();
        table1.setEditingColumn(-1);
        DataPanel.add(table1, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 50), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Sosmed");
        DataPanel.add(label11, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        DataPanel.add(spacer1, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Alamat");
        DataPanel.add(label12, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText(":");
        DataPanel.add(label13, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InputAlamat = new JTextField();
        DataPanel.add(InputAlamat, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        InputTglLahir = new DatePicker();
        DataPanel.add(InputTglLahir, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Kategori");
        DataPanel.add(label14, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText(":");
        DataPanel.add(label15, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InputKategori = new JComboBox();
        DataPanel.add(InputKategori, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonPanel = new JPanel();
        ButtonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(ButtonPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        simpanButton = new JButton();
        simpanButton.setText("Simpan");
        ButtonPanel.add(simpanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        clearButton.setText("Clear");
        ButtonPanel.add(clearButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }
}
