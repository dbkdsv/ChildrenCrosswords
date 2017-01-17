package com.dbkudryavtsev.ccw.childrencrosswords.main;

public class crossword {
    cwords[] _cwords;
    int _hor_count;

    crossword() {
        /*-----------2 magic variables HERE-----------------*/
        _cwords = new cwords[4];
        for (int i = 0; i < _cwords.length; i++)
            _cwords[i] = new cwords();
        _hor_count = 2;
        for (int i = 0; i < _cwords.length; i++) {
            if (i == 0) {
                _cwords[i]._word = "расчёска";
                _cwords[i]._question = "Зубов много, а ничего не ест.";
                _cwords[i]._posX = 1;
                _cwords[i]._posY = 0;
            }
            if (i == 1) {
                _cwords[i]._word = "воробей";
                _cwords[i]._question = "Маленький мальчишка в сером армячишке\nПо дворам шныряет, крохи подбирает,\nПо ночам кочует - коноплю ворует.";
                _cwords[i]._posX = 0;
                _cwords[i]._posY = 3;
            }
            if (i == 2) {
                _cwords[i]._word = "робот";
                _cwords[i]._question = "Сам - металлический,\nМозг - электрический.";
                _cwords[i]._posX = 1;
                _cwords[i]._posY = 0;
            }
            if (i == 3) {
                _cwords[i]._word = "сорока";
                _cwords[i]._question = "Верещунья белобока,\nА зовут ее ...";
                _cwords[i]._posX = 3;
                _cwords[i]._posY = 0;
            }
        }
    }
}
