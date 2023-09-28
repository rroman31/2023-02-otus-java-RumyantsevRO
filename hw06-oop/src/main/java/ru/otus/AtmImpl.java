package ru.otus;

import ru.otus.exception.NotEnoughMoneyException;
import ru.otus.model.Banknote;
import ru.otus.model.Cell;
import ru.otus.model.Nominal;

import java.util.*;
import java.util.stream.Collectors;

public class AtmImpl implements Atm {
    private final Map<Nominal, Cell> cellsStorage = new EnumMap<>(Nominal.class);

    private Integer[] nominalValues;
    private Integer nominalCount;

    public AtmImpl(List<Banknote> initialBanknotes) {
        init();
        addBanknotes(initialBanknotes);
    }

    @Override
    public void cashIn(List<Banknote> banknotes) {
        addBanknotes(banknotes);
    }

    @Override
    public List<Banknote> cashOut(int amount) {
        int amountCommon = getAtmBalance();
        if (amountCommon < amount) {
            throw new NotEnoughMoneyException();
        }

        return split(amount).stream().map(it -> {
            Nominal nominal = Nominal.of(it);
            return cellsStorage.get(nominal).getBanknote();
        }).toList();
    }

    @Override
    public int getAtmBalance() {
        return cellsStorage.values().stream().mapToInt(Cell::getAmount).sum();
    }

    @Override
    public Map<Nominal, Integer> getCellBalance() {
        return cellsStorage.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAmount()));
    }

    private void addBanknotes(List<Banknote> initialBanknotes) {
        Map<Nominal, List<Banknote>> byNominal = initialBanknotes.stream().collect(Collectors.groupingBy(Banknote::getNominal));

        byNominal.forEach((nominal, banknotes) -> {
            cellsStorage.compute(nominal, (k, cell) -> {
                if (cell == null) {
                    return new Cell(banknotes);
                } else {
                    cell.addBanknotes(banknotes);
                    return cell;
                }
            });
        });
    }

    private void init() {
        nominalValues = Arrays.stream(Nominal.values())
                .map(Nominal::getValue)
                .sorted(Collections.reverseOrder())
                .toArray(Integer[]::new);

        nominalCount = Nominal.values().length;
    }

    private List<Integer> split(int amount) {
        int[] banknoteTypeCounter = new int[nominalCount];

        for (int i = 0; i < nominalCount; i++) {
            if (amount >= nominalValues[i]) {
                banknoteTypeCounter[i] = amount / nominalValues[i];
                amount = amount - banknoteTypeCounter[i] * nominalValues[i];
            }
        }

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < nominalCount; i++) {
            if (banknoteTypeCounter[i] != 0) {
                for (int j = 0; j < banknoteTypeCounter[i]; j++) {
                    result.add(nominalValues[i]);
                }
            }
        }
        return result;
    }
}