package com.company;

import javax.swing.*;
import java.awt.*;

public class Message extends JFrame {
    public Message(String text)
    {
        super("ShowMessage");
        Container container = this.getContentPane();
        this.setResizable(false);
        this.setBounds(820, 450, 300, 100);
        //JLabel label1 = new JLabel("Ваша карта побита картой (" + text + ")");
        JLabel label = new JLabel(text);
        container.add(label);
    }
}
