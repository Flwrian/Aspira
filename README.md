![Java](https://img.shields.io/badge/Language-Java-blue.svg)
![UCI Support](https://img.shields.io/badge/Protocol-UCI-green.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

# Aspira – Java Chess Engine

Aspira is a chess engine written entirely in **Java**.  
It didn’t start as an attempt to build a strong engine, and it definitely didn’t stay simple for long.

What began as “let’s make something that plays legal moves” slowly turned into one of the most mentally demanding projects I’ve worked on. Chess engines have this special property: everything depends on everything else. One small mistake, one shortcut, one assumption that isn’t 100% correct — and suddenly nothing makes sense anymore. Wrong evaluations, illegal moves, random blunders, or performance falling off a cliff.

This project forced me to write *good* code everywhere. There’s no hiding. If one part is sloppy, the whole engine eventually explodes.

---

## About the Project

Aspira is built from the ground up. No chess libraries doing the heavy lifting, no magic abstractions that hide complexity. Every major component exists because I needed to understand it deeply enough to implement it myself.

That includes:
- board representation
- bitboards
- move generation
- legality checking
- search
- time management
- hashing
- repetition detection

At multiple points, I had to throw away large parts of the code and start again. The current version is not “v1 with patches”, it’s a full rewrite with everything I learned the hard way baked in.

This is easily one of the most headache-inducing projects I’ve done — but also one of the most rewarding.

---

## Current State

Aspira is a functional UCI chess engine with a solid core architecture. It’s not trying to compete with Stockfish, but it *does* aim to be correct, fast, and understandable.

### What’s implemented

- Full ruleset (castling, en passant, promotion, repetition)
- Bitboard-based move generation (including sliding pieces)
- Zobrist hashing
- Transposition table
- Alpha-beta search with quiescence search
- Move ordering (history heuristic, MVV-LVA, TT move)
- Time management
- UCI protocol support
- FEN / PGN handling
- Perft testing for correctness

---

## Performance

On March 2025 on a **Ryzen 7 7800X3D**, Aspira reaches around **15 million nodes per second** in realistic conditions (legal move generation, checks, full rules enforced).
December 2025: move gen ~1.5x performance

That number didn’t come from one big optimization. It came from dozens of small fixes:
- removing unnecessary allocations
- fixing subtle bugs that killed pruning
- rewriting slow paths
- simplifying logic that looked “clean” but wasn’t fast

Most performance gains came from correctness, not clever tricks.

---

## Why This Was Hard

Chess engines are unforgiving.  
You don’t just debug crashes — you debug *ideas*.

- A broken repetition check quietly kills winning lines.
- A slightly wrong make/undo corrupts the position three plies later.
- A bad quiescence search looks fine… until it doesn’t.
- One incorrect bit operation and evaluation becomes noise.

You spend hours staring at code that *looks* correct, only to realize the bug is conceptually wrong, not syntactically wrong.

That’s what makes this project special to me.

---

## Contributing

Aspira is open to contributions.

If you want to contribute:
1. Fork the repository
2. Open an issue or pick an existing one
3. Submit a pull request with a clear explanation

I’m always open to discussions about engine design, performance trade-offs, or chess programming in general.

---

## Closing Thoughts

Aspira isn’t just a chess engine.  
It’s the result of wrestling with complexity until things finally started to make sense.

The name comes from *aspiring* — not just to build something stronger, but to understand something deeply enough that it stops being mysterious.  
Somewhere along the way, it also started aspiring my soul.

There’s still a lot to improve. But this is a solid foundation, earned the hard way.
