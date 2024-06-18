package com.example;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class InvoiceGenerator {
    private static final Scanner scanner = new Scanner(System.in);

    static int invoiceCounter = 1;

    public static void main(String[] args) {
        List<InvoiceDetails> invoiceDetailsList = new ArrayList<>();
        while (true) {
            InvoiceDetails invoiceDetails = getInvoiceDetails();
            invoiceDetailsList.add(invoiceDetails);

            System.out.println("Want to enter another Customer details? \nPress 1 for 'YES' 2 for 'NO'");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                continue;
            } else if (choice == 2) {
                double total = 0;
                for (InvoiceDetails details : invoiceDetailsList) {
                    total += details.getFee();
                }
                System.out.println("Ending Invoice generation...! \nTotal fee collected = " + total);
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
        scanner.close();
    }

    private static InvoiceDetails getInvoiceDetails() {
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        System.out.println("Enter Customer Name : ");
        invoiceDetails.setCustomerName(scanner.nextLine().trim());
        while (invoiceDetails.getCustomerName().isEmpty()) {
            System.out.println("Customer name should not be empty !!");
            System.out.println("Enter Customer Name : ");
            invoiceDetails.setCustomerName(scanner.nextLine().trim());
        }

        System.out.println("Enter Fee amount : ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid fee amount. Please enter a valid number:");
            scanner.next();
        }
        invoiceDetails.setFee(scanner.nextDouble());
        scanner.nextLine();

        System.out.println("Customer Type make a selection : \n 1. Member \n 2. Student \n 3. Others");
        int choice = scanner.nextInt();
        scanner.nextLine();
        while (true) {
            switch (choice) {
                case 1:
                    invoiceDetails.setCustomerType("Member");
                    break;
                case 2:
                    invoiceDetails.setCustomerType("Student");
                    break;
                case 3:
                    invoiceDetails.setCustomerType("Others");
                    break;
                default:
                    System.out.println("Make a valid choice : ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    continue;
            }
            break;
        }

        System.out.println("Enter City : ");
        invoiceDetails.setCity(scanner.nextLine().trim());

        System.out.println("Fee Payment mode make a selection : \n 1. Online \n 2. Offline");
        int ch = scanner.nextInt();
        scanner.nextLine();
        while (true) {
            switch (ch) {
                case 1:
                    invoiceDetails.setPaymentMode("Online");
                    break;
                case 2:
                    invoiceDetails.setPaymentMode("Offline");
                    break;
                default:
                    System.out.println("Make a valid choice : ");
                    ch = scanner.nextInt();
                    scanner.nextLine();
                    continue;
            }
            break;
        }

        Document document = null;
        try {
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(invoiceDetails.getCustomerName() + ".pdf"));
            document.open();

            // Adding invoice title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Adding logo
            Image logo = Image.getInstance("src/main/resources/nandi_group_of_companies_india_logo.jpeg");
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
            addDetailsCell(detailsTable, "Invoice No: " + getNextInvoiceNumber(), PdfPCell.ALIGN_RIGHT);
            addDetailsCell(detailsTable, "Name: " + invoiceDetails.getCustomerName() + "\n" + "Type: " + invoiceDetails.getCustomerType() + "\n" + "Place: " + invoiceDetails.getCity(), PdfPCell.ALIGN_LEFT);
            addDetailsCell(detailsTable, "Invoice Date: " + date, PdfPCell.ALIGN_RIGHT);

            document.add(detailsTable);

            // Adding table header
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table);

            // Adding table rows
            addRow(table, "1", "Monthly Fee\nPaid " + invoiceDetails.getPaymentMode(), "1", String.valueOf(invoiceDetails.getFee()), String.valueOf(invoiceDetails.getFee()));

            document.add(table);

            // Adding total amount
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(75);
            addTotalRow(totalTable, "", "");
            addTotalRow(totalTable, "Subtotal", String.valueOf(invoiceDetails.getFee()));
            addTotalRow(totalTable, "Total", String.valueOf(invoiceDetails.getFee()));
            addTotalRow(totalTable, "Paid (" + date + ")", String.valueOf(invoiceDetails.getFee()));
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
            Image signature = Image.getInstance("src/main/resources/signature.png");
            signature.scaleAbsolute(100, 50);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);
            Paragraph signatoryParagraph = new Paragraph("Authorized Signatory", detailsFont);
            signatoryParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(signatoryParagraph);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }

        return invoiceDetails;
    }

    private static void addDetailsCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    public static String getNextInvoiceNumber() {
        int PADDING = 4;
        String PREFIX = "NPBA-";
        return PREFIX + String.format("%0" + PADDING + "d", invoiceCounter++);
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
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(value));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private static class InvoiceDetails {
        private String customerName;
        private double fee;
        private String customerType;
        private String city;
        private String paymentMode;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }
    }
}
