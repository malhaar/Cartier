package xyz.vopen.cartier.commons.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author Zero.zhao
 */
public class Base64 {
    /** */
    private static final int BASELENGTH = 255;

    /** */
    private static final int LOOKUPLENGTH = 64;

    /** */
    private static final int TWENTYFOURBITGROUP = 24;

    /** */
    private static final int EIGHTBIT = 8;

    /** */
    private static final int SIXTEENBIT = 16;

    /** */
    // private static final int SIXBIT = 6;
    /** */
    private static final int FOURBYTE = 4;

    /**
     * The sign bit as an int
     */
    private static final int SIGN = -128;

    /**
     * The padding character
     */
    private static final byte PAD = (byte) '=';

    /**
     * The alphabet
     */
    private static final byte[] BASE64_ALPHABET = new byte[BASELENGTH];

    /**
     * The lookup alphabet
     */
    private static final byte[] LOOKUP_BASE64_ALPHABET = new byte[LOOKUPLENGTH];

    private static final byte[] encodingTable = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
            (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W',
            (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
            (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
            (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    static {

        for (int i = 0; i < BASELENGTH; i++) {
            BASE64_ALPHABET[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - 'a' + 26);
        }

        for (int i = '9'; i >= '0'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - '0' + 52);
        }

        BASE64_ALPHABET['+'] = 62;
        BASE64_ALPHABET['/'] = 63;

        for (int i = 0; i <= 25; i++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('a' + j);
        }

        for (int i = 52, j = 0; i <= 61; i++, j++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('0' + j);
        }
        LOOKUP_BASE64_ALPHABET[62] = (byte) '+';
        LOOKUP_BASE64_ALPHABET[63] = (byte) '/';

    }

    /**
     * Create an instance.
     */
    private Base64 () {
        // the constructor is intentionally private
    }

    public static String encodeToString (byte[] data) {
        if (null == data) {
            return null;
        }

        return new String(encodeInner(data));

    }

    public static String encodeToString (String data) {
        if (null == data) {
            return null;
        }
        return encodeToString(data.getBytes());
    }

    private static byte[] encodeInner (byte[] data) {
        byte[] bytes;

        int modulus = data.length % 3;
        if (modulus == 0) {
            bytes = new byte[4 * data.length / 3];
        } else {
            bytes = new byte[4 * ((data.length / 3) + 1)];
        }

        int dataLength = (data.length - modulus);
        int a1, a2, a3;
        for (int i = 0, j = 0; i < dataLength; i += 3, j += 4) {
            a1 = data[i] & 0xff;
            a2 = data[i + 1] & 0xff;
            a3 = data[i + 2] & 0xff;

            bytes[j] = encodingTable[(a1 >>> 2) & 0x3f];
            bytes[j + 1] = encodingTable[((a1 << 4) | (a2 >>> 4)) & 0x3f];
            bytes[j + 2] = encodingTable[((a2 << 2) | (a3 >>> 6)) & 0x3f];
            bytes[j + 3] = encodingTable[a3 & 0x3f];
        }

		/*
         * process the tail end.
		 */
        int b1, b2, b3;
        int d1, d2;

        switch (modulus) {
            case 0: /* nothing left to do */
                break;
            case 1:
                d1 = data[data.length - 1] & 0xff;
                b1 = (d1 >>> 2) & 0x3f;
                b2 = (d1 << 4) & 0x3f;

                bytes[bytes.length - 4] = encodingTable[b1];
                bytes[bytes.length - 3] = encodingTable[b2];
                bytes[bytes.length - 2] = (byte) '=';
                bytes[bytes.length - 1] = (byte) '=';
                break;
            case 2:
                d1 = data[data.length - 2] & 0xff;
                d2 = data[data.length - 1] & 0xff;

                b1 = (d1 >>> 2) & 0x3f;
                b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
                b3 = (d2 << 2) & 0x3f;

                bytes[bytes.length - 4] = encodingTable[b1];
                bytes[bytes.length - 3] = encodingTable[b2];
                bytes[bytes.length - 2] = encodingTable[b3];
                bytes[bytes.length - 1] = (byte) '=';
                break;
        }

        return bytes;
    }

    /**
     * Return true if the specified string is base64 encoded.
     *
     * @param isValidString
     *         The string to test.
     *
     * @return boolean True if the string is base64.
     */
    public static boolean isBase64 (String isValidString) {
        return isArrayByteBase64(getAsciiBytes(isValidString));
    }

    public static byte[] getAsciiBytes (final String data) {

        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }

        try {
            return data.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }

    /**
     * Return true if the specified octect is base64
     *
     * @param octect
     *         The octet to test.
     *
     * @return boolean True if the octect is base64.
     */
    static boolean isBase64 (byte octect) {
        // Should we ignore white space?
        return (octect == PAD || BASE64_ALPHABET[octect] != -1);
    }

    /**
     * Return true if the specified byte array is base64
     *
     * @param arrayOctect
     *         The array to test.
     *
     * @return boolean true if the specified byte array is base64
     */
    public static boolean isArrayByteBase64 (byte[] arrayOctect) {
        int length = arrayOctect.length;
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Base64.isBase64(arrayOctect[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData
     *         Array containing binaryData
     *
     * @return Base64-encoded array
     */
    public static byte[] encode (byte[] binaryData) {

        int lengthDataBits = binaryData.length * EIGHTBIT;
        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
        byte encodedData[] = null;

        if (fewerThan24bits != 0) { // data not divisible by 24 bit
            encodedData = new byte[(numberTriplets + 1) * 4];
        } else { // 16 or 8 bit
            encodedData = new byte[numberTriplets * 4];
        }

        byte k = 0;
        byte l = 0;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        for (i = 0; i < numberTriplets; i++) {

            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            encodedIndex = i * 4;
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);

            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
            encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[(l << 2) | val3];
            encodedData[encodedIndex + 3] = LOOKUP_BASE64_ALPHABET[b3 & 0x3f];
        }

        // form integral number of 6-bit groups
        dataIndex = i * 3;
        encodedIndex = i * 4;
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) (b1 & 0x03);
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
            encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[k << 4];
            encodedData[encodedIndex + 2] = PAD;
            encodedData[encodedIndex + 3] = PAD;
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
            encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[l << 2];
            encodedData[encodedIndex + 3] = PAD;
        }
        return encodedData;
    }

    /**
     * Decodes Base64 data into octects
     *
     * @param base64Data
     *         byte array containing Base64 data
     *
     * @return Array containing decoded data.
     */
    public static byte[] decode (byte[] base64Data) {
        // Should we throw away anything not in base64Data ?

        // handle the edge case, so we don't have to worry about it later
        if (base64Data == null || base64Data.length == 0) {
            return new byte[0];
        }

        int numberQuadruple = base64Data.length / FOURBYTE;
        byte decodedData[] = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

        int encodedIndex = 0;
        int dataIndex = 0;
        {
            // this block sizes the output array properly - rlw
            int lastData = base64Data.length;
            // ignore the '=' padding
            while (base64Data[lastData - 1] == PAD) {
                if (--lastData == 0) {
                    return new byte[0];
                }
            }
            decodedData = new byte[lastData - numberQuadruple];
        }

        for (int i = 0; i < numberQuadruple; i++) {
            dataIndex = i * 4;
            marker0 = base64Data[dataIndex + 2];
            marker1 = base64Data[dataIndex + 3];

            b1 = BASE64_ALPHABET[base64Data[dataIndex]];
            b2 = BASE64_ALPHABET[base64Data[dataIndex + 1]];

            if (marker0 != PAD && marker1 != PAD) { // No PAD e.g 3cQl
                b3 = BASE64_ALPHABET[marker0];
                b4 = BASE64_ALPHABET[marker1];

                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
            } else if (marker0 == PAD) { // Two PAD e.g. 3c[Pad][Pad]
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            } else if (marker1 == PAD) { // One PAD e.g. 3cQ[Pad]
                b3 = BASE64_ALPHABET[marker0];
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }

    /**
     * @param src
     *
     * @return
     */
    public static String encodeBuffer (byte[] src) {
        byte[] aim = encode(src);
        StringBuffer sbuf = new StringBuffer("");
        // add /r/n after every 76 chars. This function is same with
        // sun.misc.BASE64Encoder.encodeBuffer()
        for (int offset = 0; offset < aim.length; offset += 76) {
            int length = ((aim.length - offset) > 76) ? 76 : (aim.length - offset);
            try {
                sbuf.append(new String(aim, offset, length, "UTF-8")).append("\r\n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sbuf.toString();
    }

    /**
     * @param src
     *
     * @return
     */
    public static byte[] decodeBuffer (String src) {
        // remove /r/n in src. This function is same with
        // sun.misc.BASE64Decoder.decodeBuffer()
        src = src.replace("\r", "");
        src = src.replace("\n", "");
        byte[] data = null;
        try {
            data = src.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] aim = Base64.decode(data);
        return aim;
    }

    /**
     * @param src
     *
     * @return
     */
    public static String encodeBufferWithoutEnd (byte[] src) {
        byte[] aim = encode(src);
        StringBuffer sbuf = new StringBuffer("");
        // add /r/n after every 76 chars. This function is same with
        // sun.misc.BASE64Encoder.encodeBuffer()
        for (int offset = 0; offset < aim.length; offset += 76) {
            int length = ((aim.length - offset) > 76) ? 76 : (aim.length - offset);
            try {
                sbuf.append(new String(aim, offset, length, "UTF-8"));
                if (length == 76)
                    sbuf.append("\r\n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sbuf.toString();
    }
}
