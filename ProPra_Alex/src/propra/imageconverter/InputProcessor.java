package propra.imageconverter;

import java.io.File;

public final class InputProcessor {

	private static String inputString;
	private static String outputString;

	public static String IN_PATH;
	public static String OUT_PATH;

	public static String IN_TYPE;
	public static String OUT_TYPE;

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
			if (inputString.contains(".tga")) IN_TYPE = "tga";
			else if (inputString.contains(".propra")) IN_TYPE = "propra";
			else {
				System.err.println("Ungültige Dateitypen!");
				System.exit(123);
			}
	
			if (outputString.contains(".tga")) OUT_TYPE = "tga";
			else if (outputString.contains(".propra")) OUT_TYPE = "propra";
			else {
				System.err.println("Ungültige Dateitypen!");
				System.exit(123);
			}
			
			getIOfilePaths(inputString, outputString);
	}

	/* Bestimme/prüfe Quell- und Zielpfad */
	private static void getIOfilePaths(String inputString, String outputString) {
		OUT_PATH = outputString.substring(9);
		IN_PATH = inputString.substring(8);

		File tempFile = new File(IN_PATH);
		if (!tempFile.exists()) {
			System.err.println("Quelldatei fehlerhaft oder nicht vorhanden!");
			System.exit(123);
		}

		/* TEST/DEV - Parameter-Ausgaben */
		System.out.println("Konvertiere von Format " + InputProcessor.IN_TYPE + " nach " + InputProcessor.OUT_TYPE + "...");
		System.out.println("Quellpfad: " + InputProcessor.IN_PATH);
		System.out.println("Zielpfad: " + InputProcessor.OUT_PATH);
	}
}





