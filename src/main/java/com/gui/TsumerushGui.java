package com.gui;

import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;

import java.awt.*;

import com.bl.AudioHelper;
import com.bl.TsumeLoader;
import com.bl.TsumePosition;
import com.bl.AudioHelper.AudioFile;

import java.awt.GridLayout;
import java.util.Random;

public class TsumerushGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private BoardPanel boardPanel;
    private ArrayList<TsumePosition> tsumes;
    private int correct, incorrect;
    private AudioInputStream audioCorrect;
    private AudioInputStream audioIncorrect;
    private CountdownPanel countdownPanel;
    private JSpinner minuteSpinner;
    private int time;
    private static final Font font = new Font("Arial", Font.PLAIN, 40);

    public TsumerushGui() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(130 * 6, 145 * 6);

        this.setTitle("Tsumerush v0.1");
        this.setResizable(false);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.incorrect = 0;
        this.correct = 0;
        this.time = 0;
        this.getContentPane().setLayout(new GridLayout(3, 4));
        for (int i = 0; i < 8; i++) {
            JButton btTmp = new JButton(i * 2 + 1 + "");
            btTmp.setFont(font);
            btTmp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonClicked(e);
                }
            });
            this.getContentPane().add(btTmp);
        }
        minuteSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 180, 1));
        minuteSpinner.setFont(font);
        this.add(minuteSpinner);
        this.setVisible(true);

    }

    private void setNewBoard() {

        Container contentPane = this.getContentPane();

        contentPane.removeAll();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);

        // countdownPanel.setPreferredSize(new Dimension((int) (parentHeight*0.1), (int)
        // parentWidth));
        contentPane.add(countdownPanel, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weighty = 1;
        c.weightx = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);

        // boardPanel.setPreferredSize(new Dimension((int) (parentHeight*0.9), (int)
        // parentWidth));
        contentPane.add(boardPanel, c);

        // mainPanel.setSize(width, height);

    }

    private void buttonClicked(ActionEvent e) {
        this.countdownPanel = new CountdownPanel();

        TsumeLoader loader = new TsumeLoader();
        this.tsumes = loader.getTsume(Integer.parseInt(((JButton) e.getSource()).getText()));

        Random rand = new Random();
        int randomInt = rand.nextInt(tsumes.size());
        this.boardPanel = new BoardPanel(tsumes.get(randomInt).getSfen(), tsumes.get(randomInt).getSolutionMoves());
        setNewBoard();
        countdownPanel.toggleCountdown(Integer.parseInt(minuteSpinner.getValue().toString()));
        this.time = Integer.parseInt(minuteSpinner.getValue().toString()) * 60;
        Thread t = new Thread(new ThreadLoop(this));
        t.start();
    }

    private void playSound(boolean isCorrect) {
        try {
            audioCorrect = AudioSystem.getAudioInputStream(AudioHelper.accessFile(AudioFile.CORRECT));
            audioIncorrect = AudioSystem.getAudioInputStream(AudioHelper.accessFile(AudioFile.INCORRECT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(50);
            Clip clip = AudioSystem.getClip();
            clip.open(isCorrect ? audioCorrect : audioIncorrect);
            clip.loop(0);
            Thread.sleep(1200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showResult() {
        Container contentPane = this.getContentPane();

        contentPane.removeAll();

        contentPane.setLayout(new GridLayout(6, 1));
        JLabel lb = new JLabel("Correct: " + correct);
        lb.setFont(font);
        contentPane.add(lb);

        lb = new JLabel("Incorrect: " + incorrect);
        lb.setFont(font);
        contentPane.add(lb);

        lb = new JLabel("Total: " + (incorrect + correct));
        lb.setFont(font);
        contentPane.add(lb);

        lb = new JLabel(String.format("Percentage correct: %.2f%%", (correct / ((float) incorrect + correct) * 100) ));
        lb.setFont(font);
        contentPane.add(lb);

        lb = new JLabel(String.format("Avg. time per tsume: %.2f seconds", (time / (double) (incorrect + correct)) ));
        lb.setFont(font);
        contentPane.add(lb);

        JButton bt = new JButton("Back to Menu");
        bt.setFont(font);
        bt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
              getContentPane().removeAll();
              start();
              validate();
              repaint();
            }
        });
        contentPane.add(bt);

        AudioInputStream victory = null;
        try {
            victory = AudioSystem.getAudioInputStream(AudioHelper.accessFile(AudioFile.VICTORY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(victory);
            clip.loop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ThreadLoop implements Runnable {
        private TsumerushGui parent;

        public ThreadLoop(TsumerushGui parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (parent.boardPanel) {
                    if (parent.boardPanel.isTsumeOver()) {
                        if (parent.boardPanel.isCorrect())
                            correct++;
                        else
                            incorrect++;
                        playSound(parent.boardPanel.isCorrect());

                        Random rand = new Random();
                        int randomInt = rand.nextInt(tsumes.size());
                        parent.remove(parent.boardPanel);
                        parent.boardPanel = new BoardPanel(tsumes.get(randomInt).getSfen(),
                                tsumes.get(randomInt).getSolutionMoves());

                        GridBagConstraints c = new GridBagConstraints();
                        c.fill = GridBagConstraints.BOTH;
                        c.gridx = 0;
                        c.weighty = 1;
                        c.weightx = 1;
                        c.gridy = 1;
                        c.insets = new Insets(0, 0, 0, 0);
                        parent.add(parent.boardPanel, c);
                        parent.validate();
                        parent.repaint();
                    }
                    if (countdownPanel.isCountdownOver()) {
                        parent.showResult();
                        parent.validate();
                        parent.repaint();
                        return;
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
        TsumerushGui gui = new TsumerushGui();
        gui.start();

    }

}