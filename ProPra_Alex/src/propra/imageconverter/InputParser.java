package propra.imageconverter;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse umbenannt: InputProcessor -> InputParser
 * - Erweiterung um Attribute für Base32 und Kompression
 * - Entsprechende Erweiterung der Auflösungsroutinen
 * - Einführung eines Programm-Modus, um Launcher besser zu steuern
 */


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
 * Diese Klasse parst die Übergabeparameter aus dem args[]-Array und 
 * ermittelt abhängig davon Dateitypen, Dateipfade und die auszuführenden
 * Operationen. 
 */

public final class InputParser {

	private static String inString = "";
	private static String outString = "";
	private static String compressionString = "";
	private static String base32String = "";

	public static String inType;
	public static String outType;
	public static String inPath;
	public static String outPath;
	public static boolean inCompression = false;
	public static boolean outCompression = false;
	public static boolean doBase32Encode = false;
	public static boolean doBase32Decode = false;
	public static String programMode = "";


	// Input validieren und aufteilen, passende Folgemethode aufrufen
	public static void validateInput(String[] params) {
		switch (params.length) {
		case 2:
			// 2 Parameter: entweder Abschnitt1-Konvertierung oder Base32
			for (String string : params) {
				if (string.contains("--input="))
					inString = string;
				if (string.contains("--output"))
					outString = string;
				if (string.contains("--decode") || string.contains("--encode")) {
					base32String = string;
				}
			}
			if (base32String.length() == 0) {
				getIODataForLegacyConversions(inString, outString);
			} else {
				getIODataForBase32Encoding(inString, base32String);
			}
			break;
		case 3:
			// 3 Parameter: Konvertierung mit Kompression/Dekompression
			for (String string : params) {
				if (string.contains("--input="))
					inString = string;
				if (string.contains("--output"))
					outString = string;
				if (string.contains("--compression"))
					compressionString = string;
			}
			getIODataForCompressionConversions(inString, outString, compressionString);
			break;
		default:
			System.err.println("Ungültige Ein- und Ausgabeparameter!");
			System.exit(123);
		}
	}



	// Dateitypen und -pfade ermitteln, wenn Abschnitt1-Konvertierungen
	private static void getIODataForLegacyConversions(String inString, String outString) {
		System.out.println("Legacy conversions mode invoked!");
		programMode = "legacy";
		if (inString.contains(".tga")) {
			inType = "tga";
		} else if (inString.contains(".propra")) {
			inType = "propra";
		} else {
			System.err.println("Ungültiges Dateiformat!");
			System.exit(123);
		}

		if (outString.contains(".tga"))
			outType = "tga";
		else if (outString.contains(".propra"))
			outType = "propra";
		else {
			System.err.println("Ungültiges Dateiformat!");
			System.exit(123);
		}
		outPath = outString.substring(9);
		inPath = inString.substring(8);

		System.out.println("inType = " + inType + ", outType = " + outType);
		System.out.println("inPath = " + inPath + ", outPath = " + outPath);

		File tempFile = new File(inPath);
		if (!tempFile.exists()) {
			System.err.println("Quelldatei fehlerhaft oder nicht vorhanden!");
			System.exit(123);
		}
	}


	// Quellpfad, Zielpfad und Dekodierungstyp setzen, wenn BASE32-Operationen
	private static void getIODataForBase32Encoding(String inString, String base32String) {
		System.out.println("Base32 operations  mode invoked!");
		programMode = "base32";
		if (inString.contains(".base-32") && base32String.contains("--decode")) {
			inType = "random";
			inPath = inString.substring(8);
			outPath = inPath.substring(0, inPath.length() - 7);
			doBase32Encode = false;
		} else if (!inString.contains(".base-32") && base32String.contains("--encode")) {
			inType = "random";
			inPath = inString.substring(8);
			outPath = inPath + ".base-32";
			doBase32Encode = true;
		} else {
			System.err.println("Ungültige Base32-Parameter!");
			System.exit(123);
		}
	}


	// Quell- und Zielpfad, Quell- und Zieltyp, Kompressionsmodus setzen
	private static void getIODataForCompressionConversions(String inString, String outString,
			String compressionString) {
		System.out.println("Compression conversions  mode invoked!");
		programMode = "compression";
		if (inString.contains(".tga")) {
			inType = "tga";
		} else if (inString.contains(".propra")) {
			inType = "propra";
		} else {
			System.err.println("Ungültiges Dateiformat!");
			System.exit(123);
		}

		if (outString.contains(".tga")) {
			outType = "tga";
		} else if (outString.contains(".propra"))
			outType = "propra";
		else {
			System.err.println("Ungültiges Dateiformat!");
			System.exit(123);
		}

		outPath = outString.substring(9);
		inPath = inString.substring(8);

		try {
			if (inType == "propra") {
				byte compressionType = (Files.readAllBytes(Paths.get(inPath)))[12];
				inCompression = (compressionType == 0) ? false : true;
			} else if (inType == "tga") {
				byte compressionType = (Files.readAllBytes(Paths.get(inPath)))[2];
				inCompression = (compressionType == 2) ? false : true;
			}
		} catch (IOException e) {
			System.err.println("Quelldatei existiert nicht am angegebenen Pfad!");
			System.exit(123);
		}

		if (compressionString.contains("rle")) {
			// Zieldatei wird RLE-komprimiert verarbeitet
			outCompression = true;
		} else if (compressionString.contains("uncompressed")) {
			// Zieldatei wird unkomprimiert verarbeitet
			outCompression = false;
		} else {
			System.err.println("Ungültiger Kompressionsparameter!");
			System.exit(123);
		}
		System.out.println("inType = " + inType + ", outType = " + outType);
		System.out.println("inPath = " + inPath + ", outPath = " + outPath);

		File tempFile = new File(inPath);
		if (!tempFile.exists()) {
			System.err.println("Quelldatei fehlerhaft oder nicht vorhanden!");
			System.exit(123);
		}
	}
}