package com.example.mediverify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mediverify.ui.navigation.AppNavigation
import com.example.mediverify.ui.theme.MediVerifyTheme
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MediVerifyViewModel by viewModels {
        MediVerifyViewModel.Factory((application as MediVerifyApplication).medicineRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediVerifyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}
