package KAG;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;

public class ChooseEvent {
    private JPanel panel1;
    private JButton pilihButton;
    private JComboBox comboBox1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private DatePicker datePick;
    private JButton submitButton;
    private DatePickerSettings dateSetting;
    private JFrame chooseEventFrame;
    private String tanggal;
    private Timestamp timestamp;
    private Date date;
    private SimpleDateFormat sf;
    private Locale locale;
    private ArrayList IDevent;
    private dbAccess db;
    private String idx;
    public String jenis;

    public ChooseEvent() {

        this.tanggal = setDate();
        initComponents();
        getEvent(this.tanggal);
        setFrame();

        /*comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                getData();
            }
        });*/
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                getData();
            }
        });
        datePick.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                tanggal = datePick.getText();
                getEvent(tanggal);
            }
        });
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (comboBox1.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(chooseEventFrame, "Silahkan pilih tema terlebih dahulu");
                } else {
                    if (jenis.equals("R")) {
                        RegisterUmum registerUmum = new RegisterUmum(textField3.getText());
                        chooseEventFrame.setVisible(false);
                        registerUmum.Label_Tema.setText(comboBox1.getSelectedItem().toString());
                        registerUmum.Label_Tgl.setText(tanggal);
                        registerUmum.lbl_pembicara.setText(textField2.getText());
                        registerUmum.eventID = idx;
                        registerUmum.jenisPD = textField3.getText();
                        registerUmum.umatRB.setSelected(true);
                        registerUmum.loadTableUmat();
                    } else if (jenis.equals("A")) {
                        Absensi absensi = new Absensi();
                        chooseEventFrame.setVisible(false);
                        absensi.lbl_pembicara.setText(textField2.getText());
                        absensi.lbl_tema.setText(comboBox1.getSelectedItem().toString());
                        absensi.lbl_tgl.setText(tanggal);
                        absensi.jns_pd.setText("PD " + textField3.getText());
                        absensi.eventID = idx;
                        absensi.getData();
                        absensi.loadTableUmat();
                        absensi.umatRB.setSelected(true);
                    }
                }
            }
        });
    }

    private void initComponents() {
        chooseEventFrame = new JFrame();
        db = new dbAccess();
        IDevent = new ArrayList();
    }

    private void setFrame() {
        chooseEventFrame.setContentPane(panel1);
        chooseEventFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chooseEventFrame.setSize(400, 250);
        chooseEventFrame.setLocationRelativeTo(null);
        chooseEventFrame.setTitle("Pilih Tema");
        //chooseEventFrame.pack();
        chooseEventFrame.setVisible(true);
    }

    private void setDateSetting() {
        locale = new Locale("in");
        dateSetting = new DatePickerSettings(locale);
        dateSetting.setFormatForDatesCommonEra("d MMMM yyyy");
    }

    private String setDate() {
        setDateSetting();
        datePick.setSettings(dateSetting);
        datePick.setDateToToday();

        return datePick.getText();

    }

    private void getEvent(String tgl) {
        IDevent.removeAll(IDevent);
        comboBox1.removeAllItems();
        try {
            ResultSet rs = db.st.executeQuery("select * From HD_EVENT");

            while (rs.next()) {
                timestamp = rs.getTimestamp(6);
                sf = new SimpleDateFormat("d MMMM yyyy", locale);
                date = new Date(Long.valueOf(this.timestamp.getTime()));

                if (tgl.equals(this.sf.format(date))) {
                    comboBox1.addItem(rs.getString(2));
                    IDevent.add(rs.getString(1));
                }
            }
            getData();

        } catch (SQLException e) {
            e.printStackTrace();
            db.closeDB();
        }

    }

    private void getData() {
        if (IDevent.isEmpty()) {
            textField3.setText("");
            textField2.setText("");
            textField4.setText("");
        } else {
            idx = (String) IDevent.get(comboBox1.getSelectedIndex());
            try {
                ResultSet rs = db.st.executeQuery("select * From HD_EVENT where ID_EVENT = " + idx);

                while (rs.next()) {
                    textField2.setText(rs.getString(4));
                    textField3.setText(rs.getString(3));
                    textField4.setText(rs.getString(5));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                db.closeDB();
            }
        }
    }


    public static void main(String[] args) {
        new ChooseEvent();
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
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Tanggal Event :");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        datePick = new DatePicker();
        panel2.add(datePick, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        submitButton = new JButton();
        submitButton.setText("Submit");
        panel2.add(submitButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tema Event");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Pembicara");
        panel3.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Tempat");
        panel3.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Kategori");
        panel3.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        panel3.add(comboBox1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField2 = new JTextField();
        panel3.add(textField2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textField3 = new JTextField();
        panel3.add(textField3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textField4 = new JTextField();
        panel3.add(textField4, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText(":");
        panel3.add(label6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText(":");
        panel3.add(label7, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText(":");
        panel3.add(label8, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText(":");
        panel3.add(label9, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 20), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
