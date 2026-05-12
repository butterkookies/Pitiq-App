package com.pitiq.app.data.repository;

import android.content.Context;
import com.pitiq.app.data.local.db.dao.LayoutDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
public final class LayoutSyncManager_Factory implements Factory<LayoutSyncManager> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  private final Provider<LayoutDao> layoutDaoProvider;

  private final Provider<HttpClient> httpClientProvider;

  private final Provider<Context> contextProvider;

  public LayoutSyncManager_Factory(Provider<SupabaseClient> supabaseClientProvider,
      Provider<LayoutDao> layoutDaoProvider, Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
    this.layoutDaoProvider = layoutDaoProvider;
    this.httpClientProvider = httpClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public LayoutSyncManager get() {
    return newInstance(supabaseClientProvider.get(), layoutDaoProvider.get(), httpClientProvider.get(), contextProvider.get());
  }

  public static LayoutSyncManager_Factory create(Provider<SupabaseClient> supabaseClientProvider,
      Provider<LayoutDao> layoutDaoProvider, Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    return new LayoutSyncManager_Factory(supabaseClientProvider, layoutDaoProvider, httpClientProvider, contextProvider);
  }

  public static LayoutSyncManager newInstance(SupabaseClient supabaseClient, LayoutDao layoutDao,
      HttpClient httpClient, Context context) {
    return new LayoutSyncManager(supabaseClient, layoutDao, httpClient, context);
  }
}
