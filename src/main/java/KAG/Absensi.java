package KAG;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Absensi {
    private JPanel MainPanel;
    private JPanel RadioPanel;
    private JPanel TablePanel;
    private JPanel DownloadPanel;
    public JRadioButton umatRB;
    private JRadioButton timRB;
    private JTable table1;
    private JButton but_download;
    private JLabel jumlahpeserta;
    public JLabel lbl_pembicara;
    public JLabel lbl_tgl;
    public JLabel jns_pd;
    public JLabel lbl_tema;
    private JFrame absensiFrame;
    private ButtonGroup radiobg;
    private dbAccess db;
    private DefaultTableModel tableModel;
    public String eventID;
    private DefaultTableModel tabelumat;
    private DefaultTableModel tabeltim;
    private Vector<Vector<Object>> dataumat;
    private Vector<String> columnumat;
    private Vector<Vector<Object>> datatim;
    private Vector<String> columntim;

    Absensi() {
        initComponents();
        setFrame();


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

        but_download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String tglabsen = lbl_tgl.getText();
                String FILE_NAME = "C:/Users/u059289/Desktop/Absensi " + tglabsen + ".xlsx";

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
        absensiFrame = new JFrame();
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

    private void setFrame() {
        absensiFrame.setContentPane(MainPanel);
        absensiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        absensiFrame.setSize(1200, 600);
        absensiFrame.setLocationRelativeTo(null);
        absensiFrame.setTitle("Absensi");
        //chooseEventFrame.pack();
        absensiFrame.setVisible(true);
    }

    public void getData() {
        try {
            ResultSet rs = db.st.executeQuery("select DT_EVENT.NoHP, MD_UMAT.Nama, MD_UMAT.Paroki, MD_UMAT.TglLahir, MD_UMAT.Kategori From DT_EVENT INNER JOIN MD_UMAT ON DT_EVENT.NoHP = MD_UMAT.NoHP WHERE DT_EVENT.ID_EVENT = " + Integer.parseInt(eventID));
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
                dataumat.add(vector);
            }
        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }


        try {
            ResultSet rs = db.st.executeQuery("select ABSENSI_TIM.NoHP, MD_TIM.Nama, MD_TIM.TglLahir, MD_TIM.Kategori From ABSENSI_TIM INNER JOIN MD_TIM ON ABSENSI_TIM.NoHP = MD_TIM.NoHP WHERE ABSENSI_TIM.ID_EVENT = " + Integer.parseInt(eventID));
            ResultSetMetaData metaData = rs.getMetaData();

            //get column name
            columntim = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columntim.add(metaData.getColumnName(i));
            }

            // Data of the table
            datatim = new Vector<Vector<Object>>();
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
                datatim.add(vector);
            }
        } catch (SQLException eDb) {
            eDb.printStackTrace();
        }

        tabelumat.setDataVector(dataumat, columnumat);
        tabeltim.setDataVector(datatim, columntim);

    }

    public void loadTableUmat() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};

        jumlahpeserta.setText("Total Umat : " + dataumat.size());
        tableModel.setDataVector(dataumat, columnumat);

    }

    private void loadTableTim() {
        table1.setModel(tableModel);
        //String[] column = {"No HP", "Nama", "Paroki", "Tgl Lahir", "Kategori"};

        jumlahpeserta.setText("Total tim yang hadir : " + datatim.size());
        tableModel.setDataVector(datatim, columntim);

    }

    public static void main(String[] args) {
        new Absensi();
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
        RadioPanel = new JPanel();
        RadioPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(RadioPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        umatRB = new JRadioButton();
        umatRB.setText("Umat");
        RadioPanel.add(umatRB, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timRB = new JRadioButton();
        timRB.setText("Tim");
        RadioPanel.add(timRB, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbl_pembicara = new JLabel();
        lbl_pembicara.setText("Label");
        RadioPanel.add(lbl_pembicara, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbl_tgl = new JLabel();
        lbl_tgl.setText("Label");
        RadioPanel.add(lbl_tgl, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbl_tema = new JLabel();
        Font lbl_temaFont = this.$$$getFont$$$(null, Font.BOLD, 14, lbl_tema.getFont());
        if (lbl_temaFont != null) lbl_tema.setFont(lbl_temaFont);
        lbl_tema.setText("Label");
        RadioPanel.add(lbl_tema, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jns_pd = new JLabel();
        jns_pd.setText("Label");
        RadioPanel.add(jns_pd, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TablePanel = new JPanel();
        TablePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(TablePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        TablePanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        table1.setEnabled(false);
        scrollPane1.setViewportView(table1);
        jumlahpeserta = new JLabel();
        jumlahpeserta.setText("Label");
        TablePanel.add(jumlahpeserta, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        DownloadPanel = new JPanel();
        DownloadPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainPanel.add(DownloadPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        but_download = new JButton();
        but_download.setText("Download to Excel");
        DownloadPanel.add(but_download, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        DownloadPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
        return MainPanel;
    }
}
