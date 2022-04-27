package com.company;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.EventHandler;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Game extends JFrame
{
    Container container = this.getContentPane();
    CustomLabel[] labels = new CustomLabel[48];
    ImageIcon[] arrow = new ImageIcon[2];
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
    Bot bot;

    public Game() throws IOException {
        super("Дурак (подкидной) ");
        this.setBounds(0, 0, 1920, 1080);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //this.getContentPane().add(new JPanelWithBackground("src/Cards/background.jpg"));
        container.setLayout(new GridLayout(4,12, 5, 10));
        container.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        button1.addActionListener(new Button1EventListener());
        button2.addActionListener(new Button2EventListener());
        button1.setVisible(false);
        button2.setVisible(false);
        n_koloda = 24;
        koloda_empty = false;
        n_pictures = 12;
        queue.sbros = 1;
        queue.hod = 1;
        int i,k;
        for (i = 0; i < 37; i++)
        {
            ImageIcon icon = new ImageIcon("src/Cards/" + Integer.toString(i) + ".jpg");
            img[i] = icon;
            if (i < 36)
                img_pos[i] = true;
            pictures[i + 1] = new picture();
        }
        for (i = 0; i < 47; i++)
            labels[i + 1] = new CustomLabel();
        arrow[0] = new ImageIcon("src/Cards/up.png");
        arrow[1] = new ImageIcon("src/Cards/down.png");
        //выдача карт верхнему игроку (боту)
        bot = new Bot();
        i = 1;
        while (i < 7)
        {
            //labels[i].addMouseListener(new LabelClicker());
            find_card(i);
            bot.AddValue(pictures[i].M, pictures[i].V);
            i++;
        }
        //выдача пустых 6 дополнительных слотов для карт верхнего игрока
        for (i = 28; i < 34; i++)
        {
            container.add(labels[i]);
        }
        //выдача пустых 6 слотов для скидываемых карт верхнего игрока
        for (i = 16; i < 27; i = i + 2)
        {
            container.add(labels[i]);
        }
        //не используемые слоты, для правильного расположения остальных слотов
        for (i = 40; i < 45; i++)
            container.add(labels[i]);
        labels[44].setText("Осталось 24 карты");
        //генерация и выдача козырной карты
        find_card(13);
        bot.M_K = pictures[13].M;
        //выдача пустых 6 слотов для скидываемых карт нижнего игрока
        for (i = 15; i < 26; i = i + 2)
        {
            container.add(labels[i]);
        }
        ////не используемые слоты, для правильного расположения остальных слотов
        for (i = 45; i < 48; i++)
            container.add(labels[i]);
        container.add(button1);
        container.add(button2);
        //стрелка - направление игры
        labels[14].setIcon(arrow[0]);
        container.add(labels[14]);
        //генерация и выдача 6 карт нижнего игрока
        i = 7;
        while (i < 13)
        {
            labels[i].addMouseListener(new LabelClicker());
            find_card(i);
            i++;
        }
        ////выдача пустых 6 дополнительных слотов для карт нижнего игрока
        for (i = 34; i < 40; i++)
        {
            container.add(labels[i]);
        }

    }
    public void find_card(int i)
    {
        int k;
        do {
            k = rnd.nextInt(36);
            if (img_pos[k])
            {
                pictures[i] = new picture();
                pictures[i].Enabled = true;
                pictures[i].M = k % 4;
                pictures[i].V = k / 4;
                if (i < 7)
                {
                    labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                    container.add(labels[i]);
                }
                else
                {
                    labels[i].setIcon(img[k]);
                    container.add(labels[i]);
                    Index index = new Index();
                    index.I = i;
                    labels[i].SetTag(index);
                }
            }
        }
        while (!img_pos[k]);
        img_pos[k] = false;
    }

    class Button1EventListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int i;
            button1.setVisible(false);
            button2.setVisible(false);
            Message bito = new Message("БИТО");
            bito.setVisible(true);
            int n_b1 = 0, n_b2 = 0, n_c1 = 0, n_c2 = 0;
            for (i = 34; i < 40; i++)
            {
                if ((labels[i] != null) && (labels[i].getIcon() != null)) n_b1 += 1;
                if (labels[i - 6].getIcon() != null) n_b2 += 1;
            }
            for (i = 15; i < 27; i++)
            {
                labels[i].setIcon(labels[47].getIcon());
                if (pictures[i] == null)
                    pictures[i] = new picture();
                pictures[i].M = -1;
                pictures[i].V = -1;
            }
            for (i = 1; i < 13; i++)
            {
                if ((labels[i] != null) && (!labels[i].isVisible()) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0))))
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
                                    if ((i < 7) || ((i > 27) && (i < 34)))
                                        labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                                    else labels[i].setIcon(img[k]);
                                    labels[i].setVisible(true);
                                    pictures[i].M = k % 4;
                                    pictures[i].V = k / 4;
                                    labels[44].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
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
                            labels[44].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[13].setVisible(false);
                        }
                    }
                }
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b1 > 0) && (i > 6))
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
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b2 > 0) && (i < 7))
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
            if (labels[14].getIcon() == arrow[0])
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            queue.sbros *= -1;

            BotValues card = null;
            if ((queue.sbros == -1) && (queue.hod == -1))
                card = bot.Hod();
            int k = 15;
            int n = 1;
            if (card != null)
            {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n].setVisible(false);
                button2.setVisible(true);
                button1.setVisible(false);
                n += 1;
                k += 2;
                //queue.sbros *= -1;
            }
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
            Message beru = new Message("БЕРУ");
            beru.setVisible(true);
            if (queue.hod == -1) h = -6;
            else h = 0;
            for (int i = 34 + h; i < 40 + h; i++)
                if ((labels[i] != null) && (labels[i].getIcon() != null))
                    n_b += 1;
            n_b1 = n_b;
            for (int i = 7 + h; i < 13 + h; i++)
            {
                if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b == 0))
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
                                    labels[44].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
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
                            labels[44].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[13].setVisible(false);
                            labels[14].setVisible(false);
                        }
                    }
                }
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b > 0))
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
                if ((labels[i] != null) && (!labels[i].isVisible()))
                {
                    labels[i].setVisible(true);
                }
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
                if ((i > 15) && (i < 26))
                    pictures[i].Enabled = true;
            }
            if (queue.sbros != queue.hod)
                queue.sbros = queue.hod;
            BotValues card = null;
            if ((queue.sbros == -1) && (queue.hod == -1))
                card = bot.Hod();
            int n = 1;
            int k = 15;
            if (card != null)
            {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n].setVisible(false);
                button2.setVisible(true);
                button1.setVisible(false);
                n += 1;
                k += 2;
                //queue.sbros *= -1;
            }
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
                        queue.myvalues[(k-15) / 2] = pictures[k].V;
                        labels[index].setVisible(false);
                        queue.sbros *= -1;
                        button2.setVisible(true);
                        button1.setVisible(false);
                    }
                }
                else
                {
                    if ((queue.sbros == 1) || go)
                        if (((pictures[k + 1].M == pictures[index].M) && (pictures[index].V > pictures[k + 1].V)) || ((pictures[index].M == pictures[13].M) && (pictures[k + 1].M != pictures[13].M)))
                        {
                            labels[k].setIcon(labels[index].getIcon());
                            if (pictures[k] == null)
                                pictures[k] = new picture();
                            n_pictures += 1;
                            pictures[k].Enabled = false;
                            pictures[k].M = pictures[index].M;
                            pictures[k].V = pictures[index].V;
                            queue.myvalues[(k - 15) / 2] = pictures[k].V;
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
                        queue.myvalues[(k - 15) / 2] = pictures[k + 1].V;
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
                            queue.myvalues[(k - 15) / 2] = pictures[k + 1].V;
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
            bot.AddEnemyValue(pictures[k].M, pictures[k].V);
            int n = bot.values.size();
            BotValues card = bot.Bito();
            if (card != null)
            {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n].setVisible(false);
                n -= 1;
                button1.setVisible(true);
                button2.setVisible(false);
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

 class JPanelWithBackground extends JPanel {

    private Image backgroundImage;
    public JPanelWithBackground(String fileName) throws IOException {
        try {
            backgroundImage = ImageIO.read(new File(fileName));
        }
        catch (IOException e)
        {}
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this);
    }
}
