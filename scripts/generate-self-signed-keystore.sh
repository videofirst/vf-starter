#!/bin/bash

# Script for generating self signed certificate (for local development).

KEY_FOLDER=../src/main/resources

rm ${KEY_FOLDER}/vf-starter-self-signed.p12

echo ==========================================================
echo Generate Self Signed KeyStore
echo ==========================================================
echo

echo " - 1) Generating Self-Signed Certificate + KeyStore (PKCS12 format - better than JKS format)"
keytool -genkey -storetype PKCS12 \
-alias selfsigned_localhost_sslserver \
-keyalg RSA -keysize 2048 -validity 3650 \
-dname "CN=localhost, OU=Engineering, O=Video First, L=Belfast, S=Antrim, C=GB" \
-noprompt -keypass changeit -storepass changeit \
-keystore ${KEY_FOLDER}/vf-starter-self-signed.p12
