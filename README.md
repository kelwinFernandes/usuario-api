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

**Descrição:** Permite consultar a lista de usuários cadastrados, com suporte a paginação e filtro por nome.

**Parametros de consulta:**
- page: (int, default: 0): Número da página a ser retornada (base 0).
- size: (int, default: 10): Quantidade de usuários por página.
- nome: (string): Filtra usuários por qualquer parte do nome (case-insensitive).

### `GET /usuarios/{id}`

**Descrição:** Permite consultar os detalhes de um usuário específico utilizando seu ID.

**Parametros de caminho:**
- {id} (long): ID único do usuário a ser consultado.

# Testes Unitários
Não foram criados testes unitários específicos para as interfaces de serviço (UsuarioService e Email Service) porque não contêm lógica de implementação.

No caso da implementação do serviço, os testes de sucesso para o cadastro de usuários asseguram que a senha é devidamente criptografada, que o usuário é persistido no banco de dados e que o e-mail de confirmação é acionado. Já os cenários de erro, como a tentativa de cadastrar um usuário com um e-mail já existente, são cruciais para verificar se a lógica de negócio impede operações inválidas e lança as exceções apropriadas. Para as consultas, os testes garantem que a busca por ID retorne o usuário correto ou indique sua ausência, e que a consulta paginada funcione com e sem filtros, fornecendo os dados e metadados de paginação esperados.

Para o controller, a importância dos testes reside em validar a interação da API com o mundo exterior. Os cenários de sucesso confirmam que as requisições POST e GET são processadas corretamente, retornando os dados esperados com os status HTTP apropriados.

# Possibilidades para Implementação do Frontend
## JavaScript com React (Opção Escolhida)

### Prós:

- Controle de Estado Poderoso: o React oferece mecanismos robustos para gerenciar o estado da aplicação (como useState, useReducer, Context API e bibliotecas como Redux ou Zustand).

- Componentização: Facilita a criação de interfaces reutilizáveis e modulares, agilizando o desenvolvimento e a manutenção.

- Comunidade e Ecossistema Maduros: Possui uma das maiores comunidades de desenvolvedores, vasto material de aprendizado, e um ecossistema gigantesco de bibliotecas, ferramentas e frameworks complementares.

- Performance: Utiliza um "Virtual DOM" que otimiza as atualizações na interface, resultando em aplicações rápidas e responsivas.

- Reatividade: A UI se atualiza automaticamente quando o estado dos dados muda, o que simplifica o desenvolvimento de interfaces dinâmicas.

- Manipulação de Dados e Autenticação: A capacidade de usar localStorage (ou sessionStorage) para armazenar informações como tokens de autenticação é padrão em aplicações web modernas e totalmente compatível com React e JavaScript. Hooks como useEffect são ideais para lidar com o ciclo de vida da autenticação, requisições à API, etc.

### Contras:

- Curva de Aprendizagem: Embora seja poderoso, o React (e o ecossistema JavaScript moderno) pode ter uma curva de aprendizado inicial íngreme, especialmente para quem não tem experiência prévia com conceitos como JSX e gerenciamento de estado.

- Configuração Inicial (sem frameworks): Iniciar um projeto React do zero pode ser complexo devido às muitas ferramentas envolvidas (Babel, Webpack, ESLint, etc.).

- Sobrecarga de Escolhas: A vasta quantidade de bibliotecas e abordagens no ecossistema pode ser esmagadora, levando à "fadiga de decisão".

- Necessidade de Otimização Manual para Evitar Re-renderizações Excessivas: Se não forem aplicadas otimizações como React.memo para componentes, useCallback para funções e useMemo para valores calculados, o aplicativo pode sofrer de re-renderizações desnecessárias que afetam o desempenho, especialmente em listas grandes ou árvores de componentes complexas.

## Segunda opção: Angular

### Prós:

- Estrutura Definida (Angular): Angular impõe uma estrutura MVC/MVVM que é ótima para grandes equipes e projetos de longo prazo, garantindo consistência.

- Injeção de Dependências Integrada: O sistema de Injeção de Dependências (DI) do Angular é poderoso e vem integrado ao framework. Ele promove a modularidade, o reuso de código e facilita muito os testes unitários, pois as dependências podem ser facilmente substituídas por mocks.

### Contras:

- Flexibilidade Limitada para Personalização Extrema: Sendo um framework opinionado, o Angular tende a ter "uma maneira certa" de fazer as coisas.

- Tamanho do Bundle Inicial: Aplicações Angular tendem a ter um bundle JavaScript maior em comparação com React ou Vue, o que poderia afetar o tempo de carregamento inicial em redes mais lentas.

# Collection

**[Link para baixar collection insomnia](https://raw.githubusercontent.com/kelwinFernandes/usuario-api/refs/heads/main/collections/Insomnia_2025-06-29.yaml)**
