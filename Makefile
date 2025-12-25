EXE ?= Aspira_dev
MAVEN ?= mvn

.PHONY: all bench clean

all:
	$(MAVEN) clean package
	cat stub.sh target/chess-engine.jar > $(EXE)
	chmod +x $(EXE)

bench:
	$(MAVEN) clean package -Pbench
	java -jar target/benchmarks.jar

clean:
	$(MAVEN) clean
	rm -f $(EXE)
