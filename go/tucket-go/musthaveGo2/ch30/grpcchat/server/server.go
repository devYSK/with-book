package main

import (
	"flag"
	"fmt"
	pb "grpcchat/chatproto"
	"io"
	"log"
	"net"
	"sync"

	"google.golang.org/grpc"
)

var port = flag.Int("port", 50051, "The server port")

func main() {
	flag.Parse()
	// 1 연결을 기다립니다.
	lis, err := net.Listen("tcp", fmt.Sprintf("localhost:%d", *port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	grpcServer := grpc.NewServer()
	// 2 Chat 서비스를 등록합니다.
	pb.RegisterChatServiceServer(grpcServer, newServer())
	grpcServer.Serve(lis)
}
func newServer() *chatServer {
	return &chatServer{}
}

// 3 Chat 서비스 인터페이스를 구현한 객체입니다.
type chatServer struct {
	pb.UnimplementedChatServiceServer
	mu      sync.Mutex
	streams []pb.ChatService_ChatServer
}

func (s *chatServer) Chat(stream pb.ChatService_ChatServer) error {
	// 4 streams 리스트에 추가합니다.
	s.mu.Lock()
	s.streams = append(s.streams, stream)
	s.mu.Unlock()
	var err error
	for {
		// 5 클라이언트로 전송된 입력값을 읽습니다.
		in, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			break
		}
		// 6 전체 클라이언트로 방송합니다.
		s.mu.Lock()
		for _, strm := range s.streams {
			strm.Send(&pb.ChatMsg{
				Sender:  in.Sender,
				Message: in.Message,
			})
		}
		s.mu.Unlock()
	}
	// 7 연결이 끊어졌기 때문에 리스트에서 삭제합니다.
	s.mu.Lock()
	for i, strm := range s.streams {
		if strm == stream {
			s.streams = append(s.streams[:i], s.streams[i+1:]...)
			break
		}
	}
	s.mu.Unlock()
	return err
}
