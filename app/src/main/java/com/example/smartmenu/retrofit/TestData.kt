package com.example.smartmenu.retrofit

data class TestData(val id: Int, val title: String)
data class TestDataResponse(
    val results: List<TestData>
)