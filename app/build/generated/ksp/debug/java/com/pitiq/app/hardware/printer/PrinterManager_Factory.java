package com.pitiq.app.hardware.printer;

import android.content.Context;
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
public final class PrinterManager_Factory implements Factory<PrinterManager> {
  private final Provider<Context> contextProvider;

  public PrinterManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PrinterManager get() {
    return newInstance(contextProvider.get());
  }

  public static PrinterManager_Factory create(Provider<Context> contextProvider) {
    return new PrinterManager_Factory(contextProvider);
  }

  public static PrinterManager newInstance(Context context) {
    return new PrinterManager(context);
  }
}
