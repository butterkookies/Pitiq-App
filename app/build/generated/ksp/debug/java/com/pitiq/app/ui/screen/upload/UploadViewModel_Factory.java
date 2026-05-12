package com.pitiq.app.ui.screen.upload;

import android.content.Context;
import com.pitiq.app.data.local.db.dao.UploadQueueDao;
import com.pitiq.app.hardware.media.MediaProcessor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
public final class UploadViewModel_Factory implements Factory<UploadViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SupabaseClient> supabaseClientProvider;

  private final Provider<UploadQueueDao> uploadQueueDaoProvider;

  private final Provider<MediaProcessor> mediaProcessorProvider;

  public UploadViewModel_Factory(Provider<Context> contextProvider,
      Provider<SupabaseClient> supabaseClientProvider,
      Provider<UploadQueueDao> uploadQueueDaoProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    this.contextProvider = contextProvider;
    this.supabaseClientProvider = supabaseClientProvider;
    this.uploadQueueDaoProvider = uploadQueueDaoProvider;
    this.mediaProcessorProvider = mediaProcessorProvider;
  }

  @Override
  public UploadViewModel get() {
    return newInstance(contextProvider.get(), supabaseClientProvider.get(), uploadQueueDaoProvider.get(), mediaProcessorProvider.get());
  }

  public static UploadViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SupabaseClient> supabaseClientProvider,
      Provider<UploadQueueDao> uploadQueueDaoProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    return new UploadViewModel_Factory(contextProvider, supabaseClientProvider, uploadQueueDaoProvider, mediaProcessorProvider);
  }

  public static UploadViewModel newInstance(Context context, SupabaseClient supabaseClient,
      UploadQueueDao uploadQueueDao, MediaProcessor mediaProcessor) {
    return new UploadViewModel(context, supabaseClient, uploadQueueDao, mediaProcessor);
  }
}
