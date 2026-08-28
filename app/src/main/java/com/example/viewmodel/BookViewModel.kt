package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookPage
import com.example.data.BookRepository
import com.example.ui.components.ArabicFontOption
import com.example.ui.theme.BookThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class ReaderUiState(
    val currentPageNumber: Int = 1,
    val isPagedMode: Boolean = true,
    val themeMode: BookThemeMode = BookThemeMode.MIDNIGHT_GOLD,
    val fontSize: Float = 18f,
    val fontOption: ArabicFontOption = ArabicFontOption.SERIF,
    val bookmarks: Set<Int> = emptySet(),
    val isAudioBarVisible: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val speechRate: Float = 1.0f,
    val isTtsReady: Boolean = false,
    val searchQuery: String = "",
    val showTOCSheet: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showQuotesDialog: Boolean = false,
    val showAuthorDialog: Boolean = false
)

class BookViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val prefs = application.getSharedPreferences("book_reader_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        // Load saved state
        val savedPage = prefs.getInt("last_page", 1).coerceIn(1, 16)
        val savedFontSize = prefs.getFloat("font_size", 18f)
        val savedThemeIndex = prefs.getInt("theme_mode", 0)
        val savedBookmarks = prefs.getStringSet("bookmarks", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val savedPagedMode = prefs.getBoolean("is_paged_mode", true)

        val theme = BookThemeMode.values().getOrElse(savedThemeIndex) { BookThemeMode.MIDNIGHT_GOLD }

        _uiState.update {
            it.copy(
                currentPageNumber = savedPage,
                fontSize = savedFontSize,
                themeMode = theme,
                bookmarks = savedBookmarks,
                isPagedMode = savedPagedMode
            )
        }

        // Initialize TTS
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(_uiState.value.speechRate)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _uiState.update { it.copy(isAudioPlaying = true) }
                }

                override fun onDone(utteranceId: String?) {
                    _uiState.update { it.copy(isAudioPlaying = false) }
                }

                override fun onError(utteranceId: String?) {
                    _uiState.update { it.copy(isAudioPlaying = false) }
                }
            })
            _uiState.update { it.copy(isTtsReady = true) }
        }
    }

    fun goToPage(page: Int) {
        val safePage = page.coerceIn(1, 16)
        _uiState.update { it.copy(currentPageNumber = safePage) }
        prefs.edit().putInt("last_page", safePage).apply()

        if (_uiState.value.isAudioPlaying) {
            speakCurrentPage(safePage)
        }
    }

    fun nextPage() {
        if (_uiState.value.currentPageNumber < 16) {
            goToPage(_uiState.value.currentPageNumber + 1)
        }
    }

    fun prevPage() {
        if (_uiState.value.currentPageNumber > 1) {
            goToPage(_uiState.value.currentPageNumber - 1)
        }
    }

    fun toggleBookmark(pageNumber: Int) {
        val currentBookmarks = _uiState.value.bookmarks.toMutableSet()
        if (currentBookmarks.contains(pageNumber)) {
            currentBookmarks.remove(pageNumber)
        } else {
            currentBookmarks.add(pageNumber)
        }
        _uiState.update { it.copy(bookmarks = currentBookmarks) }
        prefs.edit().putStringSet("bookmarks", currentBookmarks.map { it.toString() }.toSet()).apply()
    }

    fun setThemeMode(mode: BookThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
        prefs.edit().putInt("theme_mode", mode.ordinal).apply()
    }

    fun setFontSize(size: Float) {
        _uiState.update { it.copy(fontSize = size) }
        prefs.edit().putFloat("font_size", size).apply()
    }

    fun setFontOption(option: ArabicFontOption) {
        _uiState.update { it.copy(fontOption = option) }
    }

    fun setPagedMode(isPaged: Boolean) {
        _uiState.update { it.copy(isPagedMode = isPaged) }
        prefs.edit().putBoolean("is_paged_mode", isPaged).apply()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // Audio & TTS
    fun toggleAudioBar() {
        val nextState = !_uiState.value.isAudioBarVisible
        _uiState.update { it.copy(isAudioBarVisible = nextState) }
        if (!nextState && _uiState.value.isAudioPlaying) {
            stopAudio()
        }
    }

    fun toggleAudioPlayback() {
        if (_uiState.value.isAudioPlaying) {
            stopAudio()
        } else {
            speakCurrentPage(_uiState.value.currentPageNumber)
        }
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
        tts?.setSpeechRate(rate)
        if (_uiState.value.isAudioPlaying) {
            speakCurrentPage(_uiState.value.currentPageNumber)
        }
    }

    private fun speakCurrentPage(pageNumber: Int) {
        val page = BookRepository.pages.find { it.pageNumber == pageNumber } ?: return
        val textToRead = buildString {
            append(page.title)
            append(". ")
            if (page.subtitle.isNotBlank()) {
                append(page.subtitle)
                append(". ")
            }
            page.paragraphs.forEach {
                append(it)
                append(". ")
            }
            page.keyVersesOrHadith.forEach {
                append(it)
                append(". ")
            }
        }

        tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "page_$pageNumber")
        _uiState.update { it.copy(isAudioPlaying = true, isAudioBarVisible = true) }
    }

    fun stopAudio() {
        tts?.stop()
        _uiState.update { it.copy(isAudioPlaying = false) }
    }

    // Dialog toggles
    fun setShowTOCSheet(show: Boolean) = _uiState.update { it.copy(showTOCSheet = show) }
    fun setShowSettingsDialog(show: Boolean) = _uiState.update { it.copy(showSettingsDialog = show) }
    fun setShowQuotesDialog(show: Boolean) = _uiState.update { it.copy(showQuotesDialog = show) }
    fun setShowAuthorDialog(show: Boolean) = _uiState.update { it.copy(showAuthorDialog = show) }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
