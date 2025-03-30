package com.eva.features.workwithcamera.ui.imagesfrominternet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ImagesFromInternetScreen(
    viewModel: ImagesFromInternetScreenVM = hiltViewModel()
) {
    Surface(
        color = Color.White
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.padding(paddingValues))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    itemsIndexed(uiState.listOfOrganisations) { _, organisation ->
//                        ItemCard(
//                            name = organisation.nameOfOrganisation,
//                            description = organisation.descriptionOfOrganisation
//                        )
//                    }
                }
            }
        }
    }
}