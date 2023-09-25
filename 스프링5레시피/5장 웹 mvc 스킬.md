[toc]









# 웹소켓

```groovy
implementation 'org.springframework.boot:spring-boot-starter-websocket'
```

설정

```java
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Bean
    public EchoHandler echoHandler() {
        return new EchoHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoHandler(), "/echo")
            .addInterceptors();
    }

}
```

버퍼 크기, 타임아웃 등 웹소켓 엔진을 추가 설정할 경우 servletservercontainerf actorys ean 객체를 추가합니다.

## WebSocketHandler

웹소켓 메시지를 처리하고 생애주기 이벤트(핸드셰이크, 접속 체결) 등을 구현하여 엔드포인트에 등록

| **메서드**                     | **설명**                                                     |
| ------------------------------ | ------------------------------------------------------------ |
| `afterConnectionEstablished()` | 웹소켓이 열리고 사용할 준비가 되면 호출됩니다.               |
| `handleMessage()`              | 웹소켓 메시지가 도착하면 호출됩니다.                         |
| `handleTransportError()`       | 에러가 발생하면 호출됩니다.                                  |
| `afterConnectionClosed()`      | 웹소켓 접속이 닫힌 후 호출됩니다.                            |
| `supportsPartialMessages()`    | 핸들러의 부분 메시지(partial message) 지원 여부. `true`이면 웹소켓 메시지를 여러 번 호출해서 받아올 수 있습니다. |

```java
public class EchoHandler extends TextWebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		session.sendMessage(new TextMessage("CONNECTION ESTABLISHED"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		session.sendMessage(new TextMessage("CONNECTION CLOSED"));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload();
		session.sendMessage(new TextMessage("RECEIVED: " + msg));
	}
}
```

다음 app.is 파일처럼 자바스크립트와 HTML을 약간 곁들이면 됩니다.



```js
var ws = null;
var url = "ws://localhost:8080/echo-ws/echo-endpoint";

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('echo').disabled = !connected;
}

function connect() {
    ws = webstomp.client(url);
    ws.connect({}, function (frame) {
        setConnected(true);
        log(frame);
        ws.subscribe('/topic/echo', function (message) {
            log(message.body);
        })
    });
}

function disconnect() {
    if (ws != null) {
        ws.disconnect();
        ws = null;
    }
    setConnected(false);
}

function echo() {
    if (ws != null) {
        var message = document.getElementById('message').value;
        log('Sent: ' + message);
        ws.send("/app/echo", message);
    } else {
        alert('connection not established, please connect.');
    }
}

function log(message) {
    var console = document.getElementById('logging');
    var p = document.createElement('p');
    p.appendChild(document.createTextNode(message));
    console.appendChild(p);
    while (console.childNodes.length > 12) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
}

```

Connect 버튼을 클릭하면 좀 전에 핸들러를 만 들 때 등록한 ws://localhost: 8080/echo-ws/echo URL.에 접속해 처음으로 웹소켓을 엽니다.

클라이언트는 서버 접속 후 생성한 자바스크립트 객체 webSocket을 사용해 메시지, 이벤트를 리스닝할 수 있습니다. 이 객체의 콜백 메서드 onopen, onmessage, onclose에 각각 기능을 넣 습니다. 이 중 가장 중요한 onmessage는 서버에서 메시지를 수신할 때마다 호출되는 콜백입니 다



## STOMP와 MEssaingMapping

웹소켓 프로토콜을 그대로 사용할 수도 있지만 하위 프로토콜을 쓰는 것도 가능합니다. 스 프링 웹소켓이 지원하는 STOMPSitmple Text-Oriened Protocol (단순 텍스트 지향 프로토콜)도 웹소켓 의 하위 프로토콜 중 하나입니다.

스프링 웹소켓 지원 기능을 이용해 STOMP를 설정하면 웹소켓 애플리케이션은 모든 접속 클라이언트에 대해 중개기처럼 작동합니다. 중개기는 인메모리 중개기 또는 (RabbitMQ나 ActiveMQ처럼) STOMP 프로토콜을 지원하는 온갖 기능이 구비된 기업용 솔루션을 쓸 수 있다.

```java
@Controller
public class EchoHandler {

	@MessageMapping("/echo")
	@SendTo("/topic/echo")
	public String echo(String msg) {
		return "RECEIVED: " + msg;
	}

}
```

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/echo-endpoint");
    }
}

```

중개기는 configurewessageBroker () 메서드에서 구성하는데요,

실제 엔터프라이즈 중개기에 접속할 때에는 registry.enablestompBrokerRelay() 메서드를 씁니다

수신된 STOMP 메시지를 리스닝하는 웹소켓 엔드포인트 (예제는 /echo-endpoint)는

registerstompEndpoints () 메서드를 오버라이드해서 등록합니다.



처리한 메시지는 각각 접두어를 달리하여 분간합니다. 

도착지가 / topic으로 시작하는 메시지 는 중개기로,

 /app으로 시작하는 메시지는 BMessagelapping을 붙인 핸들러 메서드로 각각 보 냅니다.





| 타입           | 설명                                                         |
| -------------- | ------------------------------------------------------------ |
| Message        | 헤더와 본문이 포함된 실제 하부 메시지입니다.                 |
| @Payload       | 메시지 페이로드(기본). 인수에 @Validated를 붙이면 검증 로직이 적용됩니다. |
| @Header        | Message에서 주어진 헤더를 얻습니다.                          |
| @Headers       | 전체 메시지 헤더를 Map 인수에 넣습니다.                      |
| MessageHeaders | 전체 Message 헤더                                            |
| Principal      | 현재 유저(설정됐을 경우만)                                   |