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
    static ImageIcon[] img = new ImageIcon[37];
    static boolean[] img_pos = new boolean[37];
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
        // Инициализация объекта игры GL
        GL = new Game();
        // инициализация labels
        for (int i = 1; i < labels.length; i++)
            labels[i] = new CustomLabel();
        for (int i = 1; i < img_pos.length; i++) // создание массива картинок
        {
            f = new File("src/Cards/" + Integer.toString(i) + ".jpg");
            if (f.exists())
                img[i] = new ImageIcon(f.toString());
            else throw new IOException("Картинка(и) с картами не найдены!");
            img_pos[i - 1] = true;
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
                    bot.AddValue(labels[value].getMast(), labels[value].getValue());
            }
            else if (value < 0)
            {
                if (value == -1)
                    container.add(button1);
                else container.add(button2);
            }
            if (value > 0)
                container.add(labels[value]);
        }
        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось 24 карты");
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
        if ((labels[i].getIcon() == null) && (((i > 6) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0))))// выдача карт из колоды в случае отсутствия доп.карт и у игрока, и у бота
        {
            if (!GL.isKoloda_empty())
            {
                if (GL.getN_koloda() > 1) // если в колоде пока больше 1 карты
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
                            labels[i].setMast(k % 4);
                            labels[i].setValue(k / 4);
                            labels[i].setIcon(img[k]);
                            if ((labels[i].GetTag() == null) && ((i < 13) || ((i > 27) && (i < 40))))
                            {
                                Index index = new Index();
                                index.setI(i);
                                labels[i].SetTag(index);
                            }
                        }
                    }
                    while (!img_pos[k]);
                    img_pos[k] = false;
                    GL.setN_koloda(GL.getN_koloda() - 1);
                    if (GL.getN_koloda() > 1)
                        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось " + Integer.toString(GL.getN_koloda()) + " карт(ы)");
                    else
                        labels[layout_indexes[(6*2 - 1) * 2]].setText("Карты в колоде закончились");
                }
                else if (GL.getN_koloda() == 1) // если в колоде осталась 1 карта (а именно та, которая нарисована на экране)
                {
                    PassCards((6*2 - 1) * 2 + 1, 0);
                    GL.setKoloda_empty(true);
                }
            }
        }
        else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b1 > 0) && (i > 6)) // выдача карт игроку в случае присутствия доп.карт у игрока
        {
            // n_c1 - инкремент прохода по доп.картам игрока
            PassCards(42, n_c1);
            n_b1 -= 1;
            n_c1 += 1;
        }
        else if ((labels[i] != null) && (!labels[i].isVisible()) && (n_b2 > 0) && (i < 7)) // выдача карт боту в случае присутствия доп.карт у бота
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
                labels[i].NullCard();

            for (i = 1; i < 13; i++) // повторная выдача недостающих карт игроку и боту
            {
                try {
                    delivering_cards(i, n_b1, n_b2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            for (int step = 0; step < 7; step += 6) // цикл для постепенного перетаскивания доп.карт на слоты, ближние к левому краю экрана (очищение доп.слотов от избыточных карт)
                for (i = layout_indexes[11]; i > layout_indexes[6]; i--)
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
            for (i = 0; i < 6; i++) // обнуление списка значений величин карт на столе
            {
                GL.player_values[i] = -1;
                GL.bot_values[i] = -1;
            }
            GL.hod(GL.hod() * -1);
            GL.sbros(GL.sbros() * -1);
            if (labels[14].getIcon() == arrow[0]) // стрелка хода переворачивается при смене хода с игрока на бота
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            // После нажатия кнопки "Бито" бот начинает свой ход
            BotValues card = null;
            if ((GL.sbros() == -1) && (GL.hod() == -1)) {
                try {
                    card = GL.bot.Bito(card);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
                try {
                    delivering_cards(i, n_b1, 0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            for (int i = 7; i < 13; i++) {
                try {
                    delivering_cards(i, 0, n_b2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            int step = 2 * layout_indexes[42] - layout_indexes[12]; // вычисляемый шаг для сопоставления картам на столе слотов для доп.карт у игрока
            for (int i = layout_indexes[12]; i <= layout_indexes[17]; i += 2) // игрок берет карты бота со стола к себе в доп.карты
            {
                GL.setN_pictures(GL.getN_pictures() + 1);
                labels[((i + step) / 2) + n_b1].setIcon(labels[i].getIcon());
                labels[((i + step) / 2) + n_b1].addMouseListener(new LabelClicker());
                Index index = new Index();
                index.setI((i + step) / 2 + n_b1);
                labels[((i + step) / 2) + n_b1].SetTag(index);
                GL.setN_pictures(GL.getN_pictures() + 1);
                labels[(i + step) / 2 + n_b1].setMast(labels[i].getMast());
                labels[(i + step) / 2 + n_b1].setValue(labels[i].getValue());
                labels[i].NullCard();
            }
            for (int i = 0; i < GL.player_values.length; i++) // обнуление списка значений величин карт на столе
            {
                GL.player_values[i] = -1;
                GL.bot_values[i] = -1;
            }
            for (int i = 1; i < labels.length; i++) // взаимодействие с массивами карт, которые располагаются на столе
            {
                if (((i >= layout_indexes[24]) && (i <= layout_indexes[29])) || ((i >= layout_indexes[12]) && (i <= layout_indexes[17]))) {
                    labels[i].setEnabled(true);
                    labels[i].NullCard();
                }
            }
            if (GL.sbros() != GL.hod())
                GL.sbros(GL.hod());
            // После нажатия кнопки "Беру" бот начинает свой ход (продолжая прошлый ход)
            BotValues card = null;
            if ((GL.sbros() == -1) && (GL.hod() == -1)) {
                try {
                    card = GL.bot.Bito(card);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
    public void PassLabels(int k, int index)
    {
        labels[k].setIcon(labels[index].getIcon());
        GL.setN_pictures(GL.getN_pictures() + 1);
        labels[k].setEnabled(false);
        labels[k].setMast(labels[index].getMast());
        labels[k].setValue(labels[index].getValue());
        GL.player_values[(k-15) / 2] = labels[k].getValue();
        labels[index].NullCard();
        GL.sbros(GL.sbros() * -1);
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
            int k = layout_indexes[24];
            boolean go = true; // переменная go - отвечает за подкидывание карт на стол
            while ((labels[k].getIcon() != null) && (index > 6))
            {
                k += 2;
                go = false;
            }
            for (int i = 0; i < GL.player_values.length; i++) // определяется возможность подкидывания карт на стол
            {
                if (GL.hod() == 1)
                    if ((labels[index].getValue() == GL.player_values[i]) || (labels[index].getValue() == GL.bot_values[i]))
                    {
                        go = true;
                        break;
                    }
                //else
                //if (bot.Podkid(queue.player_values, queue.bot_values))
                //go = true;

            }
            if ((go) || (GL.hod() == GL.sbros()))
                GL.sbros(GL.sbros() * -1);// бот начинает биться (во время хода игрока)
            if (GL.hod() == 1) // если ходит игрок
            {
                if (go) // если можно подкидывать карты
                {
                    PassLabels(k,index);
                    button2.setVisible(true);
                    button1.setVisible(false);
                }
            }
            else // если ходит бот
            {
                if ((GL.sbros() == 1) || go) // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                    if (((labels[k + 1].getMast() == labels[index].getMast()) && (labels[index].getValue() > labels[k + 1].getValue()))
                            || ((labels[index].getMast() == GL.bot.getM_K()) && (labels[k + 1].getMast() != GL.bot.getM_K())))
                    {
                        PassLabels(k ,index);
                        for (int i = 0; i < 6; i++) // если у бота появляется непобитая им карта
                        {
                            if ((GL.player_values[i] > -1) && (GL.bot_values[i] == -1))
                            {
                                GL.sbros(GL.sbros() * -1);
                                break;
                            }
                        }
                        button1.setVisible(true);
                        button2.setVisible(false);
                    }
            }
            if ((GL.hod() == 1) && (GL.sbros() == -1))
            {
                GL.bot.AddEnemyValue(labels[k].getMast(), labels[k].getValue());
                int n = GL.bot.values.size();
                /*BotValues card = bot.Bito();
                if (card != null)
                {
                    labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                    labels[n--].setVisible(false);
                    button1.setVisible(true);
                    button2.setVisible(false);
                } */
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
    public void setIcon(ImageIcon icon){
        this.icon = icon;
    }
    private ImageIcon icon;
    @Override
    public boolean isEnabled() {
        return Enabled;
    }
    @Override
    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }
    private boolean Enabled;

    public CustomLabel()
    {
        this.Mast = -1;
        this.Value = -1;
        this.icon = null;
        this.Tag = null;
        this.f = null;
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


