package org.bibletranslationtools.sun.ui.navigation

import org.bibletranslationtools.sun.R

enum class MainTab(
    val title: Int,
    val icon: Int
) {
    Home(R.string.home, R.drawable.home),
    Progress(R.string.progress, R.drawable.user),
    Lessons(R.string.lessons, R.drawable.book),
    Settings(R.string.settings, R.drawable.settings)
}
