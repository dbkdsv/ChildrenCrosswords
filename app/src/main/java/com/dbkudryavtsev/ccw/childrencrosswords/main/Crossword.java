package com.dbkudryavtsev.ccw.childrencrosswords.main;

class Crossword {
    Cwords[] _cwords;
    int _hor_count;

    Crossword() {
        /*-----------2 magic variables HERE-----------------*/
        _cwords = new Cwords[4];
        for (int i = 0; i < _cwords.length; i++)
            _cwords[i] = new Cwords();
        _hor_count = 2;
        for (int i = 0; i < _cwords.length; i++) {
            if (i == 0) {
                _cwords[i]._word = "РАСЧЁСКА";
                _cwords[i]._question = "Зубов много, а ничего не ест.";
                _cwords[i]._posX = 1;
                _cwords[i]._posY = 0;
            }
            if (i == 1) {
                _cwords[i]._word = "ВОРОБЕЙ";
                _cwords[i]._question = "Маленький мальчишка в сером армячишке\nПо дворам шныряет, крохи подбирает,\nПо ночам кочует - коноплю ворует.";
                _cwords[i]._posX = 0;
                _cwords[i]._posY = 3;
            }
            if (i == 2) {
                _cwords[i]._word = "РОБОТ";
                _cwords[i]._question = "Сам - металлический,\nМозг - электрический.";
                _cwords[i]._posX = 1;
                _cwords[i]._posY = 0;
            }
            if (i == 3) {
                _cwords[i]._word = "СОРОКА";
                _cwords[i]._question = "Верещунья белобока,\nА зовут ее ...";
                _cwords[i]._posX = 3;
                _cwords[i]._posY = 0;
            }
        }
    }
    String[] getAllQuestions(){
        String[] questions=new String[_cwords.length];
        for(int i=0; i<_cwords.length;i++){
            questions[i]=Integer.toString(i+1)+". "+_cwords[i]._question;
        }
        return questions;
    }
}


