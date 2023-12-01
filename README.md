# Keytar
Software which allows a Guitar Hero live controller to be used as a keyboard. 

## Usage
Download the zip from the releases page. This contains the JAR, as a well as three configuration files. Running the JAR will use the built-in GUI and default keysets. This configuration works well and is suggested for first time users. 

## Configuration
Keytar contains several configuration options across several files. 

### Java Properties
The highest level configuration occurs in the `keytar.properties` file. This file must be placed in the same directory as the JAR, and named the same as well. System properties may also be specifed via command line options, however the `keytar.properties` file must still exist. Command line options override options configured in the file. Unfortunately, the `java.library.path` property must be configured via a command line argument. The path must point to the `natives` directory.

### Logging
Keytar uses Log4j for logging. Any logging configuration should be done in an XML file which follows the log4j rules. Note that if stdout is captured by the GUI (see above), ASCII color characters will be removed. This means that colored logging is safe to use, the default configuration uses color logging.  

### Keysets
Keysets are grids of characters which can be typed using the guitars fret keys. Keysets are configured using JSON. The config file should contain only one key called "keys". The value of "keys" should be a three dimensional array. The first level of the array is the keyset, which is a 5 by 5 two dimensional array. The array should contain a single character. The character may be capitalize or not where applicable. However, having both a capitalized and lowercase character is wasteful as the user could instead use the caps key. At least one keyset should be specified. See `keyset.json` in the release for an example configuration.