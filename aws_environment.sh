#!/bin/bash

sudo yum update -y

# iptables
sudo yum install iptables
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8082

# docker
sudo yum install -y docker 
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user
sudo docker-compose up
sudo chmod 777 /var/run/docker.sock
sudo chkconfig docker on
