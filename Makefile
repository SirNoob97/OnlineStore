PROJECTS:=$(shell find . -path */postgresql -prune -false -path */target -prune -false -o -type f -name 'pom.xml')
CONF-SER:=$(shell find . -path */postgresql -prune -false -path */target -prune -false -o -type f -path '*config-service*/*' -name 'pom.xml')
REG-SER:=$(shell find . -path */postgresql -prune -false -path */target -prune -false -o -type f -path '*registry-service*/*' -name 'pom.xml')

.PHONY: clean test package build run

test: clean
	@for p in $(PROJECTS); do \
		mvn -f $$p -Dspring.profiles.active=test test; \
	done

clean:
	@for p in $(PROJECTS); do \
		mvn -f $$p clean compile; \
	done
