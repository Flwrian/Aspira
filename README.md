# Chess Game + Engine
This is a chess game and engine written in Java. Note that this is a work in progress and is not finished yet. There are a lot of things that need to be done. If you want to help, feel free to do so. I'm still learning new things and trying to play better chess too :)

## More info
I am aware that there are more efficient ways to program a chess engine. I am not trying to make the most efficient chess engine, but rather a chess engine that I made myself. I am also aware that there are a lot of bugs and glitches and that the code is not very clean.
## Done
- [x] Add a chess engine
- [x] Multiple algorithms for the engine
- [x] You can implement your own algorithm
- [x] Save and load games (FEN and PGN formats)
- [x] Fix weird pawn glitch (A file to H file)
- [x] UCI protocol
## Need to do
- [ ] Add a GUI
- [ ] Add castling
- [ ] Add en passant
- [ ] Add pawn promotion to other pieces than queen
- [ ] Add a timer
- [ ] Add a lot of tests


## Improvements
- [ ] Make the engine faster by using bitboards and other optimizations (see [here](https://www.chessprogramming.org/Bitboards))
- [ ] Use Zobrist hashing to speed up the engine

# V2 09/07/2024

V2 of the engine will be a fully working chess engine. Faster & better.

# Plan

The previous version was a bit bad in terms of performance and the game itself since there was no such things as en passant or castling.

So I went on to fix those problems.

## Speed

My goal was to make things faster using differents techniques such as :

- bitboards
- better movegen
- drop space constraint for better runtime

## Rules

This time, I wanted to make something that could really compete with me and also other bots. With the previous version, since there was no castling and en passant, the AI couldn't really play if those moves were played.

Now, the engine is fully working with every rules.

## Testing

I made testing better using Perft test.

Testing is very important to know if there are no bugs or if performances increase.

## 20/03/2025

passing ethereal perft suite