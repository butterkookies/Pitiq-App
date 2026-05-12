package com.pitiq.app.hardware.media;

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
public final class MediaProcessor_Factory implements Factory<MediaProcessor> {
  private final Provider<Context> contextProvider;

  public MediaProcessor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MediaProcessor get() {
    return newInstance(contextProvider.get());
  }

  public static MediaProcessor_Factory create(Provider<Context> contextProvider) {
    return new MediaProcessor_Factory(contextProvider);
  }

  public static MediaProcessor newInstance(Context context) {
    return new MediaProcessor(context);
  }
}
