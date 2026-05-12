package com.pitiq.app;

import com.pitiq.app.kiosk.KioskController;
import com.pitiq.app.kiosk.KioskManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<KioskController> kioskControllerProvider;

  private final Provider<KioskManager> kioskManagerProvider;

  public MainActivity_MembersInjector(Provider<KioskController> kioskControllerProvider,
      Provider<KioskManager> kioskManagerProvider) {
    this.kioskControllerProvider = kioskControllerProvider;
    this.kioskManagerProvider = kioskManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<KioskController> kioskControllerProvider,
      Provider<KioskManager> kioskManagerProvider) {
    return new MainActivity_MembersInjector(kioskControllerProvider, kioskManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectKioskController(instance, kioskControllerProvider.get());
    injectKioskManager(instance, kioskManagerProvider.get());
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.kioskController")
  public static void injectKioskController(MainActivity instance, KioskController kioskController) {
    instance.kioskController = kioskController;
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.kioskManager")
  public static void injectKioskManager(MainActivity instance, KioskManager kioskManager) {
    instance.kioskManager = kioskManager;
  }
}
