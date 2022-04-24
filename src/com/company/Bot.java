package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;

public class Bot {

    int M_K;
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

    public BotValues Bito()
    {
        BotValues returned_card = null;
        for (int i = 0; i < envalues.size(); i++)
        {
            BotValues encard = envalues.get(i);
            for (int j = 0; j < values.size(); j++)
            {
                BotValues card = values.get(j);
                if (((card.Mast != M_K) && (card.Mast == encard.Mast) && (card.Value > encard.Value)) || ((card.Mast == M_K)
                        && (((encard.Mast == M_K) && (card.Value > encard.Value)) || (encard.Mast != M_K))))
                {
                    //Message mes = new Message(Integer.toString(card.Value) + " ," + Integer.toString(card.Mast));
                    //mes.setVisible(true);
                    returned_card = card;
                    break;
                }
            }
        }
        return returned_card;
    }
}

class BotValues
{
    public int Mast;
    public int Value;
    public boolean Enabled;

    public BotValues(int M, int V)
    {
        this.Mast = M;
        this.Value = V;
        this.Enabled = true;
    }
}
