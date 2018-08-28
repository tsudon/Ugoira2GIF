package tsudon.image;

public enum ChunkTYPE {
// must chunks 
	IHDR, // Information header
	PLTE, // Color pallet
	IDAT, // Image Data
	IEND, // END of FILE
// APNG Chunks
	acTL, // Animation Control
	fcTL, // Frame Control
	fdAT, // Frame Data

// must before PLTE and IDAT
	cHRM, tRNS, gAMA, // Gamma scale
	sRGB, // sRPG
// between PLTE and IDAT
	iCCP, bKGD,
// before IDAT
	pHYs, hIST,
// non constrains 
	tIME, // modify time - only single chunk
// Multiple chunk OK
	sPLT, tEXt, // TEXT
	iTXt, // i18n TEXT
	zTXt // Archived TEXT
	;
}
