package org.bibletranslationtools.sun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import org.bibletranslationtools.sun.ui.MainAppTheme
import org.bibletranslationtools.sun.ui.components.DefaultRootComponent
import org.bibletranslationtools.sun.ui.components.RootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = retainedComponent { componentContext ->
            DefaultRootComponent(componentContext) {
                finish()
            }
        }

        setContent {
            MainAppTheme {
                RootScreen(root)
            }
        }
    }
}