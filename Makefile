EXE ?= Aspira_dev
MAVEN_EXE ?= mvn

MAVEN_COMMAND_PREFIX :=
ifdef JAVA_HOME
    MAVEN_COMMAND_PREFIX := JAVA_HOME=$(JAVA_HOME)
endif

.PHONY: all

all:
	$(MAVEN_COMMAND_PREFIX) $(MAVEN_EXE) -f ./pom.xml package
	cat stub.sh ./target/demo-1.jar > $(EXE)
	chmod +x $(EXE)
	cp ./target/demo-1.jar ./engines/Aspira_dev.jar