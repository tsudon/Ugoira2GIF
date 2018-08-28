package tsudon.image;

/* This class is not debug because I think useless. */

public class PNGPallet {
	int size;
	byte[] red = new byte[255];
	byte[] green = new byte[255];
	byte[] blue = new byte[255];

	public void setPalletSize(int size) {
		if (size >= 0 && size <= 255) {
			this.size = size;
		} else {
			this.size = 256;
		}
	}

	public void setPallet(int number, byte R, byte G, byte B) {
		if (number >= 0 && number <= 255) {
			this.red[number] = R;
			this.green[number] = G;
			this.blue[number] = B;
		}
	}

	public byte getPalletRed(int number) {
		if (number >= 0 && number <= 255) {
			return this.red[number];
		}
		return -1;
	}

	public byte getPalletGreen(int number) {
		if (number >= 0 && number <= 255) {
			return this.green[number];
		}
		return -1;
	}

	public byte getPalletBlue(int number) {
		if (number >= 0 && number <= 255) {
			return this.blue[number];
		}
		return -1;
	}

	public byte[] getPalletRGB(int number) {
		if (number >= 0 && number <= 255) {
			return new byte[] { this.red[number], this.green[number], this.blue[number] };
		}
		return null;
	}

	public byte[] getPalletAll() {
		byte[] pallet = new byte[this.size * 3];
		for (int i = 0; i < this.size; i++) {
			int pos = i * 3;
			pallet[pos] = this.red[i];
			pallet[pos + 1] = this.green[i];
			pallet[pos + 2] = this.blue[i];
		}
		return pallet;
	}

	public void setPalletAll(byte[] pallet) {
		if (pallet.length < this.size * 3)
			return; // buffer check;
		for (int i = 0; i < this.size; i++) {
			int pos = i * 3;
			this.red[i] = pallet[pos];
			this.green[i] = pallet[pos + 1];
			this.blue[i] = pallet[pos + 2];
		}
	}

	static public PNGChunk createPalletChunk(int size, byte[] pallet) {
		PNGChunk chunk = new PNGChunk(ChunkTYPE.PLTE);
		chunk.setLength(size * 3);
		byte[] buffer = chunk.getBuffer();
		System.arraycopy(pallet, 0, buffer, 0, size * 3);
		;
		return chunk;

	}

}
