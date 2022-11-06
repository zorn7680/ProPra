package propra.imageconverter;

import java.io.IOException;
import propra.imageconverter.tools.ByteTools;

public class ImageConverter {
	public static void main(String[] args) throws ProgramMalfunctionException, IOException {
		
		TGAImage testImage = new TGAImage("images/alex.tga");
//		testImage.printHeader("byte");
		
		/* write function to extract width and height from byte data */
		
		short test = ByteTools.getShortFromByteArray(testImage.inputByteData, 12);
		System.out.println(test);
		
		
		
		
//		try {
//			InputProcessor.validateInput(args);
//			if (InputProcessor.IN_TYPE == "propra" && InputProcessor.OUT_TYPE == "tga") {
//				Converter.convertProPraToTGA(InputProcessor.IN_PATH, InputProcessor.OUT_PATH);
//			} else if (InputProcessor.IN_TYPE == "tga" && InputProcessor.OUT_TYPE == "propra") {
//				Converter.convertTGAToProPra(InputProcessor.IN_PATH, InputProcessor.OUT_PATH);
//			} else if (InputProcessor.IN_TYPE == "tga" && InputProcessor.OUT_TYPE == "tga") {
//				Converter.convertTGAToTGA(InputProcessor.IN_PATH, InputProcessor.OUT_PATH);
//			} else if (InputProcessor.IN_TYPE == "propra" && InputProcessor.OUT_TYPE == "propra") {
//				Converter.convertProPraToProPra(InputProcessor.IN_PATH, InputProcessor.OUT_PATH);
//			}
//		} catch (Exception e) {
//			System.err.println("Abbruch - Allgemeiner Programmfehler!");
//		}
	}
}