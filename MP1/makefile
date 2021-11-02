JAVAC = javac
DEBUG = -g
CLASSES = \
	src/Simulator/*.java
sim_cache:
	mkdir bin/
	$(JAVAC) -d bin/ $(CLASSES)
	jar cfm sim_cache.jar src/Simulator/META-INF/MANIFEST.MF -C bin/ .
	@echo "To Run the simulator, please enter: java -cp sim_cache.jar Simulator.MP1 'args'"
	@echo "Example:"
	@echo "java -cp sim_cache.jar Simulator.MP1 16 1024 2 0 0 0 0 gcc_trace.txt"
clean:
	$(RM) bin/**/*.class
	$(RM) bin/ -r
	$(RM) sim_cache.jar
