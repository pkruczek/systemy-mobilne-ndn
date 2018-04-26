FROM ubuntu:16.04

RUN apt-get update && apt-get install -y software-properties-common && add-apt-repository ppa:named-data/ppa && apt-get update && apt-get install -y default-jdk nfd

RUN mkdir -p /ndn
RUN ndnsec-keygen /ndn && ndnsec-sign-req /ndn | ndnsec-install-cert -
WORKDIR /ndn
