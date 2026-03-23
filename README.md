# 📊 Carbon Calculator API

API REST para cálculo de emissão de carbono baseado em consumo de energia, transporte e produção de resíduos.

## Tecnologias usadas

- Java 17
- Spring Boot 3.3.4
- Spring Data MongoDB
- Spring Security
- Docker / Docker Compose
- Swagger (OpenAPI)
- Lombok

## Como executar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/viniciusperrone/action-labs-admission-process.git
```

### 2. Subir o banco de dados

```bash
docker compose up
```

O MongoDB será iniciado na porta `27017` e populado automaticamente com os fatores de emissão via `init-mongo.js`.

Para resetar o banco ao estado inicial:

```bash
docker compose down -v
docker compose up
```

### 3. Rodar a aplicação

Pela IDE, rode a classe `CarbonCalculatorApplication`, ou via terminal:

```bash
./gradlew bootRun
```

A aplicação sobe na porta `8085`.

### 4. Acessar a documentação

Swagger disponível em: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

---

## Fluxo da aplicação

1. O usuário inicia um cálculo via `/open/start-calc`
2. Um ID é gerado e armazenado no banco
3. O usuário envia os dados de consumo via `/open/info`
4. O sistema armazena ou atualiza os dados
5. O usuário consulta o resultado via `/open/result/{id}`
6. A emissão total é calculada dinamicamente

---

## Endpoints

### POST `/open/start-calc`

Inicia um novo cálculo com os dados básicos do usuário. Todos os campos são obrigatórios.

**Request:**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "phoneNumber": "41999999999",
  "uf": "PR"
}
```

**Response:**
```json
{
  "id": "69c0f5dfbbabb42f94cb6416"
}
```

---

### PUT `/open/info`

Recebe as informações de consumo para o cálculo. Se chamado novamente para o mesmo `id`, os dados são sobrescritos.

**Request:**
```json
{
  "id": "69c0f5dfbbabb42f94cb6416",
  "energyConsumption": 300,
  "transportation": [
    { "type": "CAR", "monthlyDistance": 200 },
    { "type": "PUBLIC_TRANSPORT", "monthlyDistance": 50 }
  ],
  "solidWasteTotal": 100,
  "recyclePercentage": 0.4
}
```

> `recyclePercentage` é um valor entre `0` e `1.0` representando o percentual de resíduos recicláveis.

> Tipos de transporte disponíveis: `CAR`, `MOTORCYCLE`, `PUBLIC_TRANSPORT`, `BICYCLE`

**Response:**
```json
{
  "success": true
}
```

---

### GET `/open/result/{id}`

Retorna a pegada de carbono calculada para o id informado.

**Response:**
```json
{
  "energy": 120.0,
  "transportation": 40.0,
  "solidWaste": 70.0,
  "total": 230.0
}
```

---

## Fórmulas de cálculo

### Energia
```
emissão = consumoDeEnergia × fatorDeEmissão[UF]
```

### Transporte
```
emissão = Σ (distânciaMensal × fatorDeEmissão[tipoDeTransporte])
```

### Resíduos Sólidos
```
emissão = (totalResíduos × percRecicláveis × fatorRecicláveis[UF])
        + (totalResíduos × (1 - percRecicláveis) × fatorNãoRecicláveis[UF])
```

---

## Estrutura do projeto

```
src/main/java/br/com/actionlabs/carboncalc/
├── config/         # Configurações de segurança e Swagger
├── dto/            # Objetos de request e response
├── enums/          # TransportationType
├── model/          # Entidades MongoDB
├── repository/     # Interfaces Spring Data
├── rest/           # Controllers REST
└── service/        # Regras de negócio
```
