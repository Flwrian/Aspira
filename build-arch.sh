#!/usr/bin/env bash
set -euo pipefail

# === CONFIG ===
ENGINES_DIR="${ENGINES_DIR:-./engines}"
JAR_PATH="./target/chess-engine.jar"

# === Choix du moteur ===
read -rp "Do you want to build dev or main version? (dev/main): " versionType
case "${versionType,,}" in
  dev|main) engine="${versionType,,}" ;;
  *) echo "Invalid input. Please enter 'dev' or 'main'." >&2; exit 1 ;;
esac

# === Pré-checks ===
command -v mvn >/dev/null 2>&1 || { echo "mvn not found" >&2; exit 1; }
command -v python3 >/dev/null 2>&1 || { echo "python3 not found" >&2; exit 1; }
[ -f "sign.py" ] || { echo "sign.py missing" >&2; exit 1; }

# === Build JAR ===
echo "Building the project (JAR only)..."
mvn clean compile assembly:single

[ -f "$JAR_PATH" ] || { echo "Jar not found: $JAR_PATH" >&2; exit 1; }

# === Versioning ===
versionFile="${engine}_version.txt"
touch "$versionFile"
lastVersion="$(tr -cd '0-9' < "$versionFile" || true)"
lastVersion="${lastVersion:-0}"
newVersion=$(( lastVersion + 1 ))
printf '%s\n' "$newVersion" > "$versionFile"
echo "Version updated to $newVersion"

# === Renommage & déplacement ===
mkdir -p "$ENGINES_DIR"
jar_output_file="Aspira_${engine}_${newVersion}.jar"
mv "$JAR_PATH" "${ENGINES_DIR}/${jar_output_file}"

# === Signature (JAR) ===
echo "Signing JAR..."

sign_file() {
  local file="$1"
  local sig="${file}.sig"
  openssl dgst -sha256 -sign ./private.pem \
    -sigopt rsa_padding_mode:pss \
    -sigopt rsa_pss_saltlen:-1 \
    -sigopt rsa_mgf1_md:sha256 \
    -out "$sig" "$file"
}

# Usage:
sign_file "${ENGINES_DIR}/${jar_output_file}"


echo "JAR signed: ${jar_output_file}.sig"



echo "Uploading JAR + signature..."
curl -sS -X POST "http://192.168.0.100:8000/upload_engine" \
  -H "X-API-Key: lol" \
  -F "file=@${ENGINES_DIR}/${jar_output_file}" \
  -F "signature=@${ENGINES_DIR}/${jar_output_file}.sig" \
  -F "version=${newVersion}" \
  -F "engine_type=${engine}" \
  -F "key=Aspira.pem"

echo "Done. Uploaded: ${jar_output_file}"
