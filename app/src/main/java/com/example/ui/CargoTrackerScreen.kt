package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.data.CargoItem
import com.example.data.MarketplacePreset
import com.example.data.AddressPreset
import com.example.data.CarrierPreset
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargoTrackerScreen(
    viewModel: CargoViewModel,
    modifier: Modifier = Modifier
) {
    val activeCargos by viewModel.activeCargos.collectAsStateWithLifecycle()
    val arrivedCargos by viewModel.arrivedCargos.collectAsStateWithLifecycle()
    val marketplaces by viewModel.marketplaces.collectAsStateWithLifecycle()
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val carriers by viewModel.carriers.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Bekleyenler, 1 = Gelenler
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CargoItem?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Kargo Takibi",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                            .clickable { /* Profile placeholder */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Yeni Kargo Ekle") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_cargo_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sleek Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Bekleyenler",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            if (activeCargos.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = activeCargos.size.toString(),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("tab_active")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Gelenler",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            if (arrivedCargos.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ) {
                                    Text(
                                        text = arrivedCargos.size.toString(),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("tab_arrived")
                )
            }

            // List area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (selectedTab == 0) {
                    CargoListSection(
                        cargos = activeCargos,
                        isArrivedSection = false,
                        onToggleArrived = { viewModel.toggleArrivalStatus(it) },
                        onDeleteCargo = { itemToDelete = it },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CargoListSection(
                        cargos = arrivedCargos,
                        isArrivedSection = true,
                        onToggleArrived = { viewModel.toggleArrivalStatus(it) },
                        onDeleteCargo = { itemToDelete = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Add Cargo Dialog
    if (showAddDialog) {
        AddCargoDialog(
            onDismiss = { showAddDialog = false },
            marketplaces = marketplaces,
            addresses = addresses,
            carriers = carriers,
            onAddMarketplace = { viewModel.addMarketplacePreset(it) },
            onDeleteMarketplace = { viewModel.deleteMarketplacePreset(it) },
            onAddAddress = { viewModel.addAddressPreset(it) },
            onDeleteAddress = { viewModel.deleteAddressPreset(it) },
            onAddCarrier = { viewModel.addCarrierPreset(it) },
            onDeleteCarrier = { viewModel.deleteCarrierPreset(it) },
            onSave = { prodName, address, carrier, marketplace ->
                viewModel.addCargo(prodName, address, carrier, marketplace)
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Kargoyu Sil") },
            text = { Text("'${item.productName}' isimli kargo kaydını kalıcı olarak silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCargo(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Evet, Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun CargoListSection(
    cargos: List<CargoItem>,
    isArrivedSection: Boolean,
    onToggleArrived: (CargoItem) -> Unit,
    onDeleteCargo: (CargoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cargos.isEmpty()) {
        Box(
            modifier = modifier.padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                Icon(
                    imageVector = if (isArrivedSection) Icons.Outlined.Archive else Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = if (isArrivedSection) "Henüz ulaşmış bir kargonuz yok." else "Bekleyen kargonuz bulunmuyor.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isArrivedSection) "Bekleyen kargolarınızı teslim aldığınızda listedeki onay butonuna basarak buraya taşıyabilirsiniz." else "Yeni siparişlerinizi eklemek için sağ alttaki butonu kullanın.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cargos, key = { it.id }) { cargo ->
                CargoItemCard(
                    cargo = cargo,
                    isArrivedSection = isArrivedSection,
                    onToggleArrived = { onToggleArrived(cargo) },
                    onDelete = { onDeleteCargo(cargo) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CargoItemCard(
    cargo: CargoItem,
    isArrivedSection: Boolean,
    onToggleArrived: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val localeTr = remember { Locale("tr", "TR") }
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", localeTr) }

    val formattedAddedDate = remember(cargo.addedTimestamp) {
        try {
            dateFormatter.format(Date(cargo.addedTimestamp))
        } catch (e: Exception) {
            ""
        }
    }

    val formattedArrivedDate = remember(cargo.arrivedTimestamp) {
        cargo.arrivedTimestamp?.let {
            try {
                dateFormatter.format(Date(it))
            } catch (e: Exception) {
                null
            }
        }
    }

    // Determine badge colors based on marketplace
    val (marketplaceBg, marketplaceText) = remember(cargo.marketplace) {
        val lower = cargo.marketplace.lowercase()
        when {
            lower.contains("trendyol") -> Pair(ColorTrendyolBg, ColorTrendyolText)
            lower.contains("hepsiburada") -> Pair(ColorHepsiburadaBg, ColorHepsiburadaText)
            lower.contains("amazon") -> Pair(ColorAmazonBg, ColorAmazonText)
            lower.contains("n11") -> Pair(ColorN11Bg, ColorN11Text)
            else -> Pair(ColorDefaultMarketplaceBg, ColorDefaultMarketplaceText)
        }
    }

    // Determine badge colors based on cargo carrier
    val (carrierBg, carrierText) = remember(cargo.cargoCarrier) {
        val lower = cargo.cargoCarrier.lowercase()
        when {
            lower.contains("aras") -> Pair(ColorArasBg, ColorArasText)
            lower.contains("yurtiçi") || lower.contains("yurtici") -> Pair(ColorYurticiBg, ColorYurticiText)
            lower.contains("mng") -> Pair(ColorMngBg, ColorMngText)
            lower.contains("express") || lower.contains("trendyol express") -> Pair(ColorTrendyolExpressBg, ColorTrendyolExpressText)
            lower.contains("kolay gelsin") -> Pair(ColorKolayGelsinBg, ColorKolayGelsinText)
            lower.contains("ptt") -> Pair(ColorPttBg, ColorPttText)
            else -> Pair(ColorDefaultCargoBg, ColorDefaultCargoText)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .animateContentSize()
            .combinedClickable(
                onClick = {},
                onLongClick = onDelete
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isArrivedSection) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (!isArrivedSection) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Marketplace badge
                    Surface(
                        color = marketplaceBg,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = cargo.marketplace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = marketplaceText
                            )
                        }
                    }

                    // Cargo company badge
                    Surface(
                        color = carrierBg,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = cargo.cargoCarrier,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = carrierText
                            )
                        }
                    }
                }

                // Product Name
                Text(
                    text = cargo.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Address
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = cargo.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dates Block
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Added Date (Kayıt Tarihi)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Kayıt: $formattedAddedDate",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Arrived Date (Teslim Tarihi)
                    if (isArrivedSection && formattedArrivedDate != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Teslim: $formattedArrivedDate",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Quick Actions Block (Check/Uncheck and Trash)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Done button
                IconButton(
                    onClick = onToggleArrived,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isArrivedSection) {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            }
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isArrivedSection) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("check_cargo_${cargo.id}")
                ) {
                    Icon(
                        imageVector = if (isArrivedSection) Icons.Default.Undo else Icons.Default.Check,
                        contentDescription = if (isArrivedSection) "Geri al" else "Teslim alındı olarak işaretle",
                        tint = if (isArrivedSection) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Kargoyu sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddCargoDialog(
    onDismiss: () -> Unit,
    marketplaces: List<MarketplacePreset>,
    addresses: List<AddressPreset>,
    carriers: List<CarrierPreset>,
    onAddMarketplace: (String) -> Unit,
    onDeleteMarketplace: (MarketplacePreset) -> Unit,
    onAddAddress: (String) -> Unit,
    onDeleteAddress: (AddressPreset) -> Unit,
    onAddCarrier: (String) -> Unit,
    onDeleteCarrier: (CarrierPreset) -> Unit,
    onSave: (productName: String, address: String, cargoCarrier: String, marketplace: String) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var isManageMode by remember { mutableStateOf(false) }

    // Preset marketplaces
    val presetMarketplaces = marketplaces.map { it.name } + "Diğer"
    var selectedMarketplace by remember { mutableStateOf("") }
    var customMarketplace by remember { mutableStateOf("") }
    var newMarketplaceText by remember { mutableStateOf("") }

    // Preset addresses
    val presetAddresses = addresses.map { it.name } + "Diğer"
    var selectedAddress by remember { mutableStateOf("") }
    var customAddress by remember { mutableStateOf("") }
    var newAddressText by remember { mutableStateOf("") }

    // Preset cargo carriers
    val presetCarriers = carriers.map { it.name } + "Diğer"
    var selectedCarrier by remember { mutableStateOf("") }
    var customCarrier by remember { mutableStateOf("") }
    var newCarrierText by remember { mutableStateOf("") }

    // Auto-select first elements if selection is empty and list is non-empty
    LaunchedEffect(marketplaces) {
        if (selectedMarketplace.isEmpty() && marketplaces.isNotEmpty()) {
            selectedMarketplace = marketplaces.first().name
        } else if (selectedMarketplace != "Diğer" && marketplaces.none { it.name == selectedMarketplace }) {
            selectedMarketplace = marketplaces.firstOrNull()?.name ?: "Diğer"
        }
    }

    LaunchedEffect(addresses) {
        if (selectedAddress.isEmpty() && addresses.isNotEmpty()) {
            selectedAddress = addresses.first().name
        } else if (selectedAddress != "Diğer" && addresses.none { it.name == selectedAddress }) {
            selectedAddress = addresses.firstOrNull()?.name ?: "Diğer"
        }
    }

    LaunchedEffect(carriers) {
        if (selectedCarrier.isEmpty() && carriers.isNotEmpty()) {
            selectedCarrier = carriers.first().name
        } else if (selectedCarrier != "Diğer" && carriers.none { it.name == selectedCarrier }) {
            selectedCarrier = carriers.firstOrNull()?.name ?: "Diğer"
        }
    }

    val isFormValid = productName.isNotBlank() && 
            (selectedMarketplace.isNotBlank()) &&
            (selectedMarketplace != "Diğer" || customMarketplace.isNotBlank()) &&
            (selectedAddress.isNotBlank()) &&
            (selectedAddress != "Diğer" || customAddress.isNotBlank()) &&
            (selectedCarrier.isNotBlank()) &&
            (selectedCarrier != "Diğer" || customCarrier.isNotBlank())

    val finalMarketplace = if (selectedMarketplace == "Diğer") customMarketplace else selectedMarketplace
    val finalAddress = if (selectedAddress == "Diğer") customAddress else selectedAddress
    val finalCarrier = if (selectedCarrier == "Diğer") customCarrier else selectedCarrier

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .widthIn(max = 500.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title & Edit mode toggle
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Yeni Kargo Kaydı",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Seçenek Listelerini Özelleştir",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        TextButton(
                            onClick = { isManageMode = !isManageMode },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (isManageMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isManageMode) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text(
                                text = if (isManageMode) "Bitir" else "Düzenle",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Scrollable container for setup form fields
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Product Name field
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Aldığım Ürün") },
                        placeholder = { Text("Örn. Keten Gömlek, Mouse vb.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_product_name"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    // Marketplace Section
                    Text(
                        text = "Alınan Pazaryeri",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetMarketplaces.forEach { mkt ->
                            val isSelected = selectedMarketplace == mkt
                            val isPreset = mkt != "Diğer"
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isManageMode) {
                                        selectedMarketplace = mkt
                                    }
                                },
                                label = { Text(mkt) },
                                trailingIcon = if (isManageMode && isPreset) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Sil",
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    marketplaces.find { it.name == mkt }?.let { onDeleteMarketplace(it) }
                                                }
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    if (isManageMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newMarketplaceText,
                                onValueChange = { newMarketplaceText = it },
                                label = { Text("Yeni Pazaryeri Ekle") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (newMarketplaceText.isNotBlank()) {
                                        onAddMarketplace(newMarketplaceText)
                                        newMarketplaceText = ""
                                    }
                                },
                                enabled = newMarketplaceText.isNotBlank(),
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (newMarketplaceText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ekle",
                                    tint = if (newMarketplaceText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (selectedMarketplace == "Diğer" && !isManageMode) {
                        OutlinedTextField(
                            value = customMarketplace,
                            onValueChange = { customMarketplace = it },
                            label = { Text("Pazaryeri Adı Girin") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_custom_marketplace"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    // Address Section
                    Text(
                        text = "Teslimat Adresi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetAddresses.forEach { addr ->
                            val isSelected = selectedAddress == addr
                            val isPreset = addr != "Diğer"
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isManageMode) {
                                        selectedAddress = addr
                                    }
                                },
                                label = { Text(addr) },
                                trailingIcon = if (isManageMode && isPreset) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Sil",
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    addresses.find { it.name == addr }?.let { onDeleteAddress(it) }
                                                }
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    if (isManageMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newAddressText,
                                onValueChange = { newAddressText = it },
                                label = { Text("Yeni Adres Ekle") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (newAddressText.isNotBlank()) {
                                        onAddAddress(newAddressText)
                                        newAddressText = ""
                                    }
                                },
                                enabled = newAddressText.isNotBlank(),
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (newAddressText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ekle",
                                    tint = if (newAddressText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (selectedAddress == "Diğer" && !isManageMode) {
                        OutlinedTextField(
                            value = customAddress,
                            onValueChange = { customAddress = it },
                            label = { Text("Adres Bilgisi Girin") },
                            placeholder = { Text("Örn. Ev (Kadıköy)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_custom_address"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    // Cargo Carrier Section
                    Text(
                        text = "Kargo Şirketi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetCarriers.forEach { crr ->
                            val isSelected = selectedCarrier == crr
                            val isPreset = crr != "Diğer"
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isManageMode) {
                                        selectedCarrier = crr
                                    }
                                },
                                label = { Text(crr) },
                                trailingIcon = if (isManageMode && isPreset) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Sil",
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    carriers.find { it.name == crr }?.let { onDeleteCarrier(it) }
                                                }
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    if (isManageMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCarrierText,
                                onValueChange = { newCarrierText = it },
                                label = { Text("Yeni Kargo Şirketi Ekle") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (newCarrierText.isNotBlank()) {
                                        onAddCarrier(newCarrierText)
                                        newCarrierText = ""
                                    }
                                },
                                enabled = newCarrierText.isNotBlank(),
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (newCarrierText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ekle",
                                    tint = if (newCarrierText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (selectedCarrier == "Diğer" && !isManageMode) {
                        OutlinedTextField(
                            value = customCarrier,
                            onValueChange = { customCarrier = it },
                            label = { Text("Kargo Şirketi Adı Girin") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_custom_carrier"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = {
                            if (isFormValid) {
                                onSave(productName, finalAddress, finalCarrier, finalMarketplace)
                            }
                        },
                        enabled = isFormValid && !isManageMode,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_cargo_button")
                    ) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
}

// Custom rememberScrollState for the dialog
@Composable
fun rememberScrollState(): androidx.compose.foundation.ScrollState {
    return androidx.compose.foundation.rememberScrollState()
}
