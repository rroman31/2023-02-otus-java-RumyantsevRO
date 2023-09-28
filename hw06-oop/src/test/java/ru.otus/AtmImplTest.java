package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.exception.NotEnoughMoneyException;
import ru.otus.model.Banknote;
import ru.otus.model.Nominal;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class AtmImplTest {
    private Atm atmImpl;

    @BeforeEach
    void setUp() {
        atmImpl = new AtmImpl(
                List.of(
                        new Banknote(5000),
                        new Banknote(5000),
                        new Banknote(2000),
                        new Banknote(2000),
                        new Banknote(1000),
                        new Banknote(1000)
                )
        );
    }

    @Test
    @DisplayName("Сумма и наличие нужных купюр")
    void initial() {
        assertThat(atmImpl.getAtmBalance()).isEqualTo(16000);

        assertThat(atmImpl.getCellBalance())
                .containsOnly(
                        entry(Nominal.FIVE_THOUSAND, 10000),
                        entry(Nominal.TWO_THOUSAND, 4000),
                        entry(Nominal.THOUSAND, 2000)

                );
    }

    @Test
    @DisplayName("Пополнение")
    void cashIn() {
        atmImpl.cashIn(
                List.of(
                        new Banknote(2000),
                        new Banknote(2000),
                        new Banknote(2000),
                        new Banknote(5000)
                )
        );

        assertThat(atmImpl.getAtmBalance()).isEqualTo(27000);

        assertThat(atmImpl.getCellBalance())
                .containsOnly(
                        entry(Nominal.FIVE_THOUSAND, 15000),
                        entry(Nominal.TWO_THOUSAND, 10000),
                        entry(Nominal.THOUSAND, 2000)
                );
    }

    @Test
    @DisplayName("Выдача")
    void cashOut() {
        List<Banknote> banknotes = atmImpl.cashOut(12000);
        assertThat(banknotes).hasSize(3);

        assertThat(banknotes)
                .extracting("nominal", "value")
                .contains(
                        tuple(Nominal.TWO_THOUSAND, 2000),
                        tuple(Nominal.FIVE_THOUSAND, 5000),
                        tuple(Nominal.FIVE_THOUSAND, 5000)
                );

        assertThat(atmImpl.getAtmBalance()).isEqualTo(4_000);

        assertThat(atmImpl.getCellBalance())
                .containsOnly(
                        entry(Nominal.TWO_THOUSAND, 2000),
                        entry(Nominal.FIVE_THOUSAND, 0),
                        entry(Nominal.THOUSAND, 2000)
                );
    }

    @Test
    @DisplayName("Недостаточно денег в банкомате")
    void notEnoughMoney() {
        assertThatThrownBy(() -> atmImpl.cashOut(5000000))
                .isInstanceOf(NotEnoughMoneyException.class)
                .hasMessage("Недостаточно денег в банкомате");
    }
}