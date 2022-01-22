package com.dahun.sidepanel.provider

interface IPanelDataProvider {
    fun getPanelSlideSensitive(): Float

    fun getMaxDimAlpha(): Float

    fun getPanelWidth(): Int
}