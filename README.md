# 🧠 Aspira – Java Chess Engine

**Aspira** is a chess engine built from the ground up in **Java** — born out of curiosity, a love of chess, and a deep drive to understand how things work at a low level.

This project isn’t just about creating a strong engine. It’s about learning by doing: building everything myself — from board representation and bitboards to move generation, legality checking, and search logic. No shortcuts, no libraries doing the hard work.

---

## 🚧 Project Philosophy

Aspira started as a side project back when I was learning Java — just a basic engine to play some legal moves. But over time, it turned into a serious dive into engine architecture, bitboard logic, and performance optimization. I’ve since rewritten large parts of the codebase to make it faster, cleaner, and more scalable.

The engine is now in its **second major iteration (V2)**, fully rebuilt with performance and correctness in mind.

---

## ✅ Features Implemented

- ✅ Full rule implementation: castling, en passant, promotion, legality checking, repetition
- ⚙️ Core engine loop  
- ♟️ Bitboard-based move generation (including magic bitboards)  
- 🔄 UCI protocol support  
- 🧠 Pluggable search algorithms (minimax, WIP alpha-beta)  
- 📜 FEN/PGN parsing and saving  
- 🧪 Perft testing suite (passes full Ethereal test suite as of 20/03/2025)  
- 🔍 Zobrist hashing for fast position tracking  
- 🔢 Packed move representation (64-bit long values to reduce GC pressure)  
- ⏱️ Time management inside the search  

---

## 🧭 Roadmap & Upcoming Work

- 📈 **Evaluation function**  
  Material balance, king safety, piece activity, pawn structure, etc.

- 🔍 **Advanced search features**  
  Alpha-beta pruning, transposition tables, killer moves, etc.

- 🎯 **Legal-only move generation**  
  Full legality-based movegen

- 🧠 **NNUE / ML experiments** *(future phase)*  
  Possibly integrating simple NN models for position eval.

---

## ⚡ Performance

As of March 2025, Aspira’s move generation peaks at around **15 million nodes per second (MNPS)** on a **Ryzen 7 7800X3D**, with all rule enforcement and legality checks enabled. Optimization is ongoing.

---

## 🛠 Dev Log

- **09/07/2024 – V2 rebuild started**  
  Total rewrite focused on correctness, speed, and proper rule handling.

- **20/03/2025 – Passed Ethereal Perft suite**  
  Major milestone — all chess rules implemented and verified.

- **23/03/2025 – Packed move format**  
  Switched from Java objects to packed `long` for moves. ~20% speedup.

- **24/03/2025 – Magic bitboards**  
  Sliding pieces now use magic bitboards. ~70% boost in movegen.

- **27/03/2025 – Search time control**  
  Engine can now manage its own clock during games.

---

## 🤝 Contributing

**Aspira is open to contributions.** Whether it’s code, ideas, testing, or feedback — all help is welcome. If you're interested in contributing:

1. Fork the repo  
2. Pick an open issue (or open your own)  
3. Open a pull request

Feel free to reach out if you want to discuss architecture, optimization, or chess engine theory.

---

## 💬 Special Thanks

Huge thanks to the **Stockfish Discord community** for the resources, discussions, and general inspiration. Also shout-out to the creators of open-source tools and test suites that help keep engines like this honest.

---

## 🚀 Why Aspira?

The name *Aspira* comes from the idea of “aspiring” — to improve, to dig deeper, to push further into the mechanics of something complex. This engine is my way of doing that with both chess and code.
