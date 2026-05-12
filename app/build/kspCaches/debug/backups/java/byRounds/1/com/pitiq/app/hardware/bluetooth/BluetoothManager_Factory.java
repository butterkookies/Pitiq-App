package com.pitiq.app.hardware.bluetooth;

import android.content.Context;
import com.pitiq.app.data.local.prefs.SecurePreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BluetoothManager_Factory implements Factory<BluetoothManager> {
  private final Provider<Context> contextProvider;

  private final Provider<SecurePreferences> securePreferencesProvider;

  public BluetoothManager_Factory(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    this.contextProvider = contextProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public BluetoothManager get() {
    return newInstance(contextProvider.get(), securePreferencesProvider.get());
  }

  public static BluetoothManager_Factory create(Provider<Context> contextProvider,
      Provider<SecurePreferences> securePreferencesProvider) {
    return new BluetoothManager_Factory(contextProvider, securePreferencesProvider);
  }

  public static BluetoothManager newInstance(Context context, SecurePreferences securePreferences) {
    return new BluetoothManager(context, securePreferences);
  }
}
