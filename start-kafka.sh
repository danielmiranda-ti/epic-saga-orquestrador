#!/bin/bash

echo "🚀 Subindo ambiente Kafka e Zookeeper..."
docker-compose up -d

echo "⏳ Aguardando Kafka iniciar..."
sleep 25

echo "📌 Criando tópicos da Epic Saga..."

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.command.register \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.command.compensate \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.event.register.success \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.event.register.failed \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.event.compensation.success \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.seller.event.compensation.failed \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo "====================================================================="

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.command.register \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.command.compensate \
 --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.event.register.success \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.event.register.failed \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.event.compensation.success \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.store.event.compensation.failed \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo "====================================================================="

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.financial.command.register \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.financial.command.compensate \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.financial.event.register.success \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.financial.event.register.failed \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic  saga.epic.financial.event.compensation.success \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --create --if-not-exists --topic saga.epic.financial.event.compensation.failed \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo "====================================================================="

echo "✅ Tópicos criados:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
