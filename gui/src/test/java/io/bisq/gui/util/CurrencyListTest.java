package io.bisq.gui.util;

import com.google.common.collect.Lists;
import io.bisq.common.locale.CryptoCurrency;
import io.bisq.common.locale.FiatCurrency;
import io.bisq.common.locale.TradeCurrency;
import io.bisq.core.user.Preferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Preferences.class)
public class CurrencyListTest {
    private static final TradeCurrency USD = new FiatCurrency("USD");
    private static final TradeCurrency RUR = new FiatCurrency("RUR");
    private static final TradeCurrency BTC = new CryptoCurrency("BTC", "Bitcoin");
    private static final TradeCurrency ETH = new CryptoCurrency("ETH", "Ether");
    private static final TradeCurrency BSQ = new CryptoCurrency("BSQ", "Bisq Token");

    private CurrencyPredicates predicates;
    private Preferences preferences;
    private List<CurrencyListItem> delegate;
    private CurrencyList testedEntity;

    @Before
    public void setUp() {
        Locale.setDefault(new Locale("en", "US"));

        this.predicates = mock(CurrencyPredicates.class);
        when(predicates.isCryptoCurrency(USD)).thenReturn(false);
        when(predicates.isCryptoCurrency(RUR)).thenReturn(false);
        when(predicates.isCryptoCurrency(BTC)).thenReturn(true);
        when(predicates.isCryptoCurrency(ETH)).thenReturn(true);

        when(predicates.isFiatCurrency(USD)).thenReturn(true);
        when(predicates.isFiatCurrency(RUR)).thenReturn(true);
        when(predicates.isFiatCurrency(BTC)).thenReturn(false);
        when(predicates.isFiatCurrency(ETH)).thenReturn(false);

        this.preferences = mock(Preferences.class);
        this.delegate = new ArrayList<>();
        this.testedEntity = new CurrencyList(delegate, preferences, predicates);
    }

    @Test
    public void testUpdateWhenSortNumerically() {
        when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(true);

        List<TradeCurrency> currencies = Lists.newArrayList(USD, RUR, USD, ETH, ETH, BTC);
        testedEntity.updateWithCurrencies(currencies, null);

        List<CurrencyListItem> expected = Lists.newArrayList(
                new CurrencyListItem(USD, 2),
                new CurrencyListItem(RUR, 1),
                new CurrencyListItem(ETH, 2),
                new CurrencyListItem(BTC, 1));

        assertEquals(expected, delegate);
    }

    @Test
    public void testUpdateWhenNotSortNumerically() {
        when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(false);

        List<TradeCurrency> currencies = Lists.newArrayList(USD, RUR, USD, ETH, ETH, BTC);
        testedEntity.updateWithCurrencies(currencies, null);

        List<CurrencyListItem> expected = Lists.newArrayList(
                new CurrencyListItem(RUR, 1),
                new CurrencyListItem(USD, 2),
                new CurrencyListItem(BTC, 1),
                new CurrencyListItem(ETH, 2));

        assertEquals(expected, delegate);
    }

    @Test
    public void testUpdateWhenSortNumericallyAndFirstSpecified() {
        when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(true);

        List<TradeCurrency> currencies = Lists.newArrayList(USD, RUR, USD, ETH, ETH, BTC);
        CurrencyListItem first = new CurrencyListItem(BSQ, 5);
        testedEntity.updateWithCurrencies(currencies, first);

        List<CurrencyListItem> expected = Lists.newArrayList(
                first,
                new CurrencyListItem(USD, 2),
                new CurrencyListItem(RUR, 1),
                new CurrencyListItem(ETH, 2),
                new CurrencyListItem(BTC, 1));

        assertEquals(expected, delegate);
    }
}
