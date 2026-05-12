package com.pitiq.app.data.repository;

import com.pitiq.app.data.local.db.dao.LayoutDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LayoutRepository_Factory implements Factory<LayoutRepository> {
  private final Provider<LayoutDao> layoutDaoProvider;

  public LayoutRepository_Factory(Provider<LayoutDao> layoutDaoProvider) {
    this.layoutDaoProvider = layoutDaoProvider;
  }

  @Override
  public LayoutRepository get() {
    return newInstance(layoutDaoProvider.get());
  }

  public static LayoutRepository_Factory create(Provider<LayoutDao> layoutDaoProvider) {
    return new LayoutRepository_Factory(layoutDaoProvider);
  }

  public static LayoutRepository newInstance(LayoutDao layoutDao) {
    return new LayoutRepository(layoutDao);
  }
}
