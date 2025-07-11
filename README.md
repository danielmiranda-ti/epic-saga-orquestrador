# Exemplo aplicado: Epic Saga

## 🌀 Epic Saga <sup><small>(sao)</small></sup>

O padrão Epic Saga é uma estratégia baseada em orquestração, onde um componente central — conhecido como orquestrador — é responsável por coordenar o fluxo de uma transação distribuída. 

Cada etapa da saga é executada de forma sequencial sob o controle desse orquestrador. 

Caso qualquer uma das etapas falhe, a saga como um todo é considerada mal sucedida, mesmo que outras etapas tenham sido concluídas com sucesso. 

Para que a transação seja considerada bem-sucedida, todas as chamadas precisam ser executadas com êxito.

Esse padrão utiliza a comunicação *<b>s</b>íncrona*, consistência *<b>a</b>tômica* e coordenação *<b>o</b>rquestrada*.

### Transação de compensação

Uma transação de compensação é uma ação reversa que desfaz os efeitos de uma etapa previamente concluída com sucesso.

Vamos supor um cenário onde temos 3 serviços que são chamados pelo orquestrador:

- servico_1
- servico_2
- servico_3

Caso o serviço **servico_3** falhe, precisaremos desfazer o que foi feito nos serviços **servico_1** e **servico_2**. Isso é considerado uma transação de compensação.

Deve ser levado em consideração casos onde a compensação também irá falhar.

Esses cenários elevam muito o nível de complexidade e acoplamento da solução. 

---

## Vantagens

- Centralização do fluxo, facilitando o entendimento e controle.
- Permite implementação de compensações ordenadas.
- Facilita o monitoramento e rastreamento da saga.
- Ajuda a garantir consistência eventual em sistemas distribuídos.

---

## Desvantagens

- Pode criar acoplamento com o orquestrador central.
- Ponto único de falha se o orquestrador não for resiliente.
- Pode ter complexidade maior na implementação do orquestrador.

---

## Exemplo de projeto

### Contexto do projeto

Projeto de onboarding de vendedores em marketplace, onde várias etapas precisam ocorrer em sequência, como cadastro jurídico, análise documental e integração contábil.

### Como a saga é aplicada

Um serviço orquestrador central controla o fluxo das etapas, enviando comandos para cada microserviço e recebendo eventos de conclusão ou falha para decidir os próximos passos ou compensações.

### Componentes envolvidos

- Orquestrador Epic Saga
- Serviço de cadastro jurídico
- Serviço de análise documental
- Serviço de integração contábil
- Gateway API para exposição do serviço

### Fluxo resumido

1. Orquestrador inicia saga com cadastro jurídico.  
2. Após sucesso, orquestrador envia comando para análise documental.  
3. Após análise positiva, orquestrador comanda a integração contábil.  
4. Caso alguma etapa falhe, orquestrador executa compensações necessárias para manter consistência.

---

## Como Rodar o Projeto

### Pré-requisitos

- Java 17 (ou versão compatível)
- Maven 3.x
- Docker (para rodar o LocalStack, que simula AWS SQS localmente)
- AWS CLI configurado (opcional, para usar SQS real na nuvem)

### Passos para execução local
- Clone o repositório
- Inicie o LocalStack para simular o SQS local
- Configure as variáveis de ambiente para apontar para o LocalStack
- Compile e execute o serviço

### Executando os testes automatizados

```bash
./mvnw test
```
### Dicas Extras

- 1
- 2
