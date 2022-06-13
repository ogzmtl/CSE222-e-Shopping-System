target:compile output

compile:
	javac -d bin src/*/*/*/*.java src/*/*/*.java src/*/*.java

output:
	java -cp bin test.Driver

clean:
	rm out