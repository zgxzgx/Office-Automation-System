package cn.edu.shou.missive.domain; /**
 * Created by sqhe on 14/11/6.
 */
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageMerge {

    public static void merge(String firstInputFileName,String scendInputFileName, String outputFileName) {

        if (firstInputFileName == null || scendInputFileName== null || firstInputFileName.equals("")
                || scendInputFileName.equals("")) {
            return;
        }

        BufferedImage outputImg = null;
        BufferedImage appendImg = null;
        int outputImgW = 0;
        int outputImgH = 0;

        try {
            outputImg = ImageIO.read(new File(firstInputFileName));
            outputImgW = outputImg.getWidth();
            outputImgH = outputImg.getHeight();
            appendImg = ImageIO.read(new File(scendInputFileName));
            Graphics2D g2d = outputImg.createGraphics();
            BufferedImage imageNew = g2d.getDeviceConfiguration().createCompatibleImage(outputImgW, outputImgH,
                    Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = imageNew.createGraphics();


            g2d.drawImage(outputImg, 0, 0, outputImg.getWidth(), outputImg.getHeight(), null);
            g2d.drawImage(appendImg, 0, 0, appendImg.getWidth(), appendImg.getHeight(), null);

            g2d.dispose();
            outputImg = imageNew;
            writeImageLocal(outputFileName, outputImg);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static void writeImageLocal(String fileName, BufferedImage image) {
        if (fileName != null && image != null) {
            try {
                File file = new File(fileName);
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
