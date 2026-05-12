package com.pitiq.app.data.update;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;

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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class UpdateChecker_Factory implements Factory<UpdateChecker> {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<Context> contextProvider;

  public UpdateChecker_Factory(Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    this.httpClientProvider = httpClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UpdateChecker get() {
    return newInstance(httpClientProvider.get(), contextProvider.get());
  }

  public static UpdateChecker_Factory create(Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    return new UpdateChecker_Factory(httpClientProvider, contextProvider);
  }

  public static UpdateChecker newInstance(HttpClient httpClient, Context context) {
    return new UpdateChecker(httpClient, context);
  }
}
