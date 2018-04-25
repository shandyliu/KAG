package KAG;

import com.github.lgooddatepicker.components.DatePicker;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class JumlahKehadiran {
    private JPanel MainPanel;
    private JRadioButton umatRB;
    private JRadioButton timRB;
    private JTable table1;
    private JButton download;
    private JFrame jumlahKehadiranFrame;
    private ButtonGroup radiobg;
    private dbAccess db;
    private DefaultTableModel tableModel;
    private Vector<Vector<Object>> dataumat;
    private Vector<String> columnumat;
    private Vector<Vector<Object>> datatim;
    private Vector<String> columntim;
    private DefaultTableModel tabelumat;
    private DefaultTableModel tabeltim;

    JumlahKehadiran(String tahun) {
        initComponents();
        setFrame();
        getData(tahun);
        loadTableUmat();

        umatRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadTableUmat();
            }
        });

        timRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadTableTim();
            }
        });

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String thn = tahun;
                String FILE_NAME = "C:/Users/u059289/Desktop/JumlahKehadiran " + thn + ".xlsx";

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheetUmat = workbook.createSheet("UMAT");
                XSSFSheet sheetTim = workbook.createSheet("TIM");

                System.out.println("Creating excel");

                //Get Header
                XSSFRow hRowUmat = sheetUmat.createRow((short) 0);
                for (int j = 0; j < tabelumat.getColumnCount(); j++) {
                    XSSFCell cell = hRowUmat.createCell((short) j);
                    cell.setCellValue(tabelumat.getColumnName(j).toString());

                    XSSFCellStyle style = workbook.createCellStyle();
                    style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    cell.setCellStyle(style);
                }

                XSSFRow hRowTim = sheetTim.createRow((short) 0);
                for (int j = 0; j < tabeltim.getColumnCount(); j++) {
                    XSSFCell cell = hRowTim.createCell((short) j);
                    cell.setCellValue(tabeltim.getColumnName(j).toString());

                    XSSFCellStyle style = workbook.createCellStyle();
                    style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    cell.setCellStyle(style);
                }

                //Get Other details
                for (int i = 0; i < tabelumat.getRowCount(); i++) {
                    XSSFRow fRowUmat = sheetUmat.createRow((short) i + 1);
                    for (int j = 0; j < tabelumat.getColumnCount(); j++) {
                        XSSFCell cell = fRowUmat.createCell((short) j);
                        cell.setCellValue(tabelumat.getValueAt(i, j).toString());

                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setBorderBottom(BorderStyle.THIN);
                        style.setBorderTop(BorderStyle.THIN);
                        style.setBorderLeft(BorderStyle.THIN);
                        style.setBorderRight(BorderStyle.THIN);
                        cell.setCellStyle(style);
                    }
                }

                for (int i = 0; i < tabeltim.getRowCount(); i++) {
                    XSSFRow fRowTim = sheetTim.createRow((short) i + 1);
                    for (int j = 0; j < tabeltim.getColumnCount(); j++) {
                        XSSFCell cell = fRowTim.createCell((short) j);
                        cell.setCellValue(tabeltim.getValueAt(i, j).toString());

                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setBorderBottom(BorderStyle.THIN);
                        style.setBorderTop(BorderStyle.THIN);
                        style.setBorderLeft(BorderStyle.THIN);
                        style.setBorderRight(BorderStyle.THIN);
                        cell.setCellStyle(style);
                    }
                }


                try {
                    FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
                    workbook.write(outputStream);
                    workbook.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Done");

                JOptionPane.showMessageDialog(null, "Export Selesai");
            }
        });
    }

    private void initComponents() {
        jumlahKehadiranFrame = new JFrame();
        radiobg = new ButtonGroup();
        radiobg.add(umatRB);
        radiobg.add(timRB);
        db = new dbAccess();
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        tabelumat = new DefaultTableModel();
        tabeltim = new DefaultTableModel();

        dataumat = new Vector<Vector<Object>>();
        columnumat = new Vector<String>();

        datatim = new Vector<Vector<Object>>();
        columntim = new Vector<String>();
    }

    public void getData(String thn) {
        try {
            ResultSet rs = db.st.executeQuery("select MD_UMAT.Nama, COUNT(DT_EVENT.NoHP) AS Jumlah From MD_UMAT INNER JOIN DT_EVENT ON DT_EVENT.NoHP = MD_UMAT.NoHP INNER JOIN HD_EVENT ON HD_EVENT.ID_EVENT = DT_EVENT.ID_EVENT WHERE YEAR(HD_EVENT.TGL_EVENT) = " + thn + " GROUP BY MD_UMAT.NAMA HAVING COUNT(DT_EVENT.NoHP) > 5");
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            columnumat = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnumat.add(metaData.getColumnName(i));
            }

            // Data of the table
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                dataumat.add(vector);
            }
        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }


        try {
            ResultSet rs = db.st.executeQuery("select MD_TIM.Nama, COUNT(ABSENSI_TIM.NoHP) AS Jumlah From MD_TIM INNER JOIN ABSENSI_TIM ON ABSENSI_TIM.NoHP = MD_TIM.NoHP INNER JOIN HD_EVENT ON HD_EVENT.ID_EVENT = ABSENSI_TIM.ID_EVENT WHERE YEAR(HD_EVENT.TGL_EVENT) = " + thn + " GROUP BY MD_TIM.NAMA");
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            columntim = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columntim.add(metaData.getColumnName(i));
            }

            // Data of the table
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                datatim.add(vector);
            }
        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }

        tabelumat.setDataVector(dataumat, columnumat);
        tabeltim.setDataVector(datatim, columntim);

    }

    private void setFrame() {
        jumlahKehadiranFrame.setContentPane(MainPanel);
        jumlahKehadiranFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jumlahKehadiranFrame.setSize(1200, 600);
        jumlahKehadiranFrame.setLocationRelativeTo(null);
        jumlahKehadiranFrame.setTitle("Report Jumlah Kehadiran");
        //chooseEventFrame.pack();
        jumlahKehadiranFrame.setVisible(true);
    }

    public void loadTableUmat() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};

        tableModel.setDataVector(dataumat, columnumat);

    }

    private void loadTableTim() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};

        tableModel.setDataVector(datatim, columntim);

    }

    public static void main(String[] args) {
        new JumlahKehadiran("2018");
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
        MainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        umatRB = new JRadioButton();
        umatRB.setSelected(true);
        umatRB.setText("Umat");
        panel1.add(umatRB, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timRB = new JRadioButton();
        timRB.setText("Tim");
        panel1.add(timRB, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        download = new JButton();
        download.setText("Download to Excel");
        panel3.add(download, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }
}
