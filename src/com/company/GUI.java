package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.EventHandler;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.Random;

public class GUI extends JFrame{
    CustomLabel[] labels = new CustomLabel[54];
    JLabel[] word_labels = new JLabel[2];
    JButton button1 = new JButton("БИТО");
    JButton button2 = new JButton("БЕРУ");
    int n_pictures;
    int n_koloda;
    boolean koloda_empty;
    ImageIcon[] img = new ImageIcon[37];
    picture[] pictures = new picture[40];
    boolean[] img_pos = new boolean[36];
    Random rnd = new Random();
    Queue queue = new Queue();
    public GUI()
    {
        super("Дурак");
        this.setBounds(100, 100, 1920, 1080);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4,12, 5, 10));
        container.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        button1.addActionListener(new Button1EventListener());
        button2.addActionListener(new Button2EventListener());
        button1.setVisible(false);
        button2.setVisible(false);
        word_labels[0] = new JLabel("Осталось 24 карты");
        word_labels[1] = new JLabel("Что выбираете");
        n_koloda = 24;
        koloda_empty = false;
        n_pictures = 12;
        queue.sbros = 1;
        queue.hod = 1;
        int i;
        for (i = 0; i < 37; i++)
        {
            labels[i + 1] = new CustomLabel();
            ImageIcon icon = new ImageIcon("src/Cards/" + Integer.toString(i) + ".jpg");
            img[i] = icon;
            if (i < 36)
                img_pos[i] = true;
            pictures[i + 1] = new picture();
        }
        i = 1;
        while (i < 7)
        {
            int k;
            labels[i].addMouseListener(new LabelClicker());
            do {
                k = rnd.nextInt(36);
                if (img_pos[k])
                {
                    labels[i].setIcon(img[k]);
                    container.add(labels[i]);
                    pictures[i] = new picture();
                    pictures[i].Enabled = true;
                    pictures[i].M = k % 4;
                    pictures[i].V = k / 4;
                    Index index = new Index();
                    index.I = i;
                    labels[i].SetTag(index);
                }
            }
            while (!img_pos[k]);
            img_pos[k] = false;
            i++;
        }
        for (i = 41; i < 47; i++)
        {
            labels[i] = new CustomLabel();
            container.add(labels[i]);
        }
        for (i = 16; i < 39; i = i + 2)
        {
            if (i > 37)
                labels[i] = new CustomLabel();
            container.add(labels[i]);
        }
        labels[38].setIcon(new ImageIcon("E:\\cards\\35.jpg"));
        for (i = 15; i < 34; i = i + 2)
            container.add(labels[i]);
        container.add(button1);
        container.add(button2);
        i = 7;
        while (i < 13)
        {
            int k;
            labels[i].addMouseListener(new LabelClicker());
            do {
                k = rnd.nextInt(36);
                if (img_pos[k])
                {
                    labels[i].setIcon(img[k]);
                    container.add(labels[i]);
                    pictures[i] = new picture();
                    pictures[i].Enabled = true;
                    pictures[i].M = k % 4;
                    pictures[i].V = k / 4;
                    Index index = new Index();
                    index.I = i;
                    labels[i].SetTag(index);
                }
            }
            while (!img_pos[k]);
            img_pos[k] = false;
            i++;
        }
        for (i = 47; i < 53; i++)
        {
            labels[i] = new CustomLabel();
            container.add(labels[i]);
        }

    }

    class MouseClickHandler extends MouseAdapter
    {
        EventHandler e;
        public MouseClickHandler(EventHandler e)
        {
            this.e = e;
        }
        public void mouseClicked(MouseEvent e)
        {

        }
    }

    class Button1EventListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int i;
            button1.setVisible(false);
            button2.setVisible(false);
            int n_b1 = 0, n_b2 = 0, n_c1 = 0, n_c2 = 0;
            for (i = 34; i < 40; i++)
            {
                if (labels[i].getIcon() != null) n_b1 += 1;
                if (labels[i - 6].getIcon() != null) n_b2 += 1;
            }
            for (i = 15; i < 27; i++)
            {
                labels[i].setIcon(labels[27].getIcon());
                if (pictures[i] == null)
                    pictures[i] = new picture();
                pictures[i].M = -1;
                pictures[i].V = -1;
            }
            for (i = 1; i < 13; i++)
            {
                if ((labels[i] != null) && (labels[i].isVisible() == false) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0))))
                {
                    if (!koloda_empty)
                    {
                        if (n_koloda > 1)
                        {
                            int k;
                            do
                            {
                                k = rnd.nextInt(36);
                                if (img_pos[k])
                                {
                                    labels[i].setIcon(img[k]);
                                    labels[i].setVisible(true);
                                    pictures[i].M = k % 4;
                                    pictures[i].V = k / 4;
                                    word_labels[1].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
                                }
                            }
                            while (!img_pos[k]);
                            img_pos[k] = false;
                        }
                        else if (n_koloda == 1)
                        {
                            labels[i].setIcon(labels[13].getIcon());
                            labels[i].setVisible(true);
                            pictures[i].M = pictures[13].M;
                            pictures[i].V = pictures[13].V;
                            word_labels[1].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[13].setVisible(false);
                            labels[14].setVisible(false);
                        }
                    }
                }
                else if ((labels[i].isVisible() == false) && (n_b1 > 0) && (i > 6))
                {
                    labels[i].setIcon(labels[34 + n_c1].getIcon());
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[34 + n_c1].M;
                    pictures[i].V = pictures[34 + n_c1].V;
                    labels[34 + n_c1].setIcon(null);
                    pictures[34 + n_c1].M = -1;
                    pictures[34 + n_c1].V = -1;
                    n_b1 -= 1;
                    n_c1 += 1;
                }
                else if ((labels[i].isVisible() == false) && (n_b2 > 0) && (i < 7))
                {
                    labels[i].setIcon(labels[28 + n_c2].getIcon());
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[28 + n_c2].M;
                    pictures[i].V = pictures[28 + n_c2].V;
                    labels[28 + n_c2].setIcon(null);
                    pictures[28 + n_c2].M = -1;
                    pictures[28 + n_c2].V = -1;
                    n_b2 -= 1;
                    n_c2 += 1;
                }
            }
            for (i = 33; i > 28; i--)
            {
                if ((labels[i].getIcon() != null) && (labels[i - 1].getIcon() == null))
                {
                    labels[i - 1].setIcon(labels[i].getIcon());
                    labels[i].setIcon(null);
                    pictures[i - 1].M = pictures[i].M;
                    pictures[i - 1].V = pictures[i].V;
                    pictures[i].M = -1;
                    pictures[i].V = -1;
                }
                if ((labels[i + 6].getIcon() != null) && (labels[i + 5].getIcon() == null))
                {
                    labels[i + 5].setIcon(labels[i + 6].getIcon());
                    labels[i + 6].setIcon(null);
                    pictures[i + 5].M = pictures[i + 6].M;
                    pictures[i + 5].V = pictures[i + 6].V;
                    pictures[i + 6].M = -1;
                    pictures[i + 6].V = -1;
                }
            }
            for (i = 0; i < 6; i++)
            {
                queue.myvalues[i] = -1;
                queue.envalues[i] = -1;
            }
            queue.hod *= -1;
            //здесь прописать путь к стрелочкам
            queue.sbros *= -1;
        }
    }
    class Button2EventListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int h;
            int n_b = 0, n_c = 0, n_b1;
            button1.setVisible(false);
            button2.setVisible(false);
            if (queue.hod == -1) h = -6;
            else h = 0;
            for (int i = 34 + h; i < 40 + h; i++)
                if (labels[i].getIcon() != null)
                    n_b += 1;
            n_b1 = n_b;
            for (int i = 7 + h; i < 13 + h; i++)
            {
                if ((labels[i].isVisible() == false) && (n_b == 0))
                {
                    if (!koloda_empty)
                    {
                        if (n_koloda > 1)
                        {
                            int k;
                            do {
                                k = rnd.nextInt(36);
                                if (img_pos[k])
                                {
                                    labels[i].setIcon(img[k]);
                                    labels[i].setVisible(true);
                                    pictures[i].M = k % 4;
                                    pictures[i].V = k / 4;
                                    word_labels[1].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
                                }
                            }
                            while (!img_pos[k]);
                            img_pos[k] = false;
                        }
                        else if (n_koloda == 1)
                        {
                            labels[i].setIcon(labels[13].getIcon());
                            labels[i].setVisible(true);
                            pictures[i].M = pictures[13].M;
                            pictures[i].V = pictures[13].V;
                            word_labels[1].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[13].setVisible(false);
                            labels[14].setVisible(false);
                        }
                    }
                }
                else if ((labels[i].isVisible() == false) && (n_b > 0))
                {
                    labels[i].setIcon(labels[34 + h + n_c].getIcon());
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[34 + h + n_c].M;
                    pictures[i].V = pictures[34 + h + n_c].V;
                    labels[34 + h + n_c].setIcon(null);
                    pictures[34 + h + n_c].M = -1;
                    pictures[34 + h + n_c].V = -1;
                    n_b -= 1;
                    n_c += 1;
                }
            }
            for (int i = 1 - h; i < 7 - h; i++)
            {
                if (labels[i].isVisible() == false)
                    labels[i].setVisible(true);
            }
            for (int i = 15; i < 27; i++)
            {
                if (pictures[i] == null)
                {
                    pictures[i] = new picture();
                    n_pictures += 1;
                }
                if ((i % 2 == 1) && (h == 0))
                {
                    labels[((i + 41) / 2) + n_b1].setIcon(labels[i].getIcon());
                    labels[((i + 41) / 2) + n_b1].addMouseListener(new LabelClicker());
                    Index index = new Index();
                    index.I = ((i + 41) / 2 + n_b1);
                    labels[((i + 41) / 2) + n_b1].SetTag(index);
                    if (pictures[(i + 41) / 2 + n_b1] == null)
                        pictures[(i + 41) / 2 + n_b1] = new picture();
                    n_pictures += 1;
                    pictures[(i + 41) / 2 + n_b1].M = pictures[i].M;
                    pictures[(i + 41) / 2 + n_b1].V = pictures[i].V;

                }
                else if ((i % 2 == 0) && (h == -6))
                {
                    labels[((i + 52) / 2) + n_b1].setIcon(labels[i].getIcon());
                    labels[((i + 52) / 2) + n_b1].addMouseListener(new LabelClicker());
                    Index index = new Index();
                    index.I = ((i + 52) / 2 + n_b1);
                    labels[((i + 52) / 2) + n_b1].SetTag(index);
                    if (pictures[(i + 52) / 2 + n_b1] == null)
                        pictures[(i + 52) / 2 + n_b1] = new picture();
                    n_pictures += 1;
                    pictures[(i + 52) / 2 + n_b1].M = pictures[i].M;
                    pictures[(i + 52) / 2 + n_b1].V = pictures[i].V;
                }
                pictures[i].M = -1;
                pictures[i].V = -1;
                labels[i].setIcon(labels[27].getIcon());
            }
            for (int i = 0; i < 6; i++)
            {
                queue.myvalues[i] = -1;
                queue.envalues[i] = -1;
            }
            for (int i = 1; i < pictures.length; i++)
            {
                if (pictures[i] == null)
                    pictures[i] = new picture();
                if ((i < 15) && (i > 26))
                    pictures[i].Enabled = true;
            }
            if (queue.sbros != queue.hod)
                queue.sbros = queue.hod;
        }
    }
    class LabelClicker implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            CustomLabel p = (CustomLabel) e.getSource();
            int index = ((Index) (p.GetTag())).I;
            int k = 15;
            boolean go = true;
            while (((labels[k].getIcon() != null) && (index > 6)) || ((labels[k + 1].getIcon() != null) && (index < 7)))
            {
                k += 2;
                go = false;
            }
            for (int i = 0; i < 6; i++)
            {
                if ((pictures[index].V == queue.myvalues[i]) || (pictures[index].V == queue.envalues[i]))
                {
                    go = true;
                    break;
                }
            }
            if (go) queue.sbros *= -1;
            else if (queue.hod == queue.sbros) queue.sbros *= -1;
            if (((index > 6) && (index < 13)) || ((index > 33) && (index < 40)))
            {
                if (queue.hod == 1)
                {
                    if (go)
                    {
                        labels[k].setIcon(labels[index].getIcon());
                        if (pictures[k] == null)
                            pictures[k] = new picture();
                        n_pictures += 1;
                        pictures[k].Enabled = false;
                        pictures[k].M = pictures[index].M;
                        pictures[k].V = pictures[index].V;
                        queue.myvalues[(k-15)/2] = pictures[k].V;
                        labels[index].setVisible(false);
                        queue.sbros *= -1;
                        button2.setVisible(true);
                        button1.setVisible(false);
                    }
                }
                else
                {
                    if ((queue.sbros == 1) || go)
                        if (((pictures[k+1].M == pictures[index].M) && (pictures[index].V > pictures[k+1].V)) || ((pictures[index].M == pictures[13].M) && (pictures[k+1].M != pictures[13].M)))
                        {
                            labels[k].setIcon(labels[index].getIcon());
                            if (pictures[k] == null)
                                pictures[k] = new picture();
                            n_pictures += 1;
                            pictures[k].Enabled = false;
                            pictures[k].M = pictures[index].M;
                            pictures[k].V = pictures[index].V;
                            queue.myvalues[(k-15)/2] = pictures[k].V;
                            labels[index].setVisible(false);
                            queue.sbros *= -1;
                            for (int i = 0; i < 6; i++)
                            {
                                if ((queue.myvalues[i] > -1) && (queue.envalues[i] == -1))
                                {
                                    queue.sbros *= -1;
                                    break;
                                }
                            }
                            button1.setVisible(true);
                            button2.setVisible(false);
                        }
                }
            }
            else if ((index < 7) || ((index > 27) && (index < 34)))
            {
                if (queue.hod == -1)
                {
                    if (go)
                    {
                        labels[k + 1].setIcon(labels[index].getIcon());
                        if (pictures[k + 1] == null)
                            pictures[k + 1] = new picture();
                        n_pictures += 1;
                        pictures[k + 1].Enabled = false;
                        pictures[k + 1].M = pictures[index].M;
                        pictures[k + 1].V = pictures[index].V;
                        queue.myvalues[(k-15)/2] = pictures[k + 1].V;
                        labels[index].setVisible(false);
                        queue.sbros *= -1;
                        button2.setVisible(true);
                        button1.setVisible(false);
                    }
                }
                else
                {
                    if ((queue.sbros == -1) || go)
                        if (((pictures[k].M == pictures[index].M) && (pictures[index].V > pictures[k].V)) || ((pictures[index].M == pictures[13].M) && (pictures[k].M != pictures[13].M)))
                        {
                            labels[k + 1].setIcon(labels[index].getIcon());
                            if (pictures[k + 1] == null)
                                pictures[k + 1] = new picture();
                            n_pictures += 1;
                            pictures[k + 1].Enabled = false;
                            pictures[k + 1].M = pictures[index].M;
                            pictures[k + 1].V = pictures[index].V;
                            queue.myvalues[(k-15)/2] = pictures[k + 1].V;
                            labels[index].setVisible(false);
                            queue.sbros *= -1;
                            for (int i = 0; i < 6; i++)
                            {
                                if ((queue.myvalues[i] > -1) && (queue.envalues[i] == -1))
                                {
                                    queue.sbros *= -1;
                                    break;
                                }
                            }
                            button1.setVisible(true);
                            button2.setVisible(false);
                        }
                }
            }
            else
            {
                for (int i = 1; i < n_pictures; i++)
                {
                    if (pictures[i] == null) pictures[i] = new picture();
                    pictures[i].Enabled = false;
                }
            }
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }
    }
}


class Queue
{
    public int sbros;
    public int hod;
    public int[] myvalues = new int[] { -1, -1, -1, -1, -1, -1 };
    public int[] envalues = new int[] { -1, -1, -1, -1, -1, -1 };
}
class picture
{
    public int M;
    public int V;
    public boolean Enabled;
}
class Index
{
    public int I;
}
class CustomLabel extends JLabel
{
    public int g;
    private Object Tag;
    public Object GetTag()
    {
        return Tag;
    }
    public void SetTag(Object Tag)
    {
        this.Tag = Tag;
    }
}
