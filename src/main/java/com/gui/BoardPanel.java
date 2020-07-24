package com.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.bl.AudioHelper;
import com.bl.ImageHandler;
import com.bl.AudioHelper.AudioFile;

public class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private ArrayList<String> position = new ArrayList<>();
    private KomadaiPanel komadaiPanel = null;
    private String mousePiece = null;
    private int originalIndex = -10;
    private int mouseX, mouseY;
    private int currentMove = -1;
    private String[] solutionMoves;
    private boolean tsumeOver = false;
    private boolean correct = false;
    private int highlightIndex = -1;
    private boolean highlight = false;

    public BoardPanel(String sfen, String[] moves) {
        solutionMoves = moves;

        komadaiPanel = new KomadaiPanel(sfen.split(" ")[2]);
        fillPositionArray(sfen.split(" ")[0]);
        // this.add(komadaiPanel);

        this.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                pieceClicked(e);
            }
        });

        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        this.add(Box.createHorizontalGlue(), c);

        c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;

        c.weighty = 1;
        c.weightx = 0.1;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        this.add(komadaiPanel, c);

        this.addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }

        });
    }

    private void resetPickedUpPiece() {
        mousePiece = "";
        originalIndex = -10;
        komadaiPanel.layDownPiece();
    }

    private void pieceClicked(MouseEvent e) {
        int index = getIndexFromPosition(e.getX(), e.getY());
        if (SwingUtilities.isRightMouseButton(e)) {
            resetPickedUpPiece();
            repaint();
            return;
        }
        if (index >= 0) {
            if (mousePiece != null && mousePiece != "") {
                String nextMove = solutionMoves[currentMove + 1];
                if (Character.isDigit(nextMove.charAt(0))) {
                    if (decodeMoveCoords(nextMove.substring(0, 2)) == originalIndex
                            && decodeMoveCoords(nextMove.substring(2)) == index) {
                        position.set(originalIndex, "");
                        if (position.get(index) != "") {
                            komadaiPanel.addPiece(position.get(index).toUpperCase());
                        }
                        position.set(index, mousePiece);
                        String tmpPiece = mousePiece;
                        mousePiece = "";
                        repaint();
                        if ((index < 3 * 9 || originalIndex < 3 * 9) && tmpPiece.charAt(0) != '+'
                                && tmpPiece.charAt(0) != 'G' && tmpPiece.charAt(0) != 'K') {
                            int result = piecePromotionDialog(tmpPiece, index);
                            if (nextMove.substring(2).contains("+")) {
                                if (result == 2) {
                                    position.set(index, "+" + tmpPiece);
                                } else {
                                    position.set(index, "");
                                    position.set(originalIndex, tmpPiece);

                                    setCorrect(false);
                                    tsumeOver = true;
                                    resetPickedUpPiece();
                                    return;
                                }
                            } else {
                                if (result == 2) {
                                    position.set(index, "");
                                    position.set(originalIndex, tmpPiece);

                                    setCorrect(false);
                                    tsumeOver = true;
                                    resetPickedUpPiece();
                                    return;
                                }
                            }

                        }

                        resetPickedUpPiece();

                        currentMove++;
                        highlightIndex = index;
                        setCorrect(true);
                        highlight = true;
                        repaint();
                        playReply();

                        return;
                    } else {
                        playSound();
                        setCorrect(false);
                        tsumeOver = true;
                        highlight = true;
                        highlightIndex = index;
                        repaint();
                    }
                } else {

                    int dropTo = decodeMoveCoords(nextMove.substring(2));
                    if (index == dropTo) {
                        setCorrect(true);
                        position.set(dropTo, nextMove.charAt(0) + "");
                        komadaiPanel.drop(nextMove.charAt(0) + "");
                        resetPickedUpPiece();
                        repaint();
                        currentMove++;
                        highlight = true;
                        highlightIndex = index;
                        playReply();
                        return;
                    } else {
                        setCorrect(false);
                        tsumeOver = true;
                        highlight = true;
                        highlightIndex = index;
                        repaint();
                    }

                }
            }
            if (position.get(index) != "" && isSente(position.get(index))) {
                mousePiece = position.get(index);
                originalIndex = index;

                komadaiPanel.layDownPiece();

                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            } else {
                resetPickedUpPiece();
                repaint();
            }
        } else if (index > -9) {

            originalIndex = index;
            mousePiece = komadaiPanel.pickUpPiece(index * -1 - 1);
            repaint();
        }
    }

    private int piecePromotionDialog(String piece, int index) {
        PromotionDialog dialog = new PromotionDialog(piece, this, index);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.repaint();

        return dialog.getResult();
    }

    private void playSound() {
        AudioInputStream moveSound = null;
        try {
            moveSound = AudioSystem.getAudioInputStream(AudioHelper.accessFile(AudioFile.SNAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(moveSound);
            clip.loop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playReply() {
        if (solutionMoves.length > currentMove + 1) {
            repaint();
            playSound();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    playMove(solutionMoves[currentMove + 1]);
                    playSound();
                    highlight = false;
                    repaint();
                    currentMove++;
                }
            }, 250);

        } else {
            playSound();
            setCorrect(true);
            tsumeOver = true;

            return;
        }
    }

    private void playMove(String move) {
        if (move.contains("*")) {
            int to = decodeMoveCoords(move.substring(2));

            position.set(to, (move.charAt(0) + "").toLowerCase());
        } else {
            int from = decodeMoveCoords(move.substring(0, 2));
            int to = decodeMoveCoords(move.substring(2));

            position.set(to, position.get(from));
            position.set(from, "");
        }

        repaint();

    }

    private int decodeMoveCoords(String coords) {
        int column = 9 - Integer.parseInt(coords.charAt(0) + "");
        char rowC = coords.charAt(1);
        int row = rowC - 'a';

        return row * 9 + column;
    }

    private boolean isSente(String piece) {
        if (piece.charAt(0) == '+') {
            return Character.isUpperCase(piece.charAt(1));
        } else {
            return Character.isUpperCase(piece.charAt(0));
        }
    }

    private void fillPositionArray(String positionStr) {

        for (String element : positionStr.split("/", 9)) {
            for (int i = 0; i < element.length(); i++) {
                if (Character.isDigit(element.charAt(i))) {
                    for (int j = 0; j < Integer.parseInt(element.charAt(i) + ""); j++) {
                        position.add("");
                    }
                } else {
                    if (element.charAt(i) == '+') {
                        position.add(element.charAt(i) + "" + element.charAt(i + 1));
                        i++;
                    } else {
                        position.add(element.charAt(i) + "");
                    }
                }
            }
        }
    }

    public int getIndexFromPosition(int x, int y) {
        int column = x / (this.getWidth() / 10);
        int row = y / (this.getHeight() / 9);
        if (column == 9) {
            return -row - 1;
        }
        return row * 9 + column;
    }

    private void hightlightResult(Graphics2D g2) {
        double boardWidth = this.getWidth() * 0.9;
        g2.setColor(correct ? Color.GREEN : Color.RED);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect((int) (boardWidth / 9 * (highlightIndex % 9)),
                (int) ((double) this.getHeight() / 9 * (highlightIndex / 9)), (int) (boardWidth / 9),
                (int) (this.getHeight() / 9));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        double boardWidth = (double) this.getWidth() * 0.9;

        g2.drawImage(ImageHandler.getBoard(), 0, 0, (int) boardWidth, (int) this.getHeight(), this);

        for (int i = 0; i < position.size(); i++) {
            if (originalIndex == i) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.drawImage(ImageHandler.getPieceforString(position.get(i)), (int) (boardWidth / 9 * (i % 9)),
                        this.getHeight() / 9 * (i / 9), (int) (boardWidth / 9), (int) (this.getHeight() / 9), this);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            } else {
                g2.drawImage(ImageHandler.getPieceforString(position.get(i)), (int) (boardWidth / 9 * (i % 9)),
                        this.getHeight() / 9 * (i / 9), (int) (boardWidth / 9), (int) (this.getHeight() / 9), this);
            }

        }

        if (mousePiece != null && mousePiece != "") {
            g2.drawImage(ImageHandler.getPieceforString(mousePiece), (int) (mouseX - boardWidth / 9 * 0.5),
                    (int) (mouseY - this.getHeight() / 9 * 0.5), (int) boardWidth / 9, (int) this.getHeight() / 9,
                    this);
        }
        if (highlight) {
            hightlightResult(g2);
        }

    }

    public boolean isTsumeOver() {
        return tsumeOver;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        System.out.println("Correct: " + correct);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.correct = correct;
    }

}
