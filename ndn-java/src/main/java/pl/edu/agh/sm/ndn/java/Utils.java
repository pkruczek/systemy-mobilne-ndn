package pl.edu.agh.sm.ndn.java;

import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.der.DerDecodingException;
import net.named_data.jndn.security.*;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.certificate.IdentityCertificate;
import net.named_data.jndn.security.identity.*;

public class Utils {

    public static KeyChain buildTestKeyChain() throws net.named_data.jndn.security.SecurityException {
        MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
        MemoryPrivateKeyStorage privateKeyStorage = new MemoryPrivateKeyStorage();
        IdentityManager identityManager = new IdentityManager(identityStorage, privateKeyStorage);
        KeyChain keyChain = new KeyChain(identityManager);
        try {
            keyChain.getDefaultCertificateName();
        } catch (net.named_data.jndn.security.SecurityException e) {
            keyChain.createIdentity(new Name("/test/identity"));
            keyChain.getIdentityManager().setDefaultIdentity(new Name("/test/identity"));
        }
        return keyChain;
    }

    public static KeyChain buildFileKeyChain() throws SecurityException {
        MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
        PrivateKeyStorage privateKeyStorage = new FilePrivateKeyStorage("/home/piotrek/Dokumenty/Studia/Mobilne/ndn-java/key-storage");
        IdentityManager identityManager = new IdentityManager(identityStorage, privateKeyStorage);
        KeyChain keyChain = new KeyChain(identityManager);
        Name defaultIdentity = new Name("/test/identity");
        try {
            IdentityCertificate certificate = keyChain.getCertificate(defaultIdentity);
            System.out.println(certificate);
        } catch (DerDecodingException e) {
            e.printStackTrace();
        }
//        privateKeyStorage.generateKeyPair(defaultIdentity, new RsaKeyParams());
//        keyChain.createIdentity(defaultIdentity);
        keyChain.getIdentityManager().setDefaultIdentity(defaultIdentity);
        return keyChain;
    }

}
