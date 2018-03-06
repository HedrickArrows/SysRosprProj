#include <iostream>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <string>
#include <string.h>
#include <thread>
#include <vector>
#include <mutex>
#include <conio.h>
#include <IPTypes.h>
#include <IPHlpApi.h>
#include <regex>

#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Iphlpapi.lib")

#define IP_ADDRESS "192.168.1.115"//"localhost"
#define DEFAULT_PORT "27015"
#define DEFAULT_BUFLEN 512

struct client_type
{
	int id;
	SOCKET socket;
};

void gotoxy(int x, int y)
{
	static HANDLE h = NULL;
	if (!h)
		h = GetStdHandle(STD_OUTPUT_HANDLE);
	COORD c = { x, y };
	SetConsoleCursorPosition(h, c);
}

const char OPTION_VALUE = 1;
const int MAX_CLIENTS = 5;

std::string tab[MAX_CLIENTS];
std::mutex lo;

//Function Prototypes
int process_client(client_type &new_client, std::vector<client_type> &client_array, std::thread &thread);
int main();

int process_client(client_type &new_client, std::vector<client_type> &client_array, std::thread &thread)
{
	std::string msg = "", prevmsg = "";
	char tempmsg[DEFAULT_BUFLEN] = "";

	msg = "Welcome " + std::to_string(new_client.id) + "\n";
	send(new_client.socket, msg.c_str(), strlen(msg.c_str()), 0);

	//Session
	while (1)
	{

		memset(tempmsg, 0, DEFAULT_BUFLEN);

		if (new_client.socket != 0)
		{
			int iResult = recv(new_client.socket, tempmsg, DEFAULT_BUFLEN, 0);

			if (iResult != SOCKET_ERROR)
			{
				std::string tmp = tempmsg;
				if (std::count(tmp.begin(), tmp.end(), ' ') == 2) {
					if (strcmp("", tempmsg))
						msg = /*"Client #" +*/  std::to_string(new_client.id) + " " + tempmsg + "\n";

					//if (msg != prevmsg)
						//std::cout << msg.c_str();// << std::endl;
					prevmsg = msg;
					lo.lock();
					tab[new_client.id] = msg;
					lo.unlock();

					//Broadcast that message to the other clients
					/*for (int i = 0; i < MAX_CLIENTS; i++)
					{
					if (client_array[i].socket != INVALID_SOCKET)
					//if (new_client.id != i)
					iResult = send(client_array[i].socket, msg.c_str(), strlen(msg.c_str()), 0);
					}*/
				}
			}
			else
			{
				msg = std::to_string(new_client.id) + " Disconnected      \n";

				//gotoxy(0, 7 + MAX_CLIENTS + 3); std::cout << "Client #" << msg <<"      ";// << std::endl;

				closesocket(new_client.socket);
				closesocket(client_array[new_client.id].socket);
				client_array[new_client.id].socket = INVALID_SOCKET;

				lo.lock();
				tab[new_client.id] = msg;
				lo.unlock();
				//Broadcast the disconnection message to the other clients
				/*for (int i = 0; i < MAX_CLIENTS; i++)
				{
				if (client_array[i].socket != INVALID_SOCKET)
				iResult = send(client_array[i].socket, msg.c_str(), strlen(msg.c_str()), 0);
				}*/

				break;
			}
		}
	} //end while

	thread.detach();

	return 0;
}

int broadcast(std::vector<client_type> &client_array, std::thread &thread)
{
	std::string msg = "";
	char tempmsg[DEFAULT_BUFLEN] = "";

	//Session
	while (1)
	{
		memset(tempmsg, 0, DEFAULT_BUFLEN);


		/*if (new_client.socket != 0)
		{
		int iResult = recv(new_client.socket, tempmsg, DEFAULT_BUFLEN, 0);

		if (iResult != SOCKET_ERROR)
		{
		if (strcmp("", tempmsg))
		msg = "Client #" + std::to_string(new_client.id) + ": " + tempmsg + "\n";

		std::cout << msg.c_str();// << std::endl;*/

		//Broadcast that message to the other clients
		for (int i = 0; i < MAX_CLIENTS; i++)
		{ 

			for (int j = 0; j < MAX_CLIENTS; j++) {
				lo.lock();
				msg = tab[j];
				gotoxy(0, 7 + i);
				if (tab[i].empty()) std::cout << "                                       ";
				//else if (!tab[i].find("Disconnected\n"))std::cout << i <<" Disconn       ";
					else std::cout << tab[i];// << "                      ";
				lo.unlock();
				if (client_array[i].socket != INVALID_SOCKET)
					if (!tab[j].empty())
						int iResult = send(client_array[i].socket, msg.c_str(), strlen(msg.c_str()), 0);
			}
			Sleep(5);
		}
		/*}
		else
		{
		msg = "Client #" + std::to_string(new_client.id) + " Disconnected";

		std::cout << msg << std::endl;

		closesocket(new_client.socket);
		closesocket(client_array[new_client.id].socket);
		client_array[new_client.id].socket = INVALID_SOCKET;

		//Broadcast the disconnection message to the other clients
		for (int i = 0; i < MAX_CLIENTS; i++)
		{
		if (client_array[i].socket != INVALID_SOCKET)
		iResult = send(client_array[i].socket, msg.c_str(), strlen(msg.c_str()), 0);
		}

		break;
		}
		}*///end while
	}

	thread.detach();

	return 0;
}

int main()
{
	WSADATA wsaData;
	struct addrinfo hints;
	struct addrinfo *server = NULL;
	SOCKET server_socket = INVALID_SOCKET;
	std::string msg = "";
	std::vector<client_type> client(MAX_CLIENTS);
	int num_clients = 0;
	int temp_id = -1;
	std::thread my_thread[MAX_CLIENTS];

	//Initialize Winsock
	std::cout << "Intializing Winsock..." << std::endl;
	WSAStartup(MAKEWORD(2, 2), &wsaData);

	//Setup hints
	ZeroMemory(&hints, sizeof(hints));
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;
	hints.ai_flags = AI_PASSIVE;

	//Setup Server
	std::cout << "Enter Server's IP: "; // << std::endl;
	std::string ipaddr;
	std::cin >> ipaddr;
	struct sockaddr_in sa;
	//if (inet_pton(AF_INET, static_cast<LPCTSTR>(ipaddr.c_str()), &(sa.sin_addr)) != 0) return 1;
	

	std::cout << "Setting up server...\n";
	getaddrinfo(static_cast<LPCTSTR>(ipaddr.c_str()), DEFAULT_PORT, &hints, &server);
	//Create a listening socket for connecting to server

	std::cout << "Creating server socket..." << std::endl;
	server_socket = socket(server->ai_family, server->ai_socktype, server->ai_protocol);

	//Setup socket options
	setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &OPTION_VALUE, sizeof(int)); //Make it possible to re-bind to a port that was used within the last 2 minutes
	setsockopt(server_socket, IPPROTO_TCP, TCP_NODELAY, &OPTION_VALUE, sizeof(int)); //Used for interactive programs

																					 //Assign an address to the server socket.
	std::cout << "Binding socket..." << std::endl;
	bind(server_socket, server->ai_addr, (int)server->ai_addrlen);

	//Listen for incoming connections.
	std::cout << "Listening..." << std::endl;
	listen(server_socket, SOMAXCONN);

	//Initialize the client list
	for (int i = 0; i < MAX_CLIENTS; i++)
	{
		client[i] = { -1, INVALID_SOCKET };
	}

	std::thread brod = std::thread(broadcast, std::ref(client), std::ref(my_thread[temp_id]));

	while (1)
	{

		SOCKET incoming = INVALID_SOCKET;
		incoming = accept(server_socket, NULL, NULL);

		if (incoming == INVALID_SOCKET) continue;

		//Reset the number of clients
		num_clients = -1;

		//Create a temporary id for the next client
		temp_id = -1;
		for (int i = 0; i < MAX_CLIENTS; i++)
		{
			if (client[i].socket == INVALID_SOCKET && temp_id == -1)
			{
				client[i].socket = incoming;
				client[i].id = i;
				temp_id = i;
			}

			if (client[i].socket != INVALID_SOCKET)
				num_clients++;

			//std::cout << client[i].socket << std::endl;
		}

		if (temp_id != -1)
		{
			//Send the id to that client
			//gotoxy(0, 7 + MAX_CLIENTS + 3); std::cout << "Client #" << client[temp_id].id << " Accepted    " << std::endl;
			msg = std::to_string(client[temp_id].id);
			send(client[temp_id].socket, msg.c_str(), strlen(msg.c_str()), 0);

			//Create a thread process for that client
			my_thread[temp_id] = std::thread(process_client, std::ref(client[temp_id]), std::ref(client), std::ref(my_thread[temp_id]));
		}
		else
		{
			gotoxy(0, 7 + MAX_CLIENTS + 3); msg = "Server is full";
			send(incoming, msg.c_str(), strlen(msg.c_str()), 0);
			std::cout << msg << std::endl;
		}
	} //end while


	  //Close listening socket
	closesocket(server_socket);

	//Close client socket
	for (int i = 0; i < MAX_CLIENTS; i++)
	{
		my_thread[i].detach();
		closesocket(client[i].socket);
	}

	//Clean up Winsock
	WSACleanup();
	gotoxy(0, 7 + MAX_CLIENTS + 3); std::cout << "Program has ended successfully" << std::endl;

	system("pause");
	return 0;
}