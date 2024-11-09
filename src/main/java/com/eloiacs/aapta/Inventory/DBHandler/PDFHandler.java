package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PDFHandler {

    private int TABLE_WIDTH = 550;
    private Font level1 = null;
    private Font level3 = null;
    private Font level4 = null;

    @Value("classpath:Assets/logo.png")
    Resource resource;

    public void generatePDFForOrders(OrderResponse orderResponse) {

        level1 = new Font(Font.FontFamily.COURIER, 13, Font.BOLD);
        level3 = new Font(Font.FontFamily.COURIER, 10);
        level4 = new Font(Font.FontFamily.COURIER, 9);

        Document document = new Document();
        String fileName = orderResponse.getOrderId().replace("/", "").toLowerCase();

        try {
            PdfWriter.getInstance(document, new FileOutputStream( fileName.toUpperCase() + ".pdf"));

            document.open();

            PdfPTable pdfPTable = new PdfPTable(1);
            pdfPTable.setTotalWidth(TABLE_WIDTH);
            pdfPTable.setLockedWidth(true);
            addHeader(pdfPTable);
            header2Section(pdfPTable);

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    public void addHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setLockedWidth(true);
        headerTable.setTotalWidth(TABLE_WIDTH);

        PdfPCell imageCell = new PdfPCell();
        imageCell.setBorder(0);
        PdfPCell addressText = new PdfPCell();
        addressText.setBorder(0);
        File file = null;

        try {
            file = resource.getFile();
            Image img = Image.getInstance(file.getAbsolutePath());
            img.setWidthPercentage(30);

            imageCell.addElement(img);
            imageCell.setBorder(0);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadElementException e) {
            throw new RuntimeException(e);
        }

        PdfPTable addressTable = new PdfPTable(1);
        addressTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell line1 = new PdfPCell(new Paragraph("#05, Appta Market Campus", level3));
        line1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        line1.setBorder(0);
        PdfPCell line2 = new PdfPCell(new Paragraph("NAGERCOIL - 629901", level3));
        line2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        line2.setBorder(0);

        PdfPCell phone = new PdfPCell(new Paragraph("ph: +91-8870880722", level3));
        phone.setHorizontalAlignment(Element.ALIGN_RIGHT);
        phone.setBorder(0);

        addressTable.addCell(line1);
        addressTable.addCell(line2);
        addressTable.addCell(phone);

        addressText.addElement(addressTable);

        headerTable.addCell(imageCell);
        headerTable.addCell(addressText);

        cell.addElement(headerTable);
        table.addCell(cell);
    }

    public void header2Section(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPaddingTop(10);

        PdfPTable billHeader = new PdfPTable(1);
        PdfPCell cashBill = new PdfPCell(new Paragraph("Cash Bill", level1));
        cashBill.setBorder(0);
        cashBill.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(0);
        detailsCell.setPaddingTop(15);
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setTotalWidth(TABLE_WIDTH);
        detailsTable.setLockedWidth(true);

        PdfPCell invoiceDetails = new PdfPCell();
        invoiceDetails.setBorder(0);
        invoiceDetails.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPTable invoiceTable = new PdfPTable(1);
        invoiceTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(0);
        invoiceCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPTable invoiceDetailsTable = new PdfPTable(2);
        invoiceDetailsTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell staticCell = new PdfPCell(new Paragraph("Invoice No", level4));
        staticCell.setBorder(0);
        staticCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell dynamicValue = new PdfPCell(new Paragraph("100", level4));
        dynamicValue.setBorder(0);
        dynamicValue.setHorizontalAlignment(Element.ALIGN_LEFT);

        invoiceDetailsTable.addCell(staticCell);
        invoiceDetailsTable.addCell(dynamicValue);

        invoiceCell.addElement(invoiceDetailsTable);


        PdfPTable customerDetailsTable = new PdfPTable(1);
        customerDetailsTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell customerDetailsCell = new PdfPCell();
        customerDetailsCell.setBorder(0);
        customerDetailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPTable customerDataTable = new PdfPTable(2);
        customerDataTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell staticDataCell = new PdfPCell(new Paragraph("Customer:", level4));
        staticDataCell.setBorder(0);
        staticDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell dynamicDataCell = new PdfPCell(new Paragraph("Sujith", level4));
        dynamicDataCell.setBorder(0);
        dynamicDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        customerDataTable.addCell(staticDataCell);
        customerDataTable.addCell(dynamicDataCell);

        customerDetailsCell.addElement(customerDataTable);
        customerDetailsTable.addCell(customerDetailsCell);

        invoiceCell.addElement(customerDetailsTable);



        invoiceTable.addCell(invoiceCell);
        invoiceDetails.addElement(invoiceTable);


        PdfPCell dateCell = new PdfPCell();
        dateCell.setBorder(0);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPTable pdfDateTable = new PdfPTable(1);
        pdfDateTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell dateMainCell = new PdfPCell();
        dateMainCell.setBorder(0);
        dateMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPTable dateContentTable = new PdfPTable(2);
        dateContentTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell staticDateCell = new PdfPCell(new Paragraph("Date", level4));
        staticDateCell.setBorder(0);
        staticDateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell dynamicDateCell = new PdfPCell(new Paragraph("09/11/2024", level4));
        dynamicDateCell.setBorder(0);
        dynamicDateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dateContentTable.addCell(staticDateCell);
        dateContentTable.addCell(dynamicDateCell);

        dateContentTable.addCell(staticDateCell);
        dateMainCell.addElement(dateContentTable);
        pdfDateTable.addCell(dateMainCell);

        PdfPTable timeTableMain = new PdfPTable(1);
        timeTableMain.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell timeTableMainCell = new PdfPCell();
        timeTableMainCell.setBorder(0);
        timeTableMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPTable timeTableContent = new PdfPTable(2);
        timeTableContent.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell timeCellStatic = new PdfPCell(new Paragraph("Time", level4));
        timeCellStatic.setBorder(0);
        timeCellStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell timeCellDynamic = new PdfPCell(new Paragraph("01:05 AM", level4));
        timeCellDynamic.setBorder(0);
        timeCellDynamic.setHorizontalAlignment(Element.ALIGN_RIGHT);

        timeTableContent.addCell(timeCellStatic);
        timeTableContent.addCell(timeCellDynamic);
        timeTableMainCell.addElement(timeTableContent);
        timeTableMain.addCell(timeTableMainCell);




        dateCell.addElement(pdfDateTable);
        dateCell.addElement(timeTableMain);


        detailsTable.addCell(invoiceDetails);
        detailsTable.addCell(dateCell);

        detailsCell.setBorder(0);
        detailsCell.addElement(detailsTable);


        billHeader.addCell(cashBill);
        billHeader.addCell(detailsCell);
        cell.addElement(billHeader);
        table.addCell(cell);
    }

}
