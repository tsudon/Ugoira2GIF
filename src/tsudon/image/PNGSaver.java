package tsudon.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PNGSaver implements PNG {

	static public void PNGWriteFile(String outpath, javafx.scene.image.Image img) throws IOException {
		File outFile = new File(outpath);
		if (!outFile.exists()) {
			FileOutputStream outStream = (new FileOutputStream(outpath));
			outStream.write(PNG.getPNGSignature());
			PNGChunk header = PNG.createIHDR((int) img.getWidth(), (int) img.getHeight(), true);
			header.writeChunk(outStream);

			PNGChunk dataarray[] = PNG.createIDATs(img, 65524, true);
			for (int i = 0; i < dataarray.length; i++) {
				dataarray[i].writeChunk(outStream);
			}
			PNGChunk eof = PNG.createIEND();
			eof.writeChunk(outStream);

			outStream.close();
		} else {
			throw new IOException("File is already exist.");
		}
	}

	static public void APNGWriteFile(String outpath, javafx.scene.image.Image[] imgs, int delays[]) throws IOException {
		File outFile = new File(outpath);
		int buffersize = 64 * 1024 - 12;
		if (imgs.length == 1) {
			PNGWriteFile(outpath, imgs[0]);
			return;
		}

		if (delays == null) {
			delays = new int[imgs.length];
			for (int i = 0; i < delays.length; i++) {
				delays[0] = 33;
			}
		} else if (imgs.length < delays.length) {
			System.err.println("delays array size is short.");
			return;
		}

		if (!outFile.exists()) {
			int width = (int) imgs[0].getWidth();
			int height = (int) imgs[0].getHeight();
			int seqNumber = 0;
			FileOutputStream outStream = (new FileOutputStream(outpath));
			outStream.write(PNG.getPNGSignature());

			PNGChunk header = PNG.createIHDR(width, height, true);
			header.writeChunk(outStream);

			PNGChunk actl = PNG.createACTL(imgs.length, 0);
			actl.writeChunk(outStream);
			PNGChunk fctl = PNG.createFCTL(seqNumber++, width, height, 0, 0, delays[0]);
			fctl.writeChunk(outStream);

			/*
			 * PNGChunk data =PNG.createIDAT(imgs[0]); data.writeChunk(outStream);
			 */
			PNGChunk dataarray[] = PNG.createIDATs(imgs[0], buffersize, true);
			for (int i = 0; i < dataarray.length; i++) {
				dataarray[i].writeChunk(outStream);
			}

			for (int i = 1; i < imgs.length; i++) {

				PNGChunk cfctl = PNG.createFCTL(seqNumber++, width, height, 0, 0, delays[i]);
				cfctl.writeChunk(outStream);
				/*
				 * PNGChunk cdata = PNG.createFDAT(seqNumber++,imgs[i],true);
				 * cdata.writeChunk(outStream);
				 */
				PNGChunk cdataarray[] = PNG.createFDATs(seqNumber, imgs[i], buffersize, true);
				seqNumber += cdataarray.length;
				for (int j = 0; j < cdataarray.length; j++) {
					cdataarray[j].writeChunk(outStream);
				}

			}

			PNGChunk eof = PNG.createIEND();
			eof.writeChunk(outStream);

			outStream.close();
		} else {
			throw new IOException("File is already exist.");
		}

	}
}
