package com.pitiq.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.pitiq.app.data.local.db.PitiqDatabase;
import com.pitiq.app.data.local.db.dao.LayoutDao;
import com.pitiq.app.data.local.db.dao.UploadQueueDao;
import com.pitiq.app.data.local.prefs.SecurePreferences;
import com.pitiq.app.data.repository.LayoutRepository;
import com.pitiq.app.data.repository.LayoutSyncManager;
import com.pitiq.app.data.update.UpdateChecker;
import com.pitiq.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.pitiq.app.di.DatabaseModule_ProvideLayoutDaoFactory;
import com.pitiq.app.di.DatabaseModule_ProvideUploadQueueDaoFactory;
import com.pitiq.app.di.NetworkModule_ProvideHttpClientFactory;
import com.pitiq.app.di.SupabaseModule_ProvideSupabaseClientFactory;
import com.pitiq.app.hardware.bluetooth.BluetoothManager;
import com.pitiq.app.hardware.bluetooth.CoinAcceptorRepository;
import com.pitiq.app.hardware.media.MediaProcessor;
import com.pitiq.app.hardware.printer.PrinterManager;
import com.pitiq.app.kiosk.KioskController;
import com.pitiq.app.kiosk.KioskManager;
import com.pitiq.app.kiosk.KioskViewModel;
import com.pitiq.app.kiosk.KioskViewModel_HiltModules;
import com.pitiq.app.kiosk.KioskViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.kiosk.KioskViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.session.SessionCleaner;
import com.pitiq.app.session.SessionViewModel;
import com.pitiq.app.session.SessionViewModel_HiltModules;
import com.pitiq.app.session.SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.session.SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.attract.AttractViewModel;
import com.pitiq.app.ui.screen.attract.AttractViewModel_HiltModules;
import com.pitiq.app.ui.screen.attract.AttractViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.attract.AttractViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.edit.EditViewModel;
import com.pitiq.app.ui.screen.edit.EditViewModel_HiltModules;
import com.pitiq.app.ui.screen.edit.EditViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.edit.EditViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.layout.LayoutSelectionViewModel;
import com.pitiq.app.ui.screen.layout.LayoutSelectionViewModel_HiltModules;
import com.pitiq.app.ui.screen.layout.LayoutSelectionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.layout.LayoutSelectionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.payment.PaymentViewModel;
import com.pitiq.app.ui.screen.payment.PaymentViewModel_HiltModules;
import com.pitiq.app.ui.screen.payment.PaymentViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.payment.PaymentViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.print.PrintViewModel;
import com.pitiq.app.ui.screen.print.PrintViewModel_HiltModules;
import com.pitiq.app.ui.screen.print.PrintViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.print.PrintViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.qrshare.QRShareViewModel;
import com.pitiq.app.ui.screen.qrshare.QRShareViewModel_HiltModules;
import com.pitiq.app.ui.screen.qrshare.QRShareViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.qrshare.QRShareViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.setup.OperatorSetupViewModel;
import com.pitiq.app.ui.screen.setup.OperatorSetupViewModel_HiltModules;
import com.pitiq.app.ui.screen.setup.OperatorSetupViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.setup.OperatorSetupViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.pitiq.app.ui.screen.upload.UploadViewModel;
import com.pitiq.app.ui.screen.upload.UploadViewModel_HiltModules;
import com.pitiq.app.ui.screen.upload.UploadViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.pitiq.app.ui.screen.upload.UploadViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.github.jan.supabase.SupabaseClient;
import io.ktor.client.HttpClient;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerPitiqApplication_HiltComponents_SingletonC {
  private DaggerPitiqApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PitiqApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PitiqApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PitiqApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PitiqApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PitiqApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PitiqApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PitiqApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PitiqApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PitiqApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PitiqApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PitiqApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PitiqApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PitiqApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(10).put(AttractViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AttractViewModel_HiltModules.KeyModule.provide()).put(EditViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, EditViewModel_HiltModules.KeyModule.provide()).put(KioskViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, KioskViewModel_HiltModules.KeyModule.provide()).put(LayoutSelectionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LayoutSelectionViewModel_HiltModules.KeyModule.provide()).put(OperatorSetupViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, OperatorSetupViewModel_HiltModules.KeyModule.provide()).put(PaymentViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PaymentViewModel_HiltModules.KeyModule.provide()).put(PrintViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PrintViewModel_HiltModules.KeyModule.provide()).put(QRShareViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, QRShareViewModel_HiltModules.KeyModule.provide()).put(SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SessionViewModel_HiltModules.KeyModule.provide()).put(UploadViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, UploadViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectKioskController(instance, singletonCImpl.kioskControllerProvider.get());
      MainActivity_MembersInjector.injectKioskManager(instance, singletonCImpl.kioskManagerProvider.get());
      MainActivity_MembersInjector.injectLayoutSyncManager(instance, singletonCImpl.layoutSyncManagerProvider.get());
      MainActivity_MembersInjector.injectUpdateChecker(instance, singletonCImpl.updateCheckerProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends PitiqApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AttractViewModel> attractViewModelProvider;

    Provider<EditViewModel> editViewModelProvider;

    Provider<KioskViewModel> kioskViewModelProvider;

    Provider<LayoutSelectionViewModel> layoutSelectionViewModelProvider;

    Provider<OperatorSetupViewModel> operatorSetupViewModelProvider;

    Provider<PaymentViewModel> paymentViewModelProvider;

    Provider<PrintViewModel> printViewModelProvider;

    Provider<QRShareViewModel> qRShareViewModelProvider;

    Provider<SessionViewModel> sessionViewModelProvider;

    Provider<UploadViewModel> uploadViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.attractViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.editViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.kioskViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.layoutSelectionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.operatorSetupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.paymentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.printViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.qRShareViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.sessionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.uploadViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(10).put(AttractViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) attractViewModelProvider)).put(EditViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) editViewModelProvider)).put(KioskViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) kioskViewModelProvider)).put(LayoutSelectionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) layoutSelectionViewModelProvider)).put(OperatorSetupViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) operatorSetupViewModelProvider)).put(PaymentViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) paymentViewModelProvider)).put(PrintViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) printViewModelProvider)).put(QRShareViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) qRShareViewModelProvider)).put(SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) sessionViewModelProvider)).put(UploadViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) uploadViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pitiq.app.ui.screen.attract.AttractViewModel
          return (T) new AttractViewModel(singletonCImpl.printerManagerProvider.get());

          case 1: // com.pitiq.app.ui.screen.edit.EditViewModel
          return (T) new EditViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.mediaProcessorProvider.get());

          case 2: // com.pitiq.app.kiosk.KioskViewModel
          return (T) new KioskViewModel(singletonCImpl.kioskControllerProvider.get(), singletonCImpl.securePreferencesProvider.get());

          case 3: // com.pitiq.app.ui.screen.layout.LayoutSelectionViewModel
          return (T) new LayoutSelectionViewModel(singletonCImpl.layoutRepositoryProvider.get());

          case 4: // com.pitiq.app.ui.screen.setup.OperatorSetupViewModel
          return (T) new OperatorSetupViewModel(singletonCImpl.securePreferencesProvider.get(), singletonCImpl.kioskControllerProvider.get());

          case 5: // com.pitiq.app.ui.screen.payment.PaymentViewModel
          return (T) new PaymentViewModel(singletonCImpl.coinAcceptorRepositoryProvider.get());

          case 6: // com.pitiq.app.ui.screen.print.PrintViewModel
          return (T) new PrintViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.printerManagerProvider.get(), singletonCImpl.mediaProcessorProvider.get());

          case 7: // com.pitiq.app.ui.screen.qrshare.QRShareViewModel
          return (T) new QRShareViewModel();

          case 8: // com.pitiq.app.session.SessionViewModel
          return (T) new SessionViewModel(singletonCImpl.securePreferencesProvider.get(), singletonCImpl.sessionCleanerProvider.get(), singletonCImpl.layoutSyncManagerProvider.get());

          case 9: // com.pitiq.app.ui.screen.upload.UploadViewModel
          return (T) new UploadViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.uploadQueueDao(), singletonCImpl.mediaProcessorProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PitiqApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PitiqApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PitiqApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<SecurePreferences> securePreferencesProvider;

    Provider<KioskController> kioskControllerProvider;

    Provider<KioskManager> kioskManagerProvider;

    Provider<SupabaseClient> provideSupabaseClientProvider;

    Provider<PitiqDatabase> provideDatabaseProvider;

    Provider<HttpClient> provideHttpClientProvider;

    Provider<LayoutSyncManager> layoutSyncManagerProvider;

    Provider<UpdateChecker> updateCheckerProvider;

    Provider<PrinterManager> printerManagerProvider;

    Provider<MediaProcessor> mediaProcessorProvider;

    Provider<LayoutRepository> layoutRepositoryProvider;

    Provider<BluetoothManager> bluetoothManagerProvider;

    Provider<CoinAcceptorRepository> coinAcceptorRepositoryProvider;

    Provider<SessionCleaner> sessionCleanerProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    LayoutDao layoutDao() {
      return DatabaseModule_ProvideLayoutDaoFactory.provideLayoutDao(provideDatabaseProvider.get());
    }

    UploadQueueDao uploadQueueDao() {
      return DatabaseModule_ProvideUploadQueueDaoFactory.provideUploadQueueDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.securePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SecurePreferences>(singletonCImpl, 1));
      this.kioskControllerProvider = DoubleCheck.provider(new SwitchingProvider<KioskController>(singletonCImpl, 0));
      this.kioskManagerProvider = DoubleCheck.provider(new SwitchingProvider<KioskManager>(singletonCImpl, 2));
      this.provideSupabaseClientProvider = DoubleCheck.provider(new SwitchingProvider<SupabaseClient>(singletonCImpl, 4));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<PitiqDatabase>(singletonCImpl, 5));
      this.provideHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<HttpClient>(singletonCImpl, 6));
      this.layoutSyncManagerProvider = DoubleCheck.provider(new SwitchingProvider<LayoutSyncManager>(singletonCImpl, 3));
      this.updateCheckerProvider = DoubleCheck.provider(new SwitchingProvider<UpdateChecker>(singletonCImpl, 7));
      this.printerManagerProvider = DoubleCheck.provider(new SwitchingProvider<PrinterManager>(singletonCImpl, 8));
      this.mediaProcessorProvider = DoubleCheck.provider(new SwitchingProvider<MediaProcessor>(singletonCImpl, 9));
      this.layoutRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LayoutRepository>(singletonCImpl, 10));
      this.bluetoothManagerProvider = DoubleCheck.provider(new SwitchingProvider<BluetoothManager>(singletonCImpl, 12));
      this.coinAcceptorRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CoinAcceptorRepository>(singletonCImpl, 11));
      this.sessionCleanerProvider = DoubleCheck.provider(new SwitchingProvider<SessionCleaner>(singletonCImpl, 13));
    }

    @Override
    public void injectPitiqApplication(PitiqApplication pitiqApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pitiq.app.kiosk.KioskController
          return (T) new KioskController(singletonCImpl.securePreferencesProvider.get());

          case 1: // com.pitiq.app.data.local.prefs.SecurePreferences
          return (T) new SecurePreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.pitiq.app.kiosk.KioskManager
          return (T) new KioskManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.pitiq.app.data.repository.LayoutSyncManager
          return (T) new LayoutSyncManager(singletonCImpl.provideSupabaseClientProvider.get(), singletonCImpl.layoutDao(), singletonCImpl.provideHttpClientProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // io.github.jan.supabase.SupabaseClient
          return (T) SupabaseModule_ProvideSupabaseClientFactory.provideSupabaseClient();

          case 5: // com.pitiq.app.data.local.db.PitiqDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // io.ktor.client.HttpClient
          return (T) NetworkModule_ProvideHttpClientFactory.provideHttpClient();

          case 7: // com.pitiq.app.data.update.UpdateChecker
          return (T) new UpdateChecker(singletonCImpl.provideHttpClientProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.pitiq.app.hardware.printer.PrinterManager
          return (T) new PrinterManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.pitiq.app.hardware.media.MediaProcessor
          return (T) new MediaProcessor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.pitiq.app.data.repository.LayoutRepository
          return (T) new LayoutRepository(singletonCImpl.layoutDao());

          case 11: // com.pitiq.app.hardware.bluetooth.CoinAcceptorRepository
          return (T) new CoinAcceptorRepository(singletonCImpl.bluetoothManagerProvider.get());

          case 12: // com.pitiq.app.hardware.bluetooth.BluetoothManager
          return (T) new BluetoothManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.securePreferencesProvider.get());

          case 13: // com.pitiq.app.session.SessionCleaner
          return (T) new SessionCleaner(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
