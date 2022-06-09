package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

// игровое поле, загружающееся после запуска игры из меню
public class GUI extends JFrame {
    private final static int[] layout_indexes = new int[]
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 26, 28, 30, 32, 34, 36, 37, 38, 39, 40, 41, 42,
                    25, 27, 29, 31, 33, 35, 43, 44, -1, -2, 45, 46, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
    // раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg)
    CustomLabel[] labels = new CustomLabel[48];
    private final static ImageIcon[] arrow = new ImageIcon[2];
    static JButton button1 = new JButton("БИТО");
    static JButton button2 = new JButton("БЕРУ");
    private final static ImageIcon[] img = new ImageIcon[37];
    private final static boolean[] img_pos = new boolean[37];
    private final static String[] dirs = new String[]{"down", "up"};
    protected final static String path_output = "src/output/output.txt";
    protected static ImageIcon front;
    protected static ImageIcon koloda;
    protected static File f;
    protected static boolean go;
    protected Game GL;
    protected Bot bot;
    protected Values card;
    private int n, k;
    protected int index_clicked;

    public GUI() throws IOException {
        super("Дурак (подкидной) ");
        Container container = this.getContentPane();
        this.setBounds(0, 0, 1920, 1080);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        container.setBackground(Color.GREEN);
        container.setLayout(new GridLayout(4, 12, 5, 10));
        container.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        button1.addActionListener(new Button1EventListener());
        button2.addActionListener(new Button2EventListener());
        button1.setVisible(false);
        button2.setVisible(false);
        bot = new Bot();
        // Инициализация объекта игры GL (GameLogic)
        GL = new Game();
        bot.setM_K((new Random()).nextInt(bot.getN_masts()));
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
            else
                throw new IOException("Картинка(и) со стрелками на найдены!");
        }
        f = new File("src/Cards/front.jpg");
        if (f.exists())
            front = new ImageIcon(f.toString());
        else
            throw new IOException("Картинка с рубашкой карты не найдена!");
        koloda = new ImageIcon("src/Cards/koloda.jpg");
        // Генерация козырной масти и отображаемой на поле карты
        bot.setM_K((new Random()).nextInt(bot.getN_masts()));
        ImageIcon temp;
        for (int i = 0; i < bot.getN_values(); i++){
            temp = img[4 * i + bot.getM_K()];
            for (int j = bot.getM_K() + 1; j < bot.getN_masts(); j++){
                img[4 * i + j - 1] = img[4 * i + j];
            }
            img[4 * i + 3] = temp;
        }
        bot.setM_K(bot.getN_masts() - 1);
        // Помещение на форму всех карт и пустых слотов согласно раскладке в массиве layout_indexes (файл layout.jpg)
        // добавление элементов на форму согласно раскладке
        delivering(1, 1);
        for (int value : layout_indexes) {
            if (value > 0){
                container.add(labels[value]);
                if ((value > 2 * GL.getN_pictures_hand()) && (value < 4 * GL.getN_pictures_hand() + 1))
                    labels[value].addMouseListener(new LabelClicker());
            }
            else if (value == -1)
                container.add(button1);
            else
                container.add(button2);
            if (value == 41)
                labels[value].setIcon(arrow[GL.hod()]);
            else if (value == 46)
                labels[value].setIcon(koloda);
        }
        // Генерация козырной масти и отображаемой на поле карты
        delivering_M_K(42);
    }
    public void delivering_M_K(int index){
        int i_rand;
        do {
            i_rand = new Random().nextInt(36);
            // Если сгенерированная случайно карта есть в колоде
            if ((img_pos[i_rand]) && ((i_rand % 4) == bot.getM_K())){
                // установка параметров козырной карты
                labels[index].setMast(i_rand % 4);
                labels[index].setValue(i_rand / 4);
                int value = 4 * (i_rand / 4) + i_rand % 4;
                try {
                    labels[index].setIcon(img[value]);
                }
                catch (NullPointerException e) {}
            }
        }
        while ((!img_pos[i_rand]) || ((i_rand % 4) != bot.getM_K()));
        img_pos[i_rand] = false;
    }
    public void deliveringtobot(int type) {
        int n_kart, i_rand;
        n_kart = GL.getN_pictures_hand() - bot.values.size();
        int i = 0;
        while ((i < n_kart) && (GL.getN_koloda() > 0)) {
            if (GL.getN_koloda() > 1){
                do {
                    i_rand = new Random().nextInt(36);
                    // Если сгенерированная случайно карта есть в колоде
                    if (img_pos[i_rand]) {
                        bot.AddValue(i_rand % 4, i_rand / 4);
                    }
                }
                while (!img_pos[i_rand]);
                img_pos[i_rand] = false;
            }
            else if (GL.getN_koloda() == 1){
                bot.AddValue(labels[42].getMast(), labels[42].getValue());
                GL.setN_koloda(GL.getN_koloda() - 1);
                GL.setKoloda_empty(true);
                labels[42].NullCard();
                break;
            }
            i += 1;
            GL.setN_koloda(GL.getN_koloda() - 1);
        }
        //if (type == 2)
            //draggingcardplayer();
    }

    public void deliveringtoplayer(int type) {
        int n_kart, i_rand;
        n_kart = GL.getN_pictures_hand() - bot.playervalues.size();
        int i = 0;
        while ((i < n_kart) && (GL.getN_koloda() > 0)) {
            if (GL.getN_koloda() > 1){
                do {
                    i_rand = new Random().nextInt(36);
                    // Если сгенерированная случайно карта есть в колоде
                    if (img_pos[i_rand]) {
                        bot.AddPlayerValue(i_rand % 4, i_rand / 4);
                    }
                }
                while (!img_pos[i_rand]);
                img_pos[i_rand] = false;
            }
            else if (GL.getN_koloda() == 1){
                bot.AddValue(labels[42].getMast(), labels[42].getValue());
                GL.setN_koloda(GL.getN_koloda() - 1);
                GL.setKoloda_empty(true);
                labels[42].NullCard();
                break;
            }
            i += 1;
            GL.setN_koloda(GL.getN_koloda() - 1);
        }
        //if (type == 2)
            //draggingcardsbot();
    }

    public void draggingcardplayer() {
        Values card;
            for (int i = 0; i < bot.envalues.size() + bot.botvalues.size(); i++) {
                if (i < bot.envalues.size())
                    card = bot.envalues.get(i);
                else
                    card = bot.botvalues.get(i - bot.envalues.size());
                bot.AddPlayerValue(card.getMast(), card.getValue());
            }
        bot.envalues.clear();
        bot.botvalues.clear();
    }

    public void draggingcardsbot() {
        Values card;
        for (int i = 0; i < bot.envalues.size() + bot.botvalues.size(); i++) {
            if (i < bot.envalues.size())
                card = bot.envalues.get(i);
            else
                card = bot.botvalues.get(i - bot.envalues.size());
            bot.AddValue(card.getMast(), card.getValue());
        }
        bot.envalues.clear();
        bot.botvalues.clear();
    }

    public void delivering(int type, int hod) {
        if (!GL.isKoloda_empty()){
            if (type == 1) {
                if (hod == 1) {
                    deliveringtoplayer(type);
                    deliveringtobot(type);
                }
                else {
                    deliveringtobot(type);
                    deliveringtoplayer(type);
                }
            }
            else {
                if (hod == 1) {
                    deliveringtoplayer(type);
                    draggingcardsbot();
                }
                else {
                    deliveringtobot(type);
                    draggingcardplayer();
                }
            }
        }
        bot.Sort();
        deliveringlabels();
    }

    public void deliveringlabels() {
        Values card;
        int index;
        for (int i = 1; i < 4 * GL.getN_pictures_hand() + 1; i++) {
            labels[i].setIcon(null);
            if (i < 2 * GL.getN_pictures_hand() + 1) {
                if (i < bot.values.size() + 1) {
                    card = bot.values.get(i - 1);
                    labels[i].setMast(card.getMast());
                    labels[i].setValue(card.getValue());
                    try {
                        labels[i].setIcon(front);
                    }
                    catch (NullPointerException e) {}
                    /*
                    index = 4 * card.getValue() + card.getMast();
                    try {
                        labels[i].setIcon(img[index]);
                    }
                    catch (NullPointerException e) {}
                    */
                }
            }
            else {
                if (i < bot.playervalues.size() + 2 * GL.getN_pictures_hand() + 1) {
                        card = bot.playervalues.get(i - 2 * GL.getN_pictures_hand() - 1);
                        labels[i].setMast(card.getMast());
                        labels[i].setValue(card.getValue());
                        index = 4 * card.getValue() + card.getMast();
                        try {
                            labels[i].setIcon(img[index]);
                        }
                        catch (NullPointerException e) {}
                        if ((labels[i].GetTag() == null)) {
                            Index index_i = new Index();
                            index_i.setI(i);
                            labels[i].SetTag(index_i);
                        }
                }
            }
        }
        if (!GL.isKoloda_empty())
            labels[40].setText("В колоде осталось " + Integer.toString(GL.getN_koloda()) + " карт(ы)");
        else
            labels[40].setText("Колода пуста");
    }
    protected static String text;
    // Действия при окончании игры
    public void isGameEnded() {
        if (GL.isKoloda_empty() && bot.lackofcards()){
            GL.setWinner(GL.sbros());
            if (GL.getWinner() == 1)
                text = "Игрок выиграл";
            else
                text = "Игрок проиграл";
            // вызов меню окончания игры
            Window app_end = new Window("Дурак (конец игры)", text);
            app_end.setVisible(true);
            // работа с потоом вывода данных в файл
            try (FileWriter writer = new FileWriter(path_output, true)) {
                writer.write(text);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Действия, когда игрок нажимает кнопку "Бито"
    class Button1EventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isGameEnded();
            int index;
            // кнопка Бито становится невидимой
            button1.setVisible(false);
            button2.setVisible(false);
            // очищение списков карт игрока и бота на столе
            bot.envalues.clear();
            bot.botvalues.clear();
            // очистка карт на столе
            for (int i = 25; i < 37; i++)
                labels[i].NullCard();
            // стрелка хода переворачивается при смене хода с игрока на бота
            if (labels[41].getIcon() == arrow[0])
                labels[41].setIcon(arrow[1]);
            else
                labels[41].setIcon(arrow[0]);
            delivering(1, GL.hod());
            // Смена хода и сброса
            GL.InverseSbros();
            GL.InverseHod();
            // После нажатия кнопки "Бито" бот начинает свой ход
            if (GL.hod() == -1) {
                // Бот находит карту, с которой он будет начинать свой ход
                card = bot.Hod_Card();
                k = 25 + bot.botvalues.size() * 2;
                n = 1 + bot.botvalues.size();
                // если карта для хода была найдена, то она сбрасывается на поле и загорается кнопка Беру
                if ((card != null) && (bot.playervalues.size() > 0)) {
                    index = 4 * card.getValue() + card.getMast();
                    try {
                        labels[k + 1].setIcon(img[index]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    bot.AddBotValue(card.getMast(), card.getValue());
                    labels[k + 1].setMast(card.getMast());
                    labels[k + 1].setValue(card.getValue());
                    labels[n].setIcon(null);
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
            isGameEnded();
            int index;
            button1.setVisible(false);
            button2.setVisible(false);
            delivering(2, GL.hod());
            GL.sbros(GL.hod());
            // если ходит бот
            if (GL.hod() == -1) {
                // Бот ищет карту, с которой он начнет свой новый (очередной) ход
                card = bot.Hod_Card();
                k = 25 + bot.botvalues.size() * 2;
                n = 1 + bot.botvalues.size();
                if ((card != null) && (bot.playervalues.size() > 0)) {
                    index = 4 * card.getValue() + card.getMast();
                    try {
                        labels[k + 1].setIcon(img[index]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    bot.AddBotValue(card.getMast(), card.getValue());
                    labels[k + 1].setMast(card.getMast());
                    labels[k + 1].setValue(card.getValue());
                    labels[n].setIcon(null);
                    button2.setVisible(true);
                    button1.setVisible(false);
                }
                GL.InverseSbros();
            }
            // очистка карт на столе
            for (int i = 25; i < 37; i++)
                labels[i].NullCard();
            // очищение списка карт бота и игрока на столе
            bot.botvalues.clear();
            bot.envalues.clear();
            GL.sbros(GL.hod());
        }
    }

    // метод, осуществляющий сброс карты игрока на поле
    public void ThrowCardPlayer(int k, int index) {
        labels[k].setIcon(labels[index].getIcon());
        labels[k].setEnabled(false);
        labels[k].setMast(labels[index].getMast());
        labels[k].setValue(labels[index].getValue());
        labels[index].setIcon(null);
    }

    // Карты игрока (а именно карты с 7 по 12 по раскладке) имеют событие клика.
    // Также событие клика будет появляться у карт, которые игрок будет брать у бота и помещать в дополнительные слоты 34-39
    class LabelClicker implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            int index;
            CustomLabel p = (CustomLabel) e.getSource();
            // получение индекса нажатой карты
            try {
                index_clicked = ((Index) (p.GetTag())).getI();
            }
            catch (NullPointerException ex) {}
            // Если игрок нажал на существую карту, а не на пустой слот
            if (labels[index_clicked].getIcon() != null) {
                // Если игрок нажал на ЛКМ
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (GL.hod() == GL.sbros())
                        GL.InverseSbros();
                    // бот начинает биться (во время хода игрока)
                    if (GL.hod() == 1) {
                        // Определяется возможность для игрока подкидывания карт боту
                        go = bot.PodkidtoBot(labels[index_clicked].getValue());
                        // Если бот начинает крыться от карт игрока
                        if (GL.sbros() == -1) {
                            if (go) {
                                n = 1 + bot.botvalues.size();
                                k = 25 + bot.botvalues.size() * 2;
                                ThrowCardPlayer(k, index_clicked);
                                bot.AddEnemyValue(labels[k].getMast(), labels[k].getValue());
                                bot.playervalues.remove(index_clicked - 2 * GL.getN_pictures_hand() - 1);
                                card = null;
                                // Бот ищет у себя в рукаве карту, которой он может побиться
                                card = bot.Bito_Card();
                                // Если он нашел такую карту
                                if (card != null) {
                                    bot.AddBotValue(card.getMast(), card.getValue());
                                    index = 4 * card.getValue() + card.getMast();
                                    try {
                                        labels[k + 1].setIcon(img[index]);
                                    } catch (ArrayIndexOutOfBoundsException ex) {
                                    }
                                    labels[n].setIcon(null);
                                    button1.setVisible(true);
                                    button2.setVisible(false);
                                }
                                else
                                    button2.doClick();
                            }
                            else button2.doClick();
                        }
                    }
                    // if (GL.hod() = -1) если ходит бот
                    else {
                        n = 1 + (bot.botvalues.size() - 1);
                        k = 25 + (bot.botvalues.size() - 1) * 2;
                        // если игрок бьется, queue.sbros = 1 отвечает за возможность скидывания на стол карт игрока, queue.sbros = -1 - карт бота
                        if (((labels[k + 1].getMast() == labels[index_clicked].getMast()) && (labels[index_clicked].getValue() > labels[k + 1].getValue()))
                                || ((labels[index_clicked].getMast() == bot.getM_K()) && (labels[k + 1].getMast() != bot.getM_K()))) {
                            // Игрок кроется физически от карты бота
                            ThrowCardPlayer(k, index_clicked);
                            // имитация того, что бот какое-то время думает
                            try {
                                Thread.sleep(150);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            GL.InverseSbros();
                            k += 2;
                            n += 1;
                            // Бот решает, будет ли он что-то подкидывать
                            card = bot.PodkidtoPlayer();
                            // если бот нашел карту, которую он может подкинуть игроку
                            if ((card != null) && (bot.playervalues.size() > 0)) {
                                index = 4 * card.getValue() + card.getMast();
                                try {
                                    labels[k + 1].setIcon(img[index]);
                                }
                                catch (ArrayIndexOutOfBoundsException ex) {}
                                bot.AddBotValue(card.getMast(), card.getValue());
                                labels[n].setIcon(null);
                                labels[k + 1].setMast(card.getMast());
                                labels[k + 1].setValue(card.getValue());
                                button2.setVisible(true);
                                button1.setVisible(false);
                                GL.InverseSbros();
                            }
                            else
                                button1.doClick();
                        }
                    }
                    try {
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


