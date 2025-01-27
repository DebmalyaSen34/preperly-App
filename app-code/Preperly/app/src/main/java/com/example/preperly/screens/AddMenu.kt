package com.example.preperly.screens

import android.net.Uri
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.preperly.datamodels.Category
import com.example.preperly.datamodels.MenuItem
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.MenuViewModel
import com.example.preperly.viewmodels.SharedViewModel


@Composable
fun MenuUploadScreen(
    viewModel: MenuViewModel,
    sharedViewModel: SharedViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MenuItem?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategoryForSubCategory by remember { mutableStateOf<Category?>(null) }

    // LazyColumn should handle the scrolling of menu items
    val context = LocalContext.current
    Toast.makeText(context,sharedViewModel.getPhoneNumber(),Toast.LENGTH_SHORT).show()

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        item{
            ProgressIndicator(currentStep = viewModel.currentStep)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Menu Upload",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                TextButton(
                    onClick = { showAddCategoryDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = myRed)
                ) {
                    Text("Add Category")
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        items(viewModel.categories) { category ->
            CategoryItem(
                category = category,
                onAddSubCategory = { selectedCategoryForSubCategory = category }
            )
        }

        // Add Items in the list (inside LazyColumn for scroll)
        items(viewModel.menuItems) { item ->
            MenuItemCard(
                item = item,
                onEdit = { editingItem = item }
            )
        }

        item{
            TextButton(
                onClick = { showAddItemDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = myRed),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Add Item")
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        item{
            // Navigation Buttons (this is not inside LazyColumn, will stay fixed at the bottom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
//                        onNext()
                        if(viewModel.menuItems.isNotEmpty()){
                            onNext()
//                            viewModel.resMenuToApi(sharedViewModel.getPhoneNumber())
                            Toast.makeText(context,"Registered Successfully",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,"Please Add your Menu",Toast.LENGTH_SHORT).show()
                        }
                        Log.d("Next Button","Clicked")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = myRed),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }

    // Dialogs
    if (showAddItemDialog) {
        AddEditItemDialog(
            item = null,
            categories = viewModel.categories,
            onDismiss = { showAddItemDialog = false },
            onConfirm = { item ->
                viewModel.addMenuItem(item)
                showAddItemDialog = false
            }
        )
    }

    editingItem?.let { item ->
        AddEditItemDialog(
            item = item,
            categories = viewModel.categories,
            onDismiss = { editingItem = null },
            onConfirm = { updatedItem ->
                viewModel.updateMenuItem(item, updatedItem)
                editingItem = null
            }
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { categoryName ->
                viewModel.addCategory(Category(categoryName))
                showAddCategoryDialog = false
            }
        )
    }

    selectedCategoryForSubCategory?.let { category ->
        AddSubCategoryDialog(
            category = category,
            onDismiss = { selectedCategoryForSubCategory = null },
            onConfirm = { subCategoryName ->
                viewModel.addSubCategory(category, subCategoryName)
                selectedCategoryForSubCategory = null
            }
        )
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Category",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = myRed)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(categoryName) },
                        enabled = categoryName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun AddSubCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var subCategoryName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Sub-Category to ${category.name}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = subCategoryName,
                    onValueChange = { subCategoryName = it },
                    label = { Text("Sub-Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = myRed)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(subCategoryName) },
                        enabled = subCategoryName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onAddSubCategory: () -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = category.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        category.subCategories.forEach { subCategory ->
            Text(
                text = subCategory,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        TextButton(
            onClick = onAddSubCategory,
            colors = ButtonDefaults.textButtonColors(contentColor = myRed),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text("Add sub-category")
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Sub-category",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    item: MenuItem?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (MenuItem) -> Unit
) {
    var itemName by remember { mutableStateOf(item?.name ?: "") }
    var itemDescription by remember { mutableStateOf(item?.description ?: "") }
    var itemType by remember { mutableStateOf(item?.itemType ?: "") }
    var containsDairy by remember { mutableStateOf(item?.containsDairy ?: false) }
    var selectedCategory by remember { mutableStateOf(categories.find { it.name == item?.category }) }
    var selectedSubCategory by remember { mutableStateOf(item?.subCategory ?: "") }
    var imageUri by remember { mutableStateOf(item?.imageUri ?: Uri.EMPTY) }
    val itemTypeList = listOf("-Select-", "Veg", "Non-Veg","Egg")

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isSubCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isItemTypeDropDownExpanded by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri = it}
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name*") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = { imagePicker.launch("image/*") },
                    colors = ButtonDefaults.textButtonColors(contentColor = myRed),
                ) {
                    if(imageUri != Uri.EMPTY){
                        Log.d("ImageUri",imageUri.toString())
                        PhotoUploadSuccess()
                    }else {
                        Text("Add item photo*")
                    }
                }

                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Item description*") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = isItemTypeDropDownExpanded ,
                    onExpandedChange = {isItemTypeDropDownExpanded = !isItemTypeDropDownExpanded}) {

                    OutlinedTextField(
                        value = itemType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Item type*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isItemTypeDropDownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = isItemTypeDropDownExpanded ,
                        onDismissRequest = { isItemTypeDropDownExpanded = false }) {

                        itemTypeList.forEach{ item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    itemType = item
                                    isItemTypeDropDownExpanded = false
                                }
                            )

                        }
                    }
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = containsDairy,
                        onCheckedChange = { containsDairy = it }
                    )
                    Text("Tick if this contains dairy products")
                }

                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    selectedSubCategory = ""
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedCategory != null) {
                    ExposedDropdownMenuBox(
                        expanded = isSubCategoryDropdownExpanded,
                        onExpandedChange = { isSubCategoryDropdownExpanded = !isSubCategoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSubCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sub-category*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubCategoryDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = isSubCategoryDropdownExpanded,
                            onDismissRequest = { isSubCategoryDropdownExpanded = false }
                        ) {
                            selectedCategory!!.subCategories
                                .forEach { subCategory ->
                                    DropdownMenuItem(
                                        text = { Text(subCategory) },
                                        onClick = {
                                            selectedSubCategory = subCategory
                                            isSubCategoryDropdownExpanded = false
                                        }
                                    )
                                }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        ) {
                        Text("Back")
                    }
                    var itemId = 0
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                MenuItem(
                                    id = ++itemId,
                                    name = itemName,
                                    description = itemDescription,
                                    itemType = itemType,
                                    containsDairy = containsDairy,
                                    category = selectedCategory?.name ?: "",
                                    subCategory = selectedSubCategory,
                                    imageUri = imageUri,
                                    isAvailable = true
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed)
                    ) {
                        Text("Add Item")
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoUploadSuccess(){
    Row{
        Text(
            "Uploaded Successfully",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic
        )
        Image(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "completed",
            colorFilter = ColorFilter.tint(Color.Green))
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUri,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            TextButton(onClick = onEdit) {
                Text("Edit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuUploadScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        MenuUploadScreen(
            viewModel = MenuViewModel(),
            sharedViewModel = SharedViewModel(),
            onNext = {navController.navigate("step6")},
            onBack = {navController.popBackStack()}
        )
    }
}