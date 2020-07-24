package com.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CountdownPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -6925559965795478622L;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private JLabel label;
    private Timer timer;
    private boolean countdownOver = false;

    public CountdownPanel()
    {
        label = new JLabel();
        this.add(label);
        this.timer = new Timer(300, new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent e) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(endTime, now);
            if (duration.getSeconds() >= 0)
            {
                label.setText("00m 00s");
                countdownOver = true;
            }
            else{
                label.setText(formatDate(duration));
            }
           }
        });

        label.setFont(new Font("Arial", Font.PLAIN, 60));
    }

    public boolean isCountdownOver()
    {
        return countdownOver;
    }
    
    public void toggleCountdown(int mins)
    {
        if (timer.isRunning()) {
            timer.stop();
            endTime = null;
        } else {
            endTime = LocalDateTime.now().plusMinutes(mins);
            timer.start();
        }
    }

    private String formatDate(Duration duration)
    {
        return String.format("%02dm %02ds", duration.getSeconds() / 60 * -1, duration.getSeconds() % 60 * -1);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
    }
}