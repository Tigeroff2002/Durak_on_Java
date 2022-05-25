package com.company;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


public class Game
{
    public int[] layout_indexes = new int[]
            { 1, 2, 3, 4, 5, 6, 28, 29, 30, 31, 32, 33, 16, 18, 20, 22, 24, 26, 40, 41, 42, 43, 44, 13,
                    15, 17, 19, 21, 23, 25, 45, 46, 47, -1, -2, 14, 7,  8, 9, 10, 11, 12, 34, 35, 36, 37, 38, 39 };
    // раскладка карт на столе (визуальное представление доступно в папке Cards под названием layout.jpg)
    card[] cards = new card[36];
    boolean[] img_pos = new boolean[36];
    String [] dirs = new String [] {"up", "down"};
    Random rnd = new Random();
    Queue queue = new Queue();
    Bot bot;
    public Game() {
        // Инициализация параметров игры
        Queue game = new Queue();
        game.InitGame();
        for (int i = 0; i < cards.length; i++)
            cards[i] = new card();
        // Создание экземпляра бота
        bot = new Bot();
        bot.M_K = cards[layout_indexes[(6*2 - 1) * 2 + 1]].M(); // установка козырной масти
    }
}

class card
{
    private int Mast;
    private int Value;
    private ImageIcon icon;
    private boolean Enabled;
    public void M (int Mast)
    {
        this.Mast = Mast;
    }
    public int M() {return Mast;}
    public void V (int Value)
    {
        this.Value = Value;
    }
    public int V() {return Value;}
    public void En (boolean Enabled)
    {
        this.Enabled = Enabled;
    }
    public boolean En() {return Enabled;}
    private static File f;
    public void Icon(String path) throws IOException {
        f = new File(path);
        if (f.exists())
            this.icon = new ImageIcon(f.toString());
        else throw new IOException("Картинка(и) с картами не найдены!");
    }
    public card()
    {
        Random rnd = new Random();
        this.Mast = rnd.nextInt(4) + 1;
        this.Value = rnd.nextInt(9) + 1;
        this.Enabled = true;
    }
    /*
    public card(int Mast, int Value)
    {
        this.Mast = Mast;
        this.Value = Value;
        this.Enabled = true;
    }
     */
}

/* класс, представляющий ИИ программы. Бот имеет список своих карт (т.е. тех, которые у него в руке), и список карт врага (которые находятся на столе).
// Бот может биться, когда проходит ход игрока, сам кидать свои карты на стол, когда проходит его ход, а также подкидывать карты на стол в случае, если это возможно
// Этот класс пока слабо реализован, так как в нем не хватает методов, в которых бот сам принимает решение, закончил ли он биться или бросать карты.
// То есть бот сам должен решать, когда игрок побился (в этом случае происходит выдача недостающих карт обоим игрокам, т.е. как бы нажимается виртуальная кнопка "Бито")
и когда бот будет брать карты игрока со стола (как бы нажимая виртуальную кнопку "Беру")

 */
class Bot {
    static int M_K;
    ArrayList<BotValues> values = new ArrayList<>();
    ArrayList<BotValues> envalues = new ArrayList<>();

    public void AddValue(int M, int V)
    {
        BotValues value = new BotValues(M, V);
        values.add(value);
    }

    public void AddEnemyValue(int M, int V)
    {
        BotValues value = new BotValues(M, V);
        envalues.add(value);
    }

    public BotValues Bito_Card() // бот отбивается, метод возвращает выбранную им карту, которой он будет биться
    {
        BotValues returned_card = null;
        Collections.sort(values, BotValues.COMPARE_BY_VALUE);
        for (int i = 0; i < envalues.size(); i++)
        {
            BotValues encard = envalues.get(i);
            for (int j = 0; j < values.size(); j++)
            {
                BotValues card = values.get(j);
                if (((card.getMast() != M_K) && (card.getMast() == encard.getMast()) && (card.getValue() > encard.getValue())) || ((card.getMast() == M_K)
                        && (encard.getMast() != M_K || (card.getValue() > encard.getValue()))))
                {
                    envalues.remove(i);
                    values.remove(j);
                    returned_card = card;
                    break;
                }
            }
        }
        return returned_card;
    }

    public boolean Podkid_Possible() // бот подкидывает карту на стол в случае, если это возможно, метод возвращает возможность
    {
        boolean go = false;
        for (int i = 0; i < envalues.size(); i++)
        {
            BotValues card = values.get(i);
            if ((card.getValue() == envalues.get(i).getValue()) || (card.getValue() == values.get(i).getValue()))
            {
                go = true;
                break;
            }
        }
        return go;
    }

    public BotValues Hod_Card()
    {
        BotValues returned_card = null;
        Collections.sort(values, BotValues.COMPARE_BY_VALUE);
        returned_card = values.get(0);
        values.remove(0);
        return returned_card;
    }

    public void Bito()
    {
        BotValues card = null;
        int k = 15;
        int n = 1;
        card = Bito_Card();
        if (card != null)
        {
            GUI.labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
            GUI.labels[n++].setVisible(false);
            GUI.button2.setVisible(true);
            GUI.button1.setVisible(false);
            k += 2;
            GUI.button1.doClick();
        }
        //else button1.doClick();
    }

    public void Beru()
    {
        BotValues card = null;
        card = Hod_Card();
        if (Podkid_Possible())
            card = Hod_Card();
        GUI.button2.doClick();
    }
}
// класс для рабочих величин
class Queue
{
    public void InitGame()
    {
        n_pictures = 12;
        n_koloda = 36;
        koloda_empty = false;
    }
    int n_pictures;
    int n_koloda;
    boolean koloda_empty;
    private int Sbros;
    private int Hod;
    public void sbros (int Sbros)
    {
        this.Sbros = Sbros;
    }
    public int sbros() {return Sbros;}
    public void hod (int Hod)
    {
        this.Hod = Hod;
    }
    public int hod() {return Hod;}
    public void Queue()
    {
        this.Sbros = 1;
        this.Hod = 1;
    }
    public void changePlayer()
    {
        this.Sbros *= -1;
        this.Hod *= -1;
    }
    public int[] player_values = new int[] { -1, -1, -1, -1, -1, -1 };
    public int[] bot_values = new int[] { -1, -1, -1, -1, -1, -1 };
}

class BotValues
{
    private int Mast;
    private int Value;
    private static int M_K = Bot.M_K;
    public boolean Enabled;

    public int getMast()
    {
        return Mast;
    }

    public int getValue()
    {
        return Value;
    }

    public BotValues(int M, int V)
    {
        this.Mast = M;
        this.Value = V;
        this.Enabled = true;
    }
    public static final Comparator<BotValues> COMPARE_BY_VALUE = new Comparator<BotValues>() {
        @Override
        public int compare(BotValues lhs, BotValues rhs) {
            int first, second;
            if (lhs.getMast() == M_K)
                first = lhs.getValue() + 10;
            else first = lhs.getValue();
            if (rhs.getMast() == M_K)
                second = rhs.getValue() + 10;
            else second= rhs.getValue();
            return first - second;
        }
    };
}


