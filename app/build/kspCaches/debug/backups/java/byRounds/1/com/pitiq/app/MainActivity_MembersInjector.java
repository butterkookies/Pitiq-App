package com.pitiq.app;

import com.pitiq.app.data.repository.LayoutSyncManager;
import com.pitiq.app.data.update.UpdateChecker;
import com.pitiq.app.kiosk.KioskController;
import com.pitiq.app.kiosk.KioskManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<KioskController> kioskControllerProvider;

  private final Provider<KioskManager> kioskManagerProvider;

  private final Provider<LayoutSyncManager> layoutSyncManagerProvider;

  private final Provider<UpdateChecker> updateCheckerProvider;

  public MainActivity_MembersInjector(Provider<KioskController> kioskControllerProvider,
      Provider<KioskManager> kioskManagerProvider,
      Provider<LayoutSyncManager> layoutSyncManagerProvider,
      Provider<UpdateChecker> updateCheckerProvider) {
    this.kioskControllerProvider = kioskControllerProvider;
    this.kioskManagerProvider = kioskManagerProvider;
    this.layoutSyncManagerProvider = layoutSyncManagerProvider;
    this.updateCheckerProvider = updateCheckerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<KioskController> kioskControllerProvider,
      Provider<KioskManager> kioskManagerProvider,
      Provider<LayoutSyncManager> layoutSyncManagerProvider,
      Provider<UpdateChecker> updateCheckerProvider) {
    return new MainActivity_MembersInjector(kioskControllerProvider, kioskManagerProvider, layoutSyncManagerProvider, updateCheckerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectKioskController(instance, kioskControllerProvider.get());
    injectKioskManager(instance, kioskManagerProvider.get());
    injectLayoutSyncManager(instance, layoutSyncManagerProvider.get());
    injectUpdateChecker(instance, updateCheckerProvider.get());
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.kioskController")
  public static void injectKioskController(MainActivity instance, KioskController kioskController) {
    instance.kioskController = kioskController;
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.kioskManager")
  public static void injectKioskManager(MainActivity instance, KioskManager kioskManager) {
    instance.kioskManager = kioskManager;
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.layoutSyncManager")
  public static void injectLayoutSyncManager(MainActivity instance,
      LayoutSyncManager layoutSyncManager) {
    instance.layoutSyncManager = layoutSyncManager;
  }

  @InjectedFieldSignature("com.pitiq.app.MainActivity.updateChecker")
  public static void injectUpdateChecker(MainActivity instance, UpdateChecker updateChecker) {
    instance.updateChecker = updateChecker;
  }
}
