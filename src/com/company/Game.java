package com.company;

import java.util.ArrayList;
import java.util.Comparator;

// класс, представляющий игровую логику
public class Game {
    // кол-во карт в колоде
    public int getN_pictures() {
        return n_pictures;
    }
    private static final int n_pictures = 16;
    public int getN_pictures_hand()
    {
        return n_pictures_hand;
    }
    private static final int n_pictures_hand = 6;
    public int getN_koloda() {
        return n_koloda;
    }
    public void setN_koloda(int n_koloda) {
        this.n_koloda = n_koloda;
    }
    private int n_koloda;
    public boolean isKoloda_empty() {
        return koloda_empty;
    }
    public void setKoloda_empty(boolean koloda_empty) {
        this.koloda_empty = koloda_empty;
    }
    private boolean koloda_empty;
    private int Sbros;
    private int Hod;
    public int getWinner() {
        return Winner;
    }
    public void setWinner(int winner) {
        Winner = winner;
    }
    private int Winner;
    public Game() {
        InitGame();
    }
    // Инициализация параметров игры
    public void InitGame()
    {
        this.n_koloda = getN_pictures();
        this.koloda_empty = false;
        this.Sbros = 1;
        this.Hod = 1;
        this.Winner = 0;
    }
    public void sbros (int Sbros)
    {
        this.Sbros = Sbros;
    }
    public int sbros() {return Sbros;}
    public void hod (int Hod)
    {
        this.Hod = Hod;
    }
    public int hod()
    {
        return Hod;
    }
    public void InverseHod()
    {
        this.Hod = this.Hod * -1;
    }
    public void InverseSbros()
    {
        this.Sbros = this.Sbros * -1;
    }
}

/* класс, представляющий ИИ программы. Бот имеет список своих карт (т.е. тех, которые у него в руке), и список карт врага (которые находятся на столе).
 Бот может биться, когда проходит ход игрока, сам кидать свои карты на стол, когда проходит его ход, а также подкидывать карты на стол в случае, если это возможно
Этот класс пока слабо реализован, так как в нем не хватает методов, в которых бот сам принимает решение, закончил ли он биться или бросать карты.
То есть бот сам должен решать, когда игрок побился (в этом случае происходит выдача недостающих карт обоим игрокам, т.е. как бы нажимается виртуальная кнопка "Бито")
и когда бот будет брать карты игрока со стола (как бы нажимая виртуальную кнопку "Беру")
 */
class Bot {
    public int getM_K() {
        return M_K;
    }
    public void setM_K(int m_K) {
        M_K = m_K;
    }
    private static int M_K;
    private Values card;
    private Values encard;
    private Values returned_card;
    ArrayList<Values> values = new ArrayList<>(); // список карт бота у него в рукаве
    ArrayList<Values> botvalues = new ArrayList<>(); // список карт бота на его части стола
    ArrayList<Values> envalues = new ArrayList<>(); // список карт игрока на его части стола
    ArrayList<Values> playervalues = new ArrayList<>(); // список карт игрока у него в рукаве
    public void AddValue(int M, int V)
    {
        values.add(new Values(M,V));
    }
    public void AddEnemyValue(int M, int V)
    {
        envalues.add(new Values(M, V));
    }
    public void AddPlayerValue(int M, int V)
    {
        playervalues.add(new Values(M,V));
    }
    public void AddBotValue(int M, int V)
    {
        botvalues.add(new Values(M, V));
    }
    public int getN_masts()
    {
        return n_masts;
    }
    private static final int n_masts = 4;
    public int getN_values()
    {
        return n_values;
    }
    private static final int n_values = 9;
    // бот подкидывает карту на стол в случае, если это возможно, метод возвращает возможность
    public Values PodkidtoPlayer() {
        returned_card = null;
        for (int i = 0; i < envalues.size() + botvalues.size(); i++) {
            if (i < envalues.size())
                encard = envalues.get(i);
            else encard = botvalues.get(i - envalues.size());
            for (int j = 0; j < values.size(); j++) {
               card = values.get(j);
               if ((card.getValue() == encard.getValue()) && (card.getMast() != encard.getMast())) {
                   returned_card = card;
                   values.remove(j);
                   break;
               }
            }
        }
        return returned_card;
    }

    // бот подкидывает карту на стол в случае, если это возможно, метод возвращает возможность
    public boolean PodkidtoBot(int value) {
        boolean podkidtobot = false;
        if (values.size() > 0){
            if (envalues.size() + botvalues.size() == 0)
                podkidtobot = true;
            else {
                for (Values envalue : envalues)
                    if (value == envalue.getValue()) {
                        podkidtobot = true;
                        break;
                    }
                for (Values botvalue : botvalues)
                    if (value == botvalue.getValue()) {
                        podkidtobot = true;
                        break;
                    }
            }
        }
        return podkidtobot;
    }

    // бот отбивается, метод возвращает выбранную им карту, которой он будет биться
    public Values Bito_Card() {
        returned_card = null;
        //values.sort(COMPARE_BY_VALUE);
        for (int i = 0; i < envalues.size(); i++) {
            encard = envalues.get(i);
            for (int j = 0; j < values.size(); j++) {
                card = values.get(j);
                if (((card.getMast() != M_K) && (card.getMast() == encard.getMast()) && (card.getValue() > encard.getValue())) || ((card.getMast() == M_K)
                        && (encard.getMast() != M_K || (card.getValue() > encard.getValue())))) {
                    returned_card = card;
                    //envalues.remove(i);
                    values.remove(j);
                    break;
                }
            }
        }
        return returned_card;
    }

    public boolean lackofcards()
    {
        return ((values.size() == 0) || (playervalues.size() == 0));
    }

    // Бот ищет самую мелкую карту у себя в колоде
    public Values Hod_Card() {
        returned_card = null;
        //values.sort(COMPARE_BY_VALUE);
        returned_card = values.get(0);
        values.remove(0);
        return returned_card;
    }
    public void Sort() {
        // Преобразование списков в массивы
        Sorter sorter = new Sorter();
        SorterBot sorterBot = new SorterBot();
        values.sort(sorter);
        playervalues.sort(sorter);
    }

    // Интерфейс Comparator испольуется для сравнения значений карт у бота в рукаве, происходит сортировка карт по некоторому принципу
    public static class Sorter implements Comparator<Values> {
        public int compare(Values lhs, Values rhs) {
            int first = lhs.getValue();
            int second = rhs.getValue();
            first += lhs.getMast() * n_values;
            second += rhs.getMast() * n_values;
            return first - second;
        }
    };
    public static class SorterBot implements Comparator<Values> {
        public int compare(Values lhs, Values rhs) {
            int first = lhs.getMast();
            int second = rhs.getMast();
            first += lhs.getValue() * n_masts;
            second += rhs.getValue() * n_masts;
            return first - second;
        }
    };
}

// Класс, который будет постоянно использоваться в качестве обобщений в списках карт
class Values {
    public int getMast()
    {
        return Mast;
    }
    private final int Mast;
    public int getValue()
    {
        return Value;
    }
    private final int Value;
    public Values(int M, int V) {
        this.Mast = M;
        this.Value = V;
    }
}



