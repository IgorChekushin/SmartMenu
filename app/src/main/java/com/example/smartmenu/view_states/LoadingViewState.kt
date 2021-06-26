package com.example.smartmenu.view_states

sealed class LoadingViewState{
    object LoadingState : LoadingViewState()
    object LoadedState : LoadingViewState()
    object NoItemsState : LoadingViewState()
    object ErrorState: LoadingViewState()
}

