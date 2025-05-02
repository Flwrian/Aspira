from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding
import sys

# Usage : python sign.py Aspira_dev_4.exe

exe_path = sys.argv[1]
with open(exe_path, "rb") as f:
    data = f.read()

with open("private.pem", "rb") as key_file:
    private_key = serialization.load_pem_private_key(key_file.read(), password=None)

signature = private_key.sign(
    data,
    padding.PSS(
        mgf=padding.MGF1(hashes.SHA256()),
        salt_length=padding.PSS.MAX_LENGTH
    ),
    hashes.SHA256()
)

# Écrit la signature dans un fichier .sig
with open(exe_path + ".sig", "wb") as sig_file:
    sig_file.write(signature)

print(f"✅ Signature créée : {exe_path}.sig")
