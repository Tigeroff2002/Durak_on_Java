package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

// меню, появляющееся при запуске игры, содержащее на данном этапе лишь кнопку "Играть"
public class GUI extends JFrame {
    public static int[] layout_indexes = new int[]
            { 1, 2, 3, 4, 5, 6, 28, 29, 30, 31, 32, 33, 16, 18, 20, 22, 24, 26, 40, 41, 42, 43, 44, 13,
                    15, 17, 19, 21, 23, 25, 45, 46, 47, -1, -2, 14, 7,  8, 9, 10, 11, 12, 34, 35, 36, 37, 38, 39 };
    // раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg)
    static CustomLabel[] labels = new CustomLabel[48];
    ImageIcon[] arrow = new ImageIcon[2];
    static JButton button1 = new JButton("БИТО");
    static JButton button2 = new JButton("БЕРУ");
    static ImageIcon[] img = new ImageIcon[36];
    static boolean[] img_pos = new boolean[36];
    static String [] dirs = new String [] {"up", "down"};
    static File f;
    Game GL;
    public GUI() throws IOException {
        super("Дурак (подкидной) ");
        Container container = this.getContentPane();
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
        Game GL = new Game();
        for (int i = 0; i < 36; i++) // создание массива картинок
        {
            f = new File("src/Cards/" + Integer.toString(i) + ".jpg");
            if (f.exists())
                img[i] = new ImageIcon(f.toString());
            else throw new IOException("Картинка(и) с картами не найдены!");
            img_pos[i] = true;
            GL.cards[i + 1] = new card();
            labels[i + 1] = new CustomLabel();
        }
        for (int i = 0; i < dirs.length; i++)
        {
            f = new File("src/Cards/" + dirs[0] + ".png");
            if (f.exists())
            {
                arrow[i] = new ImageIcon(f.toString());
            }
            else throw new IOException("Картинка(и) со стрелками на найдены!");
        }
        // Помещение на форму всех карт и пустых слотов согласно раскладке в массиве layout_indexes (файл layout.jpg)
        Bot bot = new Bot();
        for (int i = 0; i < layout_indexes.length; i++)
        {
            int value = layout_indexes[i];
            if ((value < 14) && (value > 0))
            {
                delivering_cards(value, 0, 0);
                if ((value > 6) && (value < 13))
                    labels[value].addMouseListener(new LabelClicker());
                else if (value < 7)
                    bot.AddValue(GL.cards[value].M(), GL.cards[value].V());
            }
            else if (value < 0)
            {
                if (value == -1)
                    container.add(GUI.button1);
                else container.add(GUI.button2);
            }
            if (value > 0)
                container.add(labels[value]);
        }
        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось 24 карты");
    }
    // метод, производящий вытягивание случайной оставшейся карты из колоды в нужный момент времени
    public void delivering_cards(int i, int n_b1, int n_b2)
    {
        int n_c1 = 0, n_c2 = 0;
        if ((labels[i].getIcon() == null) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0))))// выдача карт из колоды в случае отсутствия доп.карт и у игрока, и у бота
        {
            if (!GL.queue.koloda_empty)
            {
                if (GL.queue.n_koloda > 1) // если в колоде пока больше 1 карты
                {
                    int k;
                    do
                    {
                        k = new Random().nextInt(36);
                        if (img_pos[k])
                        {
                            if ((i < 7) || ((i >= layout_indexes[6]) && (i <= layout_indexes[11])))
                                labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                            else labels[i].setIcon(img[k]);
                            labels[i].setVisible(true);
                            GL.cards[i].M(k % 4);
                            GL.cards[i].V(k / 4);
                            if ((labels[i].GetTag() == null) && ((i < 13) || ((i > 27) && (i < 40))))
                            {
                                Index index = new Index();
                                index.I = i;
                                labels[i].SetTag(index);
                            }
                        }
                    }
                    while (!img_pos[k]);
                    img_pos[k] = false;
                    GL.queue.n_koloda--;
                    if (GL.queue.n_koloda > 1)
                        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось " + Integer.toString(GL.queue.n_koloda) + " карт(ы)");
                    else
                        labels[layout_indexes[(6*2 - 1) * 2]].setText("Карты в колоде закончились");
                }
                else if (GL.queue.n_koloda == 1) // если в колоде осталась 1 карта (а именно та, которая нарисована на экране)
                {
                    labels[i].setIcon(labels[layout_indexes[(6*2 - 1) * 2 + 1]].getIcon());
                    labels[i].setVisible(true);
                    GL.cards[i].M(GL.cards[layout_indexes[(6*2 - 1) * 2 + 1]].M());
                    GL.cards[i].V(GL.cards[layout_indexes[(6*2 - 1) * 2 + 1]].V());
                    GL.queue.koloda_empty = true;
                    labels[layout_indexes[(6*2 - 1) * 2 + 1]].setVisible(false);
                }
            }
        }
        else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b1 > 0) && (i > 6)) // выдача карт игроку в случае присутствия доп.карт у игрока
        {
            labels[i].setIcon(labels[layout_indexes[42] + n_c1].getIcon()); // n_c1 - инкремент прохода по доп.картам игрока
            labels[i].setVisible(true);
            GL.cards[i].M(GL.cards[layout_indexes[42] + n_c1].M());
            GL.cards[i].V(GL.cards[layout_indexes[42] + n_c1].V());
            labels[layout_indexes[42] + n_c1].setIcon(null);
            GL.cards[layout_indexes[42] + n_c1].M(-1);
            GL.cards[layout_indexes[42] + n_c1].V(-1);
            n_b1 -= 1;
            n_c1 += 1;
        }
        else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b2 > 0) && (i < 7)) // выдача карт боту в случае присутствия доп.карт у бота
        {
            labels[i].setIcon(labels[layout_indexes[6] + n_c2].getIcon()); // n_c2 - инкремент прохода по доп.картам бота
            labels[i].setVisible(true);
            GL.cards[i].M(GL.cards[layout_indexes[6] + n_c2].M());
            GL.cards[i].V(GL.cards[layout_indexes[6] + n_c2].V());
            labels[layout_indexes[6] + n_c2].setIcon(null);
            GL.cards[layout_indexes[6] + n_c2].M(-1);
            GL.cards[layout_indexes[6] + n_c2].V(-1);
            n_b2 -= 1;
            n_c2 += 1;
        }
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
                GL.cards[i].M(-1);
                GL.cards[i].V(-1);
            }
            for (i = 1; i < 13; i++) // повторная выдача недостающих карт игроку и боту
            {
                delivering_cards(i, n_b1, n_b2);
            }
            for (int step = 0; step < 7; step += 6) // цикл для постепенного перетаскивания доп.карт на слоты, ближние к левому краю экрана
                for (i = layout_indexes[11]; i > layout_indexes[6]; i--)
                {
                    if ((labels[i + step].getIcon() != null) && (labels[i + step- 1].getIcon() == null))
                    {
                        labels[i + step- 1].setIcon(labels[i + step].getIcon());
                        labels[i + step].setIcon(null);
                        GL.cards[i + step - 1].M(GL.cards[i + step].M());
                        GL.cards[i + step - 1].V(GL.cards[i + step].V());
                        GL.cards[i + step].M(-1);
                        GL.cards[i + step].V(-1);
                    }
                }
            for (i = 0; i < 6; i++) // обнуление списка значений величин карт на столе
            {
                GL.queue.player_values[i] = -1;
                GL.queue.bot_values[i] = -1;
            }
            GL.queue.hod(GL.queue.hod() * -1);
            GL.queue.sbros(GL.queue.sbros() * -1);
            if (labels[14].getIcon() == arrow[0]) // стрелка хода переворачивается при смене хода с игрока на бота
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            // После нажатия кнопки "Бито" бот начинает свой ход
            BotValues card = null;
            if ((GL.queue.sbros() == -1) && (GL.queue.hod() == -1))
                GL.bot.Bito();
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
            //else button1.doClick();
        }
    }
    // Действия, когда игрок нажимает кнопку "Беру"
    class Button2EventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int n_b1 = 0, n_b2 = 0;
            button1.setVisible(false);
            button2.setVisible(false);
            Message beru = new Message("БЕРУ");
            beru.setVisible(true);
            for (int i = layout_indexes[42]; i <= layout_indexes[47]; i++) // подсчет кол-ва доп.карт у игрока, который после нажатия кнопки "Беру" будет брать карты бота со стола
                if ((labels[i] != null) && (labels[i].getIcon() != null))
                    n_b1 += 1;
            for (int i = layout_indexes[6]; i <= layout_indexes[11]; i++) // подсчет кол-ва доп.карт у бота
                if ((labels[i] != null) && (labels[i].getIcon() != null))
                    n_b2 += 1;
            for (int i = 1; i < 7; i++) // выдача карт боту (в случае когда игрок берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
            {
                delivering_cards(i, n_b1, 0);
            }
            for (int i = 7; i < 13; i++)
                delivering_cards(i, 0, n_b2);
            int step = 2 * layout_indexes[42] - layout_indexes[12]; // вычисляемый шаг для сопоставления картам на столе слотов для доп.карт у игрока
            for (int i = layout_indexes[12]; i <= layout_indexes[17]; i += 2) // игрок берет карты бота со стола к себе в доп.карты
            {
                GL.queue.n_pictures += 1;
                labels[((i + step) / 2) + n_b1].setIcon(labels[i].getIcon());
                labels[((i + step) / 2) + n_b1].addMouseListener(new LabelClicker());
                Index index = new Index();
                index.I = ((i + step) / 2 + n_b1);
                labels[((i + step) / 2) + n_b1].SetTag(index);
                if (GL.cards[(i + step) / 2 + n_b1] == null)
                    GL.cards[(i + step) / 2 + n_b1] = new card();
                GL.queue.n_pictures += 1;
                GL.cards[(i + step) / 2 + n_b1].M(GL.cards[i].M());
                GL.cards[(i + step) / 2 + n_b1].V(GL.cards[i].V());
                GL.cards[i].M(-1);
                GL.cards[i].V(-1);
                labels[i].setIcon(null);
            }
            for (int i = 0; i < GL.queue.player_values.length; i++) // обнуление списка значений величин карт на столе
            {
                GL.queue.player_values[i] = -1;
                GL.queue.bot_values[i] = -1;
            }
            for (int i = 1; i < GL.cards.length; i++) // взаимодействие с массивами карт, которые располагаются на столе
            {
                if (((i >= layout_indexes[24]) && (i <= layout_indexes[29])) || ((i >= layout_indexes[12]) && (i <= layout_indexes[17]))) {
                    GL.cards[i].En(true);
                    labels[i].setIcon(null);
                }
            }
            if (GL.queue.sbros() != GL.queue.hod())
                GL.queue.sbros(GL.queue.hod());
            // После нажатия кнопки "Беру" бот начинает свой ход (продолжая прошлый ход)
            BotValues card = null;
            if ((GL.queue.sbros() == -1) && (GL.queue.hod() == -1))
                GL.bot.Bito();
            int n = 1;
            int k = 15;
            if (card != null) {
                labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                labels[n++].setVisible(false);
                button2.setVisible(true);
                button1.setVisible(false);
                k += 2;
            }
            //else button1.doClick();
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
            for (int i = 0; i < GL.queue.player_values.length; i++) // определяется возможность подкидывания карт на стол
            {
                if (GL.queue.hod() == 1)
                    if ((GL.cards[index].V() == GL.queue.player_values[i]) || (GL.cards[index].V() == GL.queue.bot_values[i]))
                    {
                        go = true;
                        break;
                    }
                //else
                //if (bot.Podkid(queue.player_values, queue.bot_values))
                //go = true;

            }
            if ((go) || (GL.queue.hod() == GL.queue.sbros()))
                GL.queue.sbros(GL.queue.sbros() * -1);// бот начинает биться (во время хода игрока)
            if (GL.queue.hod() == 1) // если ходит игрок
            {
                if (go) // если можно подкидывать карты
                {
                    labels[k].setIcon(labels[index].getIcon());
                    GL.queue.n_pictures += 1;
                    GL.cards[k].En(false);
                    GL.cards[k].M(GL.cards[index].M());
                    GL.cards[k].V(GL.cards[index].V());
                    GL.queue.player_values[(k-15) / 2] = GL.cards[k].V();
                    labels[index].setIcon(null);
                    GL.queue.sbros(GL.queue.sbros() * -1);
                    button2.setVisible(true);
                    button1.setVisible(false);
                }
            }
            else // если ходит бот
            {
                if ((GL.queue.sbros() == 1) || go) // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                    if (((GL.cards[k + 1].M() == GL.cards[index].M()) && (GL.cards[index].V() > GL.cards[k + 1].V()))
                            || ((GL.cards[index].M() == GL.bot.M_K) && (GL.cards[k + 1].M() != GL.bot.M_K)))
                    {
                        labels[k].setIcon(labels[index].getIcon());
                        GL.queue.n_pictures += 1;
                        GL.cards[k].En(false);
                        GL.cards[k].M(GL.cards[index].M());
                        GL.cards[k].V(GL.cards[index].V());
                        GL.queue.player_values[(k - 15) / 2] = GL.cards[k].V(); // заполнение массива карт игрока, скинутых на поле
                        labels[index].setIcon(null);
                        GL.queue.sbros(GL.queue.sbros() * -1);
                        for (int i = 0; i < 6; i++) // если у бота появляется непобитая им карта
                        {
                            if ((GL.queue.player_values[i] > -1) && (GL.queue.bot_values[i] == -1))
                            {
                                GL.queue.sbros(GL.queue.sbros() * -1);
                                break;
                            }
                        }
                        button1.setVisible(true);
                        button2.setVisible(false);
                    }
            }
            if ((GL.queue.hod() == 1) && (GL.queue.sbros() == -1))
            {
                GL.bot.AddEnemyValue(GL.cards[k].M(), GL.cards[k].V());
                int n = GL.bot.values.size();
                /*BotValues card = bot.Bito();
                if (card != null)
                {
                    labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                    labels[n--].setVisible(false);
                    button1.setVisible(true);
                    button2.setVisible(false);
                }

                 */
            }
        }
        @Override
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
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

class Index
{
    public int I;
}


