package com.pitiq.app.ui.screen.attract;

import com.pitiq.app.hardware.printer.PrinterManager;
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
public final class AttractViewModel_Factory implements Factory<AttractViewModel> {
  private final Provider<PrinterManager> printerManagerProvider;

  public AttractViewModel_Factory(Provider<PrinterManager> printerManagerProvider) {
    this.printerManagerProvider = printerManagerProvider;
  }

  @Override
  public AttractViewModel get() {
    return newInstance(printerManagerProvider.get());
  }

  public static AttractViewModel_Factory create(Provider<PrinterManager> printerManagerProvider) {
    return new AttractViewModel_Factory(printerManagerProvider);
  }

  public static AttractViewModel newInstance(PrinterManager printerManager) {
    return new AttractViewModel(printerManager);
  }
}
