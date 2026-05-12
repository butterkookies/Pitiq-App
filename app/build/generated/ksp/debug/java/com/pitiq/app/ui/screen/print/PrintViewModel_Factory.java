package com.pitiq.app.ui.screen.print;

import android.content.Context;
import com.pitiq.app.hardware.media.MediaProcessor;
import com.pitiq.app.hardware.printer.PrinterManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PrintViewModel_Factory implements Factory<PrintViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<PrinterManager> printerManagerProvider;

  private final Provider<MediaProcessor> mediaProcessorProvider;

  public PrintViewModel_Factory(Provider<Context> contextProvider,
      Provider<PrinterManager> printerManagerProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    this.contextProvider = contextProvider;
    this.printerManagerProvider = printerManagerProvider;
    this.mediaProcessorProvider = mediaProcessorProvider;
  }

  @Override
  public PrintViewModel get() {
    return newInstance(contextProvider.get(), printerManagerProvider.get(), mediaProcessorProvider.get());
  }

  public static PrintViewModel_Factory create(Provider<Context> contextProvider,
      Provider<PrinterManager> printerManagerProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    return new PrintViewModel_Factory(contextProvider, printerManagerProvider, mediaProcessorProvider);
  }

  public static PrintViewModel newInstance(Context context, PrinterManager printerManager,
      MediaProcessor mediaProcessor) {
    return new PrintViewModel(context, printerManager, mediaProcessor);
  }
}
