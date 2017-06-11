package com.dbkudryavtsev.childrencrosswords.utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CrosswordIOJSONTest {
    @Test
    public void parseAnswersFromJson(){
        String jsonString = "{ \"answers\": [" +
                "{\n \"answer\": \"РАСЧЁСКА\"},\n" +
                "{\n \"answer\": \"ВОРОБЕЙ\"},\n" +
                "{\n \"answer\": \"РОБОТ\"},\n" +
                "{\n \"answer\": \"СОРОКА\"}" +
                "] }";

        String emptyJSON = "{\"answers\": []}";

        String nullJSON = null;

        String wrongJSON = "{ \"answers\": {" +
                "{\n \"answer\": \"РАСЧЁСКА\"},\n" +
                "{\n \"answer\": \"ВОРОБЕЙ\"},\n" +
                "{\n \"answer\": \"РОБОТ\"},\n" +
                "{\n \"answer\": \"СОРОКА\"}" +
                "] }";

        String[] rightAnswers = new String[]{"РАСЧЁСКА", "ВОРОБЕЙ", "РОБОТ", "СОРОКА"};

        String[] emptyAnswers = new String[0];

        String[] nullAnswers = null;

        boolean isThrown = false;

        String[] result = CrosswordIOJSON.parseAnswersFromJson(jsonString);

        String[] emptyResult = CrosswordIOJSON.parseAnswersFromJson(emptyJSON);

        String[] nullResult = CrosswordIOJSON.parseAnswersFromJson(nullJSON);

        assertEquals(rightAnswers, result);

        assertEquals(emptyAnswers, emptyResult);

        assertEquals(nullAnswers, nullResult);

        try {
            CrosswordIOJSON.parseAnswersFromJson(wrongJSON);
        }
        catch (RuntimeException ex){
            isThrown = true;
        }
        finally {
            assertTrue(isThrown);
        }

    }
}
