package com.company;

import javax.swing.*;
import java.awt.*;

// класс - аналогия ShowMessage в шарпе (нужен будет для того, чтобы показать, что бот побил все карты игрока или решил взять)
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
