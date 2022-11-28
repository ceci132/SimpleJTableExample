package ui;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainUI {
    private JPanel rootPanel;
    private JTable showTable;
    private JButton deleteRowButton;
    private JButton addRowButton;
    private JButton saveToFileButton;
    private JButton readFromFileButton;
    private JButton printButton;
    private JButton createPDFButton;

    public MainUI() {
        createTable();
        deleteRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) showTable.getModel();
                if (showTable.getSelectedRow() != -1) {
                    model.removeRow(showTable.getSelectedRow());
                }
            }
        });
        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) showTable.getModel();
                model.addRow(new Object[]{});
            }
        });
        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = new File("./JTable_File.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);

                    for (int i = 0; i < showTable.getRowCount(); i++) {
                        for (int j = 0; j < showTable.getColumnCount(); j++) {
                            bw.write((String) showTable.getModel().getValueAt(i, j));
                            if (showTable.getColumnCount() - 1 > j)
                                bw.write(",");
                        }

                        bw.write("\n");
                    }
                    bw.close();
                    fw.close();
                    JOptionPane.showMessageDialog(null, "Data Exported");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        createPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Document doc = new Document();
                    PdfWriter.getInstance(doc, new FileOutputStream("table.pdf"));
                    doc.open();
                    PdfPTable pdfTable = new PdfPTable(showTable.getColumnCount());
                    //adding table headers
                    for (int i = 0; i < showTable.getColumnCount(); i++) {
                        pdfTable.addCell(showTable.getColumnName(i));
                    }
                    //extracting data from the JTable and inserting it to PdfPTable
                    for (int rows = 0; rows < showTable.getRowCount() - 1; rows++) {
                        for (int cols = 0; cols < showTable.getColumnCount(); cols++) {
                            pdfTable.addCell(showTable.getModel().getValueAt(rows, cols).toString());

                        }
                    }
                    doc.add(pdfTable);
                    doc.close();
                    System.out.println("done");
                } catch (DocumentException | FileNotFoundException ex) {
                    Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!showTable.print()) {
                        System.err.println("User cancelled printing");
                    }
                } catch (java.awt.print.PrinterException ex) {
                    System.err.format("Cannot print %s%n", ex.getMessage());
                }
            }
        });
        readFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CSVReader reader = null;
                try {
                    reader = new CSVReader(new FileReader("JTable_File.txt"));
                    var myEntries = reader.readAll();
                    var model = new DefaultTableModel(null, new String[]{"First Name", "Last Name", "Average Grade"});
                    myEntries.forEach(model::addRow);
                    showTable.setModel(model);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void createTable() {
        Object[][] data = {
                {"Ivan", "Ivanov", "5.12"},
                {"Petar", "Petrov", "6.00"},
                {"Georgi", "Georgiev", "4.25"}

        };
        showTable.setModel(new DefaultTableModel(
                data,
                new String[]{"First Name", "Last Name", "Average Grade"}
        ));
        TableColumnModel columns = showTable.getColumnModel();

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        columns.getColumn(0).setCellRenderer(leftRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columns.getColumn(1).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(2).setCellRenderer(rightRenderer);

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.putIfAbsent("Table.alternateRowColor", Color.CYAN);
    }
}
