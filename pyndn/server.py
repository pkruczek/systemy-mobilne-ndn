import pyndn as ndn
import pyndn.security as ndnsec
import sys
try:
    import asyncio
except ImportError:
    import trollius as asyncio
from pyndn.threadsafe_face import ThreadsafeFace
import logging
logging.basicConfig()

class Server:
    def __init__(self, face):
        self.face = face
        self.baseName = ndn.Name("/ndn/test")
        self.counter = 0
        self.keyChain = ndnsec.KeyChain()

        self.face.setCommandSigningInfo(self.keyChain, self.keyChain.getDefaultCertificateName())
        self.face.registerPrefix(self.baseName,
                                 self._onInterest, self._onRegisterFailed)

    def _onInterest(self, prefix, interest, *k):
        print >> sys.stderr, "<< PyNDN %s" % interest.name
                
        content = "PyNDN LINE #%d\n" % self.counter
        self.counter += 1

        data = ndn.Data(interest.getName())

        meta = ndn.MetaInfo()
        meta.setFreshnessPeriod(5000)
        data.setMetaInfo(meta)

        data.setContent(content)

        self.keyChain.sign(data, self.keyChain.getDefaultCertificateName())
        
        self.face.putData(data)

    def _onRegisterFailed(self, prefix):
        print >> sys.stderr, "<< PyNDN: failed to register prefix"

if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    face = ThreadsafeFace(loop, None)
    server = Server(face)

    loop.run_forever()
face.shutdown()
