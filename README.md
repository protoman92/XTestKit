# XTestKit
Cross-platform TestKit for mobile apps.

## Requirements:

**Java 8**.

**Intellij IDEA**.

At lease one **Android Virtual Device**/**iOS Simulator**.

The Android emulator's name should have spaces replaced by underscores
(e.g. **Nexus_4_API_23**) - this name will be used to set up **Appium**. To
list all emulators, use **adb devices -l**. Unfortunately, the emulators'
names are not found in the output of the above command, so the easiest way
to get these is to use Android Studio.

On the other hand, the iOS Simulator's name can be found in
**XCode/Window/Devices**, or with **xcrun instruments -s devices**.

**ANDROID_HOME**, **JAVA_HOME** and **${JAVA_HOME/bin}** in PATH.

## How to start:

From **Intellij IDEA**, clone this repository. **Gradle** will be set up
automatically.

When prompted to set **Gradle Home**, run **gradlew getHomeDir** from the
project directory, and enter the directory path that is returned.

Copy the app bundle (e.g. **'app-debug.apk'**) to the **app** folder. Please
be mindful that iOS testing requires .app files for simulator environment,
and .ipa files for actual devices. An error will be thrown if the files'
extensions are mismatched.

**npm install -g appium-doctor**: This module checks the system and determines
whether **Appium** can be run.

**npm install -g appium**: This installs **Appium** globally so we can use
its CLI.

## Description:

Included within this package is a small library I wrote over a few days that
help automate setting up and running tests. It is based on **Appium**
and **Appium**'s Java library, and also heavily dependent on **RxJava 2** and
**TestNG**.

The main components of this library is:

### TestKit: 

This is the main handler and coordinator class. A **TestKit** object can
have a List of **Engine**, and provides convenience methods such
as *beforeClass*, *before*, *after* and *afterClass* for easy pre/post-test
setups.

### Localizer:

This class is in charge of localizing texts that are passed to element
locator methods. It uses **ResourceBundle** to locate the right translation.
Consult **Localizer.Builder** to get an idea of how to construct a
**Localizer** instance.

A **TestKit** instance will have one **Localizer**, which is constructed
via **TestKit.Builder**.

### Engine: 

A base class that abstracts away **Appium** methods to work accross different
platforms. It supplies methods to work with the **Appium** driver, and a number
of these methods are implemented differently for each platform
(e.g. **Android**, **iOS**) by subclasses. Each **Engine** contains
a set of crucial information (e.g. *platformName*, *version*, *appPackage*)
that will be supplied to **Appium**.

The idea is to provide pluggable **Engine** instances to a **TestKit**
object, which will run them while completely unaware of the platform or OS
versions. This allows us to write platform-agnostic tests that can be used on
all platforms and versions.

### AndroidEngine: 

This class provides **Android**-specific methods, many of which make use of
**adb shell**. It can start/stop emulator, enable/disable animations, clear
*SharedPreferences*, detect soft keyboard, etc.

As an example, we can have an **AndroidEngine** as such:

> AndroidEngine.newBuilder()
  .withApp('app-debug.apk')
  .withDeviceName('Nexus_4_API_23')
  .withPlatformVersion('6.0')
  .build()

### IOSEngine:

This class provides **iOS**-specific methods. Most methods are delegated to
**Appium**'s iOS handling. Setting up an **IOSEngine** instance is similar to
how it's done for **AndroidEngine**.

### XPath: 

This class allows for convenient **XPath** scripting. Using **XPath** is a
good way to write cross-platform test codes.
