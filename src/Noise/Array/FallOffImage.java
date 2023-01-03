package Noise.Array;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FallOffImage {
    BufferedImage bi;

    int width = 200;
    int height = 200;

    public FallOffImage()
    {
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        initImage();
    }

    public void initImage()
    {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//                float x = i / (float)width * 2 - 1;
//                float y = j / (float)height * 2 - 1;
//
//                float value = Math.max(Math.abs(x), Math.abs(y));
//                bi.setRGB(i, j, new Color(value, value, value).getRGB());
                float value = (float)Math.exp(
                        -Math.max(Math.abs(i - width / 2), Math.abs(j - height / 2))
                        *
                        Math.max(Math.abs(i - width / 2), Math.abs(j - height / 2))/2000f
                );
                System.out.println(value);
                bi.setRGB(i, j, new Color(value, value, value).getRGB());
            }
        }
    }

    public void saveMapImage()
    {
        try {
            File outfile = new File("falloff1.png");
            ImageIO.write(bi, "png", outfile);
        } catch (IOException e) {
            // handle exception
        }
    }

    public static void main(String[] args)
    {
        FallOffImage foi = new FallOffImage();

        foi.saveMapImage();

        long start = System.currentTimeMillis();

        for (int i = 0; i < 1_000_000_0; i++) {
            Math.pow(1.1, 2.2);
        }
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000_0; i++) {
            Math.atan(2.2);
        }
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000_0; i++) {
            Math.pow(1.1, 2.2);
        }
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000_0; i++) {
            Math.atan(2.2);
        }
        System.out.println(System.currentTimeMillis() - start);




    }
}
