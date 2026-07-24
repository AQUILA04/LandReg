# Fingerprint embedding ONNX model

Place the trained `fingerprint_embedding.onnx` model in this directory for production ONNX inference.

When the model is absent, the AFIS worker falls back to a deterministic normalized embedding derived from image bytes (development and test only).
