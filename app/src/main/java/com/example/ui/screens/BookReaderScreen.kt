package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BookPage
import com.example.data.BookRepository
import com.example.ui.components.*
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.viewmodel.BookViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    viewModel: BookViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val currentPage = remember(uiState.currentPageNumber) {
        BookRepository.pages.find { it.pageNumber == uiState.currentPageNumber }
            ?: BookRepository.pages.first()
    }

    val isBookmarked = uiState.bookmarks.contains(uiState.currentPageNumber)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = BookRepository.BOOK_TITLE,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = if (uiState.isPagedMode) "صفحة ${uiState.currentPageNumber} من ١٦ • ${currentPage.chapterTitle}" else "قراءة متصلة كاملة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setShowTOCSheet(true) },
                        modifier = Modifier.testTag("toc_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "الفهرس والمحتويات",
                            tint = GoldPrimary
                        )
                    }
                },
                actions = {
                    // Audio TTS Reader Toggle
                    IconButton(
                        onClick = { viewModel.toggleAudioBar() },
                        modifier = Modifier.testTag("audio_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isAudioPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                            contentDescription = "القارئ الصوتي",
                            tint = if (uiState.isAudioPlaying || uiState.isAudioBarVisible) GoldLight else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Quotes & Pearls
                    IconButton(
                        onClick = { viewModel.setShowQuotesDialog(true) },
                        modifier = Modifier.testTag("quotes_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "درر واقتباسات",
                            tint = GoldPrimary
                        )
                    }

                    // Reading Style & Font Settings
                    IconButton(
                        onClick = { viewModel.setShowSettingsDialog(true) },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "إعدادات الخط والمظهر",
                            tint = GoldPrimary
                        )
                    }

                    // Bookmark toggle
                    IconButton(
                        onClick = { viewModel.toggleBookmark(uiState.currentPageNumber) },
                        modifier = Modifier.testTag("bookmark_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ الصفحة",
                            tint = if (isBookmarked) GoldPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    // Author info
                    IconButton(
                        onClick = { viewModel.setShowAuthorDialog(true) },
                        modifier = Modifier.testTag("author_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "عن المؤلف والكتاب",
                            tint = GoldPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Animated Audio Player Bar
                AnimatedVisibility(
                    visible = uiState.isAudioBarVisible,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    AudioPlayerBar(
                        isPlaying = uiState.isAudioPlaying,
                        isSpeakingAvailable = uiState.isTtsReady,
                        currentPage = uiState.currentPageNumber,
                        totalPages = 16,
                        speechRate = uiState.speechRate,
                        onTogglePlay = { viewModel.toggleAudioPlayback() },
                        onNextPage = { viewModel.nextPage() },
                        onPrevPage = { viewModel.prevPage() },
                        onChangeSpeed = { viewModel.setSpeechRate(it) },
                        onClose = { viewModel.toggleAudioBar() }
                    )
                }

                // Bottom Page Switcher / Actions
                if (uiState.isPagedMode) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous Page Button
                            OutlinedButton(
                                onClick = { viewModel.prevPage() },
                                enabled = uiState.currentPageNumber > 1,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = GoldLight
                                ),
                                border = BorderStroke(1.dp, if (uiState.currentPageNumber > 1) GoldPrimary.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.2f)),
                                modifier = Modifier.testTag("bottom_prev_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIos,
                                    contentDescription = "السابق",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("السابق", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            // Center Page Indicator & Share snippet
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "صفحة ${uiState.currentPageNumber} من ١٦",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { sharePageContent(context, currentPage) },
                                    modifier = Modifier.size(34.dp).testTag("share_page_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "مشاركة الصفحة",
                                        tint = GoldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Next Page Button
                            Button(
                                onClick = { viewModel.nextPage() },
                                enabled = uiState.currentPageNumber < 16,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Color.Black,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("bottom_next_button")
                            ) {
                                Text("التالي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos,
                                    contentDescription = "التالي",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isPagedMode) {
                // Single Page Mode with smooth transitions
                Crossfade(
                    targetState = currentPage,
                    label = "page_transition",
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    SinglePageView(
                        page = page,
                        fontSize = uiState.fontSize,
                        fontFamily = uiState.fontOption.fontFamily,
                        onGoToPage = { targetPage -> viewModel.goToPage(targetPage) },
                        onShare = { sharePageContent(context, page) }
                    )
                }
            } else {
                // Continuous Scroll Mode
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(BookRepository.pages, key = { it.pageNumber }) { page ->
                        IslamicPageBorder(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                IslamicPageHeader(
                                    chapterTitle = page.chapterTitle,
                                    pageNumber = page.pageNumber
                                )

                                PageBodyContent(
                                    page = page,
                                    fontSize = uiState.fontSize,
                                    fontFamily = uiState.fontOption.fontFamily,
                                    onGoToPage = { viewModel.goToPage(it) },
                                    onShare = { sharePageContent(context, page) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheets and Dialogs
    if (uiState.showTOCSheet) {
        TableOfContentsSheet(
            currentPage = uiState.currentPageNumber,
            bookmarks = uiState.bookmarks,
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onSelectPage = { viewModel.goToPage(it) },
            onToggleBookmark = { viewModel.toggleBookmark(it) },
            onDismiss = { viewModel.setShowTOCSheet(false) }
        )
    }

    if (uiState.showSettingsDialog) {
        ReadingSettingsDialog(
            currentTheme = uiState.themeMode,
            currentFontSize = uiState.fontSize,
            currentFontOption = uiState.fontOption,
            isPagedMode = uiState.isPagedMode,
            onThemeChanged = { viewModel.setThemeMode(it) },
            onFontSizeChanged = { viewModel.setFontSize(it) },
            onFontOptionChanged = { viewModel.setFontOption(it) },
            onModeChanged = { viewModel.setPagedMode(it) },
            onDismiss = { viewModel.setShowSettingsDialog(false) }
        )
    }

    if (uiState.showQuotesDialog) {
        QuoteCardDialog(
            onSelectQuotePage = { viewModel.goToPage(it) },
            onDismiss = { viewModel.setShowQuotesDialog(false) }
        )
    }

    if (uiState.showAuthorDialog) {
        AuthorInfoDialog(
            onDismiss = { viewModel.setShowAuthorDialog(false) }
        )
    }
}

@Composable
fun SinglePageView(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily,
    onGoToPage: (Int) -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()

    IslamicPageBorder(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            IslamicPageHeader(
                chapterTitle = page.chapterTitle,
                pageNumber = page.pageNumber
            )

            // Body
            PageBodyContent(
                page = page,
                fontSize = fontSize,
                fontFamily = fontFamily,
                onGoToPage = onGoToPage,
                onShare = onShare
            )
        }
    }
}

@Composable
fun PageBodyContent(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily,
    onGoToPage: (Int) -> Unit,
    onShare: () -> Unit
) {
    when (page.pageNumber) {
        1 -> {
            // Page 1: Master Cover Page
            CoverPageContent(page = page, fontSize = fontSize, fontFamily = fontFamily)
        }
        2 -> {
            // Page 2: Dedication (الإهداء)
            DedicationPageContent(page = page, fontSize = fontSize, fontFamily = fontFamily)
        }
        3 -> {
            // Page 3: Interactive Table of Contents (الفهرس)
            TOCPageContent(page = page, fontSize = fontSize, onGoToPage = onGoToPage)
        }
        16 -> {
            // Page 16: Conclusion (الخاتمة)
            ConclusionPageContent(page = page, fontSize = fontSize, fontFamily = fontFamily)
        }
        else -> {
            // Pages 4 to 15: Standard Rich Page
            StandardPageContent(page = page, fontSize = fontSize, fontFamily = fontFamily)
        }
    }
}

@Composable
fun CoverPageContent(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BasmalaBanner()

        // Hero Banner Art
        Image(
            painter = painterResource(id = R.drawable.book_cover_hero_1787923830212),
            contentDescription = "غلاف كتاب إلى المفازة العظمى",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.2.dp, GoldPrimary, RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        // Main Book Title
        Text(
            text = "كتاب",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "إلى المفازة العظمى",
            style = MaterialTheme.typography.displayMedium,
            color = GoldLight,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontSize = (fontSize + 8).sp,
            lineHeight = (fontSize + 16).sp
        )

        Surface(
            color = GoldPrimary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "إعداد وتأليف",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldLight
                )
                Text(
                    text = "الدكتور مالك عبدالرحمن الرميمة",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ArabesqueDivider()

        // Book Idea Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ),
            border = BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "« فكرة الكتاب »",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "يتحدث الكتاب عن طريق النجاة والعلو للإنسان في الدنيا ومفازته في الدارين (الدنيا والآخرة)، مستمداً ذلك من التمسك بالقرآن الكريم والسنة النبوية الشريفة.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.55).sp
                )
            }
        }

        // Footer Date and Phone
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "البداية كانت في يوم السبت ٢٠٢٢/٧/١٦م",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "هاتف المؤلف: 771134103",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight
                )
            }
        }
    }
}

@Composable
fun DedicationPageContent(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasmalaBanner()

        Text(
            text = "الإهداء",
            style = MaterialTheme.typography.displayMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        ArabesqueDivider()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "الحمد لله رب العالمين.. وبعد:",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "لأمي الرَّحيمة بولدها..\nولأبي صانع المعروف..\nأُهدي كتابي هذا؛\nلعلّ الله أن يتقبّله صدقةً جاريةً لهما.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily,
                    fontSize = (fontSize + 2).sp,
                    lineHeight = (fontSize * 1.8).sp,
                    fontWeight = FontWeight.Medium
                )

                ArabesqueDivider(color = GoldPrimary.copy(alpha = 0.5f))

                Text(
                    text = "وصلّى الله على نبينا محمد\nوعلى آله وصحبه أجمعين",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Composable
fun TOCPageContent(
    page: BookPage,
    fontSize: Float,
    onGoToPage: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "فهرس موضوعات الكتاب",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        ArabesqueDivider()

        BookRepository.chapters.forEach { chapter ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoToPage(chapter.startPage) }
                    .border(
                        0.8.dp,
                        GoldPrimary.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = chapter.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Surface(
                        color = GoldPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "صفحة ${chapter.startPage}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StandardPageContent(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Page Title & Subtitle
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            if (page.subtitle.isNotBlank()) {
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldLight.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }

        ArabesqueDivider()

        // Main Paragraphs
        page.paragraphs.forEach { paragraph ->
            if (paragraph.startsWith("«") || paragraph.startsWith("•") || paragraph.startsWith("١.") || paragraph.startsWith("٢.") || paragraph.startsWith("٣.") || paragraph.startsWith("٤.")) {
                // Bullet points or prominent list items
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.6.dp, GoldPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = fontFamily,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.55).sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        textAlign = TextAlign.Right
                    )
                }
            } else {
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = fontFamily,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.6).sp,
                    textAlign = TextAlign.Right
                )
            }
        }

        // Verses / Hadith Callouts if any
        page.keyVersesOrHadith.forEach { verse ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GoldPrimary.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Text(
                    text = verse,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (fontSize + 1).sp,
                    lineHeight = (fontSize * 1.6).sp,
                    modifier = Modifier.padding(14.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Footnote
        if (page.footnote.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = GoldPrimary.copy(alpha = 0.25f))
            Text(
                text = "💡 ${page.footnote}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                fontFamily = fontFamily,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ConclusionPageContent(
    page: BookPage,
    fontSize: Float,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "الخاتمة ومسك الختام",
            style = MaterialTheme.typography.displayMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        ArabesqueDivider()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                page.paragraphs.forEach { p ->
                    Text(
                        text = p,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = fontFamily,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.6).sp,
                        textAlign = TextAlign.Right
                    )
                }

                ArabesqueDivider(color = GoldPrimary.copy(alpha = 0.5f))

                // Colophon / Closing metadata
                Surface(
                    color = GoldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "تم بحمد الله وتوفيقه",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "في مساء يوم السبت ١٦ / ٧ / ٢٠٢٢م",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldPrimary
                        )
                        Text(
                            text = "إعداد وتأليف: الدكتور مالك عبدالرحمن الرميمة",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun sharePageContent(context: Context, page: BookPage) {
    val content = buildString {
        append("📖 كتاب: ${BookRepository.BOOK_TITLE}\n")
        append("📄 ${page.title} (صفحة ${page.pageNumber})\n")
        append("✍️ إعداد وتأليف: ${BookRepository.AUTHOR_NAME}\n\n")
        page.paragraphs.forEach {
            append("$it\n\n")
        }
        page.keyVersesOrHadith.forEach {
            append("$it\n\n")
        }
        append("──────────────\n")
        append("تطبيق كتاب إلى المفازة العظمى")
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, content)
    }
    context.startActivity(Intent.createChooser(shareIntent, "مشاركة صفحة من كتاب إلى المفازة العظمى"))
}
