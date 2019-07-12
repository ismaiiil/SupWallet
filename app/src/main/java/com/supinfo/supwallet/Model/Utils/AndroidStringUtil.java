package com.supinfo.supwallet.Model.Utils;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECPoint;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AndroidStringUtil {
    //Applies ECDSA Signature and returns the result ( as bytes ).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "SC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }
//    //Verifies a String signature
//    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
//        try {
//            Signature ecdsaVerify = Signature.getInstance("ECDSA", "SC");
//            ecdsaVerify.initVerify(publicKey);
//            ecdsaVerify.update(data.getBytes());
//            return ecdsaVerify.verify(signature);
//        }catch(Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static String getStringFromKey(Key key) {
        return android.util.Base64.encodeToString(key.getEncoded(), android.util.Base64.DEFAULT);
    }

    public static PublicKey getPublicKeyFromString(String stringkey){
        //converting string to Bytes
        byte[] byte_pubkey  = android.util.Base64.decode(stringkey, android.util.Base64.DEFAULT);
        System.out.println("BYTE KEY::" + byte_pubkey);


        //converting it back to public key
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("ECDSA", "SC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  (factory != null ? factory.generatePublic(new X509EncodedKeySpec(byte_pubkey)) : null);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKeyFromString(String stringkey){
        //converting string to Bytes
        byte[] byte_privkey  = android.util.Base64.decode(stringkey, android.util.Base64.DEFAULT);
        System.out.println("BYTE KEY::" + byte_privkey);


        //converting it back to public key
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("ECDSA", "SC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    byte_privkey);
            return  (factory != null ? factory.generatePrivate(privateKeySpec) : null);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PublicKey derivePublicKey(PrivateKey privateKey) throws InvalidKeySpecException {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "SC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");

        ECPoint Q = ecSpec.getG().multiply(((org.spongycastle.jce.interfaces.ECPrivateKey) privateKey).getD());

        ECPublicKeySpec pubSpec = new ECPublicKeySpec(Q, ecSpec);
        return keyFactory.generatePublic(pubSpec);
    }
}
