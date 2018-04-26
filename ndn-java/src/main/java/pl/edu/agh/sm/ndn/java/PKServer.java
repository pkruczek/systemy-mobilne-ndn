package pl.edu.agh.sm.ndn.java;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.util.Blob;

import java.util.concurrent.Executors;

public class PKServer implements AutoCloseable {
    private final Face face;
    private final Name baseName;
    private final KeyChain keyChain;
    private long counter = 0;
    private boolean shouldStop = false;

    public static void main(String[] args) {
        PKServer pkserver = new PKServer();
    }


    public PKServer() {
        try {
            this.face = new ThreadPoolFace(Executors.newScheduledThreadPool(1), new TcpTransport(), new TcpTransport.ConnectionInfo("localhost", 6363));
            this.baseName = new Name("/ndn/test");
            this.keyChain = new KeyChain();

            this.face.setCommandSigningInfo(this.keyChain, this.keyChain.getDefaultCertificateName());
            this.face.registerPrefix(this.baseName, new OnInterestCallback() {
                @Override
                public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
                    System.out.println("jNDN " + interest.getName());
                    String content = String.format("jNDN LINE %d", counter);
                    counter++;

                    Data data = new Data(interest.getName());
                    MetaInfo meta = new MetaInfo();
                    meta.setFreshnessPeriod(5000);
                    data.setMetaInfo(meta);

                    data.setContent(new Blob(content.getBytes()));

                    try {
                        keyChain.sign(data, keyChain.getDefaultCertificateName());
                        face.putData(data);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    shouldStop = true;
                }
            }, new OnRegisterFailed() {
                @Override
                public void onRegisterFailed(Name prefix) {
                    System.out.println("jNDN: failed to register prefix");
                    shouldStop = true;
                }
            });
            System.out.println("Prefix registered");

            while (!shouldStop) {
                System.out.println("LOOP");
                face.processEvents();
                Thread.sleep(500);
            }
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onInterestCallback(Name name, Interest interest, Face face, long interestFilterId, InterestFilter interestFilter) {
        System.out.println("jNDN " + interest.getName());
        String content = String.format("jNDN LINE %d", counter);
        counter++;

        Data data = new Data(interest.getName());
        MetaInfo meta = new MetaInfo();
        meta.setFreshnessPeriod(5000);
        data.setMetaInfo(meta);

        data.setContent(new Blob(content.getBytes()));

        try {
            keyChain.sign(data, keyChain.getDefaultCertificateName());
            face.putData(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void onRegisterFailed(Name prefix) {
        System.out.println("jNDN: failed to register prefix");
    }

    @Override
    public void close() {
        System.out.println("Closing face");
        face.shutdown();
    }
}