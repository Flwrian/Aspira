"C:\Users\flofl\Desktop\Code\chess-java\Nouveau dossier\fastchess-windows-latest\fastchess-windows-latest.exe" ^
-engine cmd="Aspira_dev.exe" name=dev ^
-engine cmd="Aspira_main.exe" name=main ^
-each proto=uci tc=8+0.8 ^
-games 20 ^
-sprt elo0=0 elo1=5 alpha=0.05 beta=0.1 ^
-openings file="C:\Users\flofl\Desktop\Code\chess-java\Nouveau dossier\fastchess-windows-latest\8moves_v3.epd" format=epd ^
-concurrency 2