package cn.edu.shou.missive.service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by sqhe on 15/2/3.
 */
public class TrimAlpha {
    private BufferedImage img;

    public TrimAlpha(String base64) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] imageByte = decoder.decodeBuffer(base64.split(",")[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            img = ImageIO.read(bis);
        } catch (IOException e) {
            throw new RuntimeException( "Problem reading image", e );
        }
    }

    public void trim() {
        int width  = getTrimmedWidth();
        int height = getTrimmedHeight();

        int left = getTrimmedleft() ;
        int top = getTrimmedTop() ;


        BufferedImage newImg = new BufferedImage(width-left+1, height-top+1,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImg.createGraphics();
        g.drawImage( img, -left, -top, null );
        img = newImg;
    }

    public String getBase64() {
        try {
            //ImageIO.write(img, "bmp", f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img,"png",baos);
            BASE64Encoder encoder = new BASE64Encoder();
            return "data:image/png;base64,"+encoder.encode(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException( "Problem writing image", e );
        }
    }

    private int getTrimmedWidth() {
        int height       = this.img.getHeight();
        int width        = this.img.getWidth();
        int trimmedWidth = 0;

        for(int i = 0; i < height; i++) {
            for(int j = width - 1; j >= 0; j--) {
                int alpha = (img.getRGB(j, i) >> 24) & 0xff;
                if(alpha != 0 &&
                        j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }

        return trimmedWidth;
    }


    private int getTrimmedleft() {
        int height       = this.img.getHeight();
        int width        = this.img.getWidth();
        int trimmedLeft = width;

        for(int i = 0; i < height; i++) {
            for(int j = 0; j <width ; j++) {
                int alpha = (img.getRGB(j, i) >> 24) & 0xff;
                if(alpha != 0 &&
                        j < trimmedLeft) {
                    trimmedLeft = j;
                    break;
                }
            }
        }

        return trimmedLeft;
    }

    private int getTrimmedHeight() {
        int width         = this.img.getWidth();
        int height        = this.img.getHeight();
        int trimmedHeight = 0;

        for(int i = 0; i < width; i++) {
            for(int j = height - 1; j >= 0; j--) {
                int alpha = (img.getRGB(i, j) >> 24) & 0xff;
                if(alpha != 0 &&
                        j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }

        return trimmedHeight;
    }


    private int getTrimmedTop() {
        int width         = this.img.getWidth();
        int height        = this.img.getHeight();
        int trimmedTop = height;

        for(int i = 0; i < width; i++) {
            for(int j = 0 ; j <height; j++) {
                int alpha = (img.getRGB(i, j) >> 24) & 0xff;
                if(alpha != 0 &&
                        j < trimmedTop) {
                    trimmedTop = j;
                    break;
                }
            }
        }

        return trimmedTop;
    }

}