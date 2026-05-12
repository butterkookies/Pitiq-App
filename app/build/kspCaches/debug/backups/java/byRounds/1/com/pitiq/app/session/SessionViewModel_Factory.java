package com.pitiq.app.session;

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
    "deprecation"
})
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  @Override
  public SessionViewModel get() {
    return newInstance();
  }

  public static SessionViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SessionViewModel newInstance() {
    return new SessionViewModel();
  }

  private static final class InstanceHolder {
    private static final SessionViewModel_Factory INSTANCE = new SessionViewModel_Factory();
  }
}
