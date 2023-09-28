package ru.otus;

import ru.otus.model.Banknote;
import ru.otus.model.Nominal;

import java.util.List;
import java.util.Map;

public interface Atm {

    void cashIn(List<Banknote> banknotes);

    List<Banknote> cashOut(int amount);

    int getAtmBalance();

    Map<Nominal, Integer>  getCellBalance();
}
