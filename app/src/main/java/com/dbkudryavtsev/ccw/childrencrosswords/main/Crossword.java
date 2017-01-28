package com.dbkudryavtsev.ccw.childrencrosswords.main;

//TODO: Запоминать состояние заполнения последнего кроссворда и давать возможность продолжить заполнение

class Crossword {
    private Cwords[] cwords;
    private int horCount;

    Crossword() {
        /*-----------2 magic variables HERE-----------------*/
        cwords = new Cwords[4];
        horCount = 2;

        for (int i = 0; i < cwords.length; i++) {
            if (i == 0) {
                cwords[i]= new Cwords("Зубов много, а ничего не ест.", "РАСЧЁСКА", 1, 0);
            }
            if (i == 1) {
                cwords[i]= new Cwords("Маленький мальчишка в сером армячишке\nПо дворам шныряет, крохи подбирает,\n" +
                        "По ночам кочует - коноплю ворует.", "ВОРОБЕЙ", 0, 3);
            }
            if (i == 2) {
                cwords[i]= new Cwords("Сам - металлический,\nМозг - электрический.", "РОБОТ", 1, 0);
            }
            if (i == 3) {
                cwords[i]= new Cwords("Верещунья белобока,\nА зовут ее ...", "СОРОКА", 3, 0);
            }
        }
    }

    int getHorCount(){
        return horCount;
    }

    int getCwordsLength(){
        return cwords.length;
    }

    Cwords getCword(int position){
        return cwords[position];
    }

    String[] getAllQuestions(){
        String[] questions=new String[cwords.length];
        for(int i=0; i<cwords.length;i++){
            questions[i]=Integer.toString(i+1)+". "+cwords[i].getQuestion();
        }
        return questions;
    }
}