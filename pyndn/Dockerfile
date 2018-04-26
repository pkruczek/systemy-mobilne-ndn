FROM ubuntu:16.04

RUN apt-get update && apt-get install -y software-properties-common && add-apt-repository ppa:named-data/ppa && apt-get update && apt-get install -y python python-pip nfd && pip install pyndn

RUN mkdir -p /ndn
ADD server.py /ndn/
ADD client.py /ndn/
RUN ndnsec-keygen /ndn && ndnsec-sign-req /ndn | ndnsec-install-cert -
WORKDIR /ndn
