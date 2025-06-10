package com.example.mycloset.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items  // This is the key import!
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycloset.data.AppRepository
import com.example.mycloset.data.ClothingItem
import com.example.mycloset.data.Outfit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookbookScreen(repository: AppRepository) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Lookbook",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(repository.outfits) { outfit ->
                OutfitCard(
                    outfit = outfit,
                    onDelete = { repository.removeOutfit(outfit) }
                )
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create Outfit")
        }
    }

    if (showCreateDialog) {
        CreateOutfitDialog(
            repository = repository,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitCard(
    outfit: Outfit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = outfit.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Outfit",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Outfit items and model photo at the same size
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                // Model image if available (same size as clothing items)
                outfit.modelImageUri?.let { uri ->
                    item {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Model photo",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Outfit items
                items(outfit.items) { item ->
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = "Outfit item",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOutfitDialog(
    repository: AppRepository,
    onDismiss: () -> Unit
) {
    var outfitName by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(setOf<ClothingItem>()) }
    var modelImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        modelImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Outfit") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = outfitName,
                    onValueChange = { outfitName = it },
                    label = { Text("Outfit Name (Optional)") }
                )

                Text("Select Items:")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(repository.clothingItems) { item ->
                        val isSelected = selectedItems.contains(item)
                        Card(
                            onClick = {
                                selectedItems = if (isSelected) {
                                    selectedItems - item
                                } else {
                                    selectedItems + item
                                }
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            AsyncImage(
                                model = item.imageUri,
                                contentDescription = "Item",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (modelImageUri != null) "Model Photo Selected" else "Add Model Photo (Optional)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalName = if (outfitName.isBlank()) "Untitled Outfit" else outfitName
                    if (selectedItems.isNotEmpty()) {
                        repository.addOutfit(
                            Outfit(
                                name = finalName,
                                items = selectedItems.toList(),
                                modelImageUri = modelImageUri?.toString()
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = selectedItems.isNotEmpty()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}