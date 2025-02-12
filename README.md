# iSpoke

Este projeto foi desenvolvido para a apresentação da disciplina de **Programação Orientada a Objetos** do curso de **Ciência da Computação**. O **iSpoke** é um aplicativo educacional interativo que promove o aprendizado da **Língua Brasileira de Sinais (LIBRAS)** por meio do reconhecimento de gestos utilizando modelos de inteligência artificial.

## Índice
- [Descrição](#descrição)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Uso](#uso)
- [Arquitetura](#arquitetura)
- [Créditos](#créditos)
- [Contato](#contato)
- [Licença](#licença)

## Descrição
O **iSpoke** tem como objetivo facilitar o aprendizado de **LIBRAS** através de módulos interativos. Ao utilizar o aplicativo, o usuário poderá:
- Navegar entre diferentes módulos de aprendizado (por exemplo, o alfabeto em LIBRAS).
- Visualizar cada gesto individualmente para estudo.
- Praticar os gestos utilizando a câmera do dispositivo: ao realizar o gesto corretamente, o sistema contabiliza o progresso do usuário.
- Acompanhar seu desempenho por meio da tela de perfil, que exibe informações pessoais e o nível de aprendizado.

## Requisitos
- **Linguagem:** Kotlin
- **Plataforma:** Android (necessita de dispositivo físico ou emulador)
- **Ambiente de Desenvolvimento:** Android Studio

## Instalação
Siga os passos abaixo para configurar e executar o aplicativo:

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/iSpoke.git
2. **Abra o projeto no Android Studio.**
3.**Configure um dispositivo Android:**
- Conecte um dispositivo físico via cabo USB; ou
- Configure um emulador Android.
- Execute o projeto através do Android Studio.

## Uso
Ao iniciar o aplicativo, você encontrará as seguintes telas:

- Tela de Login: onde o usuário fará a autenticação.
- Tela Inicial (Home): apresenta a quantidade de gestos aprendidos e os módulos disponíveis.
- Módulos de Aprendizado: ao selecionar um módulo (como o do alfabeto), o usuário pode visualizar os gestos correspondentes a cada letra e acessar a tela de prática.
- Tela de Prática: ao clicar no botão de praticar, a câmera é ativada para capturar o gesto do usuário. Se o gesto for reconhecido corretamente, ele é contabilizado como um gesto aprendido. O usuário pode repetir a prática ou escolher outro gesto.
-  Tela de Perfil: armazena e exibe informações do usuário e seu nível de aprendizado.

## BackEnd 
https://github.com/marcosscaio/iSpokeBack-end.git
  
  ## Arquitetura
O projeto foi estruturado utilizando o padrão MVC:

- Model: Responsável pelo treinamento do modelo de aprendizado.
- Controller: Lida com a lógica principal, como a verificação dos gestos e atualização do contabilizador de pontos.
- View: Implementada com Kotlin Compose, garantindo uma navegação fluida e uma boa experiência de usuário.
  
## Créditos
Desenvolvido por:

- Júlio César - juliocshancez
- Marcos Caio - marcosscaio
  
## Contato
Em caso de dúvidas ou para mais informações, entre em contato:

Júlio César: julio.patricio08@aluno.ifce.edu.br
Marcos Caio: marcos.paulo09@aluno.ifce.edu.br

## Licença
Este projeto não possui uma licença definida.
