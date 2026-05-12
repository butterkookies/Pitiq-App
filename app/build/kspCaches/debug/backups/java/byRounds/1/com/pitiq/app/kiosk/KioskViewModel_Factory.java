package com.pitiq.app.kiosk;

import com.pitiq.app.data.local.prefs.SecurePreferences;
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
public final class KioskViewModel_Factory implements Factory<KioskViewModel> {
  private final Provider<KioskController> kioskControllerProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public KioskViewModel_Factory(Provider<KioskController> kioskControllerProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.kioskControllerProvider = kioskControllerProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public KioskViewModel get() {
    return newInstance(kioskControllerProvider.get(), securePreferencesProvider.get());
  }

  public static KioskViewModel_Factory create(Provider<KioskController> kioskControllerProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new KioskViewModel_Factory(kioskControllerProvider, securePreferencesProvider);
  }

  public static KioskViewModel newInstance(KioskController kioskController,
      SecurePreferences securePreferences) {
    return new KioskViewModel(kioskController, securePreferences);
  }
}
