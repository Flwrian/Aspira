# Issues

## Pawn teleports (fixed see commit: https://github.com/Flwrian/JavaChessEngine/commit/6de59463536028a86b383e6ee0768c8eca1c036b)
Pawn can sometimes teleports especially if they are on the A file or H file.
[]: # 6kr/5ppp/2PQb3/p2p4/8/Pq2B2P/1P1R1PPR/6K1 b - - 1 29 pawn a5 teleports to h3 engine depth 3

## King move into check sometimes
King can sometimes move into check especially if they are on the edge of the board versus a queen or maybe a rook or bishop.

## Castling
Castling is not implemented yet.

## En passant
En passant is not implemented yet.

## Checkmate
Checkmate is not really implemented in the actual game. The game will just end if the engine detects that the king is going to be captured.

## Stalemate
Stalemate is not fully implemented yet.

## Threefold repetition
Threefold repetition is fully not implemented yet.

## Fifty move rule
Fifty move rule is not implemented yet.

## Draw by insufficient material
Draw by insufficient material is not implemented yet.

## Draw by agreement
Draw by agreement is not implemented yet.

## Draw by time
Draw by time is not implemented yet.
