# CONFIGURAÇÃO DO BANCO DE DADOS

- A API utiliza MySQL.
- Passos para configuração:
    1. Instale ou abra o MySQL em seu ambiente
    2. Execute o comando SQL: `CREATE DATABASE usuarioapi`
    3. A API criará automaticamente as tabelas necessárias através do Hibernate quando executada

# Endpoints da API de Usuários

Esta seção descreve os endpoints REST disponíveis na API para gerenciamento de usuários.

| Método | Endpoint                 | Descrição                                                          |
|--------|--------------------------|--------------------------------------------------------------------|
| `POST` | `/usuarios`              | Cadastra um novo usuário e simula o envio de e-mail de confirmação |
| `GET`  | `/usuarios`              | Consulta usuários com paginação e filtro opcional por nome         |
| `GET`  | `/usuarios/{id}`         | Consulta um usuário específico por ID                              |

## Detalhes dos Endpoints

### `POST /usuarios`

**Descrição:** Permite o cadastro de um novo usuário no sistema. Após o cadastro bem-sucedido, um e-mail de confirmação é simulado para o endereço fornecido.

**Corpo da Requisição (JSON):**
```json
{
    "nome": "string",
    "email": "string (formato de email válido)",
    "senha": "string (mínimo 6 caracteres)"
}
```

### `GET /usuarios`

**Descrição:** Permite o cadastro de um novo usuário no sistema. Após o cadastro bem-sucedido, um e-mail de confirmação é simulado para o endereço fornecido.

**Parametros de consulta:**
- page: (int, default: 0): Número da página a ser retornada (base 0).
- size: (int, default: 10): Quantidade de usuários por página.
- nome: (string): Filtra usuários por qualquer parte do nome (case-insensitive).

### `GET /usuarios/{id}`

**Descrição:** Permite o cadastro de um novo usuário no sistema. Após o cadastro bem-sucedido, um e-mail de confirmação é simulado para o endereço fornecido.

**Parametros de caminho:**
- {id} (long): ID único do usuário a ser consultado.

# Testes Unitários
Não foram criados testes unitários específicos para as interfaces de serviço (UsuarioService e Email Service) porque não contêm lógica de implementação.

No caso da implementação do serviço, os testes de sucesso para o cadastro de usuários asseguram que a senha é devidamente criptografada, que o usuário é persistido no banco de dados e que o e-mail de confirmação é acionado. Já os cenários de erro, como a tentativa de cadastrar um usuário com um e-mail já existente, são cruciais para verificar se a lógica de negócio impede operações inválidas e lança as exceções apropriadas. Para as consultas, os testes garantem que a busca por ID retorne o usuário correto ou indique sua ausência, e que a consulta paginada funcione com e sem filtros, fornecendo os dados e metadados de paginação esperados.

Para o controller, a importância dos testes reside em validar a interação da API com o mundo exterior. Os cenários de sucesso confirmam que as requisições POST e GET são processadas corretamente, retornando os dados esperados com os status HTTP apropriados.
