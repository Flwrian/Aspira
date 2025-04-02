# Aspira – Java Chess Engine

**Aspira** is a chess engine written entirely in Java. It’s a work in progress, born from curiosity and a passion for both programming and chess.  
The goal isn't just to make a strong engine, but to learn deeply by building everything myself — from board representation to move generation and search.

---

## What’s done so far

- Core chess engine  
- Multiple pluggable search algorithms  
- UCI protocol  
- FEN and PGN load/save  
- Basic legality enforcement (fixed pawn bugs on file A and H)  
- Castling, en passant, and promotion rules supported  
- Move generation via bitboards  
- Full Perft testing and rule verification  
- Passing Ethereal's Perft test suite (20/03/2025)

---

## Still to do

- GUI (visual board, mouse input, move highlighting)    
- More tests for edge cases and deep search validation  

---

## Performance Goals & Engine V2

Before **V2 (09/07/2024)**, the engine was functional but very limited — it lacked key rules like castling and en passant, and performance was poor due to naive data structures and logic.  
V2 marked a turning point: a complete rebuild focused on speed, correctness, and a more competitive base.

Key improvements in V2 and beyond:

- Transitioned to bitboards for faster and cleaner board representation  
- Switched from object-based moves to packed `long` values to reduce garbage collection  
- Combined low-GC move creation with bitboards for major performance boosts  
- Integrated Zobrist hashing for position tracking  
- Built complete Perft testing tools for debugging and validation  
- Rewriting the search to allow for pruning, ordering, and efficiency  

---

## Contributing
Aspira is open to contributions! If you have ideas, suggestions, or want to help out, feel free to reach out.
You can also check out the issues page for tasks and features that need attention.
If you're interested in contributing, please fork the repository and submit a pull request.
All contributions are welcome, whether it's code, documentation, or just feedback.

---


## Dev Log

**09/07/2024** – Start of Aspira V2  
Complete rewrite of the core to fix all rule-related limitations and performance issues.  

**20/03/2025** – Passing full Ethereal Perft suite  
Rules are done. Time to work on strength and search.

**23/03/2025** – Reworked move representation  
Moved from Java `Move` objects to 64-bit packed `long` moves.  
This reduced GC pressure and heap allocations significantly. When combined with bitboard-based move generation, performance improved by ~20%.

**24/03/2025** – Implemented magic bitboards  
Aspira now uses magic bitboards for sliding piece move generation.  
This change further solidified the ~70% gain in movegen performance and laid the foundation for deeper and faster search.

**27/03/2025** – Implemented time control inside the search so the engine can now play and manage it's own time.

---

## Current Status
Aspira is currently in a state of rapid development. The core engine is functional, and the move generation is efficient.
The focus is now on enhancing the search algorithm and implementing a basic evaluation function.
The engine is capable of playing legal moves and understanding basic chess rules, but it lacks advanced features like a sophisticated search algorithm and evaluation function.
The engine is not yet competitive with top engines, but it serves as a solid foundation for future improvements.

## Performance
In terms of performance, Aspira move generation is currently around 15MNPS (million nodes per second) on a Ryzen 7 7800X3D CPU as of March 2025.

## Future Plans
- **Search**: Implement a more sophisticated search algorithm with alpha-beta pruning, transposition tables, and move ordering, killers, etc.
- **Evaluation**: Develop a basic evaluation function to assess positions and improve decision-making. Focus on material balance, piece activity, king safety, pawn structure, mobility, piece-square tables midgame/endgame, etc.
- **Legal moveGen only**: Implement a legal move generator (will result in a ~100% movegen speed increase).
- **Feature Expansion**: Explore advanced features like neural network integration, machine learning, and adaptive play styles.


## Thanks
Special thanks to everyone in the Stockfish Discord community for their support and resources.