@echo off
rem Testing batch file for if the fancy .exe launcher is having problems

set ver="2.0.5"
rem Start with no annoying console lurking in the background.
rem (This may not be desirable if debugging and you want to see console output.)
start /I /B javaw -jar ./target/packbsp-%ver%-dist.dir/packbsp-%ver%/app/packbsp.jar

