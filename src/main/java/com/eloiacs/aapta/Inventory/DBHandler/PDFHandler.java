package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
import com.eloiacs.aapta.Inventory.utils.HeaderFooterPageEvent;
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
    private Font level3Bold = null;
    private Font level4 = null;
    private Font level4Bold = null;
    private Font level6 = null;

    @Value("classpath:Assets/logo.png")
    Resource resource;

    public void generatePDFForOrders(OrderResponse orderResponse) {

        level1 = new Font(Font.FontFamily.COURIER, 13, Font.BOLD);
        level3 = new Font(Font.FontFamily.COURIER, 10);
        level4 = new Font(Font.FontFamily.COURIER, 9);
        level4Bold = new Font(Font.FontFamily.COURIER, 9, Font.BOLD);
        level3Bold = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
        level6 = new Font(Font.FontFamily.TIMES_ROMAN, 5);

        Document document = new Document();
        String fileName = orderResponse.getOrderId().replace("/", "").toLowerCase();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName.toUpperCase() + ".pdf"));
            document.open();
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);


            PdfPTable pdfPTable = new PdfPTable(1);
            pdfPTable.setTotalWidth(TABLE_WIDTH);
            pdfPTable.setLockedWidth(true);
            addHeader(pdfPTable);
            header2Section(pdfPTable);

            addProductTable(pdfPTable, orderResponse);
            addDiscounts(pdfPTable, orderResponse);
            addFooter(pdfPTable);

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

        PdfPCell gst = new PdfPCell(new Paragraph("GST: 33ACGFA9283M1ZX", level3));
        gst.setHorizontalAlignment(Element.ALIGN_RIGHT);
        gst.setBorder(0);

        addressTable.addCell(line1);
        addressTable.addCell(line2);
        addressTable.addCell(phone);
        addressTable.addCell(gst);

        addressText.addElement(addressTable);

        headerTable.addCell(imageCell);
        headerTable.addCell(addressText);

        cell.addElement(headerTable);
        table.addCell(cell);
    }

    public void header2Section(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(20);

        PdfPTable billHeader = new PdfPTable(1);
        billHeader.setTotalWidth(TABLE_WIDTH);
        billHeader.setLockedWidth(true);
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

    public void addProductTable(PdfPTable table, OrderResponse response) {
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setBorder(Rectangle.BOTTOM);
        pdfPCell.setPaddingTop(30);
        try {

            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setLockedWidth(true);
            mainTable.setTotalWidth(TABLE_WIDTH);

            PdfPCell mainTableCell1 = new PdfPCell();
            mainTableCell1.setBorder(0);
            mainTableCell1.setBackgroundColor(new BaseColor(193, 211, 197));
            mainTableCell1.setPaddingBottom(6);


            PdfPTable sectionTable = new PdfPTable(7);
            sectionTable.setLockedWidth(true);

            sectionTable.setTotalWidth(new float[]{35, 170, 70, 65, 60, 65, 85});
            PdfPCell cell1 = new PdfPCell(new Paragraph("Sl", level4Bold));
            cell1.setBorder(0);
            cell1.setPaddingBottom(5);
            cell1.setPaddingTop(5);
            cell1.setBackgroundColor(new BaseColor(193, 211, 197));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell2 = new PdfPCell(new Paragraph("Product", level4Bold));
            cell2.setBackgroundColor(new BaseColor(193, 211, 197));
            cell2.setBorder(0);
            cell2.setPaddingBottom(5);
            cell2.setPaddingTop(5);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell3 = new PdfPCell(new Paragraph("Unit Price", level4Bold));
            cell3.setBackgroundColor(new BaseColor(193, 211, 197));
            cell3.setBorder(0);
            cell3.setPaddingBottom(5);
            cell3.setPaddingTop(5);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell4 = new PdfPCell(new Paragraph("Quantity", level4Bold));
            cell4.setBackgroundColor(new BaseColor(193, 211, 197));
            cell4.setBorder(0);
            cell4.setPaddingBottom(5);
            cell4.setPaddingTop(5);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell5 = new PdfPCell(new Paragraph("Price", level4Bold));
            cell5.setBackgroundColor(new BaseColor(193, 211, 197));
            cell5.setBorder(0);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setPaddingBottom(5);
            cell5.setPaddingTop(5);
            PdfPCell cell6 = new PdfPCell(new Paragraph("Discount", level4Bold));
            cell6.setBackgroundColor(new BaseColor(193, 211, 197));
            cell6.setBorder(0);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setPaddingBottom(5);
            cell6.setPaddingTop(5);
            PdfPCell cell7 = new PdfPCell(new Paragraph("Net Amount", level4Bold));
            cell7.setBackgroundColor(new BaseColor(193, 211, 197));
            cell7.setBorder(0);
            cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell7.setPaddingBottom(5);
            cell7.setPaddingTop(5);

            sectionTable.addCell(cell1);
            sectionTable.addCell(cell2);
            sectionTable.addCell(cell3);
            sectionTable.addCell(cell4);
            sectionTable.addCell(cell5);
            sectionTable.addCell(cell6);
            sectionTable.addCell(cell7);

            mainTableCell1.addElement(sectionTable);

            PdfPCell mainTableCell2 = new PdfPCell();
            mainTableCell2.setBorder(0);

            PdfPTable sectionDynamicValues = new PdfPTable(7);
            sectionDynamicValues.setLockedWidth(true);
            sectionDynamicValues.setTotalWidth(new float[]{35, 170, 70, 65, 60, 65, 85});

            for (int i=0; i<response.getOrderItems().size(); i++) {
                PdfPCell p1 = new PdfPCell(new Paragraph(String.valueOf(i + 1), level4));
                p1.setBorder(0);
                p1.setHorizontalAlignment(Element.ALIGN_CENTER);
                p1.setPaddingTop(5);
                p1.setPaddingBottom(5);
                PdfPCell p2 = new PdfPCell(new Paragraph(response.getOrderItems().get(i).getProductName()  + response.getOrderItems().get(i).getSubCategory() + "-" + response.getOrderItems().get(i).getSize() + response.getOrderItems().get(i).getUnit(), level4));
                p2.setBorder(0);
                p2.setHorizontalAlignment(Element.ALIGN_CENTER);
                p2.setPaddingTop(5);
                p2.setPaddingBottom(5);
                PdfPCell p3 = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().get(i).getUnitPrice()), level4));
                p3.setBorder(0);
                p3.setHorizontalAlignment(Element.ALIGN_CENTER);
                p3.setPaddingTop(5);
                p3.setPaddingBottom(5);
                PdfPCell p4 = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().get(i).getQuantity()), level4));
                p4.setBorder(0);
                p4.setHorizontalAlignment(Element.ALIGN_CENTER);
                p4.setPaddingTop(5);
                p4.setPaddingBottom(5);
                PdfPCell p5 = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().get(i).getTotalAmount()), level4));
                p5.setBorder(0);
                p5.setHorizontalAlignment(Element.ALIGN_CENTER);
                p5.setPaddingTop(5);
                p5.setPaddingBottom(5);
                PdfPCell p6 = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().get(i).getDiscount()), level4));
                p6.setBorder(0);
                p6.setHorizontalAlignment(Element.ALIGN_CENTER);
                p6.setPaddingTop(5);
                p6.setPaddingBottom(5);
                PdfPCell p7 = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().get(i).getTotalAmount()), level4));
                p7.setBorder(0);
                p7.setHorizontalAlignment(Element.ALIGN_CENTER);
                p7.setPaddingTop(5);
                p7.setPaddingBottom(5);

                if (i % 2 == 1) {
                    p1.setBackgroundColor(new BaseColor(217, 217, 217));
                    p2.setBackgroundColor(new BaseColor(217, 217, 217));
                    p3.setBackgroundColor(new BaseColor(217, 217, 217));
                    p4.setBackgroundColor(new BaseColor(217, 217, 217));
                    p5.setBackgroundColor(new BaseColor(217, 217, 217));
                    p6.setBackgroundColor(new BaseColor(217, 217, 217));
                    p7.setBackgroundColor(new BaseColor(217, 217, 217));
                }

                sectionDynamicValues.addCell(p1);
                sectionDynamicValues.addCell(p2);
                sectionDynamicValues.addCell(p3);
                sectionDynamicValues.addCell(p4);
                sectionDynamicValues.addCell(p5);
                sectionDynamicValues.addCell(p6);
                sectionDynamicValues.addCell(p7);
            }



            mainTableCell2.addElement(sectionDynamicValues);

            mainTable.addCell(mainTableCell1);
            mainTable.addCell(mainTableCell2);
            pdfPCell.setPaddingBottom(15);
            pdfPCell.addElement(mainTable);
            table.addCell(pdfPCell);

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDiscounts(PdfPTable table, OrderResponse response) {
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfPCell.setBorder(Rectangle.BOTTOM);
        pdfPCell.setPaddingBottom(20);

        PdfPTable discountTableMain = new PdfPTable(1);
        discountTableMain.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell totalItemsCell = new PdfPCell();
        totalItemsCell.setBorder(0);
        totalItemsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPTable totalItemsTable = new PdfPTable(2);
        totalItemsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalItemsTable.setLockedWidth(true);
        try {
            totalItemsTable.setTotalWidth(new float[]{100, 80});


            PdfPCell staticItem = new PdfPCell(new Paragraph("Total Items", level4));
            staticItem.setBorder(0);
            PdfPCell dynamicCell = new PdfPCell(new Paragraph(String.valueOf(response.getOrderItems().size()), level4Bold));
            dynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            dynamicCell.setBorder(0);

            totalItemsTable.addCell(staticItem);
            totalItemsTable.addCell(dynamicCell);
            totalItemsCell.addElement(totalItemsTable);
            discountTableMain.addCell(totalItemsCell);

            PdfPCell totalAmountCell = new PdfPCell();
            totalAmountCell.setBorder(0);
            totalAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable totalAMountTable = new PdfPTable(2);
            totalAMountTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalAMountTable.setLockedWidth(true);
            totalAMountTable.setTotalWidth(new float[]{100, 80});
            PdfPCell totalAmountStatic = new PdfPCell(new Paragraph("Amount", level4));
            PdfPCell totalAmountDynamic = new PdfPCell(new Paragraph(String.valueOf(response.getTotalUnitPrice()), level4Bold));
            totalAmountDynamic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalAmountStatic.setBorder(0);
            totalAmountDynamic.setBorder(0);

            totalAMountTable.addCell(totalAmountStatic);
            totalAMountTable.addCell(totalAmountDynamic);
            totalAmountCell.addElement(totalAMountTable);
            discountTableMain.addCell(totalAmountCell);

            PdfPCell discountsCell = new PdfPCell();
            discountsCell.setBorder(0);
            discountsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable discountsInnerTable = new PdfPTable(2);
            discountsInnerTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            discountsInnerTable.setLockedWidth(true);
            discountsInnerTable.setTotalWidth(new float[]{100, 80});

            PdfPCell discountsStaticCell = new PdfPCell(new Paragraph("Discounts", level4));
            discountsStaticCell.setBorder(0);
            PdfPCell discountsDynamicCell = new PdfPCell(new Paragraph(String.valueOf(response.getTotalDiscount()), level4Bold));
            discountsDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            discountsDynamicCell.setBorder(0);
            discountsInnerTable.addCell(discountsStaticCell);
            discountsInnerTable.addCell(discountsDynamicCell);
            discountsCell.addElement(discountsInnerTable);
            discountTableMain.addCell(discountsCell);

            PdfPCell beforeTaxMainCell = new PdfPCell();
            beforeTaxMainCell.setBorder(0);
            beforeTaxMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable beforeTaxMainTable = new PdfPTable(2);
            beforeTaxMainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            beforeTaxMainTable.setLockedWidth(true);
            beforeTaxMainTable.setTotalWidth(new float[]{100, 80});
            PdfPCell beforeTaxStaticCell = new PdfPCell(new Paragraph("Before Tax", level4));
            beforeTaxStaticCell.setBorder(0);
            PdfPCell beforeTaxDynamicCell = new PdfPCell(new Paragraph(String.valueOf(0), level4Bold));
            beforeTaxDynamicCell.setBorder(0);
            beforeTaxDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            beforeTaxMainTable.addCell(beforeTaxStaticCell);
            beforeTaxMainTable.addCell(beforeTaxDynamicCell);
            beforeTaxMainCell.addElement(beforeTaxMainTable);
            discountTableMain.addCell(beforeTaxMainCell);

            PdfPCell taxMainCell = new PdfPCell();
            taxMainCell.setBorder(0);
            taxMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable taxMainTable = new PdfPTable(2);
            taxMainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            taxMainTable.setLockedWidth(true);
            taxMainTable.setTotalWidth(new float[]{100, 80});
            PdfPCell taxStaticCell = new PdfPCell(new Paragraph("Tax:", level4));
            taxStaticCell.setBorder(0);
            PdfPCell taxDynamicCell = new PdfPCell(new Paragraph(String.valueOf(0), level4Bold));
            taxDynamicCell.setBorder(0);
            taxDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            taxMainTable.addCell(taxStaticCell);
            taxMainTable.addCell(taxDynamicCell);
            taxMainCell.addElement(taxMainTable);
            discountTableMain.addCell(taxMainCell);


            PdfPCell cgstMainCell = new PdfPCell();
            cgstMainCell.setBorder(0);
            cgstMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable cgstMainTable = new PdfPTable(2);
            cgstMainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cgstMainTable.setLockedWidth(true);
            cgstMainTable.setTotalWidth(new float[]{100, 80});
            PdfPCell cgstStaticCell = new PdfPCell(new Paragraph("CGST:", level4));
            cgstStaticCell.setBorder(0);
            PdfPCell cgstDynamicCell = new PdfPCell(new Paragraph(String.valueOf(0), level4Bold));
            cgstDynamicCell.setBorder(0);
            cgstDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cgstMainTable.addCell(cgstStaticCell);
            cgstMainTable.addCell(cgstDynamicCell);
            cgstMainCell.addElement(cgstMainTable);
            discountTableMain.addCell(cgstMainCell);

            PdfPCell sgstMainCell = new PdfPCell();
            sgstMainCell.setBorder(0);
            sgstMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable sgstMainTable = new PdfPTable(2);
            sgstMainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sgstMainTable.setLockedWidth(true);
            sgstMainTable.setTotalWidth(new float[]{100, 80});
            PdfPCell sgstStaticCell = new PdfPCell(new Paragraph("SGST", level4));
            sgstStaticCell.setBorder(0);
            PdfPCell sgstDynamicCell = new PdfPCell(new Paragraph(String.valueOf(0), level4Bold));
            sgstDynamicCell.setBorder(0);
            sgstDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sgstMainTable.addCell(sgstStaticCell);
            sgstMainTable.addCell(sgstDynamicCell);
            sgstMainCell.addElement(sgstMainTable);
            discountTableMain.addCell(sgstMainCell);


            PdfPCell totalAmountMainCell = new PdfPCell();
            totalAmountMainCell.setBorder(0);
            totalAmountMainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPTable totalAmountMainTable = new PdfPTable(2);
            totalAmountMainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalAmountMainTable.setLockedWidth(true);
            totalAmountMainTable.setTotalWidth(new float[]{100, 80});
            PdfPCell totalStaticCell = new PdfPCell(new Paragraph("Total", level3));
            totalStaticCell.setBorder(0);
            totalStaticCell.setPaddingBottom(8);
//            totalStaticCell.setBackgroundColor(new BaseColor(193, 211, 197));
            PdfPCell totalDynamicCell = new PdfPCell(new Paragraph(String.valueOf(0), level3Bold));
            totalDynamicCell.setBorder(0);
            totalDynamicCell.setPaddingBottom(8);
//            totalDynamicCell.setBackgroundColor(new BaseColor(193, 211, 197));
            totalDynamicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalAmountMainTable.addCell(totalStaticCell);
            totalAmountMainTable.addCell(totalDynamicCell);
            totalAmountMainCell.addElement(totalAmountMainTable);
            discountTableMain.addCell(totalAmountMainCell);


            pdfPCell.addElement(discountTableMain);
            table.addCell(pdfPCell);


        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    public void addFooter(PdfPTable table) {
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setPaddingTop(150);
        pdfPCell.setBorder(0);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPTable footerMain = new PdfPTable(1);
        footerMain.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell appta = new PdfPCell(new Paragraph("Apptas Super Store", level6));
        appta.setBorder(0);
        appta.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell address = new PdfPCell(new Paragraph("#05, Appta Market Campus, NAGERCOIL - 629901", level6));
        address.setHorizontalAlignment(Element.ALIGN_CENTER);
        address.setBorder(0);
        PdfPCell phone = new PdfPCell(new Paragraph("ph: +91-8870880722", level6));
        phone.setHorizontalAlignment(Element.ALIGN_CENTER);
        phone.setBorder(0);

        footerMain.addCell(appta);
        footerMain.addCell(address);
        footerMain.addCell(phone);
        pdfPCell.addElement(footerMain);

        table.addCell(pdfPCell);

    }

}
