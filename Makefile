JAVA_HOME ?= C:/Program Files/Android/Android Studio/jbr
GRADLE    := JAVA_HOME="$(JAVA_HOME)" ./gradlew --no-configuration-cache

.PHONY: debug release clean install help

help:
	@echo "Pitiq build targets:"
	@echo "  make debug    — build debug APK"
	@echo "  make release  — build signed release APK (requires keystore.properties)"
	@echo "  make install  — build debug APK and install to connected device via adb"
	@echo "  make clean    — delete all build outputs"

debug:
	$(GRADLE) :app:assembleDebug
	@echo "APK: app/build/outputs/apk/debug/app-debug.apk"

release:
	$(GRADLE) :app:assembleRelease
	@echo "APK: app/build/outputs/apk/release/app-release.apk"

install:
	$(GRADLE) :app:installDebug

clean:
	$(GRADLE) clean
