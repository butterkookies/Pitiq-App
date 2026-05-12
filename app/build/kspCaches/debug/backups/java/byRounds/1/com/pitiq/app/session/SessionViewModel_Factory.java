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

  private final Provider<SessionCleaner> sessionCleanerProvider;

  public SessionViewModel_Factory(Provider<SecurePreferences> securePreferencesProvider,
      Provider<SessionCleaner> sessionCleanerProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
    this.sessionCleanerProvider = sessionCleanerProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(securePreferencesProvider.get(), sessionCleanerProvider.get());
  }

  public static SessionViewModel_Factory create(
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<SessionCleaner> sessionCleanerProvider) {
    return new SessionViewModel_Factory(securePreferencesProvider, sessionCleanerProvider);
  }

  public static SessionViewModel newInstance(SecurePreferences securePreferences,
      SessionCleaner sessionCleaner) {
    return new SessionViewModel(securePreferences, sessionCleaner);
  }
}
