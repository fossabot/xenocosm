#!/bin/sh

openssl aes-256-cbc -K $encrypted_d7ea611a93f7_key -iv $encrypted_d7ea611a93f7_iv -in travis-deploy-key.enc -out travis-deploy-key -d
chmod 600 travis-deploy-key
cp travis-deploy-key ~/.ssh/id_rsa
