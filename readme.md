**Readme File for Developers**

Purpose
========
PackBSP is a utilty program for games based on Valve Inc's "Source" game engine.
It allows map-makers to easily package dependencies and media into their map for
redistribution. It is intended as a successor to the aging PakRat utility.

Building
========
1. Install [Maven](http://maven.apache.org/) if you haven't already.
2. Download [jhllib](http://github.com/DHager/jhllilb) and install it to your
local repository. (Try `mvn clean install`)
3. From jhllib, copy `HLLib_32.dll` and `HLLib_64.dll` to the PackBSP's root
directory. (Having them there makes it easier to do debugging and unit tests.)
In addition, copy over `LICENSE.txt`, renaming it `hllib_license.txt`.
4. Download [hl2parse](http://github.com/DHager/hl2parse) and install it to your
local repository.
5. (Optional) If you need to create a new `.exe` launcher, download
[Launch4J](http://launch4j.sourceforge.net/) ans open the `launcher_conf.xml`
file with it. Then generate a new launcher that replaces `packbsp.exe`.

Modifying GUI components
--------
GUI forms are managed using the GUI tools in [Netbeans](http://netbeans.org/),
and any `.java` file with a corresponding `.form` file should not be modified through
any other tool, or risk inconsistencies.

When using Netbeans to edit GUI components, it helps to compile the project once
before opening any forms so that compositional dependencies can be seamlessly displayed.
Netbeans may present an error popup otherwise.

Creating a distributable copy
--------
Switch to the packbsp folder and run `mvn clean install assembly:assembly`.
This should create a redistributable set of files in the
`target/packbsp-{VERSION}-dist.dir/` directory.

When releasing a new version
--------
1. Update the changelog and readme files
2. Edit `pom.xml` and update version numbers
3. Edit `launcher_conf.xml` and update version numbers
4. Use Launch4J to create a new `packbsp.exe` file.
5. Clean, build, and assemble a new distributable.

When using a new version of jhllib
--------
1. Update the DLLs and license to match the JAR, as detailed in the general build section