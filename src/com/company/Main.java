package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    public static void main(String[] args)
    {
        Window app = new Window("Дурак (меню)", "Играть");
        app.setVisible(true);
    }
}

class Window extends JFrame {
    private static String Super;
    private static String text;
    public Window(String Super, String text)
    {
        super(Super);
        Container container = this.getContentPane();
        this.setResizable(false);
        this.setBounds(700, 120, 400, 600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        container.setLayout(new GridLayout(2,1, 10, 10));
        JLabel label = new JLabel(text);
        label.setIcon(new ImageIcon("src/Cards/logo.png"));
        JButton button0 = new JButton(text);
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
        GUI app1 = null;
        try {
            app1 = new GUI();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        app1.setVisible(true);
    }
}


