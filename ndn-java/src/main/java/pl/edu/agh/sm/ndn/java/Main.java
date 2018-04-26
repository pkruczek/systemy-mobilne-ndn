package pl.edu.agh.sm.ndn.java;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.util.Blob;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static final String NAME = "/demo/ping";

    public static void main(String[] args) {
        PKServer pkServer = new PKServer();
//        Future<Server> server = Executors.newSingleThreadExecutor().submit(Server::new);
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                server.get().close();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }));
    }

    private static void fetch() throws IOException {
        Face face = new Face("localhost");
        face.expressInterest(new Name("/ndn/test"), new OnData() {
            @Override
            public void onData(Interest interest, Data data) {
                System.out.println(Arrays.toString(data.getContent().getImmutableArray()));
            }
        }, new OnTimeout() {
            @Override
            public void onTimeout(Interest interest) {
                System.out.println("Timeout");
            }
        });
    }

    private static void register() throws IOException, SecurityException, PibImpl.Error, KeyChain.Error {
        KeyChain keyChain = new KeyChain();
        Face face = new Face("localhost");
        keyChain.setFace(face);
        face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
        Name name = new Name(NAME);
        face.registerPrefix(name,
                new OnInterestCallback() {
                    public void onInterest(Name name, Interest interest, Face face, long l, InterestFilter interestFilter) {
                        Data data = new Data(new Name(NAME));
                        Blob content = new Blob("Hello World".getBytes());
                        data.setContent(content);
                        try {
                            face.putData(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new OnRegisterFailed() {
                    public void onRegisterFailed(Name name) {
                        System.out.println("Register failed");
                    }
                }, new OnRegisterSuccess() {
                    public void onRegisterSuccess(Name name, long l) {
                        System.out.println("Register success");
                    }
                });
    }

}
