package propra.imageconverter;

import java.io.IOException;
import propra.imageconverter.tools.*;
import propra.imageconverter.compression.*;

public class ImageConverter {
	public static void main(String[] args) throws ProgramMalfunctionException, IOException {

		CompressedTGAImage testImage1 = new CompressedTGAImage("images/test_comp.tga");
		byte[] uncompressedRGB =  (new Decompressor()).decompress(testImage1.rgbData);
		for (byte b : uncompressedRGB) {
			System.out.println(b);
		}



//		TGAImage testImage2 = new TGAImage("images/test_uncomp.tga");
//		System.out.println(testImage2.rgbData.length);


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