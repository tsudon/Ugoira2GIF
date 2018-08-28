package example;

import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import tsudon.image.*;

public class ImagetoPNG {

	public static void main(String[] args) {
		String path, outpath;

		if (args.length >= 1) {
			path = args[0];
		} else {
			System.out.println("Need argument input Imagefile");
			System.out.println("ImagetoPNG inputfile [outputfile]");
			return;
		}
		File file = new File(path);
		if (args.length >= 2) {
			outpath = args[1];
		} else {
			outpath = path.substring(0, path.lastIndexOf(".")) + ".png";
		}
		if (path.compareToIgnoreCase(outpath) == 0) {
			System.out.println("Input file and Output file are same files");
			return;
		}
		System.out.println(path + " to " + outpath + " transrating...");
		try {
			FileInputStream in = new FileInputStream(file);
			Image img = new Image(in);
			PNGSaver.PNGWriteFile(outpath, img);
			System.out.println("Trancerate is success.");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.out.println("Trancerate is false.");
		}
	}
}
