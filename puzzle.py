import csv
import argparse
import chess
import chess.engine

def run_puzzles(csv_file, engine_path, num_puzzles=100, movetime=1000, out_file="failed.csv"):
    engine = chess.engine.SimpleEngine.popen_uci(engine_path)

    total = 0
    solved = 0
    failed_rows = []

    with open(csv_file, newline='', encoding="utf-8") as f:
        reader = csv.reader(f)
        header = next(reader, None)  # skip header

        for row in reader:
            if total >= num_puzzles:
                break

            puzzle_id, fen, moves_str = row[0], row[1], row[2]
            moves = moves_str.split()

            # Charger la position de base
            board = chess.Board(fen)

            # Toujours appliquer le premier coup (coup adverse)
            first_move = chess.Move.from_uci(moves[0])
            if first_move not in board.legal_moves:
                print(f"[{puzzle_id}] premier coup {moves[0]} illégal pour FEN {fen}")
                continue
            board.push(first_move)

            # Maintenant, les coups attendus commencent à partir de moves[1]
            expected_line = moves[1:]
            ok = True
            fail_info = None

            # On prend les coups du joueur uniquement (positions impaires après le premier push)
            for i, expected in enumerate(expected_line[::2]):
                result = engine.play(board, chess.engine.Limit(time=movetime/1000))
                bestmove = result.move.uci()

                if bestmove != expected:
                    ok = False
                    fail_info = (puzzle_id, board.fen(), expected, bestmove)
                    break

                board.push(result.move)  # jouer coup moteur

                # pousser le coup adverse suivant si dispo
                idx = (i * 2) + 2  # moves index du coup adverse
                if idx < len(moves):
                    board.push(chess.Move.from_uci(moves[idx]))

            total += 1
            if ok:
                solved += 1
            else:
                failed_rows.append(fail_info)

    engine.quit()

    print(f"\nRésultats:")
    print(f"{solved}/{total} puzzles résolus ({100*solved/total:.2f}%)")

    if failed_rows:
        print(f"Puzzles ratés: {len(failed_rows)}")
        with open(out_file, "w", newline="", encoding="utf-8") as f:
            writer = csv.writer(f)
            writer.writerow(["PuzzleId", "FEN_before", "ExpectedMove", "EngineMove"])
            writer.writerows(failed_rows)
        print(f"→ Sauvegardés dans {out_file}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--csv", required=True, help="fichier puzzles lichess CSV")
    parser.add_argument("--engine", required=True, help="chemin vers moteur UCI (ex: ./Aspira)")
    parser.add_argument("--n", type=int, default=100, help="nombre de puzzles à tester")
    parser.add_argument("--time", type=int, default=1000, help="temps par coup en ms")
    parser.add_argument("--out", default="failed.csv", help="fichier de sortie des puzzles ratés")
    args = parser.parse_args()

    run_puzzles(args.csv, args.engine, args.n, args.time, args.out)
