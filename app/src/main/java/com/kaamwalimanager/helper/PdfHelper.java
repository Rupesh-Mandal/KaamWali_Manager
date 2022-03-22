package com.kaamwalimanager.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.kaamwalimanager.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PdfHelper {
    Context context;

    public PdfHelper(Context context) {
        this.context = context;
    }

    public void creatTraxinvoice() throws FileNotFoundException {
        String myPath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file=new File(myPath,"tax invoice.pdf");

        OutputStream outputStream=new FileOutputStream(file);

        PdfWriter writer=new PdfWriter(String.valueOf(file));
        PdfDocument pdfDocument=new PdfDocument(writer);
        Document document=new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);
        document.setMargins(10,10,10,10);

        Drawable d= context.getDrawable(R.drawable.logo);
        Bitmap bitmap=((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] bitmapData= stream.toByteArray();
        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image=new Image(imageData).setWidth(100f).setHeight(100f).setHorizontalAlignment(HorizontalAlignment.LEFT);

        Paragraph title=new Paragraph("Tax Invoice").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER);
        Paragraph subTitle=new Paragraph("(ORIGINAL FOR RECIPIENT FOR SERVICE)").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);

        float[] invoiceDateTableWith={100f,100f};
        Table invoiceDateTable=new Table(invoiceDateTableWith);
        invoiceDateTable.addCell(new Cell().add(new Paragraph("Invoice_No.").setBold()));
        invoiceDateTable.addCell(new Cell().add(new Paragraph("Invoice Date").setBold()));
        invoiceDateTable.addCell(new Cell().add(new Paragraph("PPL/2122002696")));
        invoiceDateTable.addCell(new Cell().add(new Paragraph("10/09/2021")));
        invoiceDateTable.addCell(new Cell(1,2).add(new Paragraph("10/09/2021 To 10/09/2021")));


        Paragraph frName=new Paragraph("KING WORLDWIDE COURIER").setBold().setFontSize(20).setTextAlignment(TextAlignment.LEFT);
        Paragraph cuName=new Paragraph("RUPESH KUMAR MANDAL").setBold().setFontSize(20).setTextAlignment(TextAlignment.LEFT);

        Paragraph frAddress=new Paragraph("5/19, 405-410 SHRI SADGURU JANGALI MAHARAJ SOCIETY SENAPATI BAPAT ROAD, SHIVAJI NAGAR MUMBAI MAHARASHTRA-400016").setTextAlignment(TextAlignment.LEFT);
        Paragraph cuAddress=new Paragraph("GROUND FLOOR, BUILDING NO-3,UNIT NO-4, SONA UDYOG INDUSTRIAL ESTATE, PARSI PANCHAYAT ROAD,ANDHERI MUMBAI").setTextAlignment(TextAlignment.LEFT);

        Paragraph frPhone=new Paragraph("9503249524").setTextAlignment(TextAlignment.LEFT);
        Paragraph cuPhone=new Paragraph("9503249524").setTextAlignment(TextAlignment.LEFT);

        Paragraph frGST=new Paragraph("GST No: 27AWYPN5069J1ZJ").setBold().setTextAlignment(TextAlignment.LEFT);
        Paragraph cuGST=new Paragraph("GST No: 27AWYPN5069J1ZJ").setBold().setTextAlignment(TextAlignment.LEFT);



        float[] persionWith={300f,300f,300f,300f};
        Table persionTable=new Table(persionWith);
        persionTable.addCell(new Cell(1,2).add(frName).add(frAddress).add(frPhone).add(frGST));
        persionTable.addCell(new Cell(1,2).add(cuName).add(cuAddress).add(cuPhone).add(cuGST));



        float[] headerWith={300f,300f,300f,300f,300f};
        Table headerTable=new Table(headerWith);
        headerTable.addCell(new Cell(1,1).add(image));
        headerTable.addCell(new Cell(1,3).add(title).add(subTitle).setVerticalAlignment(VerticalAlignment.MIDDLE));
        headerTable.addCell(new Cell().add(invoiceDateTable).setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER));
        headerTable.addCell(new Cell(1,5).add(persionTable));



        float[] dataWith={100f,100f,100f,100f,100f,100f,100f,100f,100f,100f,100f,100f,100f};
        Table dataTable=new Table(dataWith).setMargins(10,0,0,0);
        dataTable.addCell(new Cell().add(new Paragraph("SrNo")));
        dataTable.addCell(new Cell().add(new Paragraph("Date")));
        dataTable.addCell(new Cell().add(new Paragraph("AwbNo.")));
        dataTable.addCell(new Cell().add(new Paragraph("Service")));
        dataTable.addCell(new Cell().add(new Paragraph("Zone")));
        dataTable.addCell(new Cell().add(new Paragraph("Destination")));
        dataTable.addCell(new Cell().add(new Paragraph("Consignee")));
        dataTable.addCell(new Cell().add(new Paragraph("Vender AWB")));
        dataTable.addCell(new Cell().add(new Paragraph("D/S")));
        dataTable.addCell(new Cell().add(new Paragraph("Weight")));
        dataTable.addCell(new Cell().add(new Paragraph("Amount")));
        dataTable.addCell(new Cell().add(new Paragraph("Fuel")));
        dataTable.addCell(new Cell().add(new Paragraph("Total")));


     dataTable.addCell(new Cell().add(new Paragraph("1")));
        dataTable.addCell(new Cell().add(new Paragraph("2022-02-04")));
        dataTable.addCell(new Cell().add(new Paragraph("325435")));
        dataTable.addCell(new Cell().add(new Paragraph("Aremx")));
        dataTable.addCell(new Cell().add(new Paragraph("H")));
        dataTable.addCell(new Cell().add(new Paragraph("Nepal")));
        dataTable.addCell(new Cell().add(new Paragraph("fdgvdfhcvb")));
        dataTable.addCell(new Cell().add(new Paragraph("Vender AWB")));
        dataTable.addCell(new Cell().add(new Paragraph("D/S")));
        dataTable.addCell(new Cell().add(new Paragraph("Weight")));
        dataTable.addCell(new Cell().add(new Paragraph("Amount")));
        dataTable.addCell(new Cell().add(new Paragraph("Fuel")));
        dataTable.addCell(new Cell().add(new Paragraph("Total")));




        document.add(headerTable);
        document.add(dataTable);


        document.close();
        Toast.makeText(context, "Please check Download folder for pdf", Toast.LENGTH_LONG).show();

    }
}
