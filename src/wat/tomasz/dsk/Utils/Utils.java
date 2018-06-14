package wat.tomasz.dsk.Utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;


public class Utils {
	
	public static boolean isValidAddress(String address) {
		if(address == null || address == "" || address.length() < 8)
			return false;
		
		try {
			if (InetAddress.getByName(address) instanceof Inet6Address )
				return false;			
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidPort(String port) {
		if(port == null || port == "")
			return false;
		
		try {
			int intPort = Integer.parseInt(port);
			if(intPort <= 0 || intPort >= 65534)
				return false;		
		} catch(NumberFormatException e) {
			return false;
		}	
		return true;
	}
	
	public static int getInt(String value) {
		int intValue = 0;
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {}
		return intValue;
	}
	
	public static int getPort(String port) {
		return getInt(port);
	}
	
	public static InetAddress getAddress(String address) {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(address);
		} catch (Exception e) {}
		return inetAddress;
	}
	
	public static PrivateKey getPrivateKeyFromString(String key64) {
	    byte[] clear = Utils.base64Decode(key64);
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
	    KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("DSA");
		    PrivateKey priv = fact.generatePrivate(keySpec);
		    Arrays.fill(clear, (byte) 0);
		    return priv;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static PublicKey getPublicKeyFromString(String stored) {
	    byte[] data = Utils.base64Decode(stored);
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
	    KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("DSA");
		    return fact.generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getPrivateKeyString(PrivateKey priv) {
	    KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("DSA");
		    PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
		            PKCS8EncodedKeySpec.class);
		    byte[] packed = spec.getEncoded();
		    String key64 = Utils.base64Encode(packed);

		    Arrays.fill(packed, (byte) 0);
		    return key64;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static String getPublicKeyString(PublicKey pub) {
	    KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("DSA");
		    X509EncodedKeySpec spec = fact.getKeySpec(pub,
		            X509EncodedKeySpec.class);
		    return Utils.base64Encode(spec.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte [] base64Decode(String message) {
		return Base64.getDecoder().decode(message);
	}
	
	public static String base64Encode(byte [] message) {
		return Base64.getEncoder().encodeToString(message);
	}
}
