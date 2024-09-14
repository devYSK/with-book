// ch28/ex28.2/ex28.2.go
package main

import (
	"fmt"
	"net/http"
	"strconv"
)

func barHandler(w http.ResponseWriter, r *http.Request) {
	values := r.URL.Query()    // ❶ 쿼리 인수 가져오기
	name := values.Get("name") // ❷ 특정 키값이 있는지 확인
	if name == "" {
		name = "World"
	}
	id, _ := strconv.Atoi(values.Get("id")) // ❸ id값을 가져와서 int타입 변환
	_, err := fmt.Fprintf(w, "Hello %s! id:%d", name, id)

	if err != nil {
		return
	}
}

func main() {
	http.HandleFunc("/bar", barHandler) // ❹ "/bar" 핸들러 등록
	http.ListenAndServe(":3000", nil)
}
