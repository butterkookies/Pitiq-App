package com.pitiq.app.session;

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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  public SessionViewModel_Factory(Provider<SecurePreferences> securePreferencesProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(securePreferencesProvider.get());
  }

  public static SessionViewModel_Factory create(
      Provider<SecurePreferences> securePreferencesProvider) {
    return new SessionViewModel_Factory(securePreferencesProvider);
  }

  public static SessionViewModel newInstance(SecurePreferences securePreferences) {
    return new SessionViewModel(securePreferences);
  }
}
