package tsudon.image;

//from PNG sample

public class CRC {

	private long[] CRCTable = new long[256];

	public CRC() {
//initialize CRC table;
		for (int n = 0; n < 256; n++) {
			long c = n;
			for (int k = 0; k < 8; k++) {
				if ((c & 1) == 1) {
					c = (0xedb88320L ^ (c >> 1));
				} else {
					c = c >> 1;
				}
			}
			CRCTable[n] = c;
		}
	}

	private long updateCRC(long crc, byte buf[], int length, ChunkTYPE type) {
		long c = crc;
		byte header[] = type.toString().getBytes();
		for (int n = 0; n < 4; n++) {
			c = CRCTable[(int) (c ^ header[n]) & 0x00ff] ^ (c >> 8);
		}
		for (int n = 0; n < length; n++) {
			c = CRCTable[(int) (c ^ buf[n]) & 0x00ff] ^ (c >> 8);
		}
		return c;
	}

	public long createCRC(byte buf[], int length, ChunkTYPE type) {
		return updateCRC(0xffffffffL, buf, length, type) ^ 0xffffffffL;
	}
}
