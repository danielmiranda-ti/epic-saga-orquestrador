#!/bin/bash

echo "🚀 Subindo ambiente Kafka e Zookeeper..."
docker-compose up -d

echo "⏳ Aguardando Kafka iniciar..."
sleep 15

echo "📌 Criando tópicos da Epic Saga..."
docker exec kafka kafka-topics --create --topic cadastro-juridico-start \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --topic cadastro-juridico-sucesso \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --topic cadastro-juridico-falha \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --topic verificacao-financeira-start \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --topic verificacao-financeira-sucesso \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --topic verificacao-financeira-falha \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

#docker exec kafka kafka-topics --create --topic onboarding.iniciado \
#  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

#docker exec kafka kafka-topics --create --topic onboarding.documentos_validados \
#  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
#
#docker exec kafka kafka-topics --create --topic onboarding.finalizado \
#  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo "✅ Tópicos criados:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
