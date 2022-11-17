package propra.imageconverter;

/*
 * CHANGELOG Abschnitt 2
 * - Erweiterung des Launchers, um Base32 und Komprimierung behandeln zu können
 */

import java.io.IOException;

public class ImageConverter {
	public static void main(String[] args) throws ProgramMalfunctionException, IOException {

		/*
		 * Launcher für die entsprechenden Programmoperationen. 
		 * Dieser Codeabschnitt ist unschön, weil zu statisch, dadurch wächst 
		 * er mit zunehmenden Operationen immens an. Für eine modularere 
		 * Verarbeitung hat aber leider die Zeit gefehlt. Wird evtl. nach 
		 * Bearbeitungsabschnitt 2 überarbeitet.
		 */

		try {
			InputParser.validateInput(args);
			switch (InputParser.programMode) {
			// kompressionslose Konvertierungen aus Bearbeitungsabschnitt 1
			case "legacy":
				if (InputParser.inType == "propra" && InputParser.outType == "tga") {
					Converter.convertProPraToTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "propra") {
					Converter.convertTGAToProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "tga") {
					Converter.convertTGAToTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "propra") {
					Converter.convertProPraToProPra(InputParser.inPath, InputParser.outPath);
				}
				break;
			// BASE32-Encoding/Decoding
			case "base32":
				if (InputParser.inType == "random" && InputParser.doBase32Encode == true) {
					Converter.encodeToBase32(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "random" && InputParser.doBase32Encode == false) {
					Converter.decodeFromBase32(InputParser.inPath, InputParser.outPath);
				}
				break;
			// Konvertierungen mit Kompression
			case "compression":
				if (InputParser.inType == "tga" && InputParser.outType == "tga" && InputParser.inCompression == true
						&& InputParser.outCompression == false) {
					Converter.convertCTGAToUTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "tga"
						&& InputParser.inCompression == true && InputParser.outCompression == true) {
					Converter.convertCTGAToCTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "tga"
						&& InputParser.inCompression == false && InputParser.outCompression == false) {
					Converter.convertTGAToTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "tga"
						&& InputParser.inCompression == false && InputParser.outCompression == true) {
					Converter.convertUTGAToCTGA(InputParser.inPath, InputParser.outPath);
				}

				else if (InputParser.inType == "tga" && InputParser.outType == "propra"
						&& InputParser.inCompression == true && InputParser.outCompression == false) {
					Converter.convertCTGAToUProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "propra"
						&& InputParser.inCompression == true && InputParser.outCompression == true) {
					Converter.convertCTGAToCProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "propra"
						&& InputParser.inCompression == false && InputParser.outCompression == false) {
					Converter.convertTGAToProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "tga" && InputParser.outType == "propra"
						&& InputParser.inCompression == false && InputParser.outCompression == true) {
					Converter.convertUTGAToCProPra(InputParser.inPath, InputParser.outPath);
				}

				else if (InputParser.inType == "propra" && InputParser.outType == "tga"
						&& InputParser.inCompression == true && InputParser.outCompression == false) {
					Converter.convertCProPraToUTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "tga"
						&& InputParser.inCompression == true && InputParser.outCompression == true) {
					Converter.convertCProPraToCTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "tga"
						&& InputParser.inCompression == false && InputParser.outCompression == false) {
					Converter.convertProPraToTGA(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "tga"
						&& InputParser.inCompression == false && InputParser.outCompression == true) {
					Converter.convertUProPraToCTGA(InputParser.inPath, InputParser.outPath);
				}

				else if (InputParser.inType == "propra" && InputParser.outType == "propra"
						&& InputParser.inCompression == true && InputParser.outCompression == false) {
					Converter.convertCProPraToUProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "propra"
						&& InputParser.inCompression == true && InputParser.outCompression == true) {
					Converter.convertCProPraToCProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "propra"
						&& InputParser.inCompression == false && InputParser.outCompression == false) {
					Converter.convertProPraToProPra(InputParser.inPath, InputParser.outPath);
				} else if (InputParser.inType == "propra" && InputParser.outType == "propra"
						&& InputParser.inCompression == false && InputParser.outCompression == true) {
					Converter.convertUProPraToCProPra(InputParser.inPath, InputParser.outPath);
				}
				break;
			default:
				System.err.println("Da ist etwas schiefgelaufen..");
				System.exit(123);
			}

		} catch (Exception e) {
			System.err.println("Abbruch - Allgemeiner Programmfehler!");
		}
	}
}