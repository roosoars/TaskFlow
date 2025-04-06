# TaskFlow

<p>
  <a href="#sobre">Sobre</a> •
  <a href="#características">Características</a> •
  <a href="#tecnologias">Tecnologias</a> •
  <a href="#padrões-de-projeto">Padrões de Projeto</a> •
  <a href="#instalação">Instalação</a> •
  <a href="#screenshots">Screenshots</a> 
</p>

## Sobre

TaskFlow é um aplicativo de gerenciamento de tarefas para Android desenvolvido com foco em padrões de projeto e princípios SOLID. O aplicativo permite aos usuários organizar suas atividades diárias, categorizá-las e acompanhar seu progresso de forma intuitiva e eficiente.

Este projeto foi desenvolvido como trabalho final para a disciplina de Programação Orientada a Objetos 2 (2024/2) do curso de Bacharelado em Sistemas de Informação da Universidade Federal de Uberlândia.

## Características

- 📝 Criar e gerenciar tarefas com datas de vencimento, descrições e prioridades
- 🗂️ Organizar tarefas em categorias personalizáveis
- 🔍 Filtrar e ordenar tarefas por data, prioridade ou categoria
- ✅ Visualizar tarefas pendentes e concluídas separadamente
- 📊 Diferentes tipos de tarefas (regulares e projetos)
- 🎨 Interface intuitiva seguindo princípios de Material Design

## Tecnologias

- **Linguagem**: Java
- **Plataforma**: Android (SDK mínimo 30 - Android 11)
- **Frameworks**:
  - [Dagger 2](https://dagger.dev/) - Injeção de Dependência
  - [Room](https://developer.android.com/training/data-storage/room) - Persistência de Dados
  - [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) (ViewModel, LiveData, Navigation)

## Padrões de Projeto

TaskFlow implementa diversos padrões de design e princípios SOLID:

- **Builder Pattern**: Construção de objetos Task de forma fluente
- **Factory Pattern**: Criação de diferentes tipos de tarefas
- **Observer Pattern**: Notificação de mudanças nas tarefas
- **Strategy Pattern**: Estratégias de ordenação de tarefas
- **Repository Pattern**: Abstração do acesso a dados
- **Decorator Pattern**: Adição de comportamentos visuais às tarefas

*Para mais detalhes sobre a implementação destes padrões, consulte a [Wiki do projeto](link-para-sua-wiki).*

## Instalação

```bash
# Clone este repositório
git clone https://github.com/seu-usuario/taskflow.git

# Abra o projeto no Android Studio

# Sincronize o projeto com os arquivos Gradle

# Execute o aplicativo em um emulador ou dispositivo físico
```

Requisitos mínimos:
- Android Studio Electric Eel (2022.1.1) ou superior
- JDK 18
- Android SDK 30 (Android 11)

## Screenshots

<p align="center">
  <img src="https://github.com/roosoars/TaskFlow/blob/main/1.png" width="200" alt="Lista de Tarefas"/>
  <img src="https://github.com/roosoars/TaskFlow/blob/main/2.png" width="200" alt="Adicionar Tarefa"/>
  <img src="https://github.com/roosoars/TaskFlow/blob/main/3.png" width="200" alt="Categorias"/>
</p>

## Estrutura do Projeto

```
app/src/main/java/com/roosoars/taskflow/
├── builder/            # Implementação do Builder Pattern
├── db/                 # DAOs e configuração do Room Database
├── di/                 # Injeção de dependência com Dagger
├── factory/            # Implementação do Factory Pattern
├── model/              # Entidades e modelos de dados
├── observer/           # Implementação do Observer Pattern
├── repository/         # Camada de repositório
├── strategy/           # Implementação do Strategy Pattern
├── ui/                 # Activities, Fragments e Adapters
│   ├── adapters/       # RecyclerView Adapters
│   ├── decorators/     # Decorators para elementos de UI
│   └── fragments/      # Fragmentos da UI
└── viewmodel/          # ViewModels
```

## Autores

- Rodrigo Soares
- César Reiss

---

<p align="center">
  Desenvolvido como projeto acadêmico para Programação Orientada a Objetos II - Universidade Federal de Uberlândia.
</p>
