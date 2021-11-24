JAVAC = javac
DEBUG = -g
CLASSES = \
	src/Simulator/*.java
sim:
	mkdir bin/
	$(JAVAC) -d bin/ $(CLASSES)
	jar cfm sim.jar src/Simulator/META-INF/MANIFEST.MF -C bin/ .
	@echo "To Run the simulator, please enter: java -cp sim.jar Simulator.MP3 'args'"
	@echo "Example:"
	@echo "java -cp sim.jar Simulator.MP3 gshare 11 5 jpeg_trace.txt"
clean:
	$(RM) bin/**/*.class
	$(RM) -r bin/ 
	$(RM) sim.jar