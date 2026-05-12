package com.pitiq.app.di;

import com.pitiq.app.data.local.db.PitiqDatabase;
import com.pitiq.app.data.local.db.dao.UploadQueueDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
public final class DatabaseModule_ProvideUploadQueueDaoFactory implements Factory<UploadQueueDao> {
  private final Provider<PitiqDatabase> dbProvider;

  public DatabaseModule_ProvideUploadQueueDaoFactory(Provider<PitiqDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UploadQueueDao get() {
    return provideUploadQueueDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideUploadQueueDaoFactory create(
      Provider<PitiqDatabase> dbProvider) {
    return new DatabaseModule_ProvideUploadQueueDaoFactory(dbProvider);
  }

  public static UploadQueueDao provideUploadQueueDao(PitiqDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUploadQueueDao(db));
  }
}
