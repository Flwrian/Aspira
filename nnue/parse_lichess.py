import json
import chess
import struct
from tqdm import tqdm

INPUT = "lichess_db_eval.jsonl"
OUTPUT = "data.bin"
MAX_POS = 500_000

def feature_indices(board):
    feats = []
    for sq in range(64):
        p = board.piece_at(sq)
        if p:
            # piece × square (IDENTIQUE à Java)
            idx = (p.piece_type - 1 + (0 if p.color else 6)) * 64 + sq
            feats.append(idx)
    return feats

count = 0

with open(INPUT, "r") as fin, open(OUTPUT, "wb") as out:
    for line in tqdm(fin):
        if count >= MAX_POS:
            break

        try:
            data = json.loads(line)
        except:
            continue

        # prendre l'éval la plus profonde
        best = max(data["evals"], key=lambda e: e["depth"])
        pv = best["pvs"][0]

        if "cp" not in pv:
            continue  # ignore mates

        cp = pv["cp"]
        cp = max(-1000, min(1000, cp))

        board = chess.Board(data["fen"])
        feats = feature_indices(board)

        # format binaire :
        # [nb_features:1B][features:2B*][cp:2B]
        out.write(struct.pack("B", len(feats)))
        for f in feats:
            out.write(struct.pack("H", f))
        out.write(struct.pack("h", cp))

        count += 1

print("Saved", count, "positions")
