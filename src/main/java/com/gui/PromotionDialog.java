package com.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints;

import javax.swing.JDialog;
import javax.swing.event.MouseInputAdapter;

import com.bl.ImageHandler;

import java.awt.image.BufferedImage;

public class PromotionDialog extends JDialog{

    /**
     *
     */
    private static final long serialVersionUID = 5129146025441758969L;
    private BufferedImage unpromoted;
    private BufferedImage promoted;
    private BoardPanel parent;
    private int index;
    private int result = 0;
 
    public PromotionDialog(String piece, BoardPanel parent, int index)
    {
        this.index = index;
        this.parent = parent;
        this.setUndecorated(true);
        this.setOpacity(0.01f);

        unpromoted = ImageHandler.getPieceforString(piece);
        promoted = ImageHandler.getPieceforString("+" + piece);

        this.setSize(parent.getWidth(), parent.getHeight());
        

        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(parent.getIndexFromPosition(e.getX(), e.getY()) == index - 1 && index % 9 > 4 || parent.getIndexFromPosition(e.getX(), e.getY()) == index + 1 && index % 9 <= 4)
                {
                    result = 1;
                }
                else if(parent.getIndexFromPosition(e.getX(), e.getY()) == index)
                {
                    result = 2;
                }
                else
                {
                    result = 0;
                }
                dispose();
            }
        });

    }

    public int getResult()
    {
        return result;    
    }

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);

        Graphics2D g2 = (Graphics2D) parent.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int x = index % 9 * parent.getWidth() / 10;
        int y = index / 9 * parent.getHeight() / 9;


        g2.setColor(Color.GRAY);
        g2.fillRect(x, y,  parent.getWidth() / 10,  parent.getHeight() / 9);
        g2.fillRect(x + parent.getWidth() / 10 * (x > parent.getWidth() / 2 ? -1 : 1), y,  parent.getWidth() / 10,  parent.getHeight() / 9);

        g2.drawImage(promoted, x, y,  parent.getWidth() / 10,  parent.getHeight() / 9,  parent);
        g2.drawImage(unpromoted, x + parent.getWidth() / 10 * (x > parent.getWidth() / 2 ? -1 : 1), y,  parent.getWidth() / 10,  parent.getHeight() / 9,  parent);
        
    }
}