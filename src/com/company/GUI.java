package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

// меню, появляющееся при запуске игры, содержащее на данном этапе лишь кнопку "Играть"
public class GUI extends JFrame {
    public GUI()
    {
        super("Дурак (меню)");
        Container container = this.getContentPane();
        this.setResizable(false);
        this.setBounds(700, 120, 400, 600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        container.setLayout(new GridLayout(2,1, 10, 10));
        JLabel label = new JLabel("");
        label.setIcon(new ImageIcon("src/Cards/logo.png"));
        JButton button0 = new JButton("Играть");
        button0.addActionListener(new Button0EventListener());
        container.add(label);
        container.add(button0);
    }
}

class Button0EventListener implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Game app1 = null;
        try {
            app1 = new Game();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        app1.setVisible(true);
    }
}

