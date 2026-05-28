# Edge AI Assistant — Intent Classification Model Card

## Model Overview

| Property | Value |
|----------|-------|
| Model type | TextCNN (Text Convolutional Neural Network) |
| Framework | TensorFlow Lite |
| File | `app/src/main/assets/intent_model.tflite` |
| Vocabulary | `app/src/main/assets/tokenizer.json` |
| Task | Multi-class intent classification |
| Input | Raw text string (max 10 tokens) |
| Output | Probability distribution over 7 intent classes |
| Model size | ~45 KB (post quantization) |
| Quantization | Full integer (INT8 weights, FLOAT32 I/O) |

---

## Intent Classes

| Index | Class | Example Inputs |
|-------|-------|----------------|
| 0 | `CALCULATE` | "what is 5 plus 3", "calculate 20 divided by 4" |
| 1 | `CONVERT_UNITS` | "convert 5 km to miles", "100 celsius in fahrenheit" |
| 2 | `GENERAL` | "hello", "how are you", "what can you do" |
| 3 | `GET_NOTES` | "show my notes", "what did i write", "list notes" |
| 4 | `OPEN_APP` | "open youtube", "launch spotify", "start camera" |
| 5 | `SET_ALARM` | "set alarm for 7am", "wake me at 6:30" |
| 6 | `TAKE_NOTE` | "note buy milk", "save call dentist tomorrow" |

---

## Architecture
Input text
↓
Tokenizer (vocab size: 500, max length: 10, OOV token: <OOV>)
↓
Embedding layer (dim: 32)
↓
Parallel Conv1D filters (kernel sizes: 3, 4)
↓
GlobalMaxPooling1D (×2)
↓
Concatenate → Dropout (0.4)
↓
Dense (64, ReLU)
↓
Dense (7, Softmax)
↓
Intent probabilities

---

## Training Data

| Class | Samples |
|-------|---------|
| SET_ALARM | 40 |
| CALCULATE | 40 |
| CONVERT_UNITS | 40 |
| TAKE_NOTE | 40 |
| OPEN_APP | 40 |
| GET_NOTES | 40 |
| GENERAL | 40 |
| **Total** | **280** |

Data includes natural phrasing variations, edge cases, and common typos.
Training script: `ml_training/train_model.py`
Dataset: `ml_training/intent_dataset.csv`

---

## Performance

| Metric | Value |
|--------|-------|
| Validation accuracy | >92% (target) |
| Inference latency (Pixel 6) | ~8ms |
| Inference latency (mid-range) | ~15ms |
| Model size on disk | ~45 KB |
| RAM usage during inference | ~2 MB |

> Note: Accuracy measured on held-out 20% split using stratified sampling.

---

## Confidence Thresholds

| Confidence | Action |
|------------|--------|
| ≥ 0.65 | Use ML prediction |
| 0.40 – 0.65 | Ask user to clarify |
| < 0.40 | Fall back to GENERAL |

Low-confidence inputs are also re-checked by `RuleEngine` before final fallback.

---

## Limitations

- Vocabulary capped at 500 tokens — rare or technical words map to `<OOV>`
- Max input length 10 tokens — longer inputs are truncated
- Trained only on English — no multilingual support in v1.0
- Small dataset (280 samples) — may misclassify novel phrasing
- No slot extraction — model only classifies intent, not entities

---

## Retraining

To retrain the model with new data:

1. Add examples to `ml_training/generate_dataset.py`
2. Run dataset generator: `python generate_dataset.py`
3. Upload CSV to Google Colab
4. Run `ml_training/train_model.py`
5. Download `intent_model.tflite` and `tokenizer.json`
6. Replace files in `app/src/main/assets/`
7. Verify `IntentClassifier.kt` output size matches class count
8. Update this model card with new accuracy figures

---

## Privacy

- All inference runs **on-device** using TensorFlow Lite
- No user input is ever sent to a server for classification
- No training data is collected from users
- Model weights are static and ship with the APK