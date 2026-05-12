package com.pitiq.app.ui.screen.edit;

import android.content.Context;
import com.pitiq.app.hardware.media.MediaProcessor;
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
public final class EditViewModel_Factory implements Factory<EditViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<MediaProcessor> mediaProcessorProvider;

  public EditViewModel_Factory(Provider<Context> contextProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    this.contextProvider = contextProvider;
    this.mediaProcessorProvider = mediaProcessorProvider;
  }

  @Override
  public EditViewModel get() {
    return newInstance(contextProvider.get(), mediaProcessorProvider.get());
  }

  public static EditViewModel_Factory create(Provider<Context> contextProvider,
      Provider<MediaProcessor> mediaProcessorProvider) {
    return new EditViewModel_Factory(contextProvider, mediaProcessorProvider);
  }

  public static EditViewModel newInstance(Context context, MediaProcessor mediaProcessor) {
    return new EditViewModel(context, mediaProcessor);
  }
}
