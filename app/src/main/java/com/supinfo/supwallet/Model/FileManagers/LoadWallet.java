package com.supinfo.supwallet.Model.FileManagers;


import android.util.Log;

import com.supinfo.supwallet.Model.Utils.AndroidStringUtil;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import static com.supinfo.supwallet.Model.Utils.AndroidStringUtil.getPublicKeyFromString;
import static com.supinfo.supwallet.Model.Utils.AndroidStringUtil.getStringFromKey;


public class LoadWallet extends Thread {
    private CompletionHandler<Boolean> success;
    public LoadWallet(CompletionHandler<Boolean> success) {
        this.success = success;
    }

    @Override
    public void run() {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        try {

            //we should also load a stored wallet, TARGET find a way to store and retreive files on android
            //create a new wallet if none are found
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","SC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            dumpKeyPair(keyPair);
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
            success.onResponse(false,e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        success.onResponse(true,null);
    }
    public void dumpKeyPair(KeyPair keyPair) {
        PublicKey pub = keyPair.getPublic();
        Log.i("WALLET-KEY","Public Key: " +getStringFromKey(pub));

        PrivateKey priv = keyPair.getPrivate();
        Log.i("WALLET-KEY","Private Key: " + getStringFromKey(priv));

        PublicKey fromstringPublicKey = getPublicKeyFromString(getStringFromKey(pub));
        Log.i("WALLET-KEY","Public Key from the string key: " + getStringFromKey(fromstringPublicKey));

        try {
            PublicKey publicKeyFromPrivateKey = AndroidStringUtil.derivePublicKey(priv);
            Log.i("WALLET-KEY","Public Key from the Private key: " + getStringFromKey(publicKeyFromPrivateKey));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }







}
