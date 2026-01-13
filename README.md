

![Java](https://img.shields.io/badge/Language-Java-blue.svg)
![UCI Support](https://img.shields.io/badge/Protocol-UCI-green.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)
<p align="center">
  <img src="logo.svg" alt="Aspira Logo" width="200"/>
</p>

# Aspira – Java Chess Engine

Aspira is a UCI-compatible chess engine written entirely in **Java**. Built from scratch with bitboards, magic bitboards, alpha-beta search...


## Quick Start

### Requirements
- Java 21 or higher
- Maven

### Build & Run
```bash
# Clone the repository
git clone https://github.com/Flwrian/Aspira.git
cd Aspira

# Build the UCI engine
mvn clean package -Puci

# Run the engine
java -jar target/chess-engine.jar
```

**Or use the Makefile:**
```bash
make          # Builds and creates executable
./Aspira_dev  # Run directly
```

### Using with a GUI
Aspira supports the UCI protocol. Use it with any UCI-compatible chess GUI:
- [Arena](http://www.playwitharena.de/)
- [Cute Chess](https://cutechess.com/)

In your GUI, point to the compiled JAR or run command.


---

## Development

For detailed development notes, design decisions, and the story behind Aspira, see [DEVLOG.md](DEVLOG.md).

---

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

## Acknowledgments

Big thanks to the engine developers on the **Stockfish Discord** for the discussions, feedback, and shared knowledge. The community has been incredibly helpful throughout this project.

---

## License

MIT License - see [LICENSE](LICENSE) for details.