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

Aspira is built from the ground up. No chess libraries doing the heavy lifting, no magic abstractions that hide complexity. Every major component exists because I needed to understand it deeply enough to implement it myself. So before writing the actual search function I needed to implement the basics:

That includes:
- board representation
- bitboards (bit ops)
- move generation
- evaluate the current board (eval function)
- legality checking
- Zobrist hashing (converts the board into a hash key, with very rare collisions)

At multiple points, I had to throw away large parts of the code and start again. The current version is not “v1 with patches”, it’s a full rewrite with everything I learned the hard way baked in.

This is easily one of the most headache-inducing projects I’ve done but also one of the most rewarding.

---

## Current State

Aspira is a functional UCI chess engine with a solid core architecture. It’s not trying to compete with Stockfish, but it *does* aim to be correct, fast, and understandable.

I aim to make Aspira a great Java chess engine by putting it above the 3000+ ELO bar.

### What’s implemented

- Full ruleset (castling, en passant, promotion, repetition)
- Bitboard-based move generation (including sliding pieces)
- Magic bitboards for sliding pieces
- Zobrist hashing
- Material evaluation + PSQT (Piece Square Table) eval
- Time management (plays autonomously based on remaining time)
- UCI protocol support
- FEN / PGN handling
- Perft testing for correctness

I’ve rewritten the search so many times that I’m still not fully satisfied with it.

I’m currently implementing a clean baseline search to aim for higher Elo.

The baseline Search will be like this:

- Alpha-beta pruning in negamax variant
- Quiescence search combined with delta pruning
- Transposition table
- History Heuristic
- Move ordering (history heuristic, MVV-LVA, TT move)
- Mate distance pruning
- Null move pruning
- Iterative Deepening

### Multi-threading

Currently, Aspira is mono-threaded.

This is a conscious design choice: multi-threading is significantly harder to implement correctly and requires proper hardware to really pay off.

### Next step

Next techniques I’m currently implementing to gain Elo and improve performance:

- LMR (Late move reductions) + PVS (Principal search variation): Elo+++
- Add more precision to the current eval function (passed pawns, king safety...): Elo+
- Convert the Pseudo Legal Move Generation into a Fully Legal Move Generation
- Packing moves into a short instead of an int (32bit -> 16bit)
- Pre allocate a MoveList stack to prevent creating new objects at runtime in the hot loop (Move generation)

### The next next step

I really want to take a big step forward with the NNUE technique, as it will significantly improve the precision of the evaluation function.

I’ve already done some tests to see how I could implement it, and it ended up working. The only problem is that I need to train the neural net a lot more to actually benefit from NNUE. I’ll need to do mass data generation with my current HCE and feed millions of games into it (should take a few hours). I could also grab a net from some other engines, but that’s not really the point.

But I also want to be proud of my HCE (hand-crafted evaluation) before taking the NNUE step.

I hope to reach high Elo before taking that step. It will depend on how I’m feeling about it.

Since my last messy search was already reaching around 2100+ Elo on Lichess, it’s just a matter of time before my current new search crushes the old one :)

With a good NNUE implementation, good HCE, good training, Aspira could very well reach the 3000+ ELO zone (if I have enough sanity to make it here)


---

## Performance

On March 2025 on a **Ryzen 7 7800X3D**, Aspira reaches around **15 million nodes per second** in realistic conditions (legal move generation, checks, full rules enforced).
December 2025: move gen about ~13Mnps on a Ryzen 5 5500U (18MNPS perft semi bulk i think).
January 2026: bumped to ~20-22MNPS Ryzen 5 5500U (about 30MNPS on Ryzen 7 7800X3D)

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

And that's just for the search function. I've literally spent nights debugging positions to pass a perft suite (aka suite of positions where you generate each possible moves, make those moves, generate each possible moves... until a certain "depth").
Move generation seems quite simple and in fact it's not that hard but the bugs you've created along the way will come up to the surface when running perft :)

You spend hours staring at code that *looks* correct, only to realize the bug is conceptually wrong, not syntactically wrong.

That’s what makes this project special to me.
I’ll likely turn this into a blog post later (leaving this here as a reminder).

---

## Contributing

Aspira is open to contributions.

If you want to contribute:
1. Fork the repository
2. Open an issue or pick an existing one
3. Submit a pull request with a clear explanation

I’m always open to discussions about engine design, performance trade-offs, or chess programming in general.

---

## HUGE Thanks to the Stockfish Discord Community

Big thanks to the Stockfish Discord Community for all the discussions, feedback, and shared knowledge.
Reading the exchanges and digging through past messages helped me a lot, especially on evaluation design and NNUE-related topics.
Even when you don’t ask questions directly, there’s a ton to learn just by following the conversations. Really appreciated.

## Closing Thoughts

Aspira isn’t just a chess engine.  
It’s the result of wrestling with complexity until things finally started to make sense.

The name comes from *aspiring* — not just to build something stronger, but to understand something deeply enough that it stops being mysterious.  
Somewhere along the way, it also started aspiring my soul.

There’s still a lot to improve. But this is a solid foundation, earned the hard way.


