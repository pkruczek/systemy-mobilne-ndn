package pl.edu.agh.sm.ndn.java.client;


import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;


public class Client {

    public static void main(String[] args) {
        Fetcher fetcher = new Fetcher();
        String data = fetcher.fetchData();
        System.out.println(">>>RESPONSE: " + data);
    }
}

class Fetcher {

    private String m_retVal;
    private Face m_face;
    private boolean m_shouldStop = false;

    String fetchData() {
        try {
            m_face = new Face("localhost");
            m_face.expressInterest(new Name("/ndn/test"),
                    new OnData() {
                        @Override
                        public void
                        onData(Interest interest, Data data) {
                            m_retVal = data.getContent().toString();
                            m_shouldStop = true;
                        }
                    },
                    new OnTimeout() {
                        @Override
                        public void onTimeout(Interest interest) {
                            m_retVal = "ERROR: Timeout";
                            m_shouldStop = true;
                        }
                    });

            while (!m_shouldStop) {
                m_face.processEvents();
                Thread.sleep(500);
            }
            m_face.shutdown();
            m_face = null;
            return m_retVal;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}