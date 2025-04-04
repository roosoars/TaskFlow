package com.roosoars.taskflow.di;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.roosoars.taskflow.viewmodel.CategoryViewModel;
import com.roosoars.taskflow.viewmodel.TaskViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Dagger module for providing ViewModels
 * Implements Dependency Injection pattern
 */
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel.class)
    abstract ViewModel bindTaskViewModel(TaskViewModel taskViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    abstract ViewModel bindCategoryViewModel(CategoryViewModel categoryViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}