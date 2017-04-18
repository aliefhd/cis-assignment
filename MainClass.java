import java.io.ByteArrayOutputStream;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.file.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Collections;

import java.lang.Math;


import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;


/**
 * TODO: CHANGE Files.readAllBytes method to a more commonly used method for handling large files.
 */
public class MainClass {

  // THESE STATIC VARIABLES ARE USED FOR TEST PURPOSES ONLY
  // ON LATER IMPLEMENTATION, USER MAY DECIDE THE NAME OF THE FILE USED
  // IN THE ENCRYPTION AND DECRYPTION PROCESSES.
  private static String TEST_FOLDER = "test/";
  private static String PLAINTEXT_FILE_NAME = TEST_FOLDER + "test.txt";
  private static String KEY_FILE_NAME       = TEST_FOLDER + "key/key";
  private static String ENCRYPTED_FILE_NAME = TEST_FOLDER + "encrypted";
  private static String DECRYPTED_FILE_NAME = TEST_FOLDER + "decrypted";

  private static String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
  private static String HASH_FILE_NAME_EXT   = "."+MESSAGE_DIGEST_ALGORITHM; 
  private static int blockSize = 4*1024*1024; //Block size for buffer, set to 4MB


  public static void main(String[] args) throws Exception {
    // Trying scanner, remove if not needed
    Scanner scan = new Scanner(System.in);
    System.out.print("Input file inside " + TEST_FOLDER + " folder (default test.txt): ");
    String file = scan.nextLine();
    if (file.equals("")) file = "test.txt";
    PLAINTEXT_FILE_NAME = TEST_FOLDER + file; 
    long startTime = System.nanoTime();
    testWithFile(); // Comment if not needed
    long duration = System.nanoTime() - startTime;
    System.out.println("Time lapsed : " + duration/1000000000.0 + "s");
  }

  // public static void bufferedEncrypt(String filenameOfPlaintext, String filenameOfKey) {
  //   try {
  //     Path keyFile = Paths.get(filenameOfKey);
  //     String keyString = new String(Files.readAllBytes(keyFile)); //Each character (0-9A-F) is read as one byte, not as 4-bit (a hex character).
  //     byte[] key = toByteArray(keyString);
  //     System.out.println("key : " + keyString);
  //     System.out.println("key length : " + keyString.length()  * 4 + "-bit");      

  //     // init Cipher
  //     byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00, 0x01 };
  //     SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
  //     IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
  //     Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
  //     cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

  //     // init I/OStream
  //     InputStream  fis = Files.newInputStream(Paths.get(filenameOfPlaintext));
  //     OutputStream fos = Files.newOutputStream(Paths.get(ENCRYPTED_FILE_NAME));
  //     MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
  //     DigestInputStream dis = new DigestInputStream(fis, md); // To hash the plaintext, decorate the inputstream

  //     // ENCRYPTION
  //     System.out.println("Encrypting...");
  //     byte[] buffer = new byte[blockSize];
  //     byte[] cipherBlock = new byte[cipher.getOutputSize(buffer.length)];
  //     int ch; //noBytes
  //     int cipherBytes;

  //     long bytesRead  = 0;
  //     long totalBytes = new File(filenameOfPlaintext).length();
  //     while ((ch = dis.read(buffer)) != -1) {
  //       cipherBytes = cipher.update(buffer, 0, ch, cipherBlock);
  //       fos.write(cipherBlock,0,cipherBytes);
  //       bytesRead += blockSize;
  //       bytesRead = Math.min(totalBytes, bytesRead);
  //       printProgress(totalBytes,bytesRead);
  //     }
  //     System.out.println("");
  //     cipherBytes = cipher.doFinal(cipherBlock,0);
  //     fos.write(cipherBlock,0,cipherBytes);

  //     // Create checksum of plaintext
  //     System.out.println("Encrypting done.");

  //     createChecksumFile(md, PLAINTEXT_FILE_NAME + HASH_FILE_NAME_EXT);

  //     fos.flush();
  //     fos.close();
  //     dis.close();
  //     fis.close();
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  // }

  // public static void bufferedDecrypt(String filenameOfCiphertext, String filenameOfKey) {
  //   try {
  //     Path keyFile = Paths.get(filenameOfKey);
  //     String keyString = new String(Files.readAllBytes(keyFile)); //Each character (0-9A-F) is read as one byte, not as 4-bit (a hex character).
  //     byte[] key = toByteArray(keyString);
  //     System.out.println("key : " + keyString);
  //     System.out.println("key length : " + keyString.length()  * 4 + "-bit");      

  //     // init Cipher
  //     byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00, 0x01 };
  //     SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
  //     IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
  //     Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
  //     cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

  //     // init I/OStream
  //     InputStream  fis = Files.newInputStream(Paths.get(filenameOfCiphertext));
  //     OutputStream fos = Files.newOutputStream(Paths.get(DECRYPTED_FILE_NAME));
  //     MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
  //     DigestOutputStream dos = new DigestOutputStream(fos, md); // To hash the deecrypted, decorate the outputstream

  //     // DECRYPTION
  //     System.out.println("Decrypting...");
  //     byte[] buffer = new byte[blockSize];
  //     byte[] cipherBlock = new byte[cipher.getOutputSize(buffer.length)];
  //     int ch; //noBytes
  //     int cipherBytes;

  //     long bytesRead  = 0;
  //     long totalBytes = new File(filenameOfCiphertext).length();      
  //     while ((ch = fis.read(buffer)) != -1) {
  //       cipherBytes = cipher.update(buffer, 0, ch, cipherBlock);
  //       dos.write(cipherBlock,0,cipherBytes);
  //       bytesRead += blockSize;
  //       bytesRead = Math.min(totalBytes, bytesRead);
  //       printProgress(totalBytes,bytesRead);
  //     }
  //     System.out.println("");
  //     cipherBytes = cipher.doFinal(cipherBlock,0);
  //     dos.write(cipherBlock,0,cipherBytes);
  //     System.out.println("Decrypting finished .");

  //     // Create checksum of decrypted
  //     createChecksumFile(md, DECRYPTED_FILE_NAME + HASH_FILE_NAME_EXT);

  //     dos.flush();
  //     dos.close();
  //     fos.close();
  //     fis.close();
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  // }

  public static void bufferedEncrypt(File plaintextFile, File keyFile) {
    try {
      String keyString = new String(Files.readAllBytes(keyFile.toPath()));
      byte[] key = toByteArray(keyString);
      System.out.println("key : " + keyString);
      System.out.println("key length : " + keyString.length()  * 4 + "-bit");      

      // init Cipher
      byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00, 0x01 };
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
      Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

      // init I/OStream
      InputStream  fis = Files.newInputStream(plaintextFile.toPath());
      OutputStream fos = Files.newOutputStream(Paths.get(ENCRYPTED_FILE_NAME));
      MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
      DigestInputStream dis = new DigestInputStream(fis, md); // To hash the plaintext, decorate the inputstream

      // ENCRYPTION
      System.out.println("Encrypting...");
      byte[] buffer = new byte[blockSize];
      byte[] readBuffer = new byte[blockSize];
      byte[] cipherBlock = new byte[cipher.getOutputSize(buffer.length)];
      int ch; //current read
      int ch2; //next read
      int cipherBytes;
      boolean padOnNewUpdate = false;

      long bytesRead  = 0;
      long totalBytes = plaintextFile.length();
      ch = dis.read(buffer);
      while (ch != -1) {
        System.arraycopy(buffer,0,readBuffer,0,buffer.length);

        ch2 = dis.read(buffer);
        if (ch2 == -1) { //ch is last read, need to pad
          if (ch == buffer.length) { // need to pad on new cipher.update,
            padOnNewUpdate = true;
          } else {
            readBuffer = padding(ch, readBuffer);
            ch = ((ch / 16) * 16) + 16;
            System.out.println("CH: " + ch);
          }
        }
        
        cipherBytes = cipher.update(readBuffer, 0, ch, cipherBlock);
        fos.write(cipherBlock,0,cipherBytes);
        bytesRead += blockSize;
        bytesRead = Math.min(totalBytes, bytesRead);
        printProgress(totalBytes,bytesRead);
        // Last Update
        if (padOnNewUpdate) {
          readBuffer = new byte[16];
          readBuffer = padding(0, readBuffer);
          ch = 16;
          cipherBytes = cipher.update(readBuffer, 0, ch, cipherBlock);
          fos.write(cipherBlock,0,cipherBytes);   
        }
        // For next iteration
        ch = ch2;
      }
      System.out.println("");
      cipherBytes = cipher.doFinal(cipherBlock,0);
      fos.write(cipherBlock,0,cipherBytes);

      // Create checksum of plaintext
      System.out.println("Encrypting done.");

      createChecksumFile(md, PLAINTEXT_FILE_NAME + HASH_FILE_NAME_EXT);

      fos.flush();
      fos.close();
      dis.close();
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void bufferedDecrypt(File ciphertextFile, File keyFile) {
    try {
      String keyString = new String(Files.readAllBytes(keyFile.toPath()));
      byte[] key = toByteArray(keyString);
      System.out.println("key : " + keyString);
      System.out.println("key length : " + keyString.length()  * 4 + "-bit");      

      // init Cipher
      byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00, 0x01 };
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
      Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

      // init I/OStream
      InputStream  fis = Files.newInputStream(ciphertextFile.toPath());
      OutputStream fos = Files.newOutputStream(Paths.get(DECRYPTED_FILE_NAME));
      MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
      DigestOutputStream dos = new DigestOutputStream(fos, md); // To hash the deecrypted, decorate the outputstream

      // DECRYPTION
      System.out.println("Decrypting...");
      byte[] buffer = new byte[blockSize];
      byte[] readBuffer = new byte[blockSize];
      byte[] cipherBlock = new byte[cipher.getOutputSize(buffer.length)];
      int ch; //current read
      int ch2; //next read
      int cipherBytes;

      long bytesRead  = 0;
      long totalBytes = ciphertextFile.length();
      ch = fis.read(buffer);      
      while (ch != -1) {
        System.arraycopy(buffer,0,readBuffer,0,buffer.length);
        cipherBytes = cipher.update(buffer, 0, ch, cipherBlock);

        ch2 = fis.read(buffer);
        if (ch2 == -1) { //ch is last read, need to remove pad
          System.out.println("BEFORE CB: " + cipherBytes);
          cipherBytes = gniddap(cipherBytes, cipherBlock);
          System.out.println("AFTER CB: " + cipherBytes);
        }
        
        dos.write(cipherBlock,0,cipherBytes);
        bytesRead += blockSize;
        bytesRead = Math.min(totalBytes, bytesRead);
        printProgress(totalBytes,bytesRead);
        ch = ch2;
      }
      System.out.println("");
      cipherBytes = cipher.doFinal(cipherBlock,0);
      dos.write(cipherBlock,0,cipherBytes);
      System.out.println("Decrypting finished .");

      // Create checksum of decrypted
      createChecksumFile(md, DECRYPTED_FILE_NAME + HASH_FILE_NAME_EXT);

      dos.flush();
      dos.close();
      fos.close();
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void createChecksumFile(MessageDigest md, String filename) {
    try {
        System.out.println("Creating checksum file " + filename + "...");
        byte[] inputMD = md.digest();
        OutputStream fOut = Files.newOutputStream(Paths.get(filename));
        fOut.write(inputMD);
        System.out.println("Checksum file created.");
    } catch (Exception e) {
        System.out.println("Checksum file was not created.");
        e.printStackTrace();
    }
  }  

  public static void checkContent(String filenameOfPlaintextMD, String filenameOfDecryptedMD) {
    System.out.println("Decrypted file has the same content as the Plaintext file : " + isContentEqual(filenameOfPlaintextMD, filenameOfDecryptedMD));
  }

  public static boolean isContentEqual(String filenameOfPlaintextMD, String filenameOfDecryptedMD) {
    Path plaintextMDFile, decryptedMDFile;
    byte[] plaintextMD = new byte[] {0}, decryptedMD = new byte[] {1};
    try {
        plaintextMDFile = Paths.get(filenameOfPlaintextMD);
        decryptedMDFile = Paths.get(filenameOfDecryptedMD);

        plaintextMD = Files.readAllBytes(plaintextMDFile);
        decryptedMD = Files.readAllBytes(decryptedMDFile);

    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println("checksum of plaintext (hex) : " + toHexString(plaintextMD));
    System.out.println("checksum of decrypted (hex) : " + toHexString(decryptedMD));
    boolean fileNotFound = Arrays.equals(plaintextMD, new byte[] {0}) || Arrays.equals(decryptedMD, new byte[] {1});
    return (!fileNotFound) && isContentEqual(plaintextMD, decryptedMD); // If fileNotFound: return false, else return isContentEqual.
  }

  public static boolean isContentEqual(byte[] plaintextMD, byte[] decryptedMD) {
    return MessageDigest.isEqual(plaintextMD, decryptedMD);
  }

  public static String toHexString(byte[] array) {
    return DatatypeConverter.printHexBinary(array);
  }

  public static byte[] toByteArray(String s) {
    return DatatypeConverter.parseHexBinary(s);
  }

  // // Method to test encryption and decryption processes.
  // public static void testWithString(){
  //   String filenameOfPlaintext = PLAINTEXT_FILE_NAME;
  //   String filenameOfKey = KEY_FILE_NAME + 128; // Change to 128, 192, 256

  //   bufferedEncrypt(filenameOfPlaintext, filenameOfKey);
  //   bufferedDecrypt(ENCRYPTED_FILE_NAME, filenameOfKey);
  //   checkContent(PLAINTEXT_FILE_NAME + HASH_FILE_NAME_EXT, DECRYPTED_FILE_NAME + HASH_FILE_NAME_EXT);
  // }

  // Method to test encryption and decryption processes.
  public static void testWithFile(){
    String filenameOfPlaintext = PLAINTEXT_FILE_NAME;
    String filenameOfKey = KEY_FILE_NAME + 128; // Change to 128, 192, 256

    File plaintextFile = new File(PLAINTEXT_FILE_NAME);
    File keyFile = new File(KEY_FILE_NAME + 128);
    File ciphertextFile = new File(ENCRYPTED_FILE_NAME);
    bufferedEncrypt(plaintextFile, keyFile);
    bufferedDecrypt(ciphertextFile, keyFile);
    checkContent(PLAINTEXT_FILE_NAME + HASH_FILE_NAME_EXT, DECRYPTED_FILE_NAME + HASH_FILE_NAME_EXT);
  }

  private static void printProgress(long total, long current) {
    StringBuilder string = new StringBuilder(140);   
    int percent = (int) (current * 100 / total);
    string
        .append('\r')
        .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
        .append(String.format(" %d%% [", percent))
        .append(String.join("", Collections.nCopies(percent, "=")))
        .append('>')
        .append(String.join("", Collections.nCopies(100 - percent, " ")))
        .append(']')
        .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
        .append(String.format(" %d/%d", current, total));

    System.out.print(string);
  }

  private static byte[] padding(int bytesRead, byte[] buffer) {
    int padlength = 16 - (bytesRead % 16);
    if (padlength == 0 ) {
      padlength = 16;
    }
    byte[] pad = createPadArray(padlength);
    System.arraycopy(pad, 0, buffer, bytesRead, pad.length);
    return buffer;
  }

  // Remove padding from decrypted
  private static int gniddap(int bytesRead, byte[] buffer) {
    //get last byte read from buffer
    byte lastByte = buffer[bytesRead - 1];
    //get supposed length of pad
    int supposedPadLength = (int) lastByte;
    if (supposedPadLength > 16 || supposedPadLength < 1) {
      System.out.println("NOTNEEDED");
      return bytesRead;
    }
    byte[] lastBytes = new byte[supposedPadLength];
    System.arraycopy(buffer,bytesRead - supposedPadLength, lastBytes, 0, supposedPadLength);
    System.out.println("LBS: " + toHexString(lastBytes));
    if(Arrays.equals(createPadArray(supposedPadLength), lastBytes)) {
      return bytesRead - supposedPadLength;
    }
    return bytesRead;
  }

  private static byte[] createPadArray(int length) {
    byte padByte = (byte) length;
    byte[] arr = new byte[length];
    Arrays.fill(arr,padByte);
    return arr;
  }

} 