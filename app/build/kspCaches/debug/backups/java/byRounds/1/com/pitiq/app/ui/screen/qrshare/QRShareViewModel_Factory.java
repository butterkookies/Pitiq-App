package com.pitiq.app.ui.screen.qrshare;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class QRShareViewModel_Factory implements Factory<QRShareViewModel> {
  @Override
  public QRShareViewModel get() {
    return newInstance();
  }

  public static QRShareViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QRShareViewModel newInstance() {
    return new QRShareViewModel();
  }

  private static final class InstanceHolder {
    static final QRShareViewModel_Factory INSTANCE = new QRShareViewModel_Factory();
  }
}
