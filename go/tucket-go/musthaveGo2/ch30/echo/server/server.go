package main

import (
	"flag"
	"fmt"
	"log"

	gnet "github.com/panjf2000/gnet/v2"
)

type echoServer struct {
	gnet.BuiltinEventEngine
	eng       gnet.Engine
	addr      string
	multicore bool
}

// 1 OnOpen은 새로운 클라이언트가 접속할 때 호출됩니다.
func (es *echoServer) OnOpen(c gnet.Conn) (out []byte, action gnet.Action) {
	log.Printf("client connected. address:%s", c.RemoteAddr().String())
	return nil, gnet.None
}

// 2 OnClose는 클라이언트 접속을 해제할 때 호출됩니다.
func (es *echoServer) OnClose(c gnet.Conn, err error) (action gnet.Action) {
	log.Printf("client disconnected. address:%s",
		c.RemoteAddr().String())
	return gnet.None
}

// 3 서버가 시작될 때 호출됩니다.
func (es *echoServer) OnBoot(eng gnet.Engine) gnet.Action {
	es.eng = eng
	log.Printf("echo server with multi-core=%t is listening on %s\n",
		es.multicore, es.addr)
	return gnet.None
}

// 4 서버가 데이터를 네트워크를 통해서 클라이언트로부터 수신할 때 호출됩니다.
func (es *echoServer) OnTraffic(c gnet.Conn) gnet.Action {
	buf, _ := c.Next(-1) // 모든 데이터를 읽는다
	c.Write(buf)         // 읽은 데이터를 다시 전송한다
	return gnet.None
}
func main() {
	var port int
	var multicore bool
	// 5
	// Example command: go run echo.go --port 9000 --multicore=true
	flag.IntVar(&port, "port", 9000, "--port 9000")
	flag.BoolVar(&multicore, "multicore", false, "--multicore true")
	flag.Parse()
	// 6
	echo := &echoServer{
		addr:      fmt.Sprintf("tcp://:%d", port),
		multicore: multicore,
	}
	log.Fatal(gnet.Run(echo, echo.addr, gnet.WithMulticore(multicore)))
}
