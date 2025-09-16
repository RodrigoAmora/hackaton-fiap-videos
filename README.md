# hackaton-fiap-videos
Descrição
---------
Micro-Serviço de processamento de vídeos da fase 5 do Tech Challenge da pós de Arquitetura de Software da FIAP.

Participantes
-------------
* Bruno do Amor Divino da Paixão - RM360643
* Lucas Matheus Testa - RM360642
* Rodrigo de Lima Amora de Freitas - RM360219

Dependências
------------
O projeto usa o Java 17 e as seguintes dependências:

* Spring Boot 3.4.5
* Spring Security
* Jakarta Mail
* Lombok
* Devtools
* Redis
* MySQL
* H2
* Swagger
* OpenAPI
* Feign
* RabbitMQ
* jUnit
* Mockito

Documentação da API
-------------------
A documentação da API pode ser vista através do Swagger e do Redoc.<br>

<b>Documentação da API via Swagger:</b>
```shell script
http://localhost:8082/swagger
```

<b>Documentação da API via Redoc:</b>
```shell script
http://localhost:8082/redoc
```

##
Na pasta <b>`Postman`</b> contém a collection para usar os endpoints via Postman.

Monitoração do projeto
----------------------
A monitoração do projeto para verificar a saúde da aplicação e os recursos utilizados:
```shell script
http://localhost:8082/health
```

Banco de Dados
--------------
O projeto usa o MySQL como banco da dados para o ambiente local e de produção e o H2 para os testes.

Gerando o arquivo .jar
----------------------
Para gerar o arquivo <b>.jar</b>, execute o comando via terminal no diretório raiz do projeto:
```shell script
mvn clean install -P{profile} -DskipTests
```

Rodando os testes
-----------------
Para rodar os testes, execute o comando no terminal na raiz do projeto:
```shell script
mvn test
```

Rodando o projeto localmente
----------------------------
Para iniciar a aplicação, execute o comando no terminal na raiz do projeto:

```shell script
mvn spring-boot:run
```

Rodando o projeto no Docker
---------------------------
Para rodar o projeto em um container Docker, primeiro deve-se gerar o .jar do projeto.<br>
Após isso, deve-se gerar o build das imagens e subir os containers do Docker.<br><br>
<b>Fazendo o build das imagens:</b>
```shell script
docker-compose build
```

<b>Subindo os containers do Docker:</b>
```shell script
docker-compose up -d
```

##
Para automatizar esse processo, basta executar o Shellscript <b>`docker_build_and_run`</b>:<br>
Linux/MacOS:
```shell script
./docker_build_and_run.sh
```

Windows:
```shell script
.\docker_build_and_run.bat
```

RabbitMQ
--------
Acesse o RabbitMQ através do endereço:
```shell script
http://localhost:15672/
```

<b>Usuário:</b> Guest <br>
<b>Senha:</b> Guest

##
Para habilitar os pluginsRabbitMQ Shovel e RabbitMQ Shovel Management, execute o comando:
```shell script
rabbitmq-plugins enable rabbitmq_shovel rabbitmq_shovel_management
```

##
Caso queria rodar o projeto loclamente e rodar o RabbitMQ via Docker, execeto o comando:
```shell script
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.10-management
```
