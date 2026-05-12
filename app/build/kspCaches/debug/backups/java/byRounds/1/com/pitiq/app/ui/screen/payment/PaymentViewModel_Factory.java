package com.pitiq.app.ui.screen.payment;

import com.pitiq.app.hardware.bluetooth.CoinAcceptorRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class PaymentViewModel_Factory implements Factory<PaymentViewModel> {
  private final Provider<CoinAcceptorRepository> coinAcceptorRepositoryProvider;

  public PaymentViewModel_Factory(Provider<CoinAcceptorRepository> coinAcceptorRepositoryProvider) {
    this.coinAcceptorRepositoryProvider = coinAcceptorRepositoryProvider;
  }

  @Override
  public PaymentViewModel get() {
    return newInstance(coinAcceptorRepositoryProvider.get());
  }

  public static PaymentViewModel_Factory create(
      Provider<CoinAcceptorRepository> coinAcceptorRepositoryProvider) {
    return new PaymentViewModel_Factory(coinAcceptorRepositoryProvider);
  }

  public static PaymentViewModel newInstance(CoinAcceptorRepository coinAcceptorRepository) {
    return new PaymentViewModel(coinAcceptorRepository);
  }
}
