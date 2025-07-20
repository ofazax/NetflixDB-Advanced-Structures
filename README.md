# 🎬 NetflixDB: Algoritmos e Estruturas de Dados III
![Banner ilustrativo do projeto](https://github.com/ofazax/NetflixDB-Advanced-Structures/blob/main/img/Netflixdb.png?raw=true)

> **Descrição:** Implementação de algoritmos e estruturas de dados avançadas em Java para resolver problemas de busca, compressão, criptografia e casamentos de padrões em uma base de dados da Netflix.

---

## 🎯 Sobre o Projeto

Este projeto foi desenvolvido como parte da disciplina de **Algoritmos e Estruturas de Dados III (AEDS 3)**. O objetivo principal era aplicar conceitos teóricos de estruturas de dados avançadas em um cenário prático e complexo: a criação de um sistema de gerenciamento de banco de dados (SGBD) funcional.

O sistema manipula uma base de dados real da Netflix, oferecendo funcionalidades completas de CRUD, indexação rápida para consultas, compressão de dados para otimização de espaço e criptografia para garantir a segurança das informações.

---

## ✨ Funcionalidades Implementadas

O projeto é dividido em módulos, cada um responsável por uma funcionalidade chave de um SGBD moderno:

* **🗂️ Gerenciamento CRUD Completo:**
    * **Create:** Adiciona novos filmes à base de dados.
    * **Read:** Busca e exibe filmes por ID.
    * **Update:** Atualiza as informações de um filme existente.
    * **Delete:** Remove um filme da base de dados.

* **⚡ Indexação de Alta Performance:**
    * **Árvore B+:** Utilizada para criar um índice primário que permite buscas e consultas por ID com o mínimo de acessos a disco, ideal para grandes volumes de dados.
    * **Hash Estendido:** Implementado como um índice secundário para buscas rápidas, demonstrando uma alternativa eficiente para acesso direto aos dados.

* **🗜️ Otimização de Armazenamento:**
    * **Compressão de Huffman:** Implementação do algoritmo clássico de Huffman para comprimir os dados dos registros, economizando espaço em disco.
    * **Compressão LZW:** Utilização do algoritmo LZW como uma segunda técnica de compressão, permitindo uma comparação de eficiência.

* **🔒 Segurança e Privacidade:**
    * **Criptografia AES:** Cifragem dos dados sensíveis com o padrão industrial AES, garantindo um alto nível de segurança.
    * **Criptografia OTP (One-Time Pad):** Implementação do conceito de cifra de uso único para demonstrar um método de criptografia teoricamente inquebrável.

* **🔍 Busca Rápida por Padrões:**
    * **Algoritmo KMP (Knuth-Morris-Pratt):** Utilizado para realizar buscas textuais eficientes (como encontrar filmes por palavras-chave no título) sem a necessidade de varrer a base de dados inteira de forma linear.

---

## 🛠️ Tecnologias e Ferramentas

* **Linguagem Principal:** **Java** (JDK 11+)
* **Controle de Versão:** Git e GitHub

---

## 🚀 Como Executar o Projeto

Para compilar e executar o projeto, você precisará ter o **Java Development Kit (JDK)** instalado.

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/ofazax/NetflixDB-Advanced-Structures.git](https://github.com/ofazax/NetflixDB-Advanced-Structures.git)
    ```

2.  **Navegue até a pasta do projeto:**
    ```bash
    cd NetflixDB-Advanced-Structures
    ```

3.  **Compile os arquivos Java:**
    ```bash
    javac *.java
    ```

4.  **Execute a classe principal:**
    ```bash
    java Main
    ```
    *(Substitua `Main` pelo nome da sua classe principal, se for diferente)*

---

## 👨‍💻 Autor

* **Brunno** - [GitHub](https://github.com/ofazax)
