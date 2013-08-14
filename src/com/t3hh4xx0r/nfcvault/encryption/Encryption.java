package com.t3hh4xx0r.nfcvault.encryption;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class Encryption 
{
	//Public for static/non-static conversion
	public Encryption(){}
	
	//String for Encryption Errors
	public static String encryptionError = "";
	
	//==========================================================
    // I/O Methods
	//==========================================================
	
	//Decrypt String
	public static String decryptString(String code, String key)
	{
		//Create new instance
		Encryption cryClass = new Encryption();
		
		//Return String place holder
		String returnString = "";
		
		try 
		{
			returnString = cryClass.decrypt(code, key);
		} 
		catch (KeyException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (InvalidAlgorithmParameterException ex) 
		{
			///Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (IllegalBlockSizeException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (BadPaddingException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (GeneralSecurityException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (IOException ex)
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		}
		
		if (returnString.length() == 0) {
			return encryptionError;
		} else {
			return returnString;
		}
	}
	
	//Encrypt String
	public static String encryptString(String code, String key)
	{
		//Create new instance
		Encryption cryClass = new Encryption();
		
		//Return String
		String returnString = "Null";
		
		try 
		{
			returnString = cryClass.encrypt(code, key);
		} 
		catch (KeyException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (InvalidAlgorithmParameterException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (IllegalBlockSizeException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (BadPaddingException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (GeneralSecurityException ex)
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		} 
		catch (IOException ex) 
		{
			//Set encryptionError = to Exception
			encryptionError = ex.toString();
		}
		
		return returnString;
		
	}

	
	//==========================================================
	
	//==========================================================
    // Encryption Variables 
	//==========================================================
	
	private final String characterEncoding = "UTF-8";
	private final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final String aesEncryptionAlgorithm = "AES";
	
	//==========================================================
	
	//==========================================================
    // Byte-Level Methods
	//==========================================================
	
	public  byte[] decrypt(byte[] cipherText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

	public byte[] encrypt(byte[] plainText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		return plainText;
	}

	private byte[] getKeyBytes(String key) throws UnsupportedEncodingException
	{
		byte[] keyBytes= new byte[16];
		byte[] parameterKeyBytes= key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	//==========================================================
	
	//==========================================================
    // Encrypt/Decrypt Methods
	//==========================================================
	
	private String encrypt(String plainText, String key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] plainTextbytes = plainText.getBytes(characterEncoding);
		byte[] keyBytes = getKeyBytes(key);
		return Base64.encodeToString(encrypt(plainTextbytes,keyBytes, keyBytes), Base64.DEFAULT);
	}

	private String decrypt(String encryptedText, String key) throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
		byte[] keyBytes = getKeyBytes(key);
		return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
	}

	//==========================================================

}
