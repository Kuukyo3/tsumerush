package com.bl;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class ImageHandler {

    private static BufferedImage shogiBan = null;
    private static Map<String, BufferedImage> pieceImages = new TreeMap<>();
    private static final String[] pieceLetter = {"K", "R", "B", "G", "S", "N", "L", "P", " ", "+R", "+B", " ", "+S", "+N", "+L", "+P"};


    static 
    {
        try {
        
            shogiBan =  ImageIO.read(getInputStream("img/shogiban.png"));
            BufferedImage tmpPieces = ImageIO.read(getInputStream("img/pieces.png"));
            double pieceWidth = tmpPieces.getWidth() / 8;
            double pieceHeight = tmpPieces.getHeight() / 4;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {
                    pieceImages.put(i < 2 ? pieceLetter[8*i + j] : pieceLetter[8*(i - 2) + j].toLowerCase(), tmpPieces.getSubimage((int) pieceWidth * j, (int) pieceHeight * i, (int) pieceWidth , (int) pieceHeight));
                }
            }
           
        } catch (Exception e) {
           System.out.println(e.toString());
        }
    }

    private static BufferedInputStream getInputStream(String resource)
    {
        InputStream input = AudioHelper.class.getResourceAsStream("/res/" + resource);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = AudioHelper.class.getClassLoader().getResourceAsStream(resource);
        }

        return new BufferedInputStream(input);
    }


    public static BufferedImage getBoard()
    {
        return shogiBan;
    }

    public static BufferedImage getPieceforString(String pieceName)
    {
        if(pieceName == null)
        {
            return null;
        }
        return pieceImages.get(pieceName);
    }
}