package KAG;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.Vector;

public class ChooseYear {
    private JButton submit;
    private JComboBox comboBox1;
    private JPanel MainPanel;
    private dbAccess db;
    private JFrame chooseYearFrame;
    private SimpleDateFormat sf;

    ChooseYear() {
        initComponents();
        getYear();
        setFrame();

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String year = comboBox1.getSelectedItem().toString();
                JumlahKehadiran jml = new JumlahKehadiran(year);
                chooseYearFrame.setVisible(false);
            }
        });
    }

    private void initComponents() {
        chooseYearFrame = new JFrame();
        db = new dbAccess();
    }

    private void setFrame() {
        chooseYearFrame.setContentPane(MainPanel);
        chooseYearFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chooseYearFrame.setSize(200, 100);
        chooseYearFrame.setLocationRelativeTo(null);
        chooseYearFrame.setTitle("Year");
        //chooseYearFrame.pack();
        chooseYearFrame.setVisible(true);
    }

    private void getYear() {
        Vector lt_year = new Vector();
        String year;
        int check = 0;
        try {
            ResultSet rs = db.st.executeQuery("select TGL_EVENT From HD_EVENT");

            while (rs.next()) {
                check = 0;
                sf = new SimpleDateFormat("yyyy");
                year = sf.format(rs.getDate(1));
                if (lt_year.isEmpty())
                {
                    lt_year.add(year);
                    comboBox1.addItem(year);
                }
                else
                {
                    for (int i = 0; i < lt_year.size(); i++) {
                        if (lt_year.get(i).toString().equals(year)) {
                            check = 1;
                        }
                    }
                    if (check == 0) {
                        comboBox1.addItem(year);
                        lt_year.add(year);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            db.closeDB();
        }
    }

    public static void main(String[] args) {
        new ChooseYear();
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Pilih Tahun : ");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        panel1.add(comboBox1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        submit = new JButton();
        submit.setText("Submit");
        panel2.add(submit, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }
}
