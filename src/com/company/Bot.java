package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.*;

/* класс, представляющий ИИ программы. Бот имеет список своих карт (т.е. тех, которые у него в руке), и список карт врага (которые находятся на столе).
// Бот может биться, когда проходит ход игрока, сам кидать свои карты на стол, когда проходит его ход, а также подкидывать карты на стол в случае, если это возможно
// Этот класс пока слабо реализован, так как в нем не хватает методов, в которых бот сам принимает решение, закончил ли он биться или бросать карты.
// То есть бот сам должен решать, когда игрок побился (в этом случае происходит выдача недостающих карт обоим игрокам, т.е. как бы нажимается виртуальная кнопка "Бито")
и когда бот будет брать карты игрока со стола (как бы нажимая виртуальную кнопку "Беру")
public class Bot {
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
            Game.labels[k + 1].setIcon(new ImageIcon("src/Cards/" + Integer.toString(4 * card.getValue() + card.getMast()) + ".jpg"));
            Game.labels[n++].setVisible(false);
            Game.button2.setVisible(true);
            Game.button1.setVisible(false);
            k += 2;
            Game.button1.doClick();
        }
        //else button1.doClick();
    }

    public void Beru()
    {
        BotValues card = null;
        card = Hod_Card();
        if (Podkid_Possible())
            card = Hod_Card();
        Game.button2.doClick();
    }
}

 */


