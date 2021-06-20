package com.example.smartmenu.google_drive_api

import com.example.smartmenu.retrofit.LoadingIngredientsViewState

sealed class LoadingImagesState {
    object LoadingState : LoadingImagesState()
    object LoadedState : LoadingImagesState()
    object NoItemsState : LoadingImagesState()
    object ErrorState: LoadingImagesState()
}