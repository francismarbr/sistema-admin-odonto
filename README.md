# Sistema de Gestão Odontológica (Java EE + Spring Security + JSF + PrimeFaces)

Este é um projeto de sistema de gestão odontológica (em formato Saas) desenvolvido em Java EE, usando **Spring Security** para autenticação e autorização, **JSF** e **PrimeFaces** para a interface web. O sistema é executado em um servidor **Tomcat** e utiliza banco de dados **PostgreSQL**.

## Funcionalidades

- **Autenticação e Autorização**: Gestão de usuários com login e controle de acesso utilizando **Spring Security**.
- **Interface de Usuário Dinâmica**: Criada com **JSF** e **PrimeFaces** para uma experiência mais interativa e responsiva.
- **Gestão de Dados**: Integração com **PostgreSQL** com separação dos dados por tenant_id.
- **Multiusuário**: Permite o acesso de múltiplos usuários simultaneamente com diferentes níveis de permissão de acesso.
- **Gestão de pacientes e fornecedores**
- **Módulos de Compra e Venda de serviços**
- **Gestão financeira:** Permite registrar contas a pagar, a receber, baixar de contas, fluxo de caixa
- **Registro de Anamnese e receita médica**
- **Gestão de comissão de dentitas**
- **Relatórios**

## Tecnologias Utilizadas

- **Backend**: Java EE, Spring Security
- **Frontend**: JSF, PrimeFaces
- **Servidor de Aplicação**: Apache Tomcat
- **Banco de Dados**: PostgreSQL 
- **Gerenciador de dependências:** Maven
- **Relatórios:** JasperReport

## Pré-requisitos

Certifique-se de ter os seguintes softwares instalados para executar o projeto:

- **Java JDK** (versão 8 ou superior)
- **Apache Tomcat** (versão 9 ou superior)
- **PostgreSQL** (versão 12 ou superior)
- **Maven** para gerenciar as dependências

## Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/seuprojeto.git
   cd seuprojeto
   ```

2. Configure o banco de dados PostgreSQL:
   - Crie um banco de dados e configure as credenciais.
   - Ajuste o arquivo de configuração do banco de dados (`persistence.xml`) com as credenciais e URL corretas.

3. Compile o projeto usando **Maven**:
   ```bash
   mvn clean install
   ```

4. Importe o projeto em sua IDE

5. Adicione-o ao Tomcat

6. Inicie Tomcat e acesse o sistema em:
   ```
   http://localhost:8080/seu-projeto
   ```

## Uso

- **Login**: Crie o usuário inicial no banco de dados com acesso master
- **Gerenciamento de Usuários**: Administre permissões de acesso e visualize as informações dos usuários.

## Contribuições

Sinta-se à vontade para fazer fork.
