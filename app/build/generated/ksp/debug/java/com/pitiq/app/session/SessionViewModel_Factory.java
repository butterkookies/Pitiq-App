package com.pitiq.app.session;

import com.pitiq.app.data.local.prefs.SecurePreferences;
import com.pitiq.app.data.repository.LayoutSyncManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<SecurePreferences> securePreferencesProvider;

  private final Provider<SessionCleaner> sessionCleanerProvider;

  private final Provider<LayoutSyncManager> layoutSyncManagerProvider;

  public SessionViewModel_Factory(Provider<SecurePreferences> securePreferencesProvider,
      Provider<SessionCleaner> sessionCleanerProvider,
      Provider<LayoutSyncManager> layoutSyncManagerProvider) {
    this.securePreferencesProvider = securePreferencesProvider;
    this.sessionCleanerProvider = sessionCleanerProvider;
    this.layoutSyncManagerProvider = layoutSyncManagerProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(securePreferencesProvider.get(), sessionCleanerProvider.get(), layoutSyncManagerProvider.get());
  }

  public static SessionViewModel_Factory create(
      Provider<SecurePreferences> securePreferencesProvider,
      Provider<SessionCleaner> sessionCleanerProvider,
      Provider<LayoutSyncManager> layoutSyncManagerProvider) {
    return new SessionViewModel_Factory(securePreferencesProvider, sessionCleanerProvider, layoutSyncManagerProvider);
  }

  public static SessionViewModel newInstance(SecurePreferences securePreferences,
      SessionCleaner sessionCleaner, LayoutSyncManager layoutSyncManager) {
    return new SessionViewModel(securePreferences, sessionCleaner, layoutSyncManager);
  }
}
