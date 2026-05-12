package com.pitiq.app.ui.screen.layout;

import com.pitiq.app.data.repository.LayoutRepository;
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
public final class LayoutSelectionViewModel_Factory implements Factory<LayoutSelectionViewModel> {
  private final Provider<LayoutRepository> layoutRepositoryProvider;

  public LayoutSelectionViewModel_Factory(Provider<LayoutRepository> layoutRepositoryProvider) {
    this.layoutRepositoryProvider = layoutRepositoryProvider;
  }

  @Override
  public LayoutSelectionViewModel get() {
    return newInstance(layoutRepositoryProvider.get());
  }

  public static LayoutSelectionViewModel_Factory create(
      Provider<LayoutRepository> layoutRepositoryProvider) {
    return new LayoutSelectionViewModel_Factory(layoutRepositoryProvider);
  }

  public static LayoutSelectionViewModel newInstance(LayoutRepository layoutRepository) {
    return new LayoutSelectionViewModel(layoutRepository);
  }
}
