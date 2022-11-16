package propra.imageconverter;

/*
 * CHANGELOG Abschnitt 2
 * - Erweiterung des Launchers, um Base32 und Komprimierung behandeln zu können
 * - 
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import propra.imageconverter.tools.*;
import propra.imageconverter.baseEncoding.Base32Decoder;
import propra.imageconverter.baseEncoding.Base32Encoder;
import propra.imageconverter.compression.*;

public class ImageConverter {
	public static void main(String[] args) throws ProgramMalfunctionException, IOException {
		
//		 ALEX Testcode für Kompression - Datenquelle: byte-Array
//		TGAImage img = new TGAImage("images/alex_unc.tga");
//		byte[] fileData = img.rgbData;
//		System.out.println("file data:");
//		for (byte b : fileData) {
//			System.out.println(b);
//		}
//		System.out.println("");
//		
//		System.out.println("data from array");
//		System.out.println("");
//		byte[] rgbData = {34,34,34,34,34,34,11,11,11,11,11,11
//				
//				
//		
//		
//		};
//		System.out.println("compressed: ");
//		
//		Compressor c = new Compressor();
//		byte[] testResult = c.compress(rgbData);
//		System.out.println();
//		for (byte b : testResult) {
//			System.out.println(b);
//		}
		
		
//		CompressedTGAImage cTGA = new CompressedTGAImage("images/test_02_rle.tga");
//		cTGA.printHeader("hex");
//		System.out.println(cTGA.rgbData.length);
//		System.out.println("Breite " + cTGA.getWidth());
//		System.out.println("Höhe " + cTGA.getHeight());
		
//		CompressedProPraImage cProPra = new CompressedProPraImage("images/test_04_rle.propra");
//		cProPra.printHeader("hex");
//		System.out.println(cProPra.rgbData.length);
//		System.out.println(cProPra.compressedRgbData.length);
//		System.out.println("Breite " + cProPra.getWidth());
//		System.out.println("Höhe " + cProPra.getHeight());
		
		
		
		

		/* Launcher für die entsprechenden Programmoperationen
		 * Dieser Codeabschnitt ist unschön, weil zu statisch, dadurch wächst
		 * er mit zunehmenden Operationen quadratisch an. Für eine modularere 
		 * Verarbeitung hat aber leider die Zeit gefehlt. Wird evtl. nach
		 * Bearbeitungsabschnitt 2 überarbeitet. 
		 */

		

		try {
			InputParser.validateInput(args);
			switch (InputParser.programMode) {
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
			case "base32":
				if(InputParser.inType == "random" && InputParser.doBase32Encode == true) {
					Converter.encodeToBase32(InputParser.inPath, InputParser.outPath);
				} else if(InputParser.inType == "random" && InputParser.doBase32Encode == false) {
					Converter.decodeFromBase32(InputParser.inPath, InputParser.outPath);
				}
				break;
			case "compression":
				if(InputParser.inType == "tga" && InputParser.outType == "tga" && 
				InputParser.inCompression == true && InputParser.outCompression == false) {
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















// ALEX Testcode für Kompression - Datenquelle: byte-Array
//byte[] rgbData = {11,11,11,11,11,11,22,22,22,11,11,11,22,22,22,22,22,22,22,22,22,11,11,11,22,22,22,22,22,22,11,11,11};
//Compressor c = new Compressor();
//byte[] testResult = c.compress(rgbData);
//System.out.println();
//for (byte b : testResult) {
//	System.out.println(b);
//}


// ALEX Testcode für Dekompresion - Datenquelle: Bilddatei
//CompressedTGAImage testImage1 = new CompressedTGAImage("images/test_comp.tga");
//byte[] uncompressedRGB =  (new Decompressor()).decompress(testImage1.rgbData);
//for (byte b : uncompressedRGB) {
//	System.out.println(b);
//}


// ALEX Testcode für Dekompression - Datenquelle: byte-Array
//byte[] rgbData = {-127,11,11,11};
//Decompressor d = new Decompressor();
//byte[] testor = d.decompress(rgbData);
//System.out.println();
//for (byte b : testor) {
//	System.out.println(b);
//}





// ALEX Testcode für Base32-Encoding - Datenquelle: Aufrufparameter
//String s = args[0];
//byte[] stringBytes = s.getBytes();
//Base32Encoder enc = new Base32Encoder();
//System.out.println("Base32-Kodierung: " + enc.encode(stringBytes));

// ALEX Testcode für Base32-Encoding - Datenquelle: String-array
//String s = "loremipsumdolorsitametsediamnonnumbycumdoloremagna";
//byte[] stringBytes = s.getBytes();
//Base32Encoder enc = new Base32Encoder();
//String result = enc.encode(stringBytes);
//System.out.println(result);



// ALEX Testcode für Base32-Decoding - Datenquelle: String array
//String s = "Conny ist süß!!!";
//System.out.println(s.length());
//Base32Encoder enc = new Base32Encoder();
//Base32Decoder dec = new Base32Decoder();
//System.out.println("Base32 decodiert: " + dec.decode(enc.encode(s)));		



// ALEX Testcode für Datei -> enc -> dec -> Datei
//byte[] inputByteData1 = Files.readAllBytes(Paths.get("images/test_01_uncompressed.tga"));
//int[] ibdu = new int[inputByteData1.length];
//
//for(int i = 0; i < inputByteData1.length; i++) {
//	ibdu[i] = Byte.toUnsignedInt(inputByteData1[i]);
//}
//
//System.out.println("länge eingabe: " + ibdu.length);
//Base32Encoder enc = new Base32Encoder();
//String result = enc.encode(ibdu);
//	Files.write(Paths.get("images/testout.tga"), result.getBytes());
//
//Base32Decoder dec = new Base32Decoder();
//byte[] result2 = dec.decode(result);
//System.out.println("länge ausgabe: " + result2.length);
//Files.write(Paths.get("images/testout2.tga"), result2);



// ALEX Testcode für Base32-Decoding - Datenquelle: ganze Datei
//byte[] inputByteData = Files.readAllBytes(Paths.get("images/test_05_base32.tga.base-32"));
//ByteArrayOutputStream bos = new ByteArrayOutputStream();
//bos.write(inputByteData);
//String inputStringData = bos.toString();
//Base32Decoder dec = new Base32Decoder();
//byte[] result = dec.decode(inputStringData);
//Files.write(Paths.get("images/testout.tga"), result);





/*

	try {
		InputParser.validateInput(args);
		switch (InputParser.programMode) {
		case "legacy":
			if (InputParser.inType == "propra" && InputParser.outType == "tga") {
				System.out.println("Konvertiere propra nach tga");
				Converter.convertProPraToTGA(InputParser.inPath, InputParser.outPath);
			} else if (InputParser.inType == "tga" && InputParser.outType == "propra") {
				System.out.println("Konvertiere tga nach propra");
				Converter.convertTGAToProPra(InputParser.inPath, InputParser.outPath);
			} else if (InputParser.inType == "tga" && InputParser.outType == "tga") {
				System.out.println("Konvertiere tga nach tga");
				Converter.convertTGAToTGA(InputParser.inPath, InputParser.outPath);
			} else if (InputParser.inType == "propra" && InputParser.outType == "propra") {
				System.out.println("Konvertiere propra nach propra");
				Converter.convertProPraToProPra(InputParser.inPath, InputParser.outPath);
			}
			break;
		case "base32":
			if(InputParser.inType == "random" && InputParser.doBase32Encode == true) {
				System.out.println("Kodiere Datei nach Base32");
				Converter.encodeToBase32(InputParser.inPath, InputParser.outPath);
			} else if(InputParser.inType == "random" && InputParser.doBase32Encode == false) {
				System.out.println("Dekodiere Base32-Datei");
				Converter.decodeFromBase32(InputParser.inPath, InputParser.outPath);
			}
			break;
		case "compression":
			if(InputParser.inType == "tga" && InputParser.outType == "tga" && 
			InputParser.inCompression == true && InputParser.outCompression == false) {
				System.out.println("Konvertiere tga_com nach tga_unc");
				// TGA_com -> TGA_unc
				Converter.convertCTGAToUTGA(InputParser.inPath, InputParser.outPath);
			}
			else if(InputParser.inType == "tga" && InputParser.outType == "tga" && 
					InputParser.inCompression == true && InputParser.outCompression == true) {
						System.out.println("Konvertiere tga_com nach tga_com");
						// TGA_com -> TGA_com
						Converter.convertCTGAToCTGA(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "tga" && InputParser.outType == "tga" && 
					InputParser.inCompression == false && InputParser.outCompression == false) {
						System.out.println("Konvertiere tga_unc nach tga_unc - legacy!");
						// TGA_unc -> TGA_unc  ==> legacy conversion!
						Converter.convertTGAToTGA(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "tga" && InputParser.outType == "tga" && 
					InputParser.inCompression == false && InputParser.outCompression == true) {
						System.out.println("Konvertiere tga_unc nach tga_com");
						// TGA_unc -> TGA_com
						Converter.convertUTGAToCTGA(InputParser.inPath, InputParser.outPath);
					}
			
			else if(InputParser.inType == "tga" && InputParser.outType == "propra" && 
					InputParser.inCompression == true && InputParser.outCompression == false) {
						System.out.println("Konvertiere tga_com nach propra_unc");		
						// TGA_com -> Propra_unc
						Converter.convertCTGAToUProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "tga" && InputParser.outType == "propra" && 
					InputParser.inCompression == true && InputParser.outCompression == true) {
						System.out.println("Konvertiere tga_com nach prpora_com");
						// TGA_com -> Propra_com
						Converter.convertCTGAToCProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "tga" && InputParser.outType == "propra" && 
					InputParser.inCompression == false && InputParser.outCompression == false) {
						System.out.println("Konvertiere tga_unc nach propra_unc => legacy!");
						// TGA_unc -> Propra_unc ==> legacy conversion!
						Converter.convertTGAToProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "tga" && InputParser.outType == "propra" && 
					InputParser.inCompression == false && InputParser.outCompression == true) {
						System.out.println("Konvertiere tga_unc nach propra_com");
						// TGA_unc -> Propra_com
						Converter.convertUTGAToCProPra(InputParser.inPath, InputParser.outPath);
					}
			
			else if(InputParser.inType == "propra" && InputParser.outType == "tga" && 
					InputParser.inCompression == true && InputParser.outCompression == false) {
						System.out.println("Konvertiere propra_com nach tga_unc");
						// Propra_com -> TGA_unc
						Converter.convertCProPraToUTGA(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "tga" && 
					InputParser.inCompression == true && InputParser.outCompression == true) {
						System.out.println("Konvertiere propra_com nach tga_com");
						// Propra_com -> TGA_com
						Converter.convertCProPraToCTGA(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "tga" && 
					InputParser.inCompression == false && InputParser.outCompression == false) {
						System.out.println("Konvertiere propra_unc nach propra_unc => legacy!");
						// Propra_unc -> TGA_unc -> legacy!
						Converter.convertProPraToTGA(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "tga" && 
					InputParser.inCompression == false && InputParser.outCompression == true) {
						System.out.println("Konvertiere propra_unc nach tga_com");
						// Propra_unc -> TGA_com
						Converter.convertUProPraToCTGA(InputParser.inPath, InputParser.outPath);
					}
			
			else if(InputParser.inType == "propra" && InputParser.outType == "propra" && 
					InputParser.inCompression == true && InputParser.outCompression == false) {
						System.out.println("Konvertiere propra_com nach propra_unc");
						// Propra_com -> Propra_unc
						Converter.convertCProPraToUProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "propra" && 
					InputParser.inCompression == true && InputParser.outCompression == true) {
						System.out.println("Konvertiere propra_com nach propra_com");
						// Propra_com -> Propra_com
						Converter.convertCProPraToCProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "propra" && 
					InputParser.inCompression == false && InputParser.outCompression == false) {
						System.out.println("Konvertiere propra_unc nach propra_unc -> legacy!");
						// Propra_unc -> Propra_unc => legacy!
						Converter.convertProPraToProPra(InputParser.inPath, InputParser.outPath);
					}
			else if(InputParser.inType == "propra" && InputParser.outType == "propra" && 
					InputParser.inCompression == false && InputParser.outCompression == true) {
						System.out.println("Konvertiere propra_unc nach propra_com");
						// Propra_unc -> Propra_com
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
*/