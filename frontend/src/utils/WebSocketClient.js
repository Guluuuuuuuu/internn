import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketClient {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.connectCallbacks = [];
    }

    connect() {
        return new Promise((resolve, reject) => {
            const socket = new SockJS('http://localhost:8888/ws');
            this.stompClient = Stomp.over(socket);

            this.stompClient.connect({}, 
                (frame) => {
                    this.connected = true;
                    console.log('Connected to WebSocket:', frame);
                    this.connectCallbacks.forEach(callback => callback());
                    resolve();
                },
                (error) => {
                    console.error('WebSocket connection error:', error);
                    this.connected = false;
                    reject(error);
                    // 重连逻辑
                    setTimeout(() => this.connect(), 5000);
                }
            );
        });
    }

    onConnect(callback) {
        if (this.connected) {
            callback();
        } else {
            this.connectCallbacks.push(callback);
        }
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.connected = false;
            this.connectCallbacks = [];
        }
    }

    subscribe(destination, callback) {
        if (this.connected && this.stompClient) {
            return this.stompClient.subscribe(destination, callback);
        } else {
            this.onConnect(() => {
                this.stompClient.subscribe(destination, callback);
            });
        }
    }

    send(destination, body) {
        if (this.connected && this.stompClient) {
            this.stompClient.send(destination, {}, JSON.stringify(body));
        }
    }
}

export default new WebSocketClient(); 