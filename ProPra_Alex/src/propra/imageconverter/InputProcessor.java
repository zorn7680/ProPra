package propra.imageconverter;

import java.io.File;

public final class InputProcessor {

	private static String inputString;
	private static String outputString;

	public static String inPath;
	public static String outPath;

	public static String inType;
	public static String outType;

	/* Prüfe Aufrufparameter vom Programmstart auf in- und output */
	public static void validateInput(String[] params) {
		if (params.length == 2) {
				for (String string : params) {
					if (string.contains("--input=")) inputString = string;
					if (string.contains("--output")) outputString = string;
				}
				getIOfileTypes(inputString, outputString);
		} else {
			System.err.println("Ungültige Ein- und Ausgabeparameter!");
			System.exit(123);
		}
	}

	/* Bestimme Eingabe- und Ausgabeformat */
	private static void getIOfileTypes(String inputString, String outputString) {
			if (inputString.contains(".tga")) inType = "tga";
			else if (inputString.contains(".propra")) inType = "propra";
			else {
				System.err.println("Ungültige Dateitypen!");
				System.exit(123);
			}
	
			if (outputString.contains(".tga")) outType = "tga";
			else if (outputString.contains(".propra")) outType = "propra";
			else {
				System.err.println("Ungültige Dateitypen!");
				System.exit(123);
			}
			
			getIOfilePaths(inputString, outputString);
	}

	/* Bestimme/prüfe Quell- und Zielpfad */
	private static void getIOfilePaths(String inputString, String outputString) {
		outPath = outputString.substring(9);
		inPath = inputString.substring(8);

		File tempFile = new File(inPath);
		if (!tempFile.exists()) {
			System.err.println("Quelldatei fehlerhaft oder nicht vorhanden!");
			System.exit(123);
		}

		/* TEST/DEV - Parameter-Ausgaben */
		System.out.println("Konvertiere von Format " + InputProcessor.inType + " nach " + InputProcessor.outType + "...");
		System.out.println("Quellpfad: " + InputProcessor.inPath);
		System.out.println("Zielpfad: " + InputProcessor.outPath);
	}
}





