package com.pitiq.app.ui.screen.setup;

import com.pitiq.app.data.local.prefs.SecurePreferences;
import com.pitiq.app.kiosk.KioskController;
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
public final class OperatorSetupViewModel_Factory implements Factory<OperatorSetupViewModel> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<KioskController> kioskControllerProvider;

  public OperatorSetupViewModel_Factory(Provider<SecurePreferences> securePreferencesProvider,
      Provider<KioskController> kioskControllerProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
    this.kioskControllerProvider = kioskControllerProvider;
  }

  @Override
  public OperatorSetupViewModel get() {
    return newInstance(securePreferencesProvider.get(), kioskControllerProvider.get());
  }

  public static OperatorSetupViewModel_Factory create(
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<KioskController> kioskControllerProvider) {
    return new OperatorSetupViewModel_Factory(securePreferencesProvider, kioskControllerProvider);
  }

  public static OperatorSetupViewModel newInstance(SecurePreferences securePreferences,
      KioskController kioskController) {
    return new OperatorSetupViewModel(securePreferences, kioskController);
  }
}
