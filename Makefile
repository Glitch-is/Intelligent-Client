CC=javac
CFLAGS=-g:none
SRC=src
RES=resources
BINDIR=bin
LSTF=temp.txt
IMGDIR=$(RES)/images
MANIFEST=$(RES)/Manifest.txt
VERSIONFILE=$(RES)/version.txt
VERSION=`cat $(VERSIONFILE)`
NAME=RenatusBot
DIST=$(NAME).jar
ACCOUNTS=$(HOME)/.$(shell echo $(NAME) | tr '[A-Z]' '[a-z]')acct
INSTALLDIR=$(HOME)/$(NAME)

.PHONY: all Bot Bundle clean remove

all: Bundle

Bot:
	@if [ ! -d "$(BINDIR)" ]; then mkdir "$(BINDIR)"; fi
	$(CC) $(CFLAGS) -d "$(BINDIR)" `find "$(SRC)" -name \*.java`

Bundle: Bot
	@rm -fv "$(LSTF)"
	@cp "$(MANIFEST)" "$(LSTF)"
	@echo "Specification-Version: \"$(VERSION)\"" >> "$(LSTF)"
	@echo "Implementation-Version: \"$(VERSION)\"" >> "$(LSTF)"
	@if [ -e "$(DIST)" ]; then rm -fv "$(DIST)"; fi
	jar cfm "$(DIST)" "$(LSTF)" -C "$(BINDIR)" . "$(VERSIONFILE)" "$(IMGDIR)"/*.png "$(RES)"/*.bat "$(RES)"/*.sh
	@rm -f "$(LSTF)"

clean:
	@rm -fv "$(DIST)"
	@rm -rfv "$(BINDIR)"

remove:
	@if [ -e "$(ACCOUNTS)" ]; then rm -fv "$(ACCOUNTS)"; fi
	@if [ -d "$(INSTALLDIR)" ]; then rm -rfv "$(INSTALLDIR)"; fi
