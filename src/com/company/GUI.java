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

// игровое поле, загружающееся после запуска игры из меню
public class GUI extends JFrame {
    private final static int[] layout_indexes = new int[]
            { 1, 2, 3, 4, 5, 6, 28, 29, 30, 31, 32, 33, 16, 18, 20, 22, 24, 26, 40, 41, 42, 43, 44, 13,
                    15, 17, 19, 21, 23, 25, 45, 46, 47, -1, -2, 14, 7, 8, 9, 10, 11, 12, 34, 35, 36, 37, 38, 39 };
    // раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg)
    CustomLabel[] labels = new CustomLabel[48];
    private final static ImageIcon[] arrow = new ImageIcon[2];
    static JButton button1 = new JButton("БИТО");
    static JButton button2 = new JButton("БЕРУ");
    private final static ImageIcon[] img = new ImageIcon[37];
    private final static boolean[] img_pos = new boolean[37];
    private final static String [] dirs = new String [] {"down", "up"};
    protected static File f;
    protected static boolean go;
    protected Game GL;
    protected Bot bot;
    protected Values card;
    private int n,k, n_b1, n_b2;
    protected Index index;
    protected int index_clicked;
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
        InitVariables();
        // инициализация labels
        for (int i = 1; i < labels.length; i++)
            labels[i] = new CustomLabel();
        // создание массива картинок
        for (int i = 0; i < img.length - 1; i++) {
            f = new File("src/Cards/" + Integer.toString(i) + ".jpg");
            if (f.exists())
                img[i] = new ImageIcon(f.toString());
            else throw new IOException("Картинка(и) с картами не найдены!");
            img_pos[i] = true;
        }
        // добавление стрелочки - показывающей, кто ходит
        for (int i = 0; i < dirs.length; i++) {
            f = new File("src/Cards/" + dirs[i] + ".png");
            if (f.exists())
                arrow[i] = new ImageIcon(f.toString());
            else throw new IOException("Картинка(и) со стрелками на найдены!");
        }
        // Помещение на форму всех карт и пустых слотов согласно раскладке в массиве layout_indexes (файл layout.jpg)
        bot = new Bot();
        // Инициализация объекта игры GL
        GL = new Game();
        // добавление элементов на форму согласно раскладке
        for (int value : layout_indexes) {
            if ((value < 14) && (value > 0)) {
                delivering_cards(value);
                if ((value > 6) && (value < 13)) {
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
        // установка козырной масти
        bot.setM_K(labels[13].getMast());
        labels[layout_indexes[(6*2 - 1) * 2]].setText("Осталось 24 карты ");
    }

    // метод, производящий вытягивание случайной оставшейся карты из колоды для всех возможных случаев игры
    public void delivering_cards(int i)  {
        n_b1 = 0;
        n_b2 = 0;
        int i_rand;
        // выдача карт из колоды в случае отсутствия доп.карт и у игрока, и у бота
        if ((labels[i].getIcon() == null) && (((i > 6) && (i < 14) && (n_b1 == 0)) || ((i < 7) && (n_b2 == 0)))) {
            if (!GL.isKoloda_empty()) {
                // если в колоде пока больше 1 карты
                if (GL.getN_koloda() > 1) {
                    do {
                        i_rand = new Random().nextInt(36);
                        // Если сгенерированная случайно карта есть в колоде
                        if (img_pos[i_rand]) {
                            labels[i].setMast(i_rand % 4);
                            labels[i].setValue(i_rand / 4);
                            // если это карты в рукаве у бота
                            if (i < 7) {
                                labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                                bot.AddValue(i_rand % 4, i_rand / 4);
                            }
                            // если это карты в рукаве у игрока
                            else {
                                labels[i].setIcon(img[i_rand]);
                                bot.AddPlayerValue(i_rand % 4, i_rand / 4);
                            }
                            // добавление тага для покрытия карт игрока событием клика
                            if ((labels[i].GetTag() == null) && (i > 6)) {
                                Index index = new Index();
                                index.setI(i);
                                labels[i].SetTag(index);
                            }
                        }
                    }
                    while (!img_pos[i_rand]);
                    img_pos[i_rand] = false;
                    // уменьшение количества карт в колоде во всех случаев, кроме карты, отображаемой на экране
                    if (i != 13)
                        GL.setN_koloda(GL.getN_koloda() - 1);
                    // вывод информации о количестве карт в колоде
                    if (GL.getN_koloda() > 1)
                        labels[layout_indexes[22]].setText("Осталось " + Integer.toString(GL.getN_koloda()) + " карт(ы)");
                    else
                        labels[layout_indexes[22]].setText("Карты в колоде закончились");
                }
                // если в колоде осталась 1 карта (а именно та, которая нарисована на экране)
                else if (GL.getN_koloda() == 1) {
                    labels[i].setMast(labels[13].getMast());
                    labels[i].setValue(labels[13].getValue());
                    if (i > 6)
                        labels[i].setIcon(labels[13].getIcon());
                    else labels[i].setIcon(new ImageIcon("src/Cards/front.jpg"));
                    // NullCard - очищение label и его полей
                    labels[13].NullCard();
                    labels[44].setText("Карты в колоде закончились");
                    GL.setKoloda_empty(true);
                }
            }
        }
        // выдача карт игроку в случае присутствия доп.карт у игрока
        else if  ((labels[i].getIcon() != null) && (n_b1 > 0) && (i > 33)) {
            TradeCard(i, 7, 13);
            TradeCard(i, 34, i);
        }
        // выдача карт боту в случае присутствия доп.карт у бота
        else if ((labels[i].getIcon() == null) && (n_b2 > 0) && (i < 7)) {
            TradeCard(i, 1, 7);
            TradeCard(i, 28, i);
        }
    }

    // Действия, когда игрок нажимает кнопку "Бито"
    class Button1EventListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            // кнопка Бито становится невидимой
            button1.setVisible(false);
            // очищение списков карт игрока и бота на столе
            bot.envalues.clear();
            bot.botvalues.clear();
            n_b1 = 0; n_b2 = 0 ;
            //кол-во дополнительно набранных карт игроком и ботом: n_b1 - кол-во доп. карт у игрока, n_b2 - у бота
            for (int i = 34; i <= 39; i++) {
                if (labels[i].getIcon() != null) n_b1 += 1; // подсчёт доп.карт игрока
                if (labels[i - 6].getIcon() != null) n_b2 += 1; // подсчёт доп.карт бота
            }
            // очистка карт на столе
            for (int i = 15; i < 27; i++)
                labels[i].NullCard();
            // шаг 27 получился как разница номера карты 28 и карты 1 (то есть первой среди дополнительных и основных карт)
            for (int step = 27; step > -1; step -= 27)
                // повторная выдача недостающих карт игроку и боту
            for (int i = 1; i < 13; i++) {
                delivering_cards(i);
            }
            // Смена хода и сброса
            GL.InverseHod();
            GL.InverseSbros();
            // стрелка хода переворачивается при смене хода с игрока на бота
            if (labels[14].getIcon() == arrow[0])
                labels[14].setIcon(arrow[1]);
            else
                labels[14].setIcon(arrow[0]);
            // После нажатия кнопки "Бито" бот начинает свой ход
            if (GL.hod() == -1)
            {
                GL.hod(-1);
                GL.sbros(-1);
                // Бот находит карту, с которой он будет начинать свой ход
                card = bot.Hod_Card();
                bot.botvalues.clear();
                k = 15 + bot.botvalues.size() * 2;
                n = 1 + bot.botvalues.size();
                // если карта для хода была найдена, то она сбрасывается на поле и загорается кнопка Беру
                if (card != null) {
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
                GL.InverseSbros();
            }
        }
    }

    // Действия, когда игрок нажимает кнопку "Беру"
    class Button2EventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // количество дополнительно набранных карт игроком (n_b1) и ботом (n_b2)
            n_b1 = 0; n_b2 = 0;
            button2.setVisible(false);
            // подсчет доп. карт игрока и бота
            for (int i = 34; i <= 39; i++) {
                if (labels[i].getIcon() != null) n_b1 += 1; // подсчёт доп.карт игрока
                if (labels[i - 6].getIcon() != null) n_b2 += 1; // подсчёт доп.карт бота
            }
            int step;
            // Если ходит бот
            if (GL.hod() == -1) {
                // вычисляемый шаг = 52 для сопоставления картам на столе слотов для доп.карт у игрока
                step  = 2 * layout_indexes[42] - layout_indexes[12];
                // игрок берет карты бота со стола к себе в доп.карты
                for (int i = 16; i <= 26; i += 2) {
                    // Если присутствует карта на слотах для карт бота на столе
                    if (labels[i].getIcon() != null) {
                        labels[(i + step) / 2 + n_b1].setMast(labels[i].getMast());
                        labels[(i + step) / 2 + n_b1].setValue(labels[i].getValue());
                        labels[((i + step) / 2) + n_b1].setIcon(labels[i].getIcon());
                        labels[((i + step) / 2) + n_b1].addMouseListener(new LabelClicker());
                        index = new Index();
                        index.setI((i + step) / 2 + n_b1);
                        labels[((i + step) / 2) + n_b1].SetTag(index);
                        labels[i].NullCard();
                    }
                }
            }
            // если ходит игрок
            else {
                // вычисляемый шаг = 41 для сопоставления картам на столе слотов для доп.карт у игрока
                step = 2 * layout_indexes[6] - layout_indexes[24];
                // бот берет карты игрока со стола к себе в доп.карты
                for (int i = 15; i <= 25; i += 2) {
                    if (labels[i].getIcon() != null)
                    {
                        labels[(i + step) / 2 + n_b2].setMast(labels[i].getMast());
                        labels[(i + step) / 2 + n_b2].setValue(labels[i].getValue());
                        labels[((i + step) / 2) + n_b2].setIcon(new ImageIcon("src/Cards/front.jpg"));
                        labels[i].NullCard();
                    }
                }
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
            // если ходит игрок
            if (GL.hod() == 1) {
                // выдача карт игроку (в случае когда бот берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
                for (int i = 7; i < 13; i++) {
                    delivering_cards(i);
                }
                GL.hod(1);
                GL.sbros(1);
            }
            // если ходит бот
            else {
                // выдача карт боту (в случае когда игрок берет карты, ему из колоды, соответственно, выдавать ничего не нужно)
                for (int i = 1; i < 7; i++) {
                    delivering_cards(i);
                    GL.hod(-1);
                    GL.hod(-1);
                    // Бот ищет карту, с которой он начнет свой новый (очередной) ход
                    card = bot.Hod_Card();
                    bot.botvalues.clear();
                    k = 15 + bot.botvalues.size() * 2;
                    n = 1 + bot.botvalues.size();
                    if (card != null) {
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
            }
            // взаимодействие с массивами карт, которые располагаются на столе
            for (int i = 1; i < labels.length; i++) {
                if (((i >= layout_indexes[24]) && (i <= layout_indexes[29])) || ((i >= layout_indexes[12]) && (i <= layout_indexes[17]))) {
                    labels[i].setEnabled(true);
                    labels[i].NullCard();
                }
            }
            // очищение списка карт бота и игрока на столе
            if (bot.envalues.size() > 0) {
                bot.envalues.subList(0, bot.envalues.size()).clear();
            }
            if (bot.botvalues.size() > 0) {
                bot.botvalues.subList(0, bot.botvalues.size()).clear();
            }
            if (GL.sbros() != GL.hod())
                GL.sbros(GL.hod());
        }
    }
    // метод, осуществляющий сброс карты игрока на поле
    public void ThrowCardPlayer(int k, int index)
    {
        labels[k].setIcon(labels[index].getIcon());
        labels[k].setEnabled(false);
        labels[k].setMast(labels[index].getMast());
        labels[k].setValue(labels[index].getValue());
        bot.AddEnemyValue(labels[k].getMast(), labels[k].getValue());
        labels[index].NullCard();
    }
    // Карты игрока (а именно карты с 7 по 12 по раскладке) имеют событие клика.
    // Также событие клика будет появляться у карт, которые игрок будет брать у бота и помещать в дополнительные слоты 34-39
    class LabelClicker implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            CustomLabel p = (CustomLabel) e.getSource();
            // получение индекса нажатой карты
            index_clicked = ((Index) (p.GetTag())).getI();
            // Если игрок нажал на существую карту, а не на пустой слот
            if (labels[index_clicked].getIcon() != null) {
                // Если игрок нажал на ЛКМ
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (GL.hod() == GL.sbros())
                        GL.sbros(GL.sbros() * -1);
                    // бот начинает биться (во время хода игрока)
                    if (GL.hod() == 1) {
                        // Определяется возможность для игрока подкидывания карт боту
                        go = bot.PodkidtoBot(labels[index_clicked].getValue());
                        // Если бот начинает крыться от карт игрока
                        if (GL.sbros() == -1) {
                            if (go) {
                                n = 1 + bot.botvalues.size();
                                k = 15 + bot.botvalues.size() * 2;
                                ThrowCardPlayer(k, index_clicked);
                                bot.AddEnemyValue(labels[k].getMast(), labels[k].getValue());
                                card = null;
                                // Бот ищет у себя в рукаве карту, которой он может побиться
                                card = bot.Bito_Card();
                                // Если он нашел такую карту
                                if (card != null) {
                                    bot.AddBotValue(card.getMast(), card.getValue());
                                    labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
                                    labels[n].setIcon(null);
                                    button2.setVisible(false);
                                    button1.setVisible(true);
                                }
                                else {
                                    bot.AddValue(labels[k].getMast(), labels[k].getValue());
                                    bot.envalues.remove(bot.envalues.size() - 1);
                                    button2.doClick();
                                }
                            }
                        }
                    }
                    // if (GL.hod() = -1) если ходит бот
                    else {
                        n = 1 + (bot.botvalues.size() - 1);
                        k = 15 + (bot.botvalues.size() - 1) * 2;
                        // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                            if (((labels[k + 1].getMast() == labels[index_clicked].getMast()) && (labels[index_clicked].getValue() > labels[k + 1].getValue()))
                                    || ((labels[index_clicked].getMast() == bot.getM_K()) && (labels[k + 1].getMast() != bot.getM_K()))) {
                                // Игрок кроется физически от карты бота
                                ThrowCardPlayer(k, index_clicked);
                                // имитация того, что бот какое-то время думает
                                try {
                                    Thread.sleep(300);
                                }
                                catch(InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                                GL.InverseSbros();
                                // Бот решает, будет ли он что-то подкидывать
                                card = bot.PodkidtoPlayer();
                                // если бот нашел карту, которую он может подкинуть игроку
                                if (card != null) {
                                    k += 2;
                                    index_clicked =  4 * card.getValue() + card.getMast();
                                    try {
                                        labels[k + 1].setIcon(img[index_clicked]);
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex) {}
                                    labels[++n].setIcon(null);
                                    labels[k + 1].setMast(card.getMast());
                                    labels[k + 1].setValue(card.getValue());
                                    bot.AddBotValue(card.getMast(), card.getValue());
                                    button2.setVisible(true);
                                    GL.InverseSbros();
                                }
                                else
                                    button1.doClick();
                            }
                    }
                    try{
                        labels[index_clicked].setClicked(false);
                    }
                    catch (NullPointerException ex) {}
                }
                // Если игрок нажимает ПКМ (то карта немножко перемещается вверх) и обратно в слот, если она была в приподнятом состоянии
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!labels[index_clicked].isClicked()) {
                        labels[index_clicked].setClicked(true);
                        labels[index_clicked].setLocation(labels[index_clicked].getLocation().x, labels[index_clicked].getLocation().y - 30);
                    }
                    else {
                        labels[index_clicked].setClicked(false);
                        labels[index_clicked].setLocation(labels[index_clicked].getLocation().x, labels[index_clicked].getLocation().y + 30);
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
    // метод, который осуществляет перетаскивание карт с дополнительных слотов на слоты, которые ближе к левому краю экрана
    public void TradeCard(int index, int index_bot, int index_top) {
        if (labels[index].getIcon() != null) {
            // цикл, который ищет пустой самый левый слот
            while (index_bot < index_top) {
                if (labels[index_bot].getIcon() == null) {
                    labels[index_bot].setMast(labels[index].getMast());
                    labels[index_bot].setValue(labels[index].getValue());
                    labels[index_bot].setIcon(labels[index].getIcon());
                    labels[index].NullCard();
                    if ((index_bot == 7) || ((index_top >= 28) && (index_top < 34)))
                        n_b2 -= 1;
                    else if ((index_bot == 13) || (index_bot >= 34))
                        n_b1 -= 1;
                    break;
                }
                index_bot += 1;
            }
        }
    }
    // Присваивание значений переменным (метод useless)
    public void InitVariables() {
        n = 0;
        n_b1 = 0;
        n_b2 = 0;
    }
}

// вспомогательный класс для работы с индексами нажатых карт
class Index {
    public void setI(int i) {
        I = i;
    }
    public int getI() {
        return I;
    }
    private int I;
}
// класс наследует Jlabel, а его объекты представляют собой карты
class CustomLabel extends JLabel {
    private Object Tag;
    protected final File f;
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
    public CustomLabel() {
        this.Mast =  -1;
        this.Value = -1;
        this.Tag = null;
        this.f = null;
        this.Clicked = false;
    }
    // метод вызывается в том случае, когда необходимо очистить слот
    public void NullCard() {
        this.Mast = -1;
       this.Value = -1;
       this.setIcon(null);
    }
}


