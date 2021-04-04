POM='pom.xml'
CONF-ID='com.sirnoob:config-service'
END-MESSAGE="Check the files in the results folder to see the output of this process"
PARENT=$(shell find . -mindepth 2 -maxdepth 2 -type f -name '$(POM)')
TARGETS:=$(shell find ./online-store-backend/ -path */dependency -prune -o -path '*target*/*' -type f -name '*.jar' -printf '%h\n')

.PHONY: cd results clean test package build run

run: package
	@sudo docker-compose up -d >./results/run
	@echo $(END-MESSAGE)

build: package
	@sudo docker-compose build >./results/build
	@echo $(END-MESSAGE)

package: clean
	@echo "packaging"
	@mvn --batch-mode --file $(PARENT) --define skipTests --activate-profiles postgresql package >./results/package
	@echo "dependencies"
	@for t in $(TARGETS);do \
		jar -xf $$t/*.jar; \
		mkdir -p $$t/dependency; \
		mv ./BOOT-INF/ $$t/dependency/; \
		mv ./META-INF/ $$t/dependency/; \
		rm -rf ./org; \
	done

test: clean
	@mvn --batch-mode --file $(PARENT) --projects $(CONF-ID) --quiet spring-boot:run >./results/config-service &
	@sleep 10s
	@mvn --batch-mode --file $(PARENT) --define spring.profiles.active=test test >./results/tests
	@pkill --full config-service >>./results/config-service
	@echo $(END-MESSAGE)

clean: results
	@echo "cleanning"
	@mvn --batch-mode --file $(PARENT) clean compile >./results/compilation 2>&1

results:
	@if [ ! -d ./results ]; then mkdir results; fi
