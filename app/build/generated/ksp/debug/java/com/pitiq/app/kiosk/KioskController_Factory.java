package com.pitiq.app.kiosk;

import com.pitiq.app.data.local.prefs.SecurePreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class KioskController_Factory implements Factory<KioskController> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  public KioskController_Factory(Provider<SecurePreferences> securePreferencesProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public KioskController get() {
    return newInstance(securePreferencesProvider.get());
  }

  public static KioskController_Factory create(
      Provider<SecurePreferences> securePreferencesProvider) {
    return new KioskController_Factory(securePreferencesProvider);
  }

  public static KioskController newInstance(SecurePreferences securePreferences) {
    return new KioskController(securePreferences);
  }
}
