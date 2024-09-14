// ch28/ex28.1/ex28.1.go
package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprint(w, "Hello World") // ❶ 웹 핸들러 등록
	})

	http.HandleFunc("/fuckyou", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprint(w, "fuck "+r.RequestURI+", ")
	})

	http.ListenAndServe(":3000", nil) // ❷ 웹 서버 시작
}
