EXE ?= Aspira_dev
MAVEN ?= mvn

.PHONY: all bench clean

all:
	$(MAVEN) clean package
	cat stub.sh target/chess-engine.jar > $(EXE)
	chmod +x $(EXE)

# Benchmark rapide pour dev (~15 secondes)
bench-quick:
	$(MAVEN) clean package -Pbench
	java -jar target/benchmarks.jar -wi 10 -i 5 -f 1 -r 200ms -w 200ms

# Benchmark full
bench-full:
	$(MAVEN) clean package -Pbench
	java -jar target/benchmarks.jar -wi 10 -i 10 -f 5 -r 1000ms -w 500ms

clean:
	$(MAVEN) clean
	rm -f $(EXE)
