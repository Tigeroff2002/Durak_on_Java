package com.company;

import org.junit.Test;
import static org.junit.Assert.*;

public class BotTest {
    @Test
    public void testPodkidtoPlayer1(){
        Bot bot = new Bot();
        bot.AddBotValue(1, 6);
        bot.AddEnemyValue(1, 7);
        bot.AddBotValue(2, 6);
        bot.AddEnemyValue(2, 7);
        bot.AddValue(2, 8);
        bot.AddValue(3, 7);
        bot.AddValue(1, 9);
        bot.AddValue(0,4);
        Values expResult = new Values(3, 7);
        Values result = bot.PodkidtoPlayer();
        assertEquals(expResult.getMast(), result.getMast());
        assertEquals(expResult.getValue(), result.getValue());
    }

    @Test
    public void testPodkidtoPlayer2(){
        Bot bot = new Bot();
        bot.AddBotValue(1, 6);
        bot.AddEnemyValue(1, 7);
        bot.AddBotValue(2, 6);
        bot.AddEnemyValue(2, 7);
        bot.AddValue(2, 8);
        bot.AddValue(3, 9);
        bot.AddValue(1, 9);
        bot.AddValue(0,4);
        Values result = bot.PodkidtoPlayer();
        assertNull(result);
    }

    @Test
    public void testBitoCard1(){
        Bot bot = new Bot();
        bot.setM_K(2);
        bot.AddValue(1, 4);
        bot.AddValue(1, 5);
        bot.AddValue(2, 5);
        bot.AddValue(2, 6);
        bot.AddValue(3, 8);
        bot.AddValue(3, 9);
        bot.AddEnemyValue(1, 6);
        Values expResult = new Values(2, 5);
        Values result = bot.Bito_Card();
        assertEquals(expResult.getMast(), result.getMast());
        assertEquals(expResult.getValue(), result.getValue());
    }

    @Test
    public void testBitoCard2(){
        Bot bot = new Bot();
        bot.setM_K(2);
        bot.AddValue(1, 4);
        bot.AddValue(1, 5);
        bot.AddValue(2, 5);
        bot.AddValue(2, 6);
        bot.AddValue(3, 8);
        bot.AddValue(3, 9);
        bot.AddEnemyValue(2, 8);
        Values result = bot.Bito_Card();
        assertNull(result);
    }
}