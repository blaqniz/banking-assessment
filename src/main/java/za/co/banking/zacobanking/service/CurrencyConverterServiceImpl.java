package za.co.banking.zacobanking.service;

import org.springframework.stereotype.Service;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.CurrencyConversionRate;
import za.co.banking.zacobanking.entity.response.ClientAccountResponse;
import za.co.banking.zacobanking.entity.response.CurrencyAccountResponse;
import za.co.banking.zacobanking.repository.CurrencyConverterRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    private static final String ZAR = "ZAR";
    private static final String DIVIDE = "/";
    private static final String MULTIPLY = "*";

    private CurrencyConverterRepository currencyConverterRepository;

    public CurrencyConverterServiceImpl(CurrencyConverterRepository currencyConverterRepository) {
        this.currencyConverterRepository = currencyConverterRepository;
    }

    @Override
    public List<ClientAccount> getAllNonZARCurrenciesConverted(List<ClientAccount> clientAccounts) {

        clientAccounts.stream().filter(clientAccount ->
                !clientAccount.getCurrency().getCurrencyCode().equals(ZAR)).forEach(clientAccount -> {
                    final CurrencyConversionRate currencyConversionRate = currencyConverterRepository.findById(clientAccount.getCurrency().getCurrencyCode()).get();
                    if (currencyConversionRate.getConversionIndicator().equals(DIVIDE)) {
                        final BigDecimal displayBalance = clientAccount.getDisplayBalance().divide(currencyConversionRate.getRate(), 2, RoundingMode.HALF_UP);
                        clientAccount.setDisplayBalance(displayBalance.setScale(2, RoundingMode.HALF_UP));
                        clientAccount.setCurrencyConversionRate(currencyConversionRate);
                    } else if(currencyConversionRate.getConversionIndicator().equals(MULTIPLY)) {
                        final BigDecimal displayBalance = clientAccount.getDisplayBalance().multiply(currencyConversionRate.getRate()).setScale(2, RoundingMode.HALF_UP);
                        clientAccount.setDisplayBalance(displayBalance.setScale(2, RoundingMode.HALF_UP));
                        clientAccount.setCurrencyConversionRate(currencyConversionRate);
                    }
                }
        );

        return getSortedClientAccounts(clientAccounts);
    }


    private List<ClientAccount> getSortedClientAccounts(List<ClientAccount> clientAccounts) {
        return clientAccounts.stream().sorted(Comparator.comparing(ClientAccount::getDisplayBalance).reversed()).collect(Collectors.toList());
    }

}
