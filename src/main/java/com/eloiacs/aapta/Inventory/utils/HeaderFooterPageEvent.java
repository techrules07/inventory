package com.eloiacs.aapta.Inventory.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    public void onStartPage(PdfWriter writer, Document document) {
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
//        PdfPTable pdfPTable = new PdfPTable(1);
//        PdfPCell cell = new PdfPCell(new Paragraph("Footer Text"));
//        pdfPTable.addCell(cell);
//
//        try {
//            document.add(pdfPTable);
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
//
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_TOP, new Phrase("Apptas Super Store"), 275, 30, 0);
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
    }

}
