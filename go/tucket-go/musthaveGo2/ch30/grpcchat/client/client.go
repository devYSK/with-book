package main

import (
	"bufio"
	"context"
	"flag"
	pb "grpcchat/chatproto"
	"io"
	"log"
	"os"
	"strings"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

// 1 실행 인수를 정의합니다.
var id = flag.String("id", "unknown", "The id name")
var serverAddr = flag.String("addr", "localhost:50051", "The server address in the format of host:port")

func main() {
	flag.Parse()
	// 2 grpc 서버에 연결합니다.
	conn, err := grpc.Dial(*serverAddr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("fail to dial: %v", err)
	}
	defer conn.Close()
	// 3 Chat 서비스 클라이언트를 실행합니다.
	client := pb.NewChatServiceClient(conn)
	runChat(client)
}
func runChat(client pb.ChatServiceClient) {
	// 4 Chat 기능을 호출합니다.
	stream, err := client.Chat(context.Background())
	if err != nil {
		log.Fatalf("client.Char failed: %v", err)
	}
	waitc := make(chan struct{})
	go func() {
		for {
			// 5 출력 스트림으로 출력값이 나오면 화면에 출력합니다.
			in, err := stream.Recv()
			if err == io.EOF {
				// read done
				close(waitc)
				return
			}
			if err != nil {
				log.Fatalf("client.Chat failed: %v", err)
			}
			log.Printf("Sender:%s Message:%s", in.Sender, in.Message)
		}
	}()
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		msg := scanner.Text()
		if strings.ToLower(msg) == "exit" {
			break
		}
		// 6 키보드로 한 줄을 입력받아 입력으로 넣어줍니다.
		stream.Send(&pb.ChatMsg{
			Sender:  *id,
			Message: msg,
		})
	}
	stream.CloseSend()
	<-waitc
}
