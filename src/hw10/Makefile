TARGET = hello-world

CXXFLAGS = -O3 -std=c++0x

all: $(TARGET)

clean:
	$(RM) *.o $(TARGET)

run: all
	./$(TARGET)

.PHONY: clean run

$(TARGET): %: %.o
	$(CXX) $(CXXFLAGS) $^ -o $@
