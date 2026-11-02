# IT Service Desk API

API REST para um sistema de atendimento de TI. A proposta é permitir que usuários abram chamados e que técnicos e administradores acompanhem e resolvam as solicitações.

> **Status: em desenvolvimento.** A autenticação e a estrutura inicial de usuários e chamados já existem, mas as operações de atendimento ainda não estão implementadas. Este projeto não está pronto para produção.

## Tecnologias

- Java 17
- Spring Boot 4
- Spring Security e JWT
- Spring Data JPA
- MySQL
- Maven

## Funcionalidades implementadas

- Cadastro público de usuários com papel `USER`.
- Login com e-mail e senha, retornando um token JWT do tipo Bearer com validade de uma hora.
- Senhas armazenadas com BCrypt; o cadastro exige pelo menos 8 caracteres e aceita até 72 bytes UTF-8.
- Validação de e-mail e dos campos obrigatórios.
- Limitação por endereço de origem: 10 tentativas de login por minuto e 5 cadastros por hora. Ao exceder o limite, a API responde com HTTP `429`.
- Rotas diferentes de cadastro e login exigem autenticação Bearer.
- Entidades iniciais de usuários e chamados, incluindo prioridade, status, solicitante e técnico responsável.

## Ainda não implementado

- Endpoints para criar, consultar, listar, atualizar, atribuir e fechar chamados.
- Autorização por papel e por propriedade do chamado (por exemplo, restringir usuários aos próprios chamados).
- Fluxo administrativo para criar técnicos e administradores.
- Comentários, histórico de alterações e comunicação sobre chamados.
- Paginação, filtros, notificações e documentação interativa da API.

Os papéis `USER`, `TECHNICIAN` e `ADMIN` e a entidade de chamados já existem no modelo, mas isso não significa que essas funcionalidades estejam disponíveis por endpoints.

## Requisitos

- JDK 17 ou superior.
- MySQL em execução.
- Maven não precisa estar instalado: o projeto inclui o Maven Wrapper (`mvnw.cmd`).

## Configuração local no PowerShell

Defina as variáveis de ambiente antes de iniciar a aplicação. Use uma chave JWT própria, aleatória e com pelo menos 32 caracteres; não reutilize a chave de exemplo de testes nem a inclua no Git.

```powershell
$env:DB_USERNAME = "seu_usuario_mysql"
$env:DB_PASSWORD = "sua_senha_mysql"
$env:JWT_SECRET = "substitua_por_uma_chave_aleatoria_com_pelo_menos_32_caracteres"
```

A aplicação usa `jdbc:mysql://localhost:3306/it_service_desk_api` e está configurada para criar o banco se ele ainda não existir. O schema é atualizado pelo Hibernate durante a inicialização (`spring.jpa.hibernate.ddl-auto=update`).

Inicie a API:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrão, a aplicação Spring Boot fica disponível em `http://localhost:8080`.

## Endpoints disponíveis

### Cadastrar usuário

`POST /auth/register`

```json
{
  "name": "Nome do usuário",
  "email": "usuario@example.com",
  "password": "uma-senha-segura"
}
```

Retorna HTTP `201` com os dados públicos do usuário. O papel é sempre `USER`; o corpo da resposta não inclui a senha.

### Fazer login

`POST /auth/login`

```json
{
  "email": "usuario@example.com",
  "password": "uma-senha-segura"
}
```

Retorna um token de acesso e os dados públicos do usuário. Use o token para chamar rotas protegidas:

```http
Authorization: Bearer <accessToken>
```

E-mails são normalizados para minúsculas e espaços externos são removidos. Um e-mail já cadastrado resulta em HTTP `409`; credenciais inválidas resultam em HTTP `401`; dados que não passam pela validação resultam em HTTP `400`.

## Limites de autenticação

Os valores padrão podem ser ajustados em `src/main/resources/application.properties`:

| Configuração | Padrão |
|---|---:|
| Tentativas de login | 10 por endereço a cada 60 segundos |
| Cadastros | 5 por endereço a cada 3600 segundos |

O controle atual é mantido em memória por instância. Em implantação com várias instâncias, recomenda-se aplicar limites também no gateway/proxy. Se houver proxy reverso, configure a infraestrutura para que a aplicação receba o endereço de origem correto e confiável.

## Testes

Execute a suíte com:

```powershell
.\mvnw.cmd test
```

Os testes usam um banco H2 em memória e uma chave JWT exclusiva de teste; não precisam de um MySQL local nem das variáveis de ambiente usadas para iniciar a aplicação.
