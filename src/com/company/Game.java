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
    public int[] layout_indexes = new int[]
            { 1, 2, 3, 4, 5, 6, 28, 29, 30, 31, 32, 33, 16, 18, 20, 22, 24, 26, 40, 41, 42, 43, 44, 13,
                    15, 17, 19, 21, 23, 25, 45, 46, 47, -1, -2, 14, 7,  8, 9, 10, 11, 12, 34, 35, 36, 37, 38, 39 };
    // конченная раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg):)
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
        container.setBackground(Color.GREEN);
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
        File f;
        ImageIcon icon;
        for (i = 0; i < 37; i++) // создание массива картинок
        {
            f = new File("src/Cards/" + Integer.toString(i) + ".jpg");
            if (f.exists())
            {
                icon = new ImageIcon(f.toString());
                img[i] = icon;
            }
            if (i < 36)
                img_pos[i] = true;
            pictures[i + 1] = new picture();
        }
        for (i = 0; i < 47; i++) // стрелочки, показывающие, чей ход
            labels[i + 1] = new CustomLabel();
            arrow[0] = new ImageIcon("src/Cards/up.png");
            arrow[1] = new ImageIcon("src/Cards/down.png");
        // Создание экземпляра бота
        bot = new Bot();
        int value;
        // Помещение на форму всех карт и пустых слотов согласно раскладке в массиве layout_indexes (файл layout.jpg)
        for (i = 0; i < layout_indexes.length; i++)
        {
            value = layout_indexes[i];
            if ((value < 14) && (value > 0))
            {
                find_card(value);
                if ((value > 6) && (value < 13))
                    labels[value].addMouseListener(new LabelClicker());
                else if (value < 7)
                    bot.AddValue(pictures[value].M, pictures[value].V);
            }
            else if (value > 0)
                container.add(labels[value]);
            else if (value < 0)
            {
                if (value == -1)
                    container.add(button1);
                else container.add(button2);
            }

        }
        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось 24 карты");
        bot.M_K = pictures[layout_indexes[(6*2 - 1) * 2 + 1]].M; // установка козырной масти
    }
    // метод, получающий случайную карту из колоду
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
                }
                else
                {
                    labels[i].setIcon(img[k]);
                    if (i != 13)
                    {
                        Index index = new Index();
                        index.I = i;
                        labels[i].SetTag(index);
                    }
                }
                container.add(labels[i]);
            }
        }
        while (!img_pos[k]);
        img_pos[k] = false;
    }
    // Действия, когда игрок нажимает кнопку "Бито"
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
            int n_b1 = 0, n_b2 = 0, n_c1 = 0, n_c2 = 0; // кол-во дополнительно набранных карт игроком и ботом
            //n_b1 - кол-во доп. карт у игрока, n_b2 - у бота
            for (i = layout_indexes[42]; i <= layout_indexes[47]; i++)
            {
                if ((labels[i] != null) && (labels[i].getIcon() != null)) n_b1 += 1; // подсчёт доп.карт игрока
                if (labels[i - 6].getIcon() != null) n_b2 += 1; // подсчёт доп.карт бота
            }
            for (i = 15; i < 27; i++) // очистка карт на столе
            {
                labels[i].setIcon(null);
                if (pictures[i] == null)
                    pictures[i] = new picture();
                pictures[i].M = -1;
                pictures[i].V = -1;
            }
            for (i = 1; i < 13; i++) // повторная выдача недостающих карт игроку и боту
            {
                if ((labels[i] != null) && (!labels[i].isVisible()) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0)))) // выдача карт из колоды в случае отсутствия доп.карт и у игрока, и у бота
                {
                    if (!koloda_empty)
                    {
                        if (n_koloda > 1) // если в колоде пока больше 1 карты
                        {
                            int k;
                            do
                            {
                                k = rnd.nextInt(36);
                                if (img_pos[k])
                                {
                                    if ((i < 7) || ((i >= layout_indexes[6]) && (i <= layout_indexes[11])))
                                        labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                                    else labels[i].setIcon(img[k]);
                                    labels[i].setVisible(true);
                                    pictures[i].M = k % 4;
                                    pictures[i].V = k / 4;
                                    labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
                                }
                            }
                            while (!img_pos[k]);
                            img_pos[k] = false;
                        }
                        else if (n_koloda == 1) // если в колоде осталась 1 карта (а именно та, которая нарисована на экране)
                        {
                            labels[i].setIcon(labels[layout_indexes[(6*2 - 1) * 2 + 1]].getIcon());
                            labels[i].setVisible(true);
                            pictures[i].M = pictures[layout_indexes[(6*2 - 1) * 2 + 1]].M;
                            pictures[i].V = pictures[layout_indexes[(6*2 - 1) * 2 + 1]].V;
                            labels[layout_indexes[(6*2 - 1) * 2]].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[layout_indexes[(6*2 - 1) * 2 + 1]].setVisible(false);
                        }
                    }
                }
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b1 > 0) && (i > 6)) // выдача карт игроку в случае присутствия доп.карт у игрока
                {
                    labels[i].setIcon(labels[layout_indexes[42] + n_c1].getIcon()); // n_c1 - инкремент прохода по доп.картам игрока
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[layout_indexes[42] + n_c1].M;
                    pictures[i].V = pictures[layout_indexes[42] + n_c1].V;
                    labels[layout_indexes[42] + n_c1].setIcon(null);
                    pictures[layout_indexes[42] + n_c1].M = -1;
                    pictures[layout_indexes[42] + n_c1].V = -1;
                    n_b1 -= 1;
                    n_c1 += 1;
                }
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b2 > 0) && (i < 7)) // выдача карт боту в случае присутствия доп.карт у бота
                {
                    labels[i].setIcon(labels[layout_indexes[6] + n_c2].getIcon()); // n_c2 - инкремент прохода по доп.картам бота
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[layout_indexes[6] + n_c2].M;
                    pictures[i].V = pictures[layout_indexes[6] + n_c2].V;
                    labels[layout_indexes[6] + n_c2].setIcon(null);
                    pictures[layout_indexes[6] + n_c2].M = -1;
                    pictures[layout_indexes[6] + n_c2].V = -1;
                    n_b2 -= 1;
                    n_c2 += 1;
                }
            }
            for (int step = 0; step < 7; step += 6) // цикл для постепенного перетаскивания доп.карт на слоты, ближние к левому краю экрана
                for (i = layout_indexes[11]; i > layout_indexes[6]; i--)
                {
                    if ((labels[i + step].getIcon() != null) && (labels[i + step- 1].getIcon() == null))
                    {
                        labels[i + step- 1].setIcon(labels[i + step].getIcon());
                        labels[i + step].setIcon(null);
                        pictures[i + step - 1].M = pictures[i + step].M;
                        pictures[i + step - 1].V = pictures[i + step].V;
                        pictures[i + step].M = -1;
                        pictures[i + step].V = -1;
                    }
                }
            for (i = 0; i < 6; i++) // обнуление списка значений величин карт на столе
            {
                queue.player_values[i] = -1;
                queue.bot_values[i] = -1;
            }
            queue.hod *= -1;
            queue.sbros *= -1;
            if (labels[14].getIcon() == arrow[0]) // стрелка хода переворачивается при смене хода с игрока на бота
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            // После нажатия кнопки "Бито" бот начинает свой ход
            BotValues card = null;
            if ((queue.sbros == -1) && (queue.hod == -1))
                card = bot.Hod();
            int k = 15;
            int n = 1;
            if (card != null)
            {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n++].setVisible(false);
                button2.setVisible(true);
                button1.setVisible(false);
                k += 2;
            }
        }
    }
    // Действия, когда игрок нажимает кнопку "Беру"
    class Button2EventListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            int n_b = 0, n_c = 0, n_b1;
            button1.setVisible(false);
            button2.setVisible(false);
            Message beru = new Message("БЕРУ");
            beru.setVisible(true);
            for (int i = layout_indexes[42]; i <= layout_indexes[47]; i++) // подсчет кол-ва доп.карт у игрока, который после нажатия кнопки "Беру" будет брать карты бота со стола
                if ((labels[i] != null) && (labels[i].getIcon() != null))
                    n_b += 1;
            n_b1 = n_b;
            for (int i = 1; i < 7; i++) // выдача карт боту (в случае когда игрок берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
            {
                if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b == 0))
                {
                    // код выдачи карт из колоды полностью дублирует такой же код для Button1 (нужно создать доп.метод и вызывать его, например, как find_card в основном классе)
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
                                    labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось " + Integer.toString(--n_koloda) + " карт(ы)");
                                }
                            }
                            while (!img_pos[k]);
                            img_pos[k] = false;
                        }
                        else if (n_koloda == 1)
                        {
                            labels[i].setIcon(labels[layout_indexes[(6*2 - 1) * 2 + 1]].getIcon());
                            labels[i].setVisible(true);
                            pictures[i].M = pictures[layout_indexes[(6*2 - 1) * 2 + 1]].M;
                            pictures[i].V = pictures[layout_indexes[(6*2 - 1) * 2 + 1]].V;
                            labels[layout_indexes[(6*2 - 1) * 2]].setText("Карты в колоде закончились");
                            koloda_empty = true;
                            labels[layout_indexes[(6*2 - 1) * 2] + 1].setVisible(false);
                        }
                    }
                }
                else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b > 0)) // выдача карт боту в случае присутствия доп.карт у бота
                {
                    labels[i].setIcon(labels[layout_indexes[6] + n_c].getIcon());
                    labels[i].setVisible(true);
                    pictures[i].M = pictures[layout_indexes[6] + n_c].M;
                    pictures[i].V = pictures[layout_indexes[6] + n_c].V;
                    labels[layout_indexes[6] + n_c].setIcon(null);
                    pictures[layout_indexes[6] + n_c].M = -1;
                    pictures[layout_indexes[6] + n_c].V = -1;
                    n_b -= 1;
                    n_c += 1;
                }
            }
            for (int i = 1; i < 7; i++) // проверка видимости карт бота
            {
                if ((labels[i] != null) && (!labels[i].isVisible()))
                {
                    labels[i].setVisible(true);
                }
            }
            int step = 2 * layout_indexes[42] - layout_indexes[12]; // вычисляемый шаг для сопоставления картам на столе слотов для доп.карт у игрока
            for (int i = layout_indexes[12]; i <= layout_indexes[17]; i += 2) // игрок берет карты бота со стола к себе в доп.карты
            {
                if (pictures[i] == null)
                {
                    pictures[i] = new picture();
                    n_pictures += 1;
                }
                labels[((i + step) / 2) + n_b1].setIcon(labels[i].getIcon());
                labels[((i + step) / 2) + n_b1].addMouseListener(new LabelClicker());
                Index index = new Index();
                index.I = ((i + step) / 2 + n_b1);
                labels[((i + step) / 2) + n_b1].SetTag(index);
                if (pictures[(i + step) / 2 + n_b1] == null)
                    pictures[(i + step) / 2 + n_b1] = new picture();
                n_pictures += 1;
                pictures[(i + step) / 2 + n_b1].M = pictures[i].M;
                pictures[(i + step) / 2 + n_b1].V = pictures[i].V;
                pictures[i].M = -1;
                pictures[i].V = -1;
                labels[i].setIcon(null);
            }
            for (int i = 0; i < queue.player_values.length; i++) // обнуление списка значений величин карт на столе
            {
                queue.player_values[i] = -1;
                queue.bot_values[i] = -1;
            }
            for (int i = 1; i < pictures.length; i++) // взаимодействие с массивами карт, которые располагаются на столе
            {
                if (pictures[i] == null)
                    pictures[i] = new picture();
                if ((i >= layout_indexes[24]) && (i <= layout_indexes[17]))
                    pictures[i].Enabled = true;
            }
            if (queue.sbros != queue.hod)
                queue.sbros = queue.hod;
            // После нажатия кнопки "Беру" бот начинает свой ход (продолжая прошлый ход)
            BotValues card = null;
            if ((queue.sbros == -1) && (queue.hod == -1))
                card = bot.Hod();
            int n = 1;
            int k = 15;
            if (card != null)
            {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n++].setVisible(false);
                button2.setVisible(true);
                button1.setVisible(false);
                k += 2;
            }
        }
    }
    // Карты игрока (а именно карты с 7 по 12 по раскладке) имеют событие клика.
    // Также событие клика будет появляться у карт, которые игрок будет брать у бота и помещать в дополнительные слоты 34-39)
    class LabelClicker implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            CustomLabel p = (CustomLabel) e.getSource();
            int index = ((Index) (p.GetTag())).I; // получение индекса нажатой карты
            int k = layout_indexes[24];
            boolean go = true; // переменная go - отвечает за подкидывание карт на стол
            while ((labels[k].getIcon() != null) && (index > 6))
            {
                k += 2;
                go = false;
            }
            for (int i = 0; i < queue.player_values.length; i++) // определяется возможность подкидывания карт на стол
            {
                if (queue.hod == 1)
                    if ((pictures[index].V == queue.player_values[i]) || (pictures[index].V == queue.bot_values[i]))
                    {
                        go = true;
                        break;
                    }
                else bot.Podkid(queue.player_values, queue.bot_values);

            }
            if ((go) || (queue.hod == queue.sbros))
                queue.sbros *= -1; // бот начинает биться (во время хода игрока)
                if (queue.hod == 1) // если ходит игрок
                {
                    if (go) // если можно подкидывать карты
                    {
                        labels[k].setIcon(labels[index].getIcon());
                        if (pictures[k] == null)
                            pictures[k] = new picture();
                        n_pictures += 1;
                        pictures[k].Enabled = false;
                        pictures[k].M = pictures[index].M;
                        pictures[k].V = pictures[index].V;
                        queue.player_values[(k-15) / 2] = pictures[k].V;
                        labels[index].setVisible(false);
                        queue.sbros *= -1;
                        button2.setVisible(true);
                        button1.setVisible(false);
                    }
                }
                else // если ходит бот
                {
                    if ((queue.sbros == 1) || go) // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                        if (((pictures[k + 1].M == pictures[index].M) && (pictures[index].V > pictures[k + 1].V)) || ((pictures[index].M == bot.M_K) && (pictures[k + 1].M != bot.M_K)))
                        {
                            labels[k].setIcon(labels[index].getIcon());
                            if (pictures[k] == null)
                                pictures[k] = new picture();
                            n_pictures += 1;
                            pictures[k].Enabled = false;
                            pictures[k].M = pictures[index].M;
                            pictures[k].V = pictures[index].V;
                            queue.player_values[(k - 15) / 2] = pictures[k].V; // заполнение массива карт игрока, скинутых на поле
                            labels[index].setVisible(false);
                            queue.sbros *= -1;
                            for (int i = 0; i < 6; i++) // если у бота появляется непобитая им карта
                            {
                                if ((queue.player_values[i] > -1) && (queue.bot_values[i] == -1))
                                {
                                    queue.sbros *= -1;
                                    break;
                                }
                            }
                            button1.setVisible(true);
                            button2.setVisible(false);
                        }
                }
            if ((queue.hod == 1) && (queue.sbros == -1))
            {
                bot.AddEnemyValue(pictures[k].M, pictures[k].V);
                int n = bot.values.size();
                BotValues card = bot.Bito();
                if (card != null)
                {
                    labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                    labels[n--].setVisible(false);
                    button1.setVisible(true);
                    button2.setVisible(false);
                }
            }
            }
        public void mouseEntered(MouseEvent e)
        {}

        public void mouseExited(MouseEvent e)
        {}

        public void mousePressed(MouseEvent e)
        {}

        public void mouseReleased(MouseEvent e)
        {}
        }
    }

// класс для рабочих величин
class Queue
{
    public int sbros;
    public int hod;
    public int[] player_values = new int[] { -1, -1, -1, -1, -1, -1 };
    public int[] bot_values = new int[] { -1, -1, -1, -1, -1, -1 };
}
//класс, содержащий масть и значение для каждой карты с тем же индексом
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
//класс для надписей (в которые и помещаются картинки с картами), который был расширен добавлением Tag (будет нужен для обработки события нажатия)
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

