package org.clevertec.managers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.clevertec.models.ProductInReceipt;
import org.clevertec.models.Receipt;
import org.clevertec.providers.ProductProvider;
import org.clevertec.utils.FormatUtil;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

public class ReceiptPrintManager {

    String getFileName(LocalDate date, LocalTime time) {
        String dateS = DateTimeFormatter.ofPattern("dd_MM_uuuu").format(date);
        String timeS = DateTimeFormatter.ofPattern("HH_mm_ss").format(time);
        return "receipt-" + dateS + "-" + timeS;
    }

    public String writeToPdfForSend(Receipt receipt, ProductProvider productProvider, ServletOutputStream outputStream)
            throws IOException, DocumentException {
        String pathReceiptsPdf = "src/main/resources/receiptsPdf";
        String filePath = pathReceiptsPdf + File.separator + getFileName(receipt.getDate(), receipt.getTime()) + ".pdf";

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        addContent(writer, document, productProvider, receipt);

        document.close();
        writer.close();
        return filePath;
    }

    private void addHeader(Document document) throws DocumentException {
        String title = "CASH RECEIPT";
        String nameStore = "LocalShop";
        String address = "Address";
        String number = "7717";
        PdfPTable receiptHeaderTable = new PdfPTable(new float[]{100});
        addReceiptHeaderRows(receiptHeaderTable, title, nameStore, address, number);
        receiptHeaderTable.setSpacingBefore(100f);
        document.add(receiptHeaderTable);
    }

    private void addBackground(PdfWriter writer, Document document) throws IOException, DocumentException {
        String pathCltTemplate = "/Clevertec_Template.pdf";
        PdfReader reader = new PdfReader(pathCltTemplate);
        PdfImportedPage bcg = writer.getImportedPage(reader, 1);
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.addTemplate(bcg, 0, 0);
        document.add(new Phrase(" "));
    }

    private void addContent(PdfWriter writer, Document document, ProductProvider productProvider, Receipt receipt) throws DocumentException, IOException {
        String dateT = DateTimeFormatter.ofPattern("dd/MM/uuuu").format(receipt.getDate());
        String timeT = DateTimeFormatter.ofPattern("HH:mm:ss").format(receipt.getTime());

        addBackground(writer, document);
        addHeader(document);

        PdfPTable receiptInfoTable = new PdfPTable(new float[]{70, 30});
        addReceiptInfoRows(receiptInfoTable,
                "CASHIER: #" + receipt.getCashier(),
                "Date: " + dateT,
                "Time: " + timeT);
        document.add(receiptInfoTable);

        PdfPTable positionTable = new PdfPTable(new float[]{7, 63, 15, 15});
        addPositionRows(positionTable, receipt.getPositions(), productProvider);
        document.add(positionTable);

        PdfPTable receiptFooterTable = new PdfPTable(new float[]{70, 30});
        addReceiptFooterRows(receiptFooterTable, receipt.getTotalPrice(), receipt.getTotalPriceWithDiscount());
        document.add(receiptFooterTable);
    }

    private void addPositionRows(PdfPTable table, List<ProductInReceipt> positions, ProductProvider productProvider) {
        addPositionRow(table, "QTY", "DESCRIPTION", "PRICE", "TOTAL");
        for (var position : positions) {
            try {
                addPositionRow(table,
                        String.valueOf(position.getQuantity()),
                        productProvider.find(position.getIdProduct()).get().getName(),
                        "$" + FormatUtil.formatNum2(position.getPrice()),
                        "$" + FormatUtil.formatNum2(position.getTotal()));
            } catch (NoSuchElementException ignored) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void addPositionRow(PdfPTable table, String qty, String desc, String price, String total) {
        addCellWithoutBorderToTable(table, qty, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, desc, Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, price, Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, total, Element.ALIGN_RIGHT);
    }

    private void addReceiptInfoRows(PdfPTable table, String cashier, String date, String time) {
        addCellWithoutBorderToTable(table, cashier, Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, date, Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, time, Element.ALIGN_RIGHT);
    }

    private void addReceiptHeaderRows(PdfPTable table, String title, String storeName, String address, String phone) {
        addCellWithoutBorderToTable(table, title, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, storeName, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, address, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, phone, Element.ALIGN_CENTER);
    }

    private void addReceiptFooterRows(PdfPTable table, double price, double total) {
        addCellWithTopBorderToTable(table, "TAXABLE TOT.", Element.ALIGN_LEFT);
        addCellWithTopBorderToTable(table, "$" + FormatUtil.formatNum2(price), Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "DISCOUNT", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, "$" + FormatUtil.formatNum2(FormatUtil.round(price - total)), Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "TOTAL", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, "$" + FormatUtil.formatNum2(total), Element.ALIGN_RIGHT);
    }

    private void addCellWithoutBorderToTable(PdfPTable table, String value, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0);
        table.addCell(cell);
    }

    private void addCellWithTopBorderToTable(PdfPTable table, String value, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0);
        cell.setBorderWidthTop(1);
        table.addCell(cell);
    }
}
