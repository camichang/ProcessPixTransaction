# Documentação Completa - Projeto Pix Transaction - ProcessPixTransaction
```
## 1. Visão Geral
Este projeto simula o processamento de transações Pix com integração a serviços de fraude e ao Banco Central (mock).  
O objetivo é validar cenários de aprovação e reprovação e notificar o cliente via WebSocket.

---
```
## 2. Fluxo da Transação
1. Cliente envia requisição `POST /pix/pay`.
2. Transação criada com status `PENDING`.
3. Evento `TransactionCreatedEvent` é disparado.
4. `TransactionCreatedListener` processa:
   - Chama serviço de fraude (se aplicável).
   - Atualiza status no banco.
   - Envia mensagem para fila do Banco Central.
5. Banco Central (mock) processa e responde.
6. `CentralBankResponseListener` atualiza status no banco e envia notificação para o cliente via `/topic/pix-status`.

---
```
## 3. Regras de Negócio
- **Conta bloqueada** → `REPROVED_ACCOUNT_BLOCKED` (interno), cliente recebe `REPROVED`.
- **Saldo insuficiente** → `REPROVED` (interno), cliente recebe `REPROVED`.
- **Valor acima de 10 mil** → `REPROVED` (interno), cliente recebe `REPROVED`.
- **Cenário feliz** → `APPROVED`.

---
```
## 4. Estrutura Técnica
- **Linguagem**: Java 21
- **Framework**: Spring Boot
- **Eventos**:  
  - `CentralBankResponseEvent`  
  - `TransactionCreatedEvent`
  - `TransactionResponseEvent`
- **DTOs**:  
  - `TransactionStatusDTO` (interno)  
  - `ClientTransactionStatusDTO` (cliente)
- **Listeners**:  
  - `TransactionCreatedListener`  
  - `CentralBankResponseListener`
- **Service**:  
  - `PixTransactionService`
  - `FraudService`
  - `AccountService`
- **Mock Banco Central**:  
  - `CentralBankMockController` (simula processamento e resposta)
- **WebSocket**:  
  - Configuração para enviar notificações de status para o cliente.
- - **Banco de Dados**:  
  - H2 (em memória) para armazenar transações e status.
- **Repositórios**:  
  - `TransactionRepository` (CRUD para transações)
- **Controllers**:  
  - `PixTransactionController`
- **Migrações**:  
  - `V1__Create_Transaction_Table.sql` (criação da tabela de transações)

---

## 5. API
### Endpoint: `POST /pay`
**Request:**
```
curl -X POST http://localhost:8080/pay \
-H "Content-Type: application/json" \
-H "idempotency_key: key125" \
-d '{
"pixKey": "pixKey1",
"amount": 500
}'
```
**Response: Approved**
```json
{
  "id": "abc123",
  "pixKey": "pixKey1",
  "amount": 500,
  "status": "APPROVED",
  "message": "Transaction was successful."
}
```
**Response: Reproved**
```json
{
  "id": "xyz456",
  "pixKey": "pixKey3",
  "amount": 10000,
  "status": "REPROVED",
  "message": "Transaction was rejected. Please check your account status or contact support."
}
```
**Exemplo de resposta para o cliente**
```json
{
  "id": "xyz456",
  "pixKey": "pixKey3",
  "amount": 10000,
  "message": "Transaction was rejected. Please check your account status or contact support."
}
```

---
## 6. Notificações WebSocket
- **Endpoint**: `/topic/pix-status`
- **Payload**:
```json
{
  "id": "abc123",
  "pixKey": "pixKey1",
  "amount": 500,
  "message": "Transaction was successful."
}
```
- **Uso**: O cliente pode se inscrever neste tópico para receber atualizações em tempo real sobre o status de suas transações Pix.

---
## 7. Testes Unitários e de Integração
- **Testes Unitários**: Cobrem a lógica de negócios em `PixTransactionService` e `FraudService`.
- **Testes de Integração**: Validam o fluxo completo da transação, incluindo a interação com o mock do Banco Central.
- **Ferramentas**: JUnit 5, Mockito, Spring Boot Test, AssertJ
- **Execução**: Os testes podem ser executados usando o comando:
```bash
./gradlew test
```

---
## 8. Considerações Finais
Este projeto é uma simulação de um sistema de processamento de transações Pix, focado em a validação de regras de negócio e integração com serviços externos. 
Ele pode ser expandido para incluir funcionalidades adicionais, como autenticação, suporte a múltiplas chaves Pix, e integração com sistemas de pagamento reais. 
A estrutura modular e o uso de eventos facilitam a manutenção e a escalabilidade do sistema.

---
## 9. Métricas New Relic
- Para enviar métricas ao New Relic, crie docs/secrets.yaml com a chave ou defina a variável de ambiente NEW_RELIC_API_KEY