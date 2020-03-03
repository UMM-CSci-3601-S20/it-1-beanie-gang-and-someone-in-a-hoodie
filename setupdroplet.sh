#!/usr/bin/env bash
if [[ ! -e /swapfile ]]; then
  echo "/swapfile does not exist, setting up swap"
  # Set up swap space
  fallocate -l 3G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  echo '/swapfile none swap sw 0 0' | tee -a /etc/fstab
  echo 'vm.swappiness=10' >> /etc/sysctl.conf
else
  echo "/swapfile already exists, skipping swap setup"
fi


ip="$(curl -s http://169.254.169.254/metadata/v1/interfaces/public/0/ipv4/address)"
domain="${ip}.nip.io"

echo
echo "Setting APP_HOST to ${domain}"
echo "APP_HOST=${domain}" > .env

echo
echo "Your site will be served over HTTPS automatically using Let's Encrypt."
echo "By continuing, you agree to the Let's Encrypt Subscriber Agreement at:"
echo "https://letsencrypt.org/documents/2017.11.15-LE-SA-v1.2.pdf"
while true; do
    read -p "Do you agree to the terms? (y/n)" yn
    case $yn in
        [Yy]* ) agreed=true; break;;
        [Nn]* ) agreed=false; break;;
        * ) echo "Please answer yes or no.";;
    esac
done
echo "Setting APP_ACME_AGREE to ${agreed}"
echo "APP_ACME_AGREE=${agreed}" >> .env

if [ "${agreed}" = false ]; then
  echo "TLS (HTTPS) will be disabled"
  echo
  echo "Your server is setup"
  echo "Once you start it with 'docker-compose up -d' it will be available at:"
  echo "http://${domain}"
else
  echo
  echo "Please enter your email address to signify agreement and to be notified"
  echo "in case of issues. This will be used by Let's Encrypt."
  read -p "Email address: " email
  echo "Setting APP_TLS_EMAIL to ${email}"
  echo "APP_TLS_EMAIL=${email}" >> .env
  echo
  echo "Your server is setup"
  echo "Once you start it with 'docker-compose up -d' it will be available at:"
  echo "https://${domain}"
fi
