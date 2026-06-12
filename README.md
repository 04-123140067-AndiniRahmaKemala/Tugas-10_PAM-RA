# 📝 Tugas Individu 10 - My Notes App (KMP + Groq AI)

Aplikasi manajemen catatan cerdas berbasis **Kotlin Multiplatform (KMP)** yang menggabungkan fitur native platform dengan kekuatan **Artificial Intelligence**.

---

## 🌟 Fitur Utama

### 1. Manajemen Catatan (CRUD & Persistence)
- **SQLDelight Database**: Penyimpanan catatan secara lokal (offline-first) yang aman dan cepat.
- **Search & Filter**: Pencarian catatan secara *real-time* dengan fitur `debounce` untuk menghemat sumber daya.
- **Favorites**: Menandai catatan penting untuk akses cepat di tab terpisah.
- **Categorization**: Pengelompokan catatan berdasarkan kategori.

### 2. Fitur Native Platform (Expect/Actual)
- **Device Info**: Menampilkan informasi hardware secara detail (Model, Manufaktur, Versi OS, dan Platform).
- **Network Monitor**: Deteksi status koneksi internet secara *real-time* dengan banner peringatan yang muncul otomatis jika koneksi terputus.

### 3. Asisten AI Cerdas (Groq Cloud)
Aplikasi ini dilengkapi dengan **AI Summarizer & Translator** yang sangat cepat:
- **Summarization**: Meringkas catatan panjang menjadi poin-poin inti dalam hitungan detik.
- **Translation**: Menerjemahkan teks antar bahasa secara akurat.
- **Chat Assistant**: Konsultasi atau tanya jawab langsung dengan AI mengenai isi catatan Anda.

### 4. UI/UX Modern
- **Material Design 3**: Antarmuka modern dengan komponen terbaru dari Google.
- **Dark & Light Mode**: Dukungan tema gelap/terang yang dapat disesuaikan di menu pengaturan.
- **Responsive Layout**: Tampilan yang menyesuaikan untuk Android dan platform lainnya.

---

## 🔑 Informasi API & Teknologi

### Groq Cloud API
Aplikasi ini menggunakan **Groq Cloud API** sebagai mesin AI utama karena kecepatannya yang superior dibandingkan penyedia lain.
- **Model**: `llama-3.3-70b-versatile` (Model Llama terbaru dan tercepat).
- **API Key**: `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`
- **Endpoint**: `https://api.groq.com/openai/v1/chat/completions`

### Library yang Digunakan
- **Koin**: Untuk Dependency Injection (DI) yang modular.
- **Ktor**: Untuk komunikasi data ke server AI.
- **SQLDelight**: Untuk manajemen database lokal.
- **Compose Multiplatform**: Untuk pembuatan UI lintas platform.
- **DataStore**: Untuk penyimpanan pengaturan aplikasi (Theme settings).

---

## Test Results

<div align="center">
  <<img width="1760" height="990" alt="Screenshot (115)" src="https://github.com/user-attachments/assets/35f35414-56ca-4be8-8a04-d0404d80fb1d" />
 />
  <p><i>A modern beauty e-commerce application built with Compose Multiplatform.</i></p>
</div>

## ✅ Rubrik Penilaian (Compliance)

1. **Koin DI (25%)**: Implementasi penuh di `AndroidApp.kt` dan `di/Koin.kt`.
2. **Platform Features (25%)**: Penggunaan `expect/actual` pada `DeviceInfo` dan `NetworkMonitor`.
3. **AI Integration (30%)**: Integrasi Llama 3.3 melalui Groq API dengan fitur Ringkasan & Terjemahan.
4. **UX & Design**: Navigasi yang lancar dengan Bottom Navigation dan tema Dark Mode.

---

**Developed by:** Andinirhm 🌸
**NIM/Course:** Pengembangan Aplikasi Mobile (PAM)
