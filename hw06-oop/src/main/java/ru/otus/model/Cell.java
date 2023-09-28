package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final List<Banknote> banknotes = new ArrayList<>();

    public Cell(List<Banknote> banknotes) {
        this.banknotes.addAll(banknotes);
    }

    public boolean addBanknote(Banknote banknote) {
        return banknotes.add(banknote);
    }

    public boolean addBanknotes(List<Banknote> banknotes) {
        return this.banknotes.addAll(banknotes);
    }

    public int getAmount() {
        return banknotes.stream().mapToInt(Banknote::getValue).sum();
    }

    public Banknote getBanknote() {
        if (banknotes.isEmpty()) {
            throw new RuntimeException("Недостаточно банкнот в ячейке");
        }
        int idx = banknotes.size() - 1;
        Banknote banknote = banknotes.get(idx);
        banknotes.remove(banknote);
        return banknote;
    }
}
