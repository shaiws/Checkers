import socket
from threading import Thread


def response(key):
    return key


def serverthread(clientSocket, opponentSocket, addr, player):
    send(clientSocket, str(player))
    send(clientSocket, 'What is your name?')
    data = clientSocket.recv(1024)
    print(data, addr)
    send(clientSocket, 'Hi %s' % data.decode('utf-8'))
    send(opponentSocket, 'Your opponent name is %s' %
         data.decode('utf-8'))
    while True:
        data = clientSocket.recv(1024)
        print(data, addr)
        send(opponentSocket, data.decode('utf-8'))
    clientSocket.close()
    print('Disconnected from %s' % addr)


def send(clientSocket, message):
    if not message.endswith("\n"):
       message = message + "\n"
    clientSocket.send(message.encode())


if __name__ == '__main__':
    print("Server is on")
    s = socket.socket()
    
    host = '127.0.0.1'
    port = 1337
    s.bind((host, port))
    s.listen(5)
    while True:
        try:
            clientSocket1, addr1 = s.accept()
            print('Server: Connection accepted for player 1: %s %d' % (addr1, 0))
            clientSocket2, addr2 = s.accept()
            print('Server: Connection accepted for player 2: %s %d' % (addr2, 1))
            thread1 = Thread(target=serverthread, args=(
                clientSocket1, clientSocket2, addr1, 0))
            thread1.start()
            thread2 = Thread(target=serverthread, args=(
                clientSocket2, clientSocket1, addr2, 1))
            thread2.start()
        except:
            s.close()
