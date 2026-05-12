package com.pitiq.app.di;

import com.pitiq.app.data.local.db.PitiqDatabase;
import com.pitiq.app.data.local.db.dao.LayoutDao;
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
public final class DatabaseModule_ProvideLayoutDaoFactory implements Factory<LayoutDao> {
  private final Provider<PitiqDatabase> dbProvider;

  public DatabaseModule_ProvideLayoutDaoFactory(Provider<PitiqDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LayoutDao get() {
    return provideLayoutDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideLayoutDaoFactory create(Provider<PitiqDatabase> dbProvider) {
    return new DatabaseModule_ProvideLayoutDaoFactory(dbProvider);
  }

  public static LayoutDao provideLayoutDao(PitiqDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLayoutDao(db));
  }
}
