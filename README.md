# 🚀 Edge AI Assistant

An offline, hybrid AI-powered assistant built for low-latency task automation on Android devices.  
Combines deterministic rule-based parsing with on-device machine learning for robust intent understanding.

---

## 🧠 Core Idea

> Deterministic where precision matters.  
> Probabilistic where language gets messy.  
> Everything runs **on-device**.

---
---

## 🔑 Key Features

- 🧮 **Calculator Engine**
  - Natural language parsing → expression evaluation
  - Supports +, −, ×, ÷ with structured + free-form input

- ⏰ **Alarm System**
  - Built using `AlarmManager` + `BroadcastReceiver`
  - Exact alarms with system-level reliability

- 📱 **App Launcher**
  - Uses `PackageManager`
  - Fuzzy matching + disambiguation handling

- 📝 **Notes System**
  - Local persistence using **RoomDB**
  - Instant save + retrieval

- 🔄 **Unit Converter**
  - Rule-based parsing for common unit transformations

- 🎤 **Voice Input**
  - Android `SpeechRecognizer` (on-device mode)
  - Partial + final transcription handling

---

## 🤖 Machine Learning Layer

- **Framework:** TensorFlow Lite (TFLite)
- **Model Type:** Lightweight intent classifier
- **Inference:** On-device (no network calls)
- **Optimization:** Quantized (low latency)

### Role of ML:
- Activated only when rule confidence is low
- Handles ambiguous or unstructured inputs

---

## 🧠 Context Engine

- Maintains short-term conversational memory
- Enables multi-step flows

---

## 📊 Analytics Engine

Tracks:
- Total commands executed
- Intent distribution
- Execution latency (ms)
- Success / failure rate

Stored locally using RoomDB.

---

## 🎯 Personalization Engine

- Tracks frequently used intents
- Generates smart suggestions based on:
  - Recency
  - Frequency

---

## 🎨 UI/UX

- Built with **Jetpack Compose**
- Conversational chat interface
- Features:
  - Message bubbles (user vs AI)
  - Typing animation
  - Smooth auto-scroll
  - Voice interaction UI

---

## 🧱 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM + Clean modular layers  
- **Database:** RoomDB  
- **ML:** TensorFlow Lite  
- **Concurrency:** Kotlin Coroutines  
- **Dependency Injection:** Hilt  
- **System APIs:**
  - AlarmManager
  - PackageManager
  - SpeechRecognizer

---

## ⚡ Performance Highlights

- Fully **offline-first**
- Near **instant rule execution (~0–50ms)**
- ML inference optimized for real-time use
- No external API dependency

---

## 🧩 What Makes This Stand Out

- Hybrid AI (Rules + ML)
- Context-aware execution
- Fully offline system
- Modular and extensible architecture
- Real-world Android system integrations

---

## 🚀 Future Improvements (V2)

- Advanced calculator (trigonometry, power functions)
- Alarm UI (snooze, dismiss gestures)
- Notes management UI (edit, delete, tagging)
- Enhanced NLP + entity extraction
- Expanded unit conversion system

---

## 📌 Summary

Edge AI Assistant is not just a feature-based app—it is a **local AI execution engine** designed for reliability, speed, and real-world usability on mobile devices.
