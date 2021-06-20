package com.example.smartmenu.retrofit

sealed class LoadingIngredientsViewState {
    object LoadingState : LoadingIngredientsViewState()
    object LoadedState : LoadingIngredientsViewState()
    object NoItemsState : LoadingIngredientsViewState()
    object ErrorState: LoadingIngredientsViewState()
}