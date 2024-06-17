package org.msBank;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class InvoiceGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Customer Name : ");
        String name = scanner.nextLine();
        System.out.println("Enter Fee amount : ");
        Double fee = scanner.nextDouble();
        System.out.println("Customer Type make a selection : \n 1. Member \n 2. Student \n 3. Others");
        int choice = scanner.nextInt();
        scanner.nextLine();
        String customerType = "";
        while (true) {
            switch (choice) {
                case 1:
                    customerType = "Member";
                    break;
                case 2:
                    customerType = "Student";
                    break;
                case 3:
                    customerType = "Others";
                    break;
                default:
                    System.out.println("Make a valid choice : ");
                    continue;
            }
            break;
        }

        System.out.println("Enter City : ");
        String city = scanner.nextLine();
        System.out.println("Fee Payment mode make a selection : \n 1. Online \n 2. Offline");
        int ch = scanner.nextInt();
        String paymentMode = "";
        while (true) {
            switch (ch) {
                case 1:
                    paymentMode = "Online";
                    break;
                case 2:
                    paymentMode = "Offline";
                    break;
                default:
                    System.out.println("Make a valid choice : ");
                    continue;
            }
            break;
        }
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(name + ".pdf"));
            document.open();

            // Adding invoice title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Adding logo
            Image logo = Image.getInstance("nandi_group_of_companies_india_logo.jpeg");
            logo.scaleAbsolute(100, 80);
            logo.setAlignment(Element.ALIGN_RIGHT);
            document.add(logo);

            // Adding invoice details
            Font detailsFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font headingFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font noteFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Nandi Pipes Badminton Academy", headingFont));
            document.add(new Paragraph("Nandyal", detailsFont));
            document.add(new Paragraph("Email: anamala.nagarjuna9@gmail.com", detailsFont));
            document.add(new Paragraph(" "));

            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingBefore(10f);
            detailsTable.setSpacingAfter(10f);

            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            addDetailsCell(detailsTable, "Bill To:-", PdfPCell.ALIGN_LEFT);
            addDetailsCell(detailsTable, "Invoice No: " + generateInvoiceNumber(), PdfPCell.ALIGN_RIGHT);
            addDetailsCell(detailsTable, "Name: " + name + "\n" + "Type: " + customerType + "\n" + "Place: " + city, PdfPCell.ALIGN_LEFT);
            addDetailsCell(detailsTable, "Invoice Date: " + date, PdfPCell.ALIGN_RIGHT);

            document.add(detailsTable);

            // Adding table header
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table);

            // Adding table rows
            addRow(table, "1", "Monthly Fee\nPaid " + paymentMode, "1", fee.toString(), fee.toString());

            document.add(table);

            // Adding total amount
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(75);
            addTotalRow(totalTable, "", "");
            addTotalRow(totalTable, "Subtotal", fee.toString());
            addTotalRow(totalTable, "Total", fee.toString());
            addTotalRow(totalTable, "Paid (" + date + ")", fee.toString());
            addTotalRow(totalTable, "Balance Due", "0.00");

            document.add(totalTable);

            // Adding notes
            document.add(new Paragraph(" "));
            Paragraph note = new Paragraph("Notes:", headingFont);
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
            Paragraph notesPara1 = new Paragraph("* Please pay every month on or before 5th *", noteFont);
            notesPara1.setAlignment(Element.ALIGN_CENTER);
            document.add(notesPara1);
            Paragraph notePara2 = new Paragraph(" * Thank you  :-) *", noteFont);
            notePara2.setAlignment(Element.ALIGN_CENTER);
            document.add(notePara2);

            // Adding signature
            Image signature = Image.getInstance("signature.png");
            signature.scaleAbsolute(100, 50);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);
            Paragraph signatoryParagraph = new Paragraph("Authorized Signatory", detailsFont);
            signatoryParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(signatoryParagraph);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            document.close();
        }
    }

    private static void addDetailsCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    public static String generateInvoiceNumber() {
        String prefix = "NPBA-";
        Random random = new Random();
        int randomFourDigitNumber = 1000 + random.nextInt(9000);
        return prefix + randomFourDigitNumber;
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("Sl.", "Description", "Qty", "Rate", "Amount").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.BLUE);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        });
    }

    private static void addRow(PdfPTable table, String sl, String description, String qty, String rate, String amount) {
        table.addCell(new PdfPCell(new Phrase(sl)));
        table.addCell(new PdfPCell(new Phrase(description)));
        table.addCell(new PdfPCell(new Phrase(qty)));
        table.addCell(new PdfPCell(new Phrase(rate)));
        table.addCell(new PdfPCell(new Phrase(amount)));
    }

    private static void addTotalRow(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(label));
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}
