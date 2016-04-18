package cn.edu.shou.missive.service;


import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.Overlay;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * Created by sqhe on 14-8-25.
 */
@Service
public class PdfService {

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;

    public String overLayerRedTitle(String input,String output )
    {
        PDDocument watermarkDoc = null;
        try {
            watermarkDoc = PDDocument.load(input);
            PDDocument realDoc = PDDocument.load(output);
            Overlay overlay = new Overlay();
            overlay.overlay(watermarkDoc,realDoc);
            String savePath = output.substring(0,output.length()-5)+"1.pdf";
            watermarkDoc.save(savePath);
            return "document-red-title.pdf";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }

        return null;


    }
    public String pdfToText(String pdfFilePath)
    {
        PDDocument pdfDoc = null;
        try {
            pdfDoc = PDDocument.load(pdfFilePath);
            PDFTextStripper stripper = new PDFTextStripper();
            StringWriter textWriter = new StringWriter();
            stripper.writeText(pdfDoc,textWriter);
            String pdftest = textWriter.toString();
            pdfDoc.close();
            return pdftest;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                pdfDoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }





    public void convertImageToPdf(String imagePath,String pdfOutputPath)
    {
        // Create a new empty document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
        document.addPage(page);


        float pageHeight = page.getMediaBox().getHeight();
        float pageWidth = page.getMediaBox().getWidth();
        float smallImageHeight = 0f;
        float smallImageWidth = 0f;


        try {
            BufferedImage temp = ImageIO.read(new File(imagePath));
            float imageHeight = temp.getHeight();
            float imageWidth = temp.getWidth();


            if (imageHeight>(pageHeight-20) && imageWidth>(pageWidth-20))
            {
                if (imageHeight>imageWidth)
                {
                    smallImageHeight = pageHeight-20;
                    smallImageWidth = (pageHeight-20)/imageHeight*imageWidth;
                }else
                {
                    smallImageWidth = pageWidth-20;
                    smallImageHeight = (pageWidth-20)/imageWidth*imageHeight;
                }

            }else if(imageHeight>(pageHeight-20))
            {
                smallImageHeight = pageHeight-20;
                smallImageWidth = (pageHeight-20)/imageHeight*imageWidth;

            }else if(imageWidth>(pageWidth-20))
            {
                smallImageWidth = pageWidth-20;
                smallImageHeight = (pageWidth-20)/imageWidth*imageHeight;
            }else
            {
                smallImageHeight = imageHeight;
                smallImageWidth = imageWidth;
            }


            BufferedImage newImage = Thumbnails.of(temp)
                    .size(Math.round(smallImageWidth), Math.round(smallImageHeight))
                    .asBufferedImage();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(newImage,"jpg",byteArrayOutputStream);

            ByteArrayInputStream imageByteArrayIS = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            PDJpeg img = new PDJpeg(document, imageByteArrayIS);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(img, 10, pageHeight-10-smallImageHeight);
            contentStream.close();
            document.save(pdfOutputPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }

//        return pdfOutputPath;
    }







}
