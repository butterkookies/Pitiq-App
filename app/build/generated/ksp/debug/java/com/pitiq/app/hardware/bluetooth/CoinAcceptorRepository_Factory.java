package com.pitiq.app.hardware.bluetooth;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class CoinAcceptorRepository_Factory implements Factory<CoinAcceptorRepository> {
  private final Provider<BluetoothManager> bluetoothManagerProvider;

  public CoinAcceptorRepository_Factory(Provider<BluetoothManager> bluetoothManagerProvider) {
    this.bluetoothManagerProvider = bluetoothManagerProvider;
  }

  @Override
  public CoinAcceptorRepository get() {
    return newInstance(bluetoothManagerProvider.get());
  }

  public static CoinAcceptorRepository_Factory create(
      Provider<BluetoothManager> bluetoothManagerProvider) {
    return new CoinAcceptorRepository_Factory(bluetoothManagerProvider);
  }

  public static CoinAcceptorRepository newInstance(BluetoothManager bluetoothManager) {
    return new CoinAcceptorRepository(bluetoothManager);
  }
}
