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
    private static int[] layout_indexes = new int[]
            { 1, 2, 3, 4, 5, 6, 28, 29, 30, 31, 32, 33, 16, 18, 20, 22, 24, 26, 40, 41, 42, 43, 44, 13,
                    15, 17, 19, 21, 23, 25, 45, 46, 47, -1, -2, 14, 7, 8, 9, 10, 11, 12, 34, 35, 36, 37, 38, 39 };
    // раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg)
    CustomLabel[] labels = new CustomLabel[48];
    private static ImageIcon[] arrow = new ImageIcon[2];
    static JButton button1 = new JButton("БИТО");
    static JButton button2 = new JButton("БЕРУ");
    private static ImageIcon[] img = new ImageIcon[37];
    private static boolean[] img_pos = new boolean[37];
    private static String [] dirs = new String [] {"down", "up"};
    private static File f;
    private static boolean go;
    Game GL;
    Bot bot;
    BotValues card;
    int n,k;
    Index index;
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
        // инициализация labels
        for (int i = 1; i < labels.length; i++)
            labels[i] = new CustomLabel();
        for (int i = 0; i < img.length - 1; i++) // создание массива картинок
        {
            f = new File("src/Cards/" + Integer.toString(i) + ".jpg");
            if (f.exists())
                img[i] = new ImageIcon(f.toString());
            else throw new IOException("Картинка(и) с картами не найдены!");
            img_pos[i] = true;
        }
        for (int i = 0; i < dirs.length; i++)
        {
            f = new File("src/Cards/" + dirs[i] + ".png");
            if (f.exists())
            {
                arrow[i] = new ImageIcon(f.toString());
            }
            else throw new IOException("Картинка(и) со стрелками на найдены!");
        }
        // Помещение на форму всех карт и пустых слотов согласно раскладке в массиве layout_indexes (файл layout.jpg)
        bot = new Bot();
        // Инициализация объекта игры GL
        GL = new Game();
        for (int value : layout_indexes) {
            if ((value < 14) && (value > 0)) {
                delivering_cards(value, 0, 0);
                if ((value > 6) && (value < 13))
                {
                    bot.AddPlayerValue(labels[value].getMast(), labels[value].getValue());
                    labels[value].addMouseListener(new LabelClicker());
                }
                else if (value < 7)
                    bot.AddValue(labels[value].getMast(), labels[value].getValue());
            } else if (value < 0) {
                if (value == -1)
                    container.add(button1);
                else container.add(button2);
            }
            if (value == 14)
                labels[value].setIcon(arrow[GL.hod()]);
            if (value > 0)
                container.add(labels[value]);
        }
        bot.setM_K(labels[layout_indexes[(6*2 - 1) * 2 + 1]].getMast());
        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось 24 карты ");
    }

    public void PassCards(int index, int step)
    {
        labels[index].setMast(labels[layout_indexes[42] + step].getMast());
        labels[index].setValue(labels[layout_indexes[42] + step].getValue());
        labels[index].setIcon(labels[layout_indexes[42] + step].getIcon());
        labels[layout_indexes[42] + step].NullCard();
        labels[index].setVisible(false);
    }
    // метод, производящий вытягивание случайной оставшейся карты из колоды в нужный момент времени
    public void delivering_cards(int i, int n_b1, int n_b2) throws IOException {
        int n_c1 = 0, n_c2 = 0;
        int i_rand;
        if ((labels[i].getIcon() == null) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0))))// выдача карт из колоды в случае отсутствия доп.карт и у игрока, и у бота
        {
            if (!GL.isKoloda_empty())
            {
                if (GL.getN_koloda() > 1) // если в колоде пока больше 1 карты
                {
                    do
                    {
                        i_rand = new Random().nextInt(36);
                        if (img_pos[i_rand])
                        {
                            labels[i].setMast(i_rand % 4);
                            labels[i].setValue(i_rand / 4);
                            if ((i < 7) || ((i >= layout_indexes[6]) && (i <= layout_indexes[11])))
                            {
                                labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                                //bot.AddValue(labels[i].getMast(), labels[i].getValue());
                            }
                            else
                            {
                                labels[i].setIcon(img[i_rand]);
                                bot.AddPlayerValue(labels[i].getMast(), labels[i].getValue());
                            }
                            if ((labels[i].GetTag() == null) && ((i < 13) || ((i > 27) && (i < 40))))
                            {
                                Index index = new Index();
                                index.setI(i);
                                labels[i].SetTag(index);
                            }
                        }
                    }
                    while (!img_pos[i_rand]);
                    img_pos[i_rand] = false;
                    GL.setN_koloda(GL.getN_koloda() - 1);
                    if (GL.getN_koloda() > 1)
                        labels[layout_indexes[22]].setText("Осталось " + Integer.toString(GL.getN_pictures()) + " карт(ы)");
                    else
                        labels[layout_indexes[22]].setText("Карты в колоде закончились");
                }
                else if (GL.getN_koloda() == 1) // если в колоде осталась 1 карта (а именно та, которая нарисована на экране)
                {
                    PassCards(23, 0);
                    GL.setKoloda_empty(true);
                }
            }
        }
        else if  ((labels[i].getIcon() == null) && (n_b1 > 0) && (i > 6)) // выдача карт игроку в случае присутствия доп.карт у игрока
        {
            // n_c1 - инкремент прохода по доп.картам игрока
            PassCards(42, n_c1);
            n_b1 -= 1;
            n_c1 += 1;
        }
        else if ((labels[i].getIcon() == null) && (n_b2 > 0) && (i < 7)) // выдача карт боту в случае присутствия доп.карт у бота
        {
            // n_c2 - инкремент прохода по доп.картам бота
            PassCards(6, n_c2);
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
            button1.setVisible(false);
            button2.setVisible(false);
            // очищение списка карт игрока на столе
            bot.envalues.clear();
            bot.botvalues.clear();
            int n_b1 = 0, n_b2 = 0, n_c1 = 0, n_c2 = 0; // кол-во дополнительно набранных карт игроком и ботом
            //n_b1 - кол-во доп. карт у игрока, n_b2 - у бота
            for (int i = layout_indexes[42]; i <= layout_indexes[47]; i++)
            {
                if (labels[i].getIcon() != null) n_b1 += 1; // подсчёт доп.карт игрока
                if (labels[i - 6].getIcon() != null) n_b2 += 1; // подсчёт доп.карт бота
            }
            for (int i = 15; i < 27; i++) // очистка карт на столе
                labels[i].NullCard();
            for (int step = 0; step < 7; step += 6) // цикл для постепенного перетаскивания доп.карт на слоты, ближние к левому краю экрана (очищение доп.слотов от избыточных карт)
                for (int i = layout_indexes[11]; i > layout_indexes[6]; i--)
                {
                    if ((labels[i + step].getIcon() != null) && (labels[i + step- 1].getIcon() == null))
                    {
                        // Оптимизация кода!
                        labels[i + step - 1].setMast(labels[i + step].getMast());
                        labels[i + step - 1].setValue(labels[i + step].getValue());
                        labels[i + step- 1].setIcon(labels[i + step].getIcon());
                        labels[i + step].NullCard();
                    }
                }
            for (int i = 1; i < 13; i++) // повторная выдача недостающих карт игроку и боту
            {
                try {
                    delivering_cards(i, n_b1, n_b2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            GL.InverseHod();
            GL.InverseSbros();
            if (labels[14].getIcon() == arrow[0]) // стрелка хода переворачивается при смене хода с игрока на бота
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            // После нажатия кнопки "Бито" бот начинает свой ход
            if (GL.hod() == -1)
            {
                GL.hod(-1);
                GL.sbros(-1);
                card = bot.Hod_Card();
                bot.botvalues.clear();
                k = 15 + bot.botvalues.size() * 2;
                n = 1 + bot.botvalues.size();
                if (card != null)
                {
                    int index =  4 * card.getValue() + card.getMast();
                    try {
                        labels[k + 1].setIcon(img[index]);
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {}
                    labels[k + 1].setMast(card.getMast());
                    labels[k + 1].setValue(card.getValue());
                    labels[n].setIcon(null);
                    bot.AddBotValue(card.getMast(), card.getValue());
                    button2.setVisible(true);
                    button1.setVisible(false);
                }
                GL.sbros(GL.sbros() * -1);
            }
            else
            {

            }
        }
    }

    // Действия, когда игрок нажимает кнопку "Беру"
    class Button2EventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int n_b1 = 0, n_b2 = 0;
            button1.setVisible(false);
            button2.setVisible(false);
            for (int i = layout_indexes[42]; i <= layout_indexes[47]; i++) // подсчет кол-ва доп.карт у игрока, который после нажатия кнопки "Беру" будет брать карты бота со стола
                if (labels[i].getIcon() != null)
                    n_b1 += 1;
            for (int i = layout_indexes[6]; i <= layout_indexes[11]; i++) // подсчет кол-ва доп.карт у бота
                if (labels[i].getIcon() != null)
                    n_b2 += 1;
            if (GL.hod() == 1)
            {
                for (int i = 7; i < 13; i++) { // выдача карт игроку (в случае когда бот берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
                    try {
                        delivering_cards(i, n_b1, 0);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            int step = 2 * layout_indexes[42] - layout_indexes[12]; // вычисляемый шаг для сопоставления картам на столе слотов для доп.карт у игрока
            if (GL.hod() == -1)
            for (int i = layout_indexes[12]; i <= layout_indexes[17]; i += 2) // игрок берет карты бота со стола к себе в доп.карты
            {
                //GL.setN_pictures(GL.getN_pictures() + 1);
                labels[((i + step) / 2) + n_b1].setIcon(labels[i].getIcon());
                labels[((i + step) / 2) + n_b1].addMouseListener(new LabelClicker());
                Index index = new Index();
                index.setI((i + step) / 2 + n_b1);
                labels[((i + step) / 2) + n_b1].SetTag(index);
                labels[(i + step) / 2 + n_b1].setMast(labels[i].getMast());
                labels[(i + step) / 2 + n_b1].setValue(labels[i].getValue());
                labels[i].NullCard();
            }
            step = 2 * layout_indexes[6] - layout_indexes[24]; // вычисляемый шаг для сопоставления картам на столе слотов для доп.карт у игрока
            if (GL.hod() == 1)
            for (int i = layout_indexes[24]; i <= layout_indexes[29]; i += 2) // игрок берет карты бота со стола к себе в доп.карты
            {
                //GL.setN_pictures(GL.getN_pictures() + 1);
                if (labels[i].getIcon() != null)
                    labels[((i + step) / 2) + n_b2].setIcon(new ImageIcon("src/Cards/front.jpg"));
                labels[(i + step) / 2 + n_b2].setMast(labels[i].getMast());
                labels[(i + step) / 2 + n_b2].setValue(labels[i].getValue());
                bot.AddValue(labels[i].getMast(), labels[i].getValue());
                labels[i].NullCard();
            }
            // В зависимости от того, кто ходит, бот или игрок забирают к себе в список карт карты бота со стола
            for (int i = 0; i < bot.botvalues.size(); i++)
                if (GL.hod() == 1)
                    bot.values.add(bot.botvalues.get(i));
                else bot.playervalues.add(bot.botvalues.get(i));
            // В зависимости от того, кто ходит, бот или игрок забирают к себе в список карт карты бота со стола
            for (int i = 0; i < bot.envalues.size(); i++)
                if (GL.hod() == 1)
                    bot.values.add(bot.envalues.get(i));
                else bot.playervalues.add(bot.envalues.get(i));
            if (GL.hod() == -1)
                for (int i = 1; i < 7; i++) // выдача карт боту (в случае когда игрок берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
                {
                    try {
                        delivering_cards(i, 0, n_b2);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    GL.hod(-1);
                    GL.hod(-1);
                    card = bot.Hod_Card();
                    bot.botvalues.clear();
                    k = 15 + bot.botvalues.size() * 2;
                    n = 1 + bot.botvalues.size();
                    if (card != null)
                    {
                        int index =  4 * card.getValue() + card.getMast();
                        try {
                            labels[k + 1].setIcon(img[index]);
                        }
                        catch (ArrayIndexOutOfBoundsException ex) {}
                        labels[k + 1].setMast(card.getMast());
                        labels[k + 1].setValue(card.getValue());
                        labels[n].setIcon(null);
                        bot.AddBotValue(card.getMast(), card.getValue());
                        button2.setVisible(true);
                        button1.setVisible(false);
                    }
                    GL.sbros(GL.sbros() * -1);
                }

            for (int i = 1; i < labels.length; i++) // взаимодействие с массивами карт, которые располагаются на столе
            {
                if (((i >= layout_indexes[24]) && (i <= layout_indexes[29])) || ((i >= layout_indexes[12]) && (i <= layout_indexes[17]))) {
                    labels[i].setEnabled(true);
                    labels[i].NullCard();
                }
            }
            // очищение списка карт бота и игрока на столе
            for (int i = 0; i < bot.envalues.size(); i++)
                bot.envalues.remove(0);
            for (int i = 0; i < bot.botvalues.size(); i++)
                bot.botvalues.remove(0);
            if (GL.sbros() != GL.hod())
                GL.sbros(GL.hod());
        }
    }
    public void ThrowCardPlayer(int k, int index)
    {
        labels[k].setIcon(labels[index].getIcon());
        GL.setN_pictures(GL.getN_pictures() + 1);
        labels[k].setEnabled(false);
        labels[k].setMast(labels[index].getMast());
        labels[k].setValue(labels[index].getValue());
        bot.AddPlayerValue(labels[k].getMast(), labels[k].getValue());
        labels[index].NullCard();
    }
    // Карты игрока (а именно карты с 7 по 12 по раскладке) имеют событие клика.
    // Также событие клика будет появляться у карт, которые игрок будет брать у бота и помещать в дополнительные слоты 34-39)
    class LabelClicker implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            CustomLabel p = (CustomLabel) e.getSource();
            int index = ((Index) (p.GetTag())).getI(); // получение индекса нажатой карты
            if (labels[index].getIcon() != null)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    if (GL.hod() == GL.sbros())
                        GL.sbros(GL.sbros() * -1);// бот начинает биться (во время хода игрока)
                    if (GL.hod() == 1) // если ходит игрок
                    {
                        go = bot.PodkidtoBot(labels[index].getValue());
                        if (GL.sbros() == -1)
                        {
                            if (go)
                            {
                                n = 1 + bot.botvalues.size();
                                k = 15 + bot.botvalues.size() * 2;
                                ThrowCardPlayer(k, index);
                                bot.AddEnemyValue(labels[k].getMast(), labels[k].getValue());
                                card = null;
                                card = bot.Bito_Card();
                                if (card != null) {
                                    bot.AddBotValue(card.getMast(), card.getValue());
                                    labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                                    labels[n].setIcon(null);
                                    button2.setVisible(false);
                                    button1.setVisible(true);
                                }
                                else
                                {
                                    bot.AddValue(labels[k].getMast(), labels[k].getValue());
                                    bot.envalues.remove(bot.envalues.size() - 1);
                                    button2.doClick();
                                }
                            }
                        }
                    }
                    else // if (GL.hod() = -1) если ходит бот
                    {
                        n = 1 + (bot.botvalues.size() - 1);
                        k = 15 + (bot.botvalues.size() - 1) * 2;
                        if (GL.sbros() == 1) // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                            if (((labels[k + 1].getMast() == labels[index].getMast()) && (labels[index].getValue() > labels[k + 1].getValue()))
                                    || ((labels[index].getMast() == bot.getM_K()) && (labels[k + 1].getMast() != bot.getM_K())))
                            {
                                // имитация того, что бот какое-то время думает
                                ThrowCardPlayer(k, index);
                                try
                                {
                                    Thread.sleep(300);
                                }
                                catch(InterruptedException ex)
                                {
                                    Thread.currentThread().interrupt();
                                }
                                // Бот решает, будет ли он что-то подкидывать
                                GL.InverseSbros();
                                card = bot.PodkidtoPlayer();
                                if (card != null) // если он нашёл, что ему подкинуть
                                {
                                    k = k + 2;
                                    index =  4 * card.getValue() + card.getMast();
                                    try {
                                        labels[k + 1].setIcon(img[index]);
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex) {}
                                    labels[++n].setIcon(null);
                                    labels[k + 1].setMast(card.getMast());
                                    labels[k + 1].setValue(card.getValue());
                                    bot.AddBotValue(card.getMast(), card.getValue());
                                    button2.setVisible(true);
                                    GL.InverseSbros();
                                }
                                else button1.doClick();
                            }
                    }
                    labels[index].setClicked(false);
                }
                else if (e.getButton() == MouseEvent.BUTTON3)
                {
                    if (!labels[index].isClicked())
                    {
                        labels[index].setClicked(true);
                        labels[index].setLocation(labels[index].getLocation().x, labels[index].getLocation().y - 30);
                    }
                    else
                    {
                        labels[index].setClicked(false);
                        labels[index].setLocation(labels[index].getLocation().x, labels[index].getLocation().y + 30);
                    }
                }
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
    private Object Tag;
    private File f;
    public Object GetTag() {
        return Tag;
    }
    public void SetTag(Object Tag) {
        this.Tag = Tag;
    }
    public int getMast() {
        return Mast;
    }
    public void setMast(int mast) {
        Mast = mast;
    }

    private int Mast;
    public int getValue() {
        return Value;
    }
    public void setValue(int value) {
        Value = value;
    }
    private int Value;

    public void setIconImage(ImageIcon icon){
       this.icon = icon;
    }
    private ImageIcon icon;
    public void setClicked(boolean clicked) {
        Clicked = clicked;
    }
    public boolean isClicked() {
        return Clicked;
    }

    private boolean Clicked;
    public boolean getEnabled() {
        return Enabled;
    }
    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }
    private boolean Enabled;

    public CustomLabel()
    {
        this.Mast =  -1;
        this.Value = -1;
        this.icon = null;
        this.Tag = null;
        this.f = null;
        this.Clicked = false;
    }

    public void NullCard()
    {
        this.Mast = -1;
       this.Value = -1;
       this.setIcon(null);
    }
}

class Index
{
    public void setI(int i) {
        I = i;
    }
    public int getI() {
        return I;
    }
    private int I;
}

