package com.dbkudryavtsev.childrencrosswords.utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AnswersParserTest {
    @Test
    public void parseAnswersFromJson(){
        String jsonString = "{ \"answers\": [" +
                "{\n \"answer\": \"РАСЧЁСКА\"},\n" +
                "{\n \"answer\": \"ВОРОБЕЙ\"},\n" +
                "{\n \"answer\": \"РОБОТ\"},\n" +
                "{\n \"answer\": \"СОРОКА\"}" +
                "] }";

        String[] rightAnswers = new String[]{"РАСЧЁСКА", "ВОРОБЕЙ", "РОБОТ", "СОРОКА"};

        String[] result = AnswersParser.parseAnswersFromJson(jsonString);

        assertEquals(rightAnswers, result);
    }

    @Test
    public void parseEmptyAnswersFromJson(){

        String emptyJSON = "{\"answers\": []}";

        String[] emptyAnswers = new String[0];

        String[] emptyResult = AnswersParser.parseAnswersFromJson(emptyJSON);

        assertEquals(emptyAnswers, emptyResult);

    }

    @Test
    public void parseNullAnswersFromJson(){

        String nullJSON = null;

        String[] nullAnswers = null;

        String[] nullResult = AnswersParser.parseAnswersFromJson(nullJSON);

        assertEquals(nullAnswers, nullResult);

    }

    @Test
    public void parseWrongAnswersFromJson(){
        String wrongJSON = "{ \"answers\": {" +
                "{\n \"answer\": \"РАСЧЁСКА\"},\n" +
                "{\n \"answer\": \"ВОРОБЕЙ\"},\n" +
                "{\n \"answer\": \"РОБОТ\"},\n" +
                "{\n \"answer\": \"СОРОКА\"}" +
                "] }";

        boolean isThrown = false;

        try {
            AnswersParser.parseAnswersFromJson(wrongJSON);
        }
        catch (RuntimeException ex){
            isThrown = true;
        }
        finally {
            assertTrue(isThrown);
        }
    }
}
