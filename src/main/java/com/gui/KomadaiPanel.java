package com.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.RenderingHints;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import com.bl.ImageHandler;

public class KomadaiPanel extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1366926969996937934L;
    private static final String[] pieceOrder = { "R", "B", "G", "S", "N", "L", "P" };
    private ArrayList<String> pieces = new ArrayList<>();
    private int pickedupPiece = -1;
    private static final Font font = new Font("Arial", Font.BOLD, 20);

    public KomadaiPanel(String pieceString) {
        fillPieceArray(pieceString);
    }

    private void fillPieceArray(String pieceString) {
        for (int i = 0; i < pieceString.length(); i++) {
            if (Character.isDigit(pieceString.charAt(i))) {
                int amount = Integer.parseInt(pieceString.charAt(i) + "");
                i++;
                if (Character.isDigit(pieceString.charAt(i))) {
                    i++;
                    amount = Integer.parseInt(pieceString.substring(i - 2, i));
                }
                if (Character.isLowerCase(pieceString.charAt(i))) {
                    i--;
                    continue;
                }
                for (int j = 0; j < amount; j++) {
                    pieces.add(pieceString.charAt(i) + "");
                }
            } else if (Character.isLowerCase(pieceString.charAt(i))) {
                continue;
            } else {
                pieces.add(pieceString.charAt(i) + "");
            }
        }
    }

    public void addPiece(String piece) {
        pieces.add(piece);
        repaint();
    }

    public String pickUpPiece(int index) {
        if (pieces.contains(pieceOrder[index])) {
            pickedupPiece = index;
            return pieceOrder[index];
        } else {
            pickedupPiece = -10;
            return "";
        }
    }

    public void layDownPiece()
    {
        pickedupPiece = -1;
    }

    public void drop(String piece) {
        pieces.remove(piece);
        pickedupPiece = -1;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int i = 0; i < pieceOrder.length; i++) {
            if (pieces.contains(pieceOrder[i])) {
                int occurrences = Collections.frequency(pieces, pieceOrder[i]);
                if (pickedupPiece == i) {
                    if (occurrences == 1) {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

                        g2.drawImage(ImageHandler.getPieceforString(pieceOrder[i]), 0, this.getHeight() / 9 * i,
                                this.getWidth(), this.getHeight() / 9, this);

                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    } else {
                        g2.drawImage(ImageHandler.getPieceforString(pieceOrder[i]), 0, this.getHeight() / 9 * i,
                                this.getWidth(), this.getHeight() / 9, this);
                    }
                    occurrences--;
                } else {

                    g2.drawImage(ImageHandler.getPieceforString(pieceOrder[i]), 0, this.getHeight() / 9 * i,
                            this.getWidth(), this.getHeight() / 9, this);
                }

                int x = (int) (this.getWidth() * 0.7);
                int y = (int) (this.getHeight() / 9 * (i + 1) - this.getHeight() / 9 * 0.05);
                drawPieceAmountStr(occurrences, g2, x, y);
            }
        }

    }

    private void drawPieceAmountStr(int occurrences, Graphics2D g2, int x, int y) {
        if (occurrences == 0) {
            return;
        }
        g2.setFont(font);
        String str = "x" + occurrences;

        g2.setColor(Color.BLACK);
        g2.drawString(str, x + 1, y - 1);
        g2.drawString(str, x + 1, y + 1);
        g2.drawString(str, x - 1, y - 1);
        g2.drawString(str, x - 1, y + 1);

        g2.setColor(Color.WHITE);
        g2.drawString(str, x, y);
    }

    
}
