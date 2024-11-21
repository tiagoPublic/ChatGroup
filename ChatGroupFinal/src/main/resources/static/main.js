'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#loginForm');
var registerForm = document.querySelector('#registerForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

// atribuir cores aos users
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];

document.addEventListener('DOMContentLoaded', function() {
    // Seleciona o formulário de login
    var loginForm = document.querySelector('#loginForm');

    // Adiciona um evento de submissão ao formulário de login
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();

        // Obtém os valores de nome de usuário e senha dos campos de entrada
        var username = document.getElementById('username').value.trim();
        var password = document.getElementById('password').value.trim();

        // Verifica se o nome de usuário e a senha foram fornecidos
        if (username && password) {
            var credentials = {
                username: username,
                password: password
            };

            // Autentica o usuário com as credenciais fornecidas
            authenticateUser(credentials)
                .then(function(data) {
                    // Se a autenticação for bem-sucedida, oculta a página de login e mostra a página do chatroom
                    document.getElementById('username-page').classList.add('hidden');
                    document.getElementById('chat-page').classList.remove('hidden');
                    connect();
                })
                .catch(function(error) {
                    console.error('Authentication error:', error);
                    document.getElementById('login-error').textContent = 'Username or password are incorrect.';
                });
        } else {
            console.error('Username and password are required.');
            document.getElementById('login-error').textContent = 'Username and password are required.';
        }
    });

    // Adiciona um evento de clique ao botão "Back to Login"
    document.getElementById('showLogin').addEventListener('click', function() {
        // Remove a classe "active" da página de registro
        document.getElementById('signup-page').classList.remove('active');
        // Exibe a página de login
        document.getElementById('username-page').style.display = 'block';
    });
});

// Função para enviar as credenciais de login para o backend e autenticar o usuário
function authenticateUser(credentials) {
    return fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
        .then(function(response) {
            if (response.ok) {
                // Se o login for bem-sucedido, retorna a resposta
                return response.json();
            } else {
                // Se o login falhar, lança um erro com a mensagem recebida
                throw new Error('Invalid username or password');
            }
        });
}

function connect(event) {
    username = document.querySelector('#username').value.trim();

    if (username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    console.log('Connected to WebSocket server');
    // subscrever o public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // enviar o username ao servidor
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');

    // Após o login bem-sucedido, oculte a página de login e mostre a página de chat
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');
}



function onError(error) {
    console.log('WebSocket connection error:', error);
    connectingElement.textContent = 'Could not connect to WebSocket server!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            timestamp: new Date().toISOString() // Capturar o timestamp atual
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    console.log('Message received:', payload);
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';

    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';

    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);

        // Adiciona o timestamp ao lado da mensagem
        //var timestampElement = document.createElement('span');*/
        //var timestampText = document.createTextNode(formatTimestamp(message.timestamp));*/
        //timestampElement.appendChild(timestampText);*/
        //messageElement.appendChild(timestampElement);*/
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight; // mostra sempre a ultima msg
}

// Função para formatar o timestamp
function formatTimestamp(timestamp) {
    var date = new Date(timestamp);
    var dat_e = date.getDate()
    var hours = date.getHours();
    var minutes = "0" + date.getMinutes();
    var seconds = "0" + date.getSeconds();
    console.log("Timestamp recebido:", timestamp);
    return dat_e + ':' + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
}

// vai buscar uma random color à tabela e atribui ao avatar
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    // com esse valor hash vamos buscar o seu valor absoluto, que será a posição na tabela colors
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

registerForm.addEventListener('submit', function(event) {
    event.preventDefault();
    console.log('Register form submitted'); // Adicione este log para verificar se o evento de submissão está sendo acionado
    var newUsername = document.getElementById('new-username').value.trim();
    var newPassword = document.getElementById('new-password').value.trim();
    if (newUsername && newPassword) {
        var registerMessage = {
            username: newUsername,
            password: newPassword
        };
        registerUser(registerMessage)
            .then(function(response) {
                if (response.ok) {
                    document.getElementById('register-error').textContent = '';
                    document.getElementById('register-success').textContent = 'User registered successfully';
                } else {
                    return response.text().then(function(text) { throw new Error(text) });
                }
            })
            .catch(function(error) {
                console.error('Registration error:', error);
                document.getElementById('register-success').textContent = '';
                document.getElementById('register-error').textContent = error.message;
            });
    } else {
        console.error('Username and password are required.');
        document.getElementById('register-error').textContent = 'Username and password are required.';
    }
});

function registerUser(user) {
    return fetch('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    });
}

messageForm.addEventListener('submit', sendMessage, true);